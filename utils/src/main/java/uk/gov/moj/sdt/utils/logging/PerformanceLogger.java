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

package uk.gov.moj.sdt.utils.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.gov.moj.sdt.utils.SdtContext;
import uk.gov.moj.sdt.utils.logging.api.ILoggingContext;

/**
 * Performance Logger.
 *
 * @author Robin Compston
 */
public final class PerformanceLogger {
    // CHECKSTYLE:OFF
    /**
     * Inbound interceptor.
     */
    public static final Long LOGGING_POINT_1 = 1L << 0;

    /**
     * Indicates the receipt of new web service message.
     */
    public static final Long LOGGING_POINT_2 = 1L << 1;

    /**
     * Call to database.
     */
    public static final Long LOGGING_POINT_3 = 1L << 2;

    /**
     * Response from database.
     */
    public static final Long LOGGING_POINT_4 = 1L << 3;

    /**
     * Enqueue individual message.
     */
    public static final Long LOGGING_POINT_5 = 1L << 4;

    /**
     * Dequeue individual message.
     */
    public static final Long LOGGING_POINT_6 = 1L << 5;

    /**
     * Forward request to target application.
     */
    public static final Long LOGGING_POINT_7 = 1L << 6;

    /**
     * Response from target application.
     */
    public static final Long LOGGING_POINT_8 = 1L << 7;

    /**
     * Server response to web service.
     */
    public static final Long LOGGING_POINT_9 = 1L << 8;

    /**
     * Outbound interceptor.
     */
    public static final Long LOGGING_POINT_10 = 1L << 9;

    /**
     * Fault interceptor.
     */
    public static final Long LOGGING_POINT_11 = 1L << 10;

    /**
     * Start tree walker.
     */
    public static final Long LOGGING_POINT_12 = 1L << 11;

    /**
     * End tree walker.
     */
    public static final Long LOGGING_POINT_13 = 1L << 12;

    /**
     * Turn on detail for all other logging points.
     */
    public static final Long LOGGING_POINT_16 = 1L << 15;

    // CHECKSTYLE:ON

    /**
     * Logging Flag value when all flags are enabled.
     */
    public static final long ALL_FLAGS_ENABLED = Long.MAX_VALUE;

    /**
     * Number of bit we want to see in log (least significant).
     */
    public static final int FLAG_BIT_LENGTH = 16;

    /**
     * Logging object. Note its name must not be in the same hierarchy as other SDT loggers so that it can be turned off
     * and on independently. Note also, this is a log4j logger not an SLF4J logger.
     */
    private static final Logger LOGGER = LogManager.getLogger("sdt.performance");

    /**
     * Length of a formatted line.
     */
    private static final int LINE_LENGTH = 16;

    /**
     * Length of a formatted line.
     */
    private static final int LABEL_LENGTH = 6;

    /**
     * Length of block of ascii.
     */
    private static final int ASCII_BLOCK = 4;

    /**
     * Length of block of ascii.
     */
    private static final int ASCII_WIDTH = 56;

    /**
     * Default Constructor.
     */
    private PerformanceLogger() {
    }

    /**
     * Log performance message on behalf of server or (via LoggingService) client.
     *
     * @param caller       the method calling of the logging service.
     * @param loggingPoint the performance logging point for which this message is being logged.
     * @param message      the message to be logged.
     * @param detail       the detail to be logged.
     */
    public static void log(final Class<?> caller, final long loggingPoint, final String message, final String detail) {
        final ILoggingContext loggingContext = SdtContext.getContext().getLoggingContext();

        // Check if this logger is active.
        if ((loggingContext.getLoggingFlags() & loggingPoint) > 0L) {
            // Convert loggingPoint to bit String for easy viewing.
            final String binaryLoggingPoint =
                    PerformanceLogger.pad(Long.toBinaryString(loggingPoint), true, "0",
                            PerformanceLogger.FLAG_BIT_LENGTH);

            // Convert loggingFlags to bit String for easy viewing.
            final String binaryLoggingFlags =
                    PerformanceLogger.pad(Long.toBinaryString(loggingContext.getLoggingFlags()), true, "0",
                            PerformanceLogger.FLAG_BIT_LENGTH);

            final StringBuilder logMessage = new StringBuilder(message);

            // Add detail if switched on - note this applies to all other active logging points.
            if ((loggingContext.getLoggingFlags() & PerformanceLogger.LOGGING_POINT_16) > 0L) {
                logMessage.append(detail);
            }

            // Write message to 'performance.log'.
            LOGGER.log(Level.getLevel("perf"),
                    "active flags={}, logging point={}, reference={}: {}", binaryLoggingFlags,
                    binaryLoggingPoint, loggingContext.getLoggingId(), logMessage);
        }
    }

    /**
     * Check if performance logging is enabled for this logging point.
     *
     * @param loggingPoint the logging point to be checked.
     * @return true - performance logging enabled for this point; false - performance logging not enabled for this.
     */
    public static boolean isPerformanceEnabled(final long loggingPoint) {
        final ILoggingContext loggingContext = SdtContext.getContext().getLoggingContext();

        // Check if performance logging is active for this logging point.
        return ((loggingContext.getLoggingFlags() & loggingPoint) > 0L);
    }

    /**
     * Render long string in more readable form.
     *
     * @param input the string to be rendered.
     * @return the rendered string.
     */
    public static String format(final String input) {
        // Create buffer to work with.
        final byte[] buffer = input.getBytes();

        // Line label for current line.
        int lineNum = 0;

        // To hold formatted output.
        final StringBuilder output = new StringBuilder();

        // Keep track of overall position.
        int position = 0;

        // Where to stop.
        final int end = buffer.length;

        // To handle a single line - initialise to force a new line.
        short linePosition = 0;

        // Hold ascii portion of a line.
        StringBuilder ascii = new StringBuilder();

        // Hold text portion of a line.
        StringBuilder text = new StringBuilder();

        // Continue formatting till none left.
        while (position < end) {
            byte current = buffer[position];

            // Insert readability delimiter.
            if ((linePosition % ASCII_BLOCK) == 0) {
                ascii.append("  ");
            }

            // Store ascii and text separately.
            ascii.append(pad(Integer.toHexString(current).toUpperCase(), true, "0", 2)).append(" ");
            if (current == '\n' || current == '\r' || current == '\t') {
                // Convert linefeeds, carriage returns and tabs to space to avoid their effect on display.
                current = ' ';
            }
            text.append((char) current);

            // Update counters.
            position++;
            linePosition++;

            // At end of line append the text gathered so far.
            if ((linePosition >= LINE_LENGTH) || position >= end) {
                if (ascii.length() < ASCII_WIDTH) {
                    ascii = new StringBuilder(pad(ascii.toString(), false, " ", ASCII_WIDTH));
                }

                linePosition = 0;
                output.append("\n\t");
                output.append(pad(Integer.toHexString(lineNum * LINE_LENGTH), true, " ", LABEL_LENGTH));
                output.append(":  ");
                output.append(ascii);
                output.append("    ");
                output.append(text);
                lineNum++;
                ascii = new StringBuilder();
                text = new StringBuilder();
            }
        }

        return output.toString();
    }

    /**
     * Left pad a String to a fixed length. Reduce length if too long.
     *
     * @param in             the input String.
     * @param left           pad left or lad right.
     * @param padChar        the character with which to left pad the String.
     * @param requiredLength the required length of the output String
     * @return the padded input String.
     */
    public static String pad(final String in, final boolean left, final String padChar, final int requiredLength) {
        String out = in;

        // Reduce length if too long.
        int len = in.length();
        if (len >= requiredLength) {
            out = in.substring(len - requiredLength);
            len = out.length();
        }

        // Left pad with zeros if too short.
        for (int i = 0; i < (requiredLength - len); i++) {
            if (left) {
                out = padChar + out;
            } else {
                out = out + padChar;
            }
        }
        return out;
    }
}
