package uk.gov.moj.sdt.consumers.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import uk.gov.moj.sdt.cmc.consumers.model.ResponseType;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResult;

public class ClaimDefencesResultsUtil {

    public List<ClaimDefencesResult> createClaimDefencesList() {

        List<ClaimDefencesResult> results = new ArrayList<>();

        results.add(createClaimDefencesResult("case11", "resp11",
                LocalDate.of(2021,10,20),
                LocalDateTime.of(2021,10,21,11,20,11),
                ResponseType.DE.name(),
                "defence11"));
        results.add(createClaimDefencesResult("case12", "resp12",
                LocalDate.of(2021,10,27),
                LocalDateTime.of(2021,10,28,6,8,10),
                ResponseType.AS.name(),
                "defence12"));
        results.add(createClaimDefencesResult("case13", "resp13",
                LocalDate.of(2021,10,29),
                LocalDateTime.of(2021,10,30,3,10,23),
                ResponseType.PA.name(),
                "defence12"));
        results.add(createClaimDefencesResult("case14", "resp14",
                LocalDate.of(2021,11,2),
                LocalDateTime.of(2021,11,2,7,15,49),
                ResponseType.DC.name(),
                "defence14"));
        String case15 = "case15";
        String resp15 = "resp15";
        results.add(createClaimDefencesResult(case15, resp15,
                                              LocalDate.of(2021,11,9),
                                              LocalDateTime.of(2021,11,10,13,19,24),
                                              ResponseType.DE.name(),
                                              "It wasn't me!"));
        results.add(createClaimDefencesResult(case15, resp15,
                                              LocalDate.of(2021,11,9),
                                              LocalDateTime.of(2021,11, 10,14,19,24),
                                              ResponseType.DE.name(),
                                              "My name is Shaggy"));
        results.add(createClaimDefencesResult(case15, resp15,
                                              LocalDate.of(2021,11,9),
                                              LocalDateTime.of(2021,11,10,15,19,24),
                                              ResponseType.DE.name(),
                                              "And I am Chaka Demus - innocent"));

        return results;
    }

    public ClaimDefencesResult createClaimDefencesResult() {
        return createClaimDefencesResult("case11", "resp11",
                LocalDate.of(2021,10,20),
                LocalDateTime.of(2021,10,21,11,20,11),
                ResponseType.DE.name(),
                "defence11");
    }

    public ClaimDefencesResult createClaimDefencesResult(String caseManRef, String respondentId,
                                                          LocalDate defendantResponseFiledDate,
                                                          LocalDateTime defendantResponseCreatedDate, String responseType,
                                                          String defence) {
        return new ClaimDefencesResult(caseManRef, respondentId, defendantResponseFiledDate,
                defendantResponseCreatedDate, responseType ,defence);
    }

}
