package com.example.basic.xml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

@Component
public class OrderItemTransformer {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemTransformer.class);

    /**
     * Transforms an order item XML document to the format expected by the external supplier
     */
    public Message<?> transformToSupplierFormat(Message<?> message) {
        try {
            // Extract input document
            Document sourceDoc = extractDocument(message.getPayload());

            // Create new supplier document
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document supplierDoc = docBuilder.newDocument();

            // Create root element for supplier order
            Element orderRequest = supplierDoc.createElement("orderRequest");
            supplierDoc.appendChild(orderRequest);

            // Create book element
            Element book = supplierDoc.createElement("book");
            orderRequest.appendChild(book);

            // Extract ISBN from source document and add to supplier document
            String isbnText = sourceDoc.getElementsByTagName("isbn").item(0).getTextContent();
            Element isbn = supplierDoc.createElement("isbn");
            isbn.setTextContent(isbnText);
            book.appendChild(isbn);

            // Extract quantity from source document and add to supplier document
            String quantityText = sourceDoc.getElementsByTagName("quantity").item(0).getTextContent();
            Element quantity = supplierDoc.createElement("quantity");
            quantity.setTextContent(quantityText);
            book.appendChild(quantity);

            // Add priority element
            Element priority = supplierDoc.createElement("priority");
            priority.setTextContent("STANDARD");
            book.appendChild(priority);

            // Log the transformation
            logTransformation(sourceDoc, supplierDoc);

            // Return new document
            return MessageBuilder.withPayload(new DOMSource(supplierDoc))
                    .copyHeaders(message.getHeaders())
                    .build();
        } catch (Exception e) {
            logger.error("Error transforming order item", e);
            throw new RuntimeException("Error transforming order item", e);
        }
    }

    private Document extractDocument(Object payload) throws Exception {
        if (payload instanceof DOMSource) {
            return (Document) ((DOMSource) payload).getNode();
        } else if (payload instanceof Source) {
            DOMResult result = new DOMResult();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform((Source) payload, result);
            return (Document) result.getNode();
        } else if (payload instanceof String) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader((String) payload));
            return builder.parse(is);
        } else {
            throw new IllegalArgumentException("Unsupported payload type: " + payload.getClass().getName());
        }
    }

    private void logTransformation(Document sourceDoc, Document targetDoc) {
        try {
            logger.debug("Transformed order item with ISBN: {}",
                    sourceDoc.getElementsByTagName("isbn")
                            .item(0).getTextContent());

            StringWriter writer = new StringWriter();
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.transform(new DOMSource(targetDoc), new StreamResult(writer));
            logger.debug("Transformed to supplier format: {}", writer.toString());
        } catch (Exception e) {
            logger.error("Error logging transformation", e);
        }
    }
}