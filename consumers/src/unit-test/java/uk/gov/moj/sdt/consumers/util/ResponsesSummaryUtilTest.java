package uk.gov.moj.sdt.consumers.util;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.moj.sdt.cmc.consumers.client.BaseXmlTest;
import uk.gov.moj.sdt.cmc.consumers.model.DefendantResponseType;
import uk.gov.moj.sdt.cmc.consumers.model.McolDefenceDetailType;
import uk.gov.moj.sdt.cmc.consumers.model.ResponseType;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResult;
import uk.gov.moj.sdt.cmc.consumers.util.ResponsesSummaryUtil;
import uk.gov.moj.sdt.response.SubmitQueryResponse;
import uk.gov.moj.sdt.ws._2013.sdt.targetapp.submitqueryresponseschema.SubmitQueryResponseType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class for the consumer gateway.
 *
 * @author Mark Dathorne
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResponseSummaryUtilTest extends BaseXmlTest {

    private ResponsesSummaryUtil responsesSummaryUtil;
    private McolDefenceDetailTypeUtil mcolDefenceDetailTypeUtil;
    private ClaimDefencesResultsUtil claimDefencesResultsUtil;

    /**
     * Method to do any pre-test set-up.
     */
    @BeforeEach
    public void setUpLocalTests() {
        claimDefencesResultsUtil = new ClaimDefencesResultsUtil();
        mcolDefenceDetailTypeUtil = new McolDefenceDetailTypeUtil();
        responsesSummaryUtil  = new ResponsesSummaryUtil(mcolDefenceDetailTypeUtil);
    }

   @Test
   void convertToMcolResultObjectsIsSuccessful() {
        List<ClaimDefencesResult> listObjects = claimDefencesResultsUtil.createClaimDefencesList();
        List<McolDefenceDetailType> listMcolTypes = responsesSummaryUtil.convertToMcolResults(listObjects);
        String xml = mcolDefenceDetailTypeUtil.convertMcolDefenceDetailListToXml(listMcolTypes);
        assertFalse(xml.isEmpty());
        assertTrue(xml.toLowerCase().contains("mcoldefencedetail"));
        assertTrue(xml.contains("defendantId=\"resp11\""));
   }

    @Test
    void shouldReturnEmptyResultsXMLWhenSubmitQueryResponseIsNull() {
        List<ClaimDefencesResult> listObjects = new ArrayList<>();
        String summaryResults = responsesSummaryUtil.getSummaryResults(null, listObjects);
        assertNotNull(summaryResults);
        assertTrue(summaryResults.contains("<ns2:results xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"/>"));
    }

    @Test
    void shouldReturnEmptyResultsXMLWhenSubmitQueryResponseTypeIsNull() {
        List<ClaimDefencesResult> listObjects = new ArrayList<>();
        SubmitQueryResponse queryResponse = mock(SubmitQueryResponse.class);
        when(queryResponse.getResponseType()).thenReturn(null);
        String summaryResults = responsesSummaryUtil.getSummaryResults(queryResponse, listObjects);
        assertNotNull(summaryResults);
        assertTrue(summaryResults.contains("<ns2:results xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"/>"));
    }

    @Test
    void shouldReturnEmptyResultsXMLWhenTargetAppDetailsNull() {
        List<ClaimDefencesResult> listObjects = new ArrayList<>();
        SubmitQueryResponse queryResponse = mock(SubmitQueryResponse.class);
        SubmitQueryResponseType responseType = mock(SubmitQueryResponseType.class);
        when(queryResponse.getResponseType()).thenReturn(responseType);
        when(responseType.getTargetAppDetail()).thenReturn(null);
        String summaryResults = responsesSummaryUtil.getSummaryResults(queryResponse, listObjects);
        assertNotNull(summaryResults);
        assertTrue(summaryResults.contains("<ns2:results xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"/>"));
    }

    @Test
    void shouldReturnEmptyResultsXMLWhenTargetAppDetailAnyCollectionNull() {
        List<ClaimDefencesResult> listObjects = new ArrayList<>();
        SubmitQueryResponse queryResponse = mock(SubmitQueryResponse.class);
        SubmitQueryResponseType responseType = mock(SubmitQueryResponseType.class);
        when(queryResponse.getResponseType()).thenReturn(responseType);
        SubmitQueryResponseType.TargetAppDetail targetAppDetail = mock(SubmitQueryResponseType.TargetAppDetail.class);
        when(responseType.getTargetAppDetail()).thenReturn(targetAppDetail);
        when(targetAppDetail.getAny()).thenReturn(null);
        String summaryResults = responsesSummaryUtil.getSummaryResults(queryResponse, listObjects);
        assertNotNull(summaryResults);
        assertTrue(summaryResults.contains("<ns2:results xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"/>"));
    }

    @Test
    void shouldReturnEmptyResultsXMLWhenTargetAppDetailAnyCollectionHasValues() {
        List<ClaimDefencesResult> listObjects = new ArrayList<>();
        SubmitQueryResponse queryResponse = mock(SubmitQueryResponse.class);
        SubmitQueryResponseType responseType = mock(SubmitQueryResponseType.class);
        when(queryResponse.getResponseType()).thenReturn(responseType);
        SubmitQueryResponseType.TargetAppDetail targetAppDetail = mock(SubmitQueryResponseType.TargetAppDetail.class);
        when(responseType.getTargetAppDetail()).thenReturn(targetAppDetail);
        McolDefenceDetailType defenceDetailType = mock(McolDefenceDetailType.class);
        when(defenceDetailType.getClaimNumber()).thenReturn("123456");
        DefendantResponseType defendantResponseType = mock(DefendantResponseType.class);
        when(defendantResponseType.getResponseType()).thenReturn(ResponseType.AS);
        when(defendantResponseType.getDefence()).thenReturn("Defence");
        when(defendantResponseType.getDefendantId()).thenReturn("ID1234");
        when(defenceDetailType.getDefendantResponse()).thenReturn(defendantResponseType);
        when(targetAppDetail.getAny()).thenReturn(Lists.newArrayList(defenceDetailType));
        String summaryResults = responsesSummaryUtil.getSummaryResults(queryResponse, listObjects);
        assertNotNull(summaryResults);
        assertFalse(summaryResults.contains("<ns2:results xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"/>"));
        String expectedContent = readFile("Expected_ResponseSummary.xml");
        assertEquals(expectedContent, summaryResults);
    }

    @Test
    void shouldReturnEmptyResultsXMLWhenTargetAppDetailAnyCollectionHasClaimDefencesResults() {
        List<ClaimDefencesResult> listObjects = Lists.newArrayList(claimDefencesResultsUtil.createClaimDefencesResult());
        SubmitQueryResponse queryResponse = mock(SubmitQueryResponse.class);
        SubmitQueryResponseType responseType = mock(SubmitQueryResponseType.class);
        when(queryResponse.getResponseType()).thenReturn(responseType);
        SubmitQueryResponseType.TargetAppDetail targetAppDetail = mock(SubmitQueryResponseType.TargetAppDetail.class);
        when(responseType.getTargetAppDetail()).thenReturn(targetAppDetail);
        McolDefenceDetailType defenceDetailType = mock(McolDefenceDetailType.class);
        when(defenceDetailType.getClaimNumber()).thenReturn("123456");
        DefendantResponseType defendantResponseType = mock(DefendantResponseType.class);
        when(defendantResponseType.getResponseType()).thenReturn(ResponseType.AS);
        when(defendantResponseType.getDefence()).thenReturn("Defence");
        when(defendantResponseType.getDefendantId()).thenReturn("ID1234");
        when(defenceDetailType.getDefendantResponse()).thenReturn(defendantResponseType);
        when(targetAppDetail.getAny()).thenReturn(Lists.newArrayList(defenceDetailType));
        String summaryResults = responsesSummaryUtil.getSummaryResults(queryResponse, listObjects);
        assertNotNull(summaryResults);
        assertFalse(summaryResults.contains("<ns2:results xmlns:ns2=\"http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema\"/>"));
        assertTrue(summaryResults.contains("<mcolDefenceDetail>"));
    }
}
