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

package uk.gov.moj.sdt.utils.logging.api;

import java.io.Serializable;

/**
 * The interface for the class that holds Logging related information for a thread.
 *
 * @author Robin Compston
 */
public interface ILoggingContext extends Serializable {
    /**
     * Serial version UID used by the Java serialization mechanism.
     */
    long SERIAL_VERSION_UID = 100L;

    /**
     * The getter method of property <code>loggingFlags</code>.
     *
     * @return <code>loggingFlags</code>.
     */
    int getLoggingFlags();

    /**
     * The setter method of property <code>loggingFlags</code>.
     *
     * @param loggingFlags property loggingFlags.
     */
    void setLoggingFlags(int loggingFlags);

    /**
     * The getter method for major logging id.
     *
     * @return major logging id.
     */
    long getMajorLoggingId();

    /**
     * The setter method of property <code>majorLoggingId</code>.
     *
     * @param majorLoggingId property majorLoggingId.
     */
    void setMajorLoggingId(long majorLoggingId);

    /**
     * The getter method for minor logging id.
     *
     * @return minor logging id.
     */
    long getMinorLoggingId();

    /**
     * The setter method of property <code>minorLoggingId</code>.
     *
     * @param minorLoggingId property minorLoggingId.
     */
    void setMinorLoggingId(long minorLoggingId);

    /**
     * The getter method for logging id.
     *
     * @return logging id.
     */
    String getLoggingId();
}
