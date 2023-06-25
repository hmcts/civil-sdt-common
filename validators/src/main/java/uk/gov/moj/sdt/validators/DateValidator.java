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
package uk.gov.moj.sdt.validators;


import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Implementation of DateValidation.
 *
 * @author Simon Holmes
 */
@Component("DateValidator")
public final class DateValidator {

    /**
     * No-argument Constructor.
     */
    private DateValidator() {
    }

    /**
     * Tests if a given date is within two dates, inclusively.
     *
     * @param dateToTest the date that is to be tested.
     * @param startDate  the start date of the range.
     * @param endDate    the end date of the range.
     * @return TRUE if within date, FALSE if not.
     */
    public static boolean isDateWitinRange(final LocalDate dateToTest, final LocalDate startDate,
                                           final LocalDate endDate) {
        // If the dateToTest is after the startDate and before the endDate, or on the same date as either, return true.
        return (dateToTest.isAfter(startDate) && dateToTest.isBefore(endDate)) || dateToTest.isEqual(startDate) ||
                dateToTest.isEqual(endDate);
    }

    /**
     * Tests if a date is BEFORE a given date, inclusively.
     *
     * @param dateToTest the date that is to be tested.
     * @param endDate    the end date of the test.
     * @return TRUE if before the given date, FALSE if not.
     */
    public static boolean isDateBefore(final LocalDate dateToTest, final LocalDate endDate) {
        // If the dateToTest is before or on the same date to the start date, return true.
        return dateToTest.isBefore(endDate) || dateToTest.isEqual(endDate);
    }

    /**
     * Tests if a date is AFTER a given date, inclusively.
     *
     * @param dateToTest the date that is to be tested.
     * @param startDate  the start date of the test.
     * @return TRUE if after the given date, FALSE if not.
     */
    public static boolean isDateAfter(final LocalDate dateToTest, final LocalDate startDate) {
        // If the dateToTest is after or on the same date, return true.
        return dateToTest.isAfter(startDate) || dateToTest.isEqual(startDate);
    }

    /**
     * Tests if a date is within a given number of days, inclusive of the Xth day.
     *
     * @param dateToTest   the date that is to be tested
     * @param numberOfDays the number of days the date should be within.
     * @return TRUE if the date is within the last X days, FALSE if it is not.
     */
    public static boolean isDateWitinLastXDays(final LocalDate dateToTest, final Integer numberOfDays) {
        // Take todays date and remove numberOfDays from it.
        final LocalDate adjustedDate = LocalDate.now().minusDays(numberOfDays);

        // If the adjusted date is before or equal to the dateToTest, it must be within X number of days.
        return adjustedDate.isBefore(dateToTest) || adjustedDate.isEqual(dateToTest);
    }

}
