package uk.gov.moj.sdt.handlers.api;

import uk.gov.moj.sdt.cmc.model.CMCUpdateRequest;

public interface ICMCFeedbackHandler {

    /**
     * Process the item update request from CMC.
     *
     * @param sdtRequestId The SDT request id of the item
     * @param cmcUpdateRequest The CMC update request
     */
    void cmcFeedback(String sdtRequestId, CMCUpdateRequest cmcUpdateRequest);
}
