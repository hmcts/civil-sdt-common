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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * This class is a base class to verify the result of a testcase by comparing the output files.
 *
 * @author Robin Compston
 */
public abstract class AbstractSdtGoodFileTestBase extends AbstractSdtUnitTestBase {

    /**
     * File separator for pathnames.
     */
    protected static final String FILE_SEPARATOR = "/";

    /**
     * Static logging object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSdtGoodFileTestBase.class);

    /**
     * Flag to say whether comparison with good file is required.
     */
    protected static final boolean COMPARE_TO_GOOD = true;

    /**
     * Flag to say whether comparison with good file is not required.
     */
    protected static final boolean NO_COMPARE_TO_GOOD = false;

    /**
     * The first lines in 'out' and 'good' files that do not match.
     */
    protected static String badLine = "";

    /**
     * Compares the contents two named text files and returns true if they are exatcly the same. Note - Any data not
     * required to be compared, e.g. run dates and timing information etc. should be placed outside of the "test
     * section" ie. either before the message "TESTS STARTED" or after the message "TESTS COMPLETED" otherwise the files
     * will not match.
     *
     * @param outFilePath   Name of output file.
     * @param goodFilePath  Name of good file.
     * @param useDelimiters Whether to only do comparison within scope of start and complete markers.
     * @return true - files match, false - files do not match.
     * @noinspection ResultOfMethodCallIgnored
     */
    protected boolean compareTestOutputFiles(final String outFilePath, final String goodFilePath,
                                             final boolean useDelimiters) {
        boolean filesMatch = false;
        boolean foundDifference = false;
        BufferedReader outFileBuf;
        BufferedReader goodFileBuf;

        if (outFilePath != null && goodFilePath != null) {
            try {
                final File outFile = new File(outFilePath);
                final File goodFile = new File(goodFilePath);

                // Check we have an output file.
                if (!outFile.exists()) {
                    throw new IOException("** ERROR - Unable to find the output file from the script (" +
                            outFile.getAbsolutePath() + ")");
                }

                // Check we have a good file.
                if (!goodFile.exists()) {
                    throw new IOException("** ERROR - Unable to find the good file for the script (" +
                            goodFile.getAbsolutePath() + ")");
                }

                outFileBuf = new BufferedReader(new FileReader(outFile));
                goodFileBuf = new BufferedReader(new FileReader(goodFile));
                String outStr = null;
                String goodStr = null;
                final List<String> outList = new ArrayList<>(500);
                final List<String> goodList = new ArrayList<>(500);

                boolean outTestStarted = false;
                boolean goodTestStarted = false;

                // Are we expecting delimiters in the comparison files?
                if (useDelimiters) {
                    // Read to the start of the test section in both files
                    // Anything before this is not considered for comparison
                    outStr = outFileBuf.readLine();
                    while (outStr != null && outStr.indexOf(this.getTestStartString()) < 0) {
                        outStr = outFileBuf.readLine();
                    }

                    if (outStr != null && outStr.indexOf(this.getTestStartString()) >= 0) {
                        outTestStarted = true;
                    }

                    goodStr = goodFileBuf.readLine();
                    while (goodStr != null && goodStr.indexOf(this.getTestStartString()) < 0) {
                        goodStr = goodFileBuf.readLine();
                    }

                    if (goodStr != null && goodStr.indexOf(this.getTestStartString()) >= 0) {
                        goodTestStarted = true;
                    }
                } else {
                    outTestStarted = true;
                    goodTestStarted = true;
                }

                boolean completed1 = false;

                // If out file found a 'test started' message ...
                if (outTestStarted) {
                    // Read thru out files until EOF or 'test completed' message.
                    // outStr = outFileBuf.readLine();
                    if (useDelimiters) {
                        outStr = outStr.substring(outStr.indexOf(this.getTestStartString()));
                    } else {
                        outStr = outFileBuf.readLine();
                    }
                    while (!completed1 && outStr != null) {
                        if (this.includeLine(outStr)) {
                            outList.add(this.dos2Unix(outStr));
                        }
                        // Look for 'test completed' message inside out file.
                        if (useDelimiters && outStr.indexOf(this.getTestCompletedString()) >= 0) {
                            completed1 = true;
                        }
                        outStr = outFileBuf.readLine();
                    }
                }

                boolean completed2 = false;

                // If good file found a 'test started' message ...
                if (goodTestStarted) {
                    // Read thru good files until EOF or 'test completed' message.
                    // goodStr = goodFileBuf.readLine();
                    if (useDelimiters) {
                        goodStr = goodStr.substring(goodStr.indexOf(this.getTestStartString()));
                    } else {
                        goodStr = goodFileBuf.readLine();
                    }
                    while (!completed2 && goodStr != null) {
                        if (this.includeLine(goodStr)) {
                            goodList.add(this.dos2Unix(goodStr));
                        } else {
                            // remove the associated line from the outFile List
                            outList.remove(goodList.size());
                        }

                        // Look for 'test completed' message inside good file.
                        if (useDelimiters && goodStr.indexOf(this.getTestCompletedString()) >= 0) {
                            completed2 = true;
                        }
                        goodStr = goodFileBuf.readLine();
                    }
                }

                // Check both completion markers were found or that we are not using delimiters.
                final boolean testsCompleted = completed1 && completed2 || !useDelimiters;

                // Sort both list to cater for ordering differences in the data
                // returned from the db.
                final Iterator<String> outIter = outList.iterator();
                final Iterator<String> goodIter = goodList.iterator();

                badLine = "";
                int line = 0;

                // Check both lists match, stop processing if a difference is
                // encountered.
                while (outIter.hasNext() && goodIter.hasNext() && !foundDifference) {
                    line++;
                    outStr = outIter.next();
                    outStr = outStr.replace(" />", "/>").trim();
                    goodStr = goodIter.next();
                    goodStr = goodStr.replace(" />", "/>").trim();

                    // Compare each sorted record.
                    if (outStr.compareTo(goodStr) != 0) {
                        foundDifference = true;
                        AbstractSdtGoodFileTestBase.LOGGER
                                .error("Differences detected between latest out file and 'good' file");

                        // Output the line with the difference and the expected line to the console for review
                        badLine =
                                "line: " + line + ", out file:  " + outStr + "\nline: " + line + ", good file: " +
                                        goodStr;
                    }
                }

                outFileBuf.close();
                goodFileBuf.close();

                // Match if all lines within test zone compare.
                if (!foundDifference && testsCompleted) {
                    filesMatch = true;

                    // Remove the out file if files match.
                    outFile.delete();
                }
            } catch (final IOException e) {
                AbstractSdtGoodFileTestBase.LOGGER.error("Exception thrown : ", e);
                filesMatch = false;
            }
        }

        if (!filesMatch) {
            if (useDelimiters) {
                AbstractSdtGoodFileTestBase.LOGGER.debug(
                        "** Check the block of text between the ({}) and ({}) messages.",
                        this.getTestStartString(), this.getTestCompletedString());
                AbstractSdtGoodFileTestBase.LOGGER.debug("** All other text before and after this block is "
                        + "ignored for comparison purposes.");
            }

            fail("Comparison failure between out file [" + outFilePath + "] and good file [" + goodFilePath +
                    "].\nFirst mismatch: \n" + badLine);
        } else {
            AbstractSdtGoodFileTestBase.LOGGER.debug("Output file matches the reference comparison file - {}",
                    goodFilePath);
        }

        return filesMatch;
    }

    /**
     * Do test type specific checks to see if this line is a valid line in include in comparison with good file.
     *
     * @param line the line to be checked for inclusion.
     * @return true - include line, false - do not include line.
     */
    protected abstract boolean includeLine(String line);

    /**
     * Get the start message from the output file.
     *
     * @return Return the start string.
     */
    public abstract String getTestStartString();

    /**
     * Get the completed message from the output file.
     *
     * @return Return the completed string.
     */
    public abstract String getTestCompletedString();
}
