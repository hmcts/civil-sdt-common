/* Copyrights and Licenses
 *
 * Copyright (c) 2013 by the Ministry of Justice. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 * - All advertising materials mentioning features or use of this software must display the
 * following acknowledgment: "This product includes Money Claims OnLine."
 * - Products derived from this software may not be called "Money Claims OnLine" nor may
 * "Money Claims OnLine" appear in their names without prior written permission of the
 * Ministry of Justice.
 * - Redistributions of any form whatsoever must retain the following acknowledgment: "This
 * product includes Money Claims OnLine."
 * This software is provided "as is" and any expressed or implied warranties, including, but
 * not limited to, the implied warranties of merchantability and fitness for a particular purpose are
 * disclaimed. In no event shall the Ministry of Justice or its contributors be liable for any
 * direct, indirect, incidental, special, exemplary, or consequential damages (including, but
 * not limited to, procurement of substitute goods or services; loss of use, data, or profits;
 * or business interruption). However caused any on any theory of liability, whether in contract,
 * strict liability, or tort (including negligence or otherwise) arising in any way out of the use of this
 * software, even if advised of the possibility of such damage.
 *
 * $Id: ClaimXsdTest.java 16414 2013-05-29 10:56:45Z agarwals $
 * $LastChangedRevision: 16414 $
 * $LastChangedDate: 2013-05-29 11:56:45 +0100 (Wed, 29 May 2013) $
 * $LastChangedBy: holmessm $ */

package uk.gov.moj.sdt.domain.visitor;

import uk.gov.moj.sdt.domain.api.IBulkCustomer;
import uk.gov.moj.sdt.domain.api.IBulkFeedbackRequest;
import uk.gov.moj.sdt.domain.api.IBulkSubmission;
import uk.gov.moj.sdt.domain.api.IErrorLog;
import uk.gov.moj.sdt.domain.api.IIndividualRequest;
import uk.gov.moj.sdt.domain.api.IServiceType;
import uk.gov.moj.sdt.domain.api.ISubmitQueryRequest;
import uk.gov.moj.sdt.domain.api.ITargetApplication;
import uk.gov.moj.sdt.domain.visitor.api.IDomainObjectVisitor;
import uk.gov.moj.sdt.utils.visitor.AbstractVisitor;
import uk.gov.moj.sdt.utils.visitor.api.ITree;

/**
 * Implement default visit methods. These should never be called and therefore raise exceptions. In each case the
 * implementation of {@link IDomainObjectVisitor} should overload the one visit method it implements and the others
 * should not be called by that class.
 *
 * @author Robin Compston
 */
public abstract class AbstractDomainObjectVisitor extends AbstractVisitor implements IDomainObjectVisitor {
    private static final String METHOD_UNEXPECTED_MSG =
            "Missing visitor implementation: this method should never be called.";

    @Override
    public void visit(final IBulkCustomer bulkCustomer, final ITree tree) {
        throw new UnsupportedOperationException(METHOD_UNEXPECTED_MSG);
    }

    @Override
    public void visit(final IBulkSubmission bulkSubmission, final ITree tree) {
        throw new UnsupportedOperationException(METHOD_UNEXPECTED_MSG);
    }

    @Override
    public void visit(final IServiceType serviceType, final ITree tree) {
        throw new UnsupportedOperationException(METHOD_UNEXPECTED_MSG);
    }

    @Override
    public void visit(final ITargetApplication targetApplication, final ITree tree) {
        throw new UnsupportedOperationException(METHOD_UNEXPECTED_MSG);
    }

    @Override
    public void visit(final IIndividualRequest individualRequest, final ITree tree) {
        throw new UnsupportedOperationException(METHOD_UNEXPECTED_MSG);
    }

    @Override
    public void visit(final ISubmitQueryRequest submitQueryRequest, final ITree tree) {
        throw new UnsupportedOperationException(METHOD_UNEXPECTED_MSG);
    }

    @Override
    public void visit(final IBulkFeedbackRequest bulkFeedbackRequest, final ITree tree) {
        throw new UnsupportedOperationException(METHOD_UNEXPECTED_MSG);
    }

    @Override
    public void visit(final IErrorLog errorLog, final ITree tree) {
        throw new UnsupportedOperationException(METHOD_UNEXPECTED_MSG);
    }
}
