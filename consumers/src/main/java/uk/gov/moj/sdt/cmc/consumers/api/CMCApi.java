package uk.gov.moj.sdt.cmc.consumers.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import uk.gov.moj.sdt.cmc.consumers.config.CMCConfig;
import uk.gov.moj.sdt.cmc.consumers.model.claimdefences.ClaimDefencesResponse;
import uk.gov.moj.sdt.cmc.consumers.request.BreathingSpaceRequest;
import uk.gov.moj.sdt.cmc.consumers.request.ClaimStatusUpdateRequest;
import uk.gov.moj.sdt.cmc.consumers.request.JudgementWarrantRequest;
import uk.gov.moj.sdt.cmc.consumers.request.WarrantRequest;
import uk.gov.moj.sdt.cmc.consumers.request.claim.ClaimRequest;
import uk.gov.moj.sdt.cmc.consumers.request.judgement.JudgementRequest;
import uk.gov.moj.sdt.cmc.consumers.response.BreathingSpaceResponse;
import uk.gov.moj.sdt.cmc.consumers.response.ClaimResponse;
import uk.gov.moj.sdt.cmc.consumers.response.ClaimStatusUpdateResponse;
import uk.gov.moj.sdt.cmc.consumers.response.JudgementWarrantResponse;
import uk.gov.moj.sdt.cmc.consumers.response.WarrantResponse;
import uk.gov.moj.sdt.cmc.consumers.response.judgement.JudgementResponse;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;


@FeignClient(name = "civil-api",
    url = "${civil.api.url}",
    configuration = CMCConfig.class,
    fallback = CMCApiFallback.class)
public interface CMCApi {

    String IDAM_ID_HEADER = "IDAMID";

    String SDT_REQUEST_ID = "SDTREQUESTID";

    String SERVICE_AUTHORIZATION = "ServiceAuthorization";

    String FROM_DATE_TIME = "fromDateTime";

    String TO_DATE = "toDate";

    @PostMapping("/breathingSpace")
    BreathingSpaceResponse breathingSpace(@RequestHeader(IDAM_ID_HEADER)  String idamId,
                                          @RequestHeader(SDT_REQUEST_ID) String sdtRequestId,
                                          @RequestBody BreathingSpaceRequest breathingSpaceRequest);

    @PostMapping("/breathingSpace")
    JudgementResponse requestJudgment(
        @RequestHeader(IDAM_ID_HEADER) String idamId,
        @RequestHeader(SDT_REQUEST_ID) String sdtRequestId,
        @RequestBody JudgementRequest breathingSpaceRequest
    );

    @PostMapping("/claimStatusUpdate")
    ClaimStatusUpdateResponse claimStatusUpdate(
        @RequestHeader(IDAM_ID_HEADER)  String idamId,
        @RequestHeader(SDT_REQUEST_ID) String sdtRequestId,
        @RequestBody ClaimStatusUpdateRequest claimStatusUpdateRequest
    );

    @GetMapping("/claimDefences")
    ClaimDefencesResponse claimDefences(
        @RequestHeader(AUTHORIZATION) String authorization,
        @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
        @RequestHeader(IDAM_ID_HEADER) String idAmId,
        @RequestHeader(FROM_DATE_TIME) String fromDateTime,
        @RequestHeader(TO_DATE) String toDate
    );

    @PostMapping("/requestWarrant")
    WarrantResponse warrantRequest(@RequestHeader(AUTHORIZATION) String authorization,
                                   @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
                                   @RequestHeader(IDAM_ID_HEADER)  String idamId,
                                   @RequestHeader(SDT_REQUEST_ID) String sdtRequestId,
                                   @RequestBody WarrantRequest warrantRequest);

    @PostMapping("/requestJudgmentAndWarrant")
    JudgementWarrantResponse judgementWarrantRequest(@RequestHeader(AUTHORIZATION) String authorization,
                                                     @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthorization,
                                                     @RequestHeader(IDAM_ID_HEADER)  String idamId,
                                                     @RequestHeader(SDT_REQUEST_ID) String sdtRequestId,
                                                     @RequestBody JudgementWarrantRequest judgmentWarrantRequest);

    @PostMapping("/createSDTClaim ")
    ClaimResponse createSDTClaim(
        @RequestHeader(IDAM_ID_HEADER)  String idamId,
        @RequestHeader(SDT_REQUEST_ID) String sdtRequestId,
        @RequestBody ClaimRequest claimRequest
    );

}
