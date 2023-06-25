package uk.gov.moj.sdt.consumers.util;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.namespace.QName;

import org.springframework.stereotype.Component;
import uk.gov.moj.sdt.cmc.consumers.model.DefendantResponseType;
import uk.gov.moj.sdt.cmc.consumers.model.McolDefenceDetailType;
import uk.gov.moj.sdt.cmc.consumers.model.McolDefenceDetailTypes;
import uk.gov.moj.sdt.cmc.consumers.model.ResponseType;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResult;
import uk.gov.moj.sdt.consumers.exception.XmlConversionException;

@Component
public class McolDefenceDetailTypeUtil {

    public McolDefenceDetailTypes createMcolDefencesDetailTypes(List<McolDefenceDetailType> detailTypes) {
        McolDefenceDetailTypes mcolDefenceDetailTypes = new McolDefenceDetailTypes();
        mcolDefenceDetailTypes.setMcolDefenceDetailTypeList(detailTypes);
        return mcolDefenceDetailTypes;
    }

    public McolDefenceDetailType convertToMcolDefenceDetailType(ClaimDefencesResult cmcResult) {
        McolDefenceDetailType detailType = new McolDefenceDetailType();
        DefendantResponseType defendantResponseType = new DefendantResponseType();
        defendantResponseType.setDefendantId(cmcResult.getRespondentId());
        defendantResponseType.setResponseType(ResponseType.fromValue(cmcResult.getResponseType()));
        defendantResponseType.setDefence(cmcResult.getDefence());
        defendantResponseType.setRaisedOnMcol(false);
        defendantResponseType.setFiledDate(
                convertLocalDateToCalendar(cmcResult.getDefendantResponseFiledDate()));
        defendantResponseType.setEventCreatedDateOnMcol(
                convertLocalDateTimeToCalendar(cmcResult.getDefendantResponseCreatedDate()));

        detailType.setClaimNumber(cmcResult.getCaseManRef());
        detailType.setDefendantResponse(defendantResponseType);

        return detailType;
    }

    @XmlElementDecl(namespace = "http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema", name = "mcolDefenceDetail")
    public JAXBElement<McolDefenceDetailType> createMcolDefenceDetail(McolDefenceDetailType value) {
        final QName qnameMcolDefenceDetail = new QName("http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema",
                "mcolDefenceDetail");
        return new JAXBElement<>(qnameMcolDefenceDetail, McolDefenceDetailType.class, null, value);
    }

    @XmlElementDecl(namespace = "http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema", name = "results")
    public JAXBElement<McolDefenceDetailTypes> createMcolDefenceDetailList(McolDefenceDetailTypes value) {
        final QName qnameMcolDefenceDetail = new QName("http://ws.sdt.moj.gov.uk/2013/mcol/QuerySchema",
                "results");
        return new JAXBElement<>(qnameMcolDefenceDetail, McolDefenceDetailTypes.class, null, value);
    }

    public String convertMcolDefenceDetailListToXml(List<McolDefenceDetailType> listDetailTypes) {
        McolDefenceDetailTypes mcolDefenceDetailTypes = createMcolDefencesDetailTypes(listDetailTypes);
        return convertMcolDefenceDetailListToXml(mcolDefenceDetailTypes);
    }

    public String convertMcolDefenceDetailListToXml(McolDefenceDetailTypes detailTypes) {

        JAXBElement<McolDefenceDetailTypes> jaxbDetailType = createMcolDefenceDetailList(detailTypes);
        StringWriter stringWriter = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(McolDefenceDetailTypes.class);
            Marshaller marshaller =  jaxbContext.createMarshaller();
            marshaller.marshal(jaxbDetailType, stringWriter);
        } catch (JAXBException e) {
            throw new XmlConversionException(e);
        }
        return stringWriter.toString();
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
