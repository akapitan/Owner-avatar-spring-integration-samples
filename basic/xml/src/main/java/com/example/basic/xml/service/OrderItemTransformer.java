package com.example.basic.xml.service;

import com.example.basic.xml.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

@Component
public class OrderItemTransformer {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemTransformer.class);

    /**
     * Transforms an order item XML document to the format expected by the external supplier
     */
    public Message<Order> transformToSupplierFormat(Message<String> message) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Order.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        Order order = (Order) unmarshaller.unmarshal(new InputSource(new StringReader(message.getPayload())));
        return MessageBuilder.withPayload(order)
                .copyHeaders(message.getHeaders())
                .build();
    }

}