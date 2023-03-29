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

package uk.gov.moj.sdt.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A utilities class to hold common methods.
 *
 * @author d301488
 */

public final class Utilities {

    /**
     * File separator for pathnames.
     */
    protected static final String FILE_SEPARATOR = "/";

    /**
     * Static logging object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Utilities.class);

    /**
     * No-args constructor.
     */
    private Utilities() {

    }

    /**
     * Look for the given file on disk and report if not there.
     *
     * @param dirName         Pathname of directory in which to look for file.
     * @param fileName        Name of file to look for.
     * @param createIfMissing Flag to create directory if missing (but not the file).
     * @return String - full pathname of file.
     * @throws IOException If file not found.
     */
    public static String checkFileExists(final String dirName, final String fileName, final boolean createIfMissing)
            throws IOException {
        // Check the directory exists.
        final File targetDirectory = new File(dirName).getAbsoluteFile();
        if (!targetDirectory.exists()) {
            if (createIfMissing) {
                final boolean success = targetDirectory.mkdir();
                if (success) {
                    Utilities.LOGGER.debug("Directory: {} created.", dirName);
                } else {
                    Utilities.LOGGER.error("** ERROR - Unable to create the directory [{}]", dirName);
                    throw new IOException("** ERROR - Unable to create the directory ["
                            + targetDirectory.getAbsolutePath() + "]");
                }
            } else {
                Utilities.LOGGER.error("** ERROR - directory [{}] not found", dirName);
                throw new IOException("** ERROR - directory [" + targetDirectory.getAbsolutePath() + "] not found");
            }
        }

        // Check the file exists.
        final String pathname = dirName + Utilities.FILE_SEPARATOR + fileName;
        final File targetFile = new File(pathname).getAbsoluteFile();
        if (!targetFile.exists()) {
            if (createIfMissing) {
                final boolean success = targetFile.createNewFile();
                if (success) {
                    Utilities.LOGGER.debug("File: {} created.", pathname);
                } else {
                    Utilities.LOGGER.error("** ERROR - Unable to create the file [{}]", pathname);
                    throw new IOException("** ERROR - Unable to create the file [" + targetFile.getAbsolutePath()
                            + "]");
                }
            } else {
                Utilities.LOGGER.error("** ERROR - Unable to find the file [{}]", pathname);
                throw new IOException("** ERROR - Unable to find the file [" + targetFile.getAbsolutePath() + "]");
            }
        }

        return targetFile.getAbsolutePath();
    }

    /**
     * Look for the given file on disk and return it if it is found. Return an error if it is not.
     *
     * @param filePath the file to check.
     * @return File - returns the found file.
     * @throws IOException If file not found.
     */
    public static File retrieveFile(final String filePath) throws IOException {
        // Set the file path.
        final File targetFile = new File(filePath).getAbsoluteFile();
        // If the file exists.
        if (!targetFile.exists()) {
            Utilities.LOGGER.error("** ERROR - Unable to find the file [{}]", targetFile.getName());
            throw new IOException("** ERROR - Unable to find the file [" + targetFile.getName() + "]");
        }
        return targetFile;
    }

    /**
     * Replaces tokens with specific string in a piece of text.
     * <p>e.g. The quick {0} jumped {1} the lazy brown {2}</p>
     * <p>List would consist of:</p>
     * <p><br/>fox<br/>over<br/>dog</p>
     *
     * @param text         the text to tokenise
     * @param replacements list of string(s)
     * @return tokenised text
     */
    public static String replaceTokens(final String text, final List<String> replacements) {
        String textToReplace = text;
        for (int i = 0; i < replacements.size(); i++) {
            // Get the replacement text from the list
            final String replacement = replacements.get(i);
            if (replacement != null) {
                // Get the next token
                final String token = "{" + i + "}";
                textToReplace = textToReplace.replace(token, replacement);
            }
        }
        return textToReplace;
    }

    /**
     * Converts Joda {@link LocalDateTime} into {@link Calendar}.
     *
     * @param localDateTime local date time instance.
     * @return Calendar
     */
    public static Calendar convertLocalDateTimeToCalendar (final LocalDateTime localDateTime)
    {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);
        final Calendar calendar = Calendar.getInstance ();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Formats a LocalDateTime into a string.
     *
     * @param localDateTime local date time object
     * @return formatted date
     */
    public static String formatDateTimeForMessage (final LocalDateTime localDateTime)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
        return  localDateTime.format(formatter);
    }

    /**
     * Obtain xml contents from given file. Removes line feeds from content so that regular expressions work as
     * expected.
     *
     * @param dirName directory for the file.
     * @param fileName name of the file.
     * @return raw xml read from the file.
     * @throws IOException in case of errors during read operation
     */
    public static String getRawXml (final String dirName, final String fileName) throws IOException
    {
        String message = "";

        final File myFile = new File (Utilities.checkFileExists (dirName, fileName, false));

        message = FileUtils.readFileToString (myFile);

        // Remove linefeeds as they stop the regular expression working.
        message = message.replace ('\n', ' ');
        message = message.replace ('\r', ' ');
        return message;

    }

}
