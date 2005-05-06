package org.dbunit.dataset.csv;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.dbunit.dataset.csv.handlers.PipelineException;

/**
 * Created By:   fede
 * Date:         10-mar-2004 
 * Time:         15.50.13
 *
 * Last Checkin: $Author$
 * Date:         $Date$
 * Revision:     $Revision$
 */
public interface CsvParser {
    List parse(File file) throws IOException, CsvParserException;
    List parse(URL url) throws IOException, CsvParserException;
    List parse(String csv) throws PipelineException, IllegalInputCharacterException;
}
