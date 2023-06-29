/* Copyrights and Licenses
 *
 * Copyright (c) 2012-2013 by the Ministry of Justice. All rights reserved.
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
 * $Id: $
 * $LastChangedRevision: $
 * $LastChangedDate: $
 * $LastChangedBy: $ */
package uk.gov.moj.sdt.validators.exception;

import java.util.List;

/**
 * The supplied SDT Bulk Reference is not listed against the Bulk Customer's Bulk Submissions detail.
 *
 * @author d130680
 */
public class InvalidBulkReferenceException extends AbstractBusinessException {
    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The supplied SDT Bulk Reference is not listed against the Bulk Customer's Bulk Submissions detail.
     *
     * @param code        error code
     * @param description error description
     */
    public InvalidBulkReferenceException(final String code, final String description) {
        super(code, description);
    }

    /**
     * The supplied SDT Bulk Reference is not listed against the Bulk Customer's Bulk Submissions detail.
     *
     * @param code         code
     * @param description  description
     * @param replacements string replacements with tokens
     */
    public InvalidBulkReferenceException(final String code, final String description, final List<String> replacements) {
        super(code, description, replacements);
    }

    /**
     * The supplied SDT Bulk Reference is not listed against the Bulk Customer's Bulk Submissions detail.
     *
     * @param s the s
     */
    public InvalidBulkReferenceException(final String s) {
        super(s);
    }

    /**
     * The supplied SDT Bulk Reference is not listed against the Bulk Customer's Bulk Submissions detail.
     *
     * @param cause the cause
     */
    public InvalidBulkReferenceException(final Throwable cause) {
        super(cause);
    }

    /**
     * The supplied SDT Bulk Reference is not listed against the Bulk Customer's Bulk Submissions detail.
     *
     * @param s     the s
     * @param cause the cause
     */
    public InvalidBulkReferenceException(final String s, final Throwable cause) {
        super(s, cause);
    }
}
