package uk.gov.moj.sdt.services.api;

import uk.gov.moj.sdt.domain.api.IIndividualRequest;

public interface ICMCFeedbackService {

    /**
     * Update the individual request with information received from CMC.
     *
     * @param individualRequest The IndividualRequest to update
     */
    void cmcFeedback(IIndividualRequest individualRequest);
}
