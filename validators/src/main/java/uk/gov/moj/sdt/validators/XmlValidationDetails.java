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
 * $Id: $
 * $LastChangedRevision: $
 * $LastChangedDate: $
 * $LastChangedBy: $ */
package uk.gov.moj.sdt.validators;

import java.util.List;

/**
 * A class to hold the details of a validation result.
 *
 * @author Simon Holmes
 */
public class XmlValidationDetails {

    /**
     * Has the xml been validated?
     *
     * @author d301488
     */
    public enum Result {
        /**
         * The xml passed validation.
         */
        PASS,

        /**
         * The xml failed validation.
         */
        FAIL
    }

    ;

    /**
     * The result of validation.
     */
    private Result result;

    /**
     * If validation failed, error messages will be output.
     */
    private List<String> resultMessages;

    /**
     * No-argument constructor.
     */
    public XmlValidationDetails() {
    }

    /**
     * Instantiate a XmlValidationDetails object.
     *
     * @param result the enum of Success/Fail
     */
    public XmlValidationDetails(final Result result) {
        this.result = result;
    }

    /**
     * Instantiate a XmlValidationDetails object.
     *
     * @param result         the enum of Success/Fail
     * @param resultMessages If an error has occured, error messages will be passed in via a List.
     */
    public XmlValidationDetails(final Result result, final List<String> resultMessages) {
        this.result = result;
        this.resultMessages = resultMessages;
    }

    /**
     * @return the result
     */
    public Result getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(final Result result) {
        this.result = result;
    }

    /**
     * @return the resultMessages
     */
    public List<String> getResultMessages() {
        return resultMessages;
    }

    /**
     * @param resultMessages the resultMessages to set
     */
    public void setResultMessages(final List<String> resultMessages) {
        this.resultMessages = resultMessages;
    }

}
