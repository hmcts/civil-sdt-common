package uk.gov.moj.sdt.utils.cmc.xml;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class XmlElementValueReader {

    private XmlMapper xmlMapper = new XmlMapper();

    public String getElementValue(String xmlContent, String xmlNodeName) {
        JsonNode entityNode = null;
        try {
            JsonNode jsonNode = xmlMapper.readValue(xmlContent.getBytes(), JsonNode.class);
            entityNode = getValuesInObject(jsonNode, xmlNodeName);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return entityNode != null ? entityNode.textValue() : "";
    }

    private JsonNode getValuesInObject(JsonNode node, String entityName) {
        if (node == null) {
            return null;
        }
        if (node.has(entityName)) {
            return node.get(entityName);
        }
        if (!node.isContainerNode()) {
            return null;
        }
        for (JsonNode child : node) {
            JsonNode childResult = getValuesInObject(child, entityName);
            if (childResult != null && !childResult.isMissingNode()) {
                return childResult;
            }
        }
        return null;
    }

}
