package uk.gov.moj.sdt.consumers.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CustomListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("CustomListener is initialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("CustomListener is destroyed");
    }
}
