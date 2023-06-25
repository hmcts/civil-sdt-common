package uk.gov.moj.sdt.cmc.consumers.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseXmlTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseXmlTest.class);

    protected String readXmlAsString(String fileName) {
        StringBuilder sb = new StringBuilder();
        try {
            URL breathingSpaceURL = getClass().getClassLoader().getResource(fileName);
            Reader fileReader = new FileReader(breathingSpaceURL.getPath());
            BufferedReader bufReader = new BufferedReader(fileReader);
            String line = bufReader.readLine();
            while (line != null) {
                sb.append(line).append("\n");
                line = bufReader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return sb.toString();
    }

    protected String readFile(String fileName) {
        StringBuilder sb = new StringBuilder();
        try {
            URL breathingSpaceURL = getClass().getClassLoader().getResource(fileName);
            Reader fileReader = new FileReader(breathingSpaceURL.getPath());
            BufferedReader bufReader = new BufferedReader(fileReader);
            String line = bufReader.readLine();
            while (line != null) {
                sb.append(line);
                line = bufReader.readLine();
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return sb.toString();
    }
}
