package uk.gov.moj.sdt.cmc.consumers.api;

import uk.gov.moj.sdt.cmc.consumers.request.WarrantRequest;
import uk.gov.moj.sdt.cmc.consumers.response.WarrantResponse;

public interface IWarrantService {

    WarrantResponse warrantRequest(String authorization,
                                   String serviceAuthorization,
                                   String idamId,
                                   String sdtRequestId,
                                   WarrantRequest warrantRequest);
}
