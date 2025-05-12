package com.example.basic.xml.service;

import com.example.basic.xml.domain.Order;
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

    public Message<Order> transformToSupplierFormat(Message<String> message) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Order.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        Order order = (Order) unmarshaller.unmarshal(new InputSource(new StringReader(message.getPayload())));
        return MessageBuilder.withPayload(order)
                .copyHeaders(message.getHeaders())
                .build();
    }

}