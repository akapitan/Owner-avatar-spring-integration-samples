package com.example.basic.xml.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;

@Component
public class WarehouseDispatch {
    private static final Logger logger = LoggerFactory.getLogger(WarehouseDispatch.class);

    public void dispatch(Object payload) {
        try {
            if (payload instanceof Source) {
                logger.info("Order dispatched to warehouse for processing (Source)");
            } else if (payload instanceof String) {
                logger.info("Order dispatched to warehouse for processing (String): " + payload);
            } else {
                logger.info("Order dispatched to warehouse for processing (Unknown type): " + payload.getClass().getName());
            }
        } catch (Exception e) {
            logger.error("Error dispatching to warehouse", e);
        }
    }
}