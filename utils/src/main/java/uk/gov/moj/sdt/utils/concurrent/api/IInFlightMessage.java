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

package uk.gov.moj.sdt.utils.concurrent.api;

import java.util.Map;

/**
 * Interface for classes implementing {@link IInFlightMessage}.
 *
 * @author Robin Compston
 */
public interface IInFlightMessage {
    /**
     * Getter for competingThreads map.
     *
     * @return competingThreads map.
     */
    Map<Thread, Thread> getCompetingThreads();

    /**
     * Setter for competingThreads map.
     *
     * @param competingThreads new competingThreads map.
     */
    void setCompetingThreads(final Map<Thread, Thread> competingThreads);

    /**
     * Getter for sdtBulkReference.
     *
     * @return sdtBulkReference.
     */
    String getSdtBulkReference();

    /**
     * Setter for sdtBulkReference.
     *
     * @param sdtBulkReference new sdtBulkReference.
     */
    void setSdtBulkReference(final String sdtBulkReference);
}
