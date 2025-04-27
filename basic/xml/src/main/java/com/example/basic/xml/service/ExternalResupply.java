package com.example.basic.xml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;

public class ExternalResupply {
    private static final Logger logger = LoggerFactory.getLogger(ExternalResupply.class);

    public void orderResupply(Object payload) {
        try {
            if (payload instanceof Source) {
                logger.info("Resupply order sent to external supplier (Source)");
            } else if (payload instanceof String) {
                logger.info("Resupply order sent to external supplier (String): " + payload);
            } else {
                logger.info("Resupply order sent to external supplier (Unknown type): " + payload.getClass().getName());
            }
        } catch (Exception e) {
            logger.error("Error sending resupply order", e);
        }
    }
}