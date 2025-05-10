package com.example.basic.xml.service;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.dom.DOMSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class StockChecker {
    private final Map<String, Boolean> stockInventory;

    public StockChecker() {
        this.stockInventory = new HashMap<>();
        // Sample inventory data
        stockInventory.put("1234", true);
        stockInventory.put("5678", false);
    }

    public Message<?> checkStock(Message<?> message) {
        try {
            // Extract the document from the message payload
            DOMSource source = (DOMSource) message.getPayload();
            Document document = (Document) source.getNode();

            // Get the ISBN from the order item
            String isbn = getIsbnFromDocument(document);
            boolean inStock = stockInventory.getOrDefault(isbn, false);

            // Add the in-stock attribute to the XML
            document.getDocumentElement().setAttribute("in-stock", String.valueOf(inStock));

            return MessageBuilder.withPayload(new DOMSource(document))
                    .copyHeaders(message.getHeaders())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error checking stock", e);
        }
    }

    private String getIsbnFromDocument(Document document) {
        // Extract ISBN using DOM API directly
        NodeList isbnNodes = document.getElementsByTagName("isbn");
        if (isbnNodes.getLength() > 0) {
            Node isbnNode = isbnNodes.item(0);
            return isbnNode.getTextContent();
        }
        return "";
    }
}