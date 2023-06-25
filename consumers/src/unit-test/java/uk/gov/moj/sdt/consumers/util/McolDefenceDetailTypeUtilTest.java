package uk.gov.moj.sdt.consumers.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.cmc.consumers.model.McolDefenceDetailType;
import uk.gov.moj.sdt.cmc.consumers.model.McolDefenceDetailTypes;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test class for the consumer gateway.
 *
 * @author Mark Dathorne
 */
@ExtendWith(MockitoExtension.class)
class McolDefenceDetailTypeUtilTest {

    private ClaimDefencesResultsUtil claimDefencesResultsUtil;
    private McolDefenceDetailTypeUtil mcolDefenceDetailTypeUtil;
    /**
     * Method to do any pre-test set-up.
     */
    @BeforeEach
    public void setUpLocalTests() {
        claimDefencesResultsUtil = new ClaimDefencesResultsUtil();
        mcolDefenceDetailTypeUtil = new McolDefenceDetailTypeUtil();
    }

    @Test
    void shouldConvertMcolDefenceDetailTypeJAXBToXml() {

        McolDefenceDetailType detailType = mcolDefenceDetailTypeUtil.convertToMcolDefenceDetailType(
                claimDefencesResultsUtil.createClaimDefencesResult()
        );

        JAXBElement<McolDefenceDetailType> jaxbDetailType = mcolDefenceDetailTypeUtil.createMcolDefenceDetail(detailType);
        StringWriter stringWriter = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(McolDefenceDetailType.class);
            Marshaller marshaller =  jaxbContext.createMarshaller();
            marshaller.marshal(jaxbDetailType, stringWriter);
        } catch (JAXBException e) {
           fail(e.getMessage());
        }
        String content = stringWriter.toString();
        assertNotNull(content);
        assertTrue(content.contains(":mcolDefenceDetail"));
        assertTrue(content.contains("<claimNumber>"));
        assertTrue(content.contains("<defence>"));
        assertTrue(content.contains("<defendantResponse"));
    }


    private List<McolDefenceDetailType> createMcolDefencesDetailTypeList() {
        List<McolDefenceDetailType> detailTypes = new ArrayList<>();
        List<ClaimDefencesResult> results = claimDefencesResultsUtil.createClaimDefencesList();
        results.forEach(cmcResult ->
                detailTypes.add(mcolDefenceDetailTypeUtil.convertToMcolDefenceDetailType(cmcResult))
        );
        return detailTypes;
    }

    @Test
    void shouldConvertMcolDefenceDetailTypeListJAXBToXml() {
        McolDefenceDetailTypes detailTypes = new McolDefenceDetailTypes();
        detailTypes.setMcolDefenceDetailTypeList(createMcolDefencesDetailTypeList());

        String content = mcolDefenceDetailTypeUtil.convertMcolDefenceDetailListToXml(detailTypes);
        assertNotNull(content);
        assertTrue(content.contains("mcolDefenceDetail"));
        assertTrue(content.contains("<claimNumber>"));
        assertTrue(content.contains("<defence>"));
        assertTrue(content.contains("<defendantResponse"));
    }

    @Test
    void shouldRemoveXmlHeader() {
        Pattern pattern = Pattern.compile("<.xml.*?>");
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<ns2:results xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">" +
                "<mcolDefenceDetail><claimNumber>case11</claimNumber><defendantResponse defendantId=\"resp11\"><filedDate>2021-10-20T00:00:00+01:00</filedDate><eventCreatedDateOnMcol>2021-10-21T11:20:11+01:00</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>defence11</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case12</claimNumber><defendantResponse defendantId=\"resp12\"><filedDate>2021-10-27T00:00:00+01:00</filedDate><eventCreatedDateOnMcol>2021-10-28T06:08:10+01:00</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>AS</responseType><defence>defence12</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case13</claimNumber><defendantResponse defendantId=\"resp13\"><filedDate>2021-10-29T00:00:00+01:00</filedDate><eventCreatedDateOnMcol>2021-10-30T03:10:23+01:00</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>PA</responseType><defence>defence12</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case14</claimNumber><defendantResponse defendantId=\"resp14\"><filedDate>2021-11-02T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-02T07:15:49Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DC</responseType><defence>defence14</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case15</claimNumber><defendantResponse defendantId=\"resp15\"><filedDate>2021-11-09T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-10T13:19:24Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>It wasn't me!</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case15</claimNumber><defendantResponse defendantId=\"resp15\"><filedDate>2021-11-09T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-10T14:19:24Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>My name is Shaggy</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case15</claimNumber><defendantResponse defendantId=\"resp15\"><filedDate>2021-11-09T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-10T15:19:24Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>And I am Chaka Demus - innocent</defence></defendantResponse></mcolDefenceDetail>" +
                "</ns2:results>";
        String replaceString = "";
        assertTrue(pattern.matcher(xml).find());
        xml = pattern.matcher(xml).replaceAll(replaceString);
        assertFalse(xml.contains("<?xml"));
    }

    @Test
    void shouldReplaceXmlForEmptyResults() {
        Pattern pattern = Pattern.compile("<(qresp|ns2)\\:results(.*?)\\/>|<(qresp|ns2)\\:results(.*?)\\>.*<\\/(qresp|ns2)\\:results\\>");
        String xml = "<blah><ns2:results/></blah>";
        String replaceString = "<ns3:results><result><node/></result></ns3:resultw>";
        assertTrue(pattern.matcher(xml).find());
        xml = pattern.matcher(xml).replaceAll(replaceString);
        assertTrue(xml.contains(replaceString));
    }

    @Test
    void shouldReplaceXmlForSomeResults() {
        Pattern pattern = Pattern.compile("<(qresp|ns2)\\:results\\/>|<(qresp|ns2)\\:results.*\\>.*<\\/(qresp|ns2)\\:results\\>");
        String xml = "<ns2:results xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">" +
                "<mcolDefenceDetail><claimNumber>case11</claimNumber><defendantResponse defendantId=\"resp11\"><filedDate>2021-10-20T00:00:00+01:00</filedDate><eventCreatedDateOnMcol>2021-10-21T11:20:11+01:00</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>defence11</defence></defendantResponse></mcolDefenceDetail>" +
                "</ns2:results>";
        String replaceString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<ns2:results xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">" +
                "<mcolDefenceDetail><claimNumber>case11</claimNumber><defendantResponse defendantId=\"resp11\"><filedDate>2021-10-20T00:00:00+01:00</filedDate><eventCreatedDateOnMcol>2021-10-21T11:20:11+01:00</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>defence11</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case12</claimNumber><defendantResponse defendantId=\"resp12\"><filedDate>2021-10-27T00:00:00+01:00</filedDate><eventCreatedDateOnMcol>2021-10-28T06:08:10+01:00</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>AS</responseType><defence>defence12</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case13</claimNumber><defendantResponse defendantId=\"resp13\"><filedDate>2021-10-29T00:00:00+01:00</filedDate><eventCreatedDateOnMcol>2021-10-30T03:10:23+01:00</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>PA</responseType><defence>defence12</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case14</claimNumber><defendantResponse defendantId=\"resp14\"><filedDate>2021-11-02T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-02T07:15:49Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DC</responseType><defence>defence14</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case15</claimNumber><defendantResponse defendantId=\"resp15\"><filedDate>2021-11-09T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-10T13:19:24Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>It wasn't me!</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case15</claimNumber><defendantResponse defendantId=\"resp15\"><filedDate>2021-11-09T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-10T14:19:24Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>My name is Shaggy</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case15</claimNumber><defendantResponse defendantId=\"resp15\"><filedDate>2021-11-09T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-10T15:19:24Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>And I am Chaka Demus - innocent</defence></defendantResponse></mcolDefenceDetail>" +
                "</ns2:results>";
        assertTrue(pattern.matcher(xml).matches());
        xml = pattern.matcher(xml).replaceAll(replaceString);
        assertTrue(xml.contains(replaceString));
    }

//    @Test
    void shouldFindAndReplaceResultsInXmlEnvelope() {
        Pattern pattern = Pattern.compile("<(qresp|ns2)\\:results\\/>|<(qresp|ns2)\\:results.*\\>.*<\\/(qresp|ns2)\\:results\\>");
        String xml = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:breq=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkRequestSchema\" xmlns:bresp=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkResponseSchema\" xmlns:qreq=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryRequestSchema\" xmlns:bfresp=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackResponseSchema\" xmlns:qresp=\"http://ws.sdt.moj.gov.uk/2013/sdt/SubmitQueryResponseSchema\" xmlns:base=\"http://ws.sdt.moj.gov.uk/2013/sdt/BaseSchema\" xmlns:bfreq=\"http://ws.sdt.moj.gov.uk/2013/sdt/BulkFeedbackRequestSchema\"><soap:Body><qresp:submitQueryResponse><base:status code=\"Ok\"/><qresp:sdtCustomerId>12345678</qresp:sdtCustomerId><qresp:sdtService>SDT</qresp:sdtService><qresp:resultCount>7</qresp:resultCount><qresp:results/></qresp:submitQueryResponse></soap:Body></soap:Envelope>";
        String replaceString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<ns2:results xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\">" +
                "<mcolDefenceDetail><claimNumber>case11</claimNumber><defendantResponse defendantId=\"resp11\"><filedDate>2021-10-20T00:00:00+01:00</filedDate><eventCreatedDateOnMcol>2021-10-21T11:20:11+01:00</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>defence11</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case12</claimNumber><defendantResponse defendantId=\"resp12\"><filedDate>2021-10-27T00:00:00+01:00</filedDate><eventCreatedDateOnMcol>2021-10-28T06:08:10+01:00</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>AS</responseType><defence>defence12</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case13</claimNumber><defendantResponse defendantId=\"resp13\"><filedDate>2021-10-29T00:00:00+01:00</filedDate><eventCreatedDateOnMcol>2021-10-30T03:10:23+01:00</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>PA</responseType><defence>defence12</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case14</claimNumber><defendantResponse defendantId=\"resp14\"><filedDate>2021-11-02T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-02T07:15:49Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DC</responseType><defence>defence14</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case15</claimNumber><defendantResponse defendantId=\"resp15\"><filedDate>2021-11-09T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-10T13:19:24Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>It wasn't me!</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case15</claimNumber><defendantResponse defendantId=\"resp15\"><filedDate>2021-11-09T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-10T14:19:24Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>My name is Shaggy</defence></defendantResponse></mcolDefenceDetail>" +
                "<mcolDefenceDetail><claimNumber>case15</claimNumber><defendantResponse defendantId=\"resp15\"><filedDate>2021-11-09T00:00:00Z</filedDate><eventCreatedDateOnMcol>2021-11-10T15:19:24Z</eventCreatedDateOnMcol><raisedOnMcol>false</raisedOnMcol><responseType>DE</responseType><defence>And I am Chaka Demus - innocent</defence></defendantResponse></mcolDefenceDetail>" +
                "</ns2:results>";
        assertTrue(pattern.matcher(xml).matches());
        xml = pattern.matcher(xml).replaceAll(replaceString);
        assertTrue(xml.contains(replaceString));
    }

}
