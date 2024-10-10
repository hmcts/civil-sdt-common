package uk.gov.moj.sdt.transformers.api;

import uk.gov.moj.sdt.cmc.model.CMCUpdateRequest;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;

public interface ICMCFeedbackTransformer {

    /**
     * Transform a CMCUpdateRequest constructed from JSON to an IndividualRequest domain object.
     *
     * @param sdtRequestId The SDT request id
     * @param cmcUpdateRequest The CMC update request
     * @return The IndividualRequest domain object
     */
    IIndividualRequest transformJsonToDomain(String sdtRequestId, CMCUpdateRequest cmcUpdateRequest);
}
