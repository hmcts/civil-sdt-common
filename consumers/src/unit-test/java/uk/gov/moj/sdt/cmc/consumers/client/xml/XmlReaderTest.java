package uk.gov.moj.sdt.cmc.consumers.client.xml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.moj.sdt.cmc.consumers.client.BaseXmlTest;
import uk.gov.moj.sdt.utils.cmc.xml.XmlElementValueReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class XmlReaderTest extends BaseXmlTest {

    private static final String BREATHING_SPACE = "BreathingSpace.xml";

    private static final String CLAIM_DEFENCES = "ClaimDefences.xml";
    private static final String MCOL_DEFENCE_CRITERIA = "ClaimDefences_McolDefenceCriteria.xml";

    private static final String CLAIM_STATUS_UPDATE = "ClaimStatusUpdate.xml";

    @BeforeEach
    public void setup() {
    }

    @Test
    void shouldConvertBreathingSpaceRequestToString() {
        readXmlFileAndValidateData(BREATHING_SPACE, "claimNumber", "H0PR0001");
    }

    @Test
    void findFromDateFromClaimDefencesXmlString() {
        readXmlFileAndValidateData(CLAIM_DEFENCES, "fromDate", "2009-12-01");
    }

    @Test
    void shouldConvertClaimStatusUpdateRequestToString() {
        readXmlFileAndValidateData(CLAIM_STATUS_UPDATE, "claimNumber", "1676030589543579");
    }

    @Test
    void findValuesFromMcolDefenceCriteriaXmlString() {
        String xmlContent = readXmlAsString(MCOL_DEFENCE_CRITERIA);
        XmlElementValueReader xmlReader = new XmlElementValueReader();
        String fromDate = xmlReader.getElementValue(xmlContent, "fromDate");
        String toDate = xmlReader.getElementValue(xmlContent, "toDate");
        assertNotNull(fromDate);
        assertEquals("2009-12-01T00:00:00", fromDate);
        assertNotNull(toDate);
        assertEquals("2009-12-02T00:00:00", toDate);
    }

    private void readXmlFileAndValidateData(String fileName, String xmlNodeName, String expectedValue) {
        String xmlContent = readXmlAsString(fileName);
        XmlElementValueReader xmlReader = new XmlElementValueReader();
        String returnValue = xmlReader.getElementValue(xmlContent, xmlNodeName);
        assertNotNull(returnValue);
        assertEquals(expectedValue, returnValue);
    }

}
