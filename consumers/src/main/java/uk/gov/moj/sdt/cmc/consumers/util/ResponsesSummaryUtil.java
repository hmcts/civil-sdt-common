package uk.gov.moj.sdt.cmc.consumers.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.cmc.consumers.model.DefendantResponseType;
import uk.gov.moj.sdt.cmc.consumers.model.McolDefenceDetailType;
import uk.gov.moj.sdt.cmc.consumers.model.McolDefenceDetailTypes;
import uk.gov.moj.sdt.cmc.consumers.model.ResponseType;
import uk.gov.moj.sdt.response.SubmitQueryResponse;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResult;
import uk.gov.moj.sdt.consumers.util.McolDefenceDetailTypeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ResponsesSummaryUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponsesSummaryUtil.class);

    private McolDefenceDetailTypeUtil mcolDefenceDetailTypeUtil;

    public ResponsesSummaryUtil(McolDefenceDetailTypeUtil mcolDefenceDetailTypeUtil) {
        this.mcolDefenceDetailTypeUtil =  mcolDefenceDetailTypeUtil;
    }

    public String getSummaryResults(SubmitQueryResponse mcolSubmitQueryResponse,
                                    List<ClaimDefencesResult> cmcResults) {

        // List of summary mcol XML objects
        List<McolDefenceDetailType> mcolSummaryResults = new ArrayList<>();

        // Add Mcol results to summary results
        if (null != mcolSubmitQueryResponse && null != mcolSubmitQueryResponse.getResponseType()
        && null != mcolSubmitQueryResponse.getResponseType().getTargetAppDetail()
        && null != mcolSubmitQueryResponse.getResponseType().getTargetAppDetail().getAny()) {
            List<Object> mcolResultObjects = mcolSubmitQueryResponse.getResponseType().getTargetAppDetail().getAny();
            mcolResultObjects.forEach(object -> {
                McolDefenceDetailType detailTypeA = (McolDefenceDetailType) object;
                mcolSummaryResults.add(detailTypeA);
            });
        }

        // Convert cmc results and add to summary results
        List<McolDefenceDetailType> cmcConvertedResults = convertToMcolResults(cmcResults);
        McolDefenceDetailTypes mcolDefenceDetailTypes = new McolDefenceDetailTypes();
        mcolSummaryResults.addAll(cmcConvertedResults);
        mcolDefenceDetailTypes.setMcolDefenceDetailTypeList(mcolSummaryResults);

        // convert summary results to xml
        String summaryResultsXml = mcolDefenceDetailTypeUtil.convertMcolDefenceDetailListToXml(mcolDefenceDetailTypes);
        LOGGER.debug("summary XML: {}", summaryResultsXml);

        return summaryResultsXml;
    }

    public List<McolDefenceDetailType> convertToMcolResults(List<ClaimDefencesResult> cmcResults) {
        List<McolDefenceDetailType> detailTypes = new ArrayList<>();
        cmcResults.forEach(cmcResult -> {
            McolDefenceDetailType detailType = convertToMcolResult(cmcResult);
            detailTypes.add(detailType);
        });
        return detailTypes;
    }

    public McolDefenceDetailType convertToMcolResult(ClaimDefencesResult cmcResult) {
        McolDefenceDetailType detailType = new McolDefenceDetailType();
        DefendantResponseType defendantResponseType = new DefendantResponseType();
        defendantResponseType.setDefendantId(cmcResult.getRespondentId());
        defendantResponseType.setResponseType(ResponseType.fromValue(cmcResult.getResponseType()));
        defendantResponseType.setDefence(cmcResult.getDefence());
        defendantResponseType.setRaisedOnMcol(false);
        defendantResponseType.setFiledDate(convertLocalDateToCalendar(cmcResult.getDefendantResponseFiledDate()));
        defendantResponseType.setEventCreatedDateOnMcol(
                convertLocalDateTimeToCalendar(cmcResult.getDefendantResponseCreatedDate()));

        detailType.setClaimNumber(cmcResult.getCaseManRef());
        detailType.setDefendantResponse(defendantResponseType);
        return detailType;
    }

    public Calendar convertLocalDateTimeToCalendar(LocalDateTime localDateTime) {
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public Calendar convertLocalDateToCalendar(LocalDate localDate) {
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

}
