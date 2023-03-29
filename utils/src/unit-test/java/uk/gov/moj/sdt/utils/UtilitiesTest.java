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

import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;


/**
 * Unit test for {@link TokenReplacer} class.
 *
 * @author d130680
 */
@ExtendWith(MockitoExtension.class)
class UtilitiesTest{

    private static final String XML_PATH = "src/unit-test/resources/xml/";
    private static final String DELETED_FILE_FOR_TEST = "doesNotExist.xml";

    private static final String DELETED_DIR_FOR_TEST = "src/unit-test/resources/xml/deleteMe/";

    private static final String EXCEPTION_HAS_BEEN_THROWN = "Exception has been thrown";

    private static final Logger LOGGER = LoggerFactory.getLogger(UtilitiesTest.class);
    /**
     * Test the tokenisation works.
     */
    @Test
    void testSingleTokenisation() {
        final List<String> l = new ArrayList<>();
        l.add("John Doe");

        final String s = Utilities.replaceTokens("User {0} was not found", l);

        assertEquals("User John Doe was not found", s);
    }

    /**
     * Test the tokenisation works with multiple tokens and out of sequence tokens.
     */
    @Test
    void testMultipleTokenisation() {
        final List<String> l = new ArrayList<>();
        l.add("fox");
        l.add("dog");
        l.add("over");
        final String s = Utilities.replaceTokens("The quick brown {0} jumped {2} the lazy brown {1}", l);

        assertEquals("The quick brown fox jumped over the lazy brown dog", s);
    }

    @Test
    void testCheckDirectoryExistsThrowException() {
        //given
        String theXmlFile = "testXMLValid.xml";
        //when
        try{
           Utilities.checkFileExists("Error_path", theXmlFile, false);
        }catch(IOException e){
            //then
            assertTrue(true, EXCEPTION_HAS_BEEN_THROWN);
        }
    }

    @Test
    void testCheckFileExistsThrowException() {
        //given
        String theXmlFile = DELETED_FILE_FOR_TEST;
        //when
        try{
            Utilities.checkFileExists(XML_PATH, theXmlFile,false);
        }catch(IOException e){
            //then
            assertTrue(true, EXCEPTION_HAS_BEEN_THROWN);
        }
    }


    @Test
    void testCheckFileExistsCreateIfMissingThrowException() {

        //given
        String theXmlFile = DELETED_FILE_FOR_TEST;
        try{
            //when
            Utilities.checkFileExists(XML_PATH, theXmlFile, true);
            deleteTestFile();

        }catch(IOException e) {
            //then
            assertTrue(true, EXCEPTION_HAS_BEEN_THROWN);
        }

    }

    @Test
    void testCheckFileExistsCreateIfMissingDirectoryThrowException()throws IOException{

        File myFile = null;
        String theXmlFile = DELETED_FILE_FOR_TEST;
        try{
            myFile = new File(Utilities.checkFileExists(DELETED_DIR_FOR_TEST, theXmlFile,
                                                   true));

        }catch(IOException e){
            assertTrue(true, EXCEPTION_HAS_BEEN_THROWN);
        }
        finally {
            if(myFile != null) {
                deleteTestFileDirectory();
            }
        }
    }


    //To Delete file creation when test has finished.
        private static void deleteTestFile(){

            String fileName = XML_PATH + DELETED_FILE_FOR_TEST;

            try {
                boolean result = Files.deleteIfExists(Paths.get(fileName));
                if (result) {
                    LOGGER.info("File is deleted!");
                } else {
                    LOGGER.info("Unable to delete the file.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private static void deleteTestFileDirectory() {
            Path directory = Paths.get(DELETED_DIR_FOR_TEST);
            try (Stream<Path> stream = Files.walk(directory)){
                    stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.info("Unable to delete the directory.");
            }
        }
}



