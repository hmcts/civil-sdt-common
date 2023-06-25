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
package uk.gov.moj.sdt.validators.validation;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.moj.sdt.utils.AbstractSdtUnitTestBase;
import uk.gov.moj.sdt.validators.DateValidator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for DateValidation.
 *
 * @author d301488
 */
@ExtendWith(MockitoExtension.class)
class DateValidatorTest extends AbstractSdtUnitTestBase {

    /**
     * Test: isDateWithinRange
     * <p>
     * Expected: TRUE as the date given is within the range.
     */
    @Test
    public void testIsDateWithinRangeTrue() {

        final LocalDate dateToTest = LocalDate.now();

        final LocalDate startDate = LocalDate.now().minusMonths(12);
        final LocalDate endDate = LocalDate.now().plusMonths(12);

        assertTrue(DateValidator.isDateWitinRange(dateToTest, startDate, endDate),
                "The date is no longer considered as within range.");
    }

    /**
     * Test: isDateWithinRange
     * <p>
     * Expected: TRUE - as the date given is on the same day as the end date.
     */
    @Test
    void testIsDateWithinRangeSameDate() {
        final LocalDate dateToTest = LocalDate.now();

        final LocalDate startDate = LocalDate.now().minusMonths(12);

        assertTrue(DateValidator.isDateWitinRange(dateToTest, startDate, dateToTest),
                "The date is no longer considered as within range.");
    }

    /**
     * Test: isDateWithinRange
     * <p>
     * Outside Range - date given is past end date.
     * <p>
     * Expected: FALSE as the date given is NOT within the range.
     */
    @Test
    void testIsDateWithinRangeFalseEnd() {
        final LocalDate dateToTest = LocalDate.now().plusMonths(12);

        final LocalDate startDate = LocalDate.now();
        final LocalDate endDate = LocalDate.now().minusMonths(12);

        assertFalse(DateValidator.isDateWitinRange(dateToTest, startDate, endDate),
                "The test date no longer is outside the end of the given range.");
    }

    /**
     * Test: isDateWithinRange
     * <p>
     * Outside Range - date given is before start date.
     * <p>
     * Expected: Method returns FALSE as the date given is NOT within the range.
     */
    @Test
    void testIsDateWithinRangeFalseStart() {
        final LocalDate dateToTest = LocalDate.now().minusMonths(12);

        final LocalDate startDate = LocalDate.now().plusMonths(12);
        final LocalDate endDate = LocalDate.now();

        assertFalse(DateValidator.isDateWitinRange(dateToTest, startDate, endDate),
                "The test date no longer is before the start of the given range.");
    }

    /**
     * Test: isDateBefore
     * <p>
     * Expected: TRUE as the date given is BEFORE the given date.
     */
    @Test
    void testIsDateBeforeTrue() {
        final LocalDate dateToTest = LocalDate.now();

        final LocalDate endDate = LocalDate.now().plusMonths(12);

        assertTrue(DateValidator.isDateBefore(dateToTest, endDate),
                "The test date is no longer considered to be after the given date.");
    }

    /**
     * Test: isDateBefore
     * <p>
     * Expected: FALSE as the date given is AFTER the end date.
     */
    @Test
    void testIsDateBeforeFalse() {
        final LocalDate dateToTest = LocalDate.now().plusMonths(12);

        final LocalDate endDate = LocalDate.now();

        assertFalse(DateValidator.isDateBefore(dateToTest, endDate),
                "The test date is now considered to be before or on the same day the given date.");
    }

    /**
     * Test: isDateAfter
     * <p>
     * Expected: TRUE as the date given is AFTER the given date.
     */
    @Test
    void testIsDateAfterTrue() {
        final LocalDate dateToTest = LocalDate.now().plusMonths(12);

        final LocalDate endDate = LocalDate.now();

        assertTrue(DateValidator.isDateAfter(dateToTest, endDate),
                "The test date is now considered to be before the given date.");
    }

    /**
     * Test: isDateAfter
     * <p>
     * Expected: FALSE as the date given is BEFORE the range.
     */
    @Test
    void testIsDateAfterFalse() {
        final LocalDate dateToTest = LocalDate.now();

        final LocalDate endDate = LocalDate.now().plusMonths(12);

        assertFalse(DateValidator.isDateAfter(dateToTest, endDate),
                "The test date is now considered to be after or on the same day the given date.");
    }

    /**
     * Test: isDateWithinXDays
     * <p>
     * Expected: TRUE the date given is within 10 days of today.
     */
    @Test
    void testIsDateWithinXDaysWITHIN() {
        final LocalDate dateToTest = LocalDate.now().minusDays(5);

        assertTrue(DateValidator.isDateWitinLastXDays(dateToTest, 10),
                "The date is now considered to be further away than the given number of days.");
    }

    /**
     * Test: isDateWithinXDays
     * <p>
     * Expected: TRUE the date given is within 10 days of today.
     */
    @Test
    void testIsDateWithinXDaysONBOUNDARY() {
        final LocalDate dateToTest = LocalDate.now().minusDays(10);

        assertTrue(DateValidator.isDateWitinLastXDays(dateToTest, 10),
                "The date is now considered to be further away than the given number of days.");
    }

    /**
     * Test: isDateWithinXDays
     * <p>
     * Expected: TRUE the date given is within 10 days of today.
     */
    @Test
    void testIsDateWithinXDaysOUTSIDE() {
        final LocalDate dateToTest = LocalDate.now().minusDays(15);

        assertFalse(DateValidator.isDateWitinLastXDays(dateToTest, 10),
                "The date is now considered to be within the given number of days.");
    }
}
