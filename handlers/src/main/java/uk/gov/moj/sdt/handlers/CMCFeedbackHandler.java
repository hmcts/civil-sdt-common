package uk.gov.moj.sdt.handlers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.moj.sdt.cmc.model.CMCUpdateRequest;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.handlers.api.ICMCFeedbackHandler;
import uk.gov.moj.sdt.services.api.ICMCFeedbackService;
import uk.gov.moj.sdt.transformers.api.ICMCFeedbackTransformer;

@Transactional(propagation = Propagation.REQUIRED)
@Component("cmcFeedbackHandler")
@Getter
@Setter
@Slf4j
public class CMCFeedbackHandler implements ICMCFeedbackHandler {

    private ICMCFeedbackTransformer cmcFeedbackTransformer;

    private ICMCFeedbackService cmcFeedbackService;

    @Autowired
    public CMCFeedbackHandler(@Qualifier("cmcFeedbackTransformer") ICMCFeedbackTransformer cmcFeedbackTransformer,
                              @Qualifier("cmcFeedbackService") ICMCFeedbackService cmcFeedbackService) {
        this.cmcFeedbackTransformer = cmcFeedbackTransformer;
        this.cmcFeedbackService = cmcFeedbackService;
    }

    @Override
    public void cmcFeedback(String sdtRequestId, CMCUpdateRequest cmcUpdateRequest) {
        log.debug("CMCUpdateRequest started for sdtRequestId [{}]", sdtRequestId);

        IIndividualRequest individualRequest =
                cmcFeedbackTransformer.transformJsonToDomain(sdtRequestId, cmcUpdateRequest);

        cmcFeedbackService.cmcFeedback(individualRequest);

        log.info("CMCUpdateRequest completed for sdtRequestId [{}]", sdtRequestId);
    }
}
