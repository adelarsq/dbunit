package org.dbunit.dataset.csv;

import org.dbunit.dataset.csv.handlers.PipelineException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created By:   fede
 * Date:         10-mar-2004 
 * Time:         15.50.13
 *
 * Last Checkin: $Author$
 * Date:         $Date$
 * Revision:     $Revision$
 */
public interface CSVParser {
    List parse(File file) throws IOException, CSVParserException;

    List parse(String csv) throws PipelineException, IllegalInputCharacterException;
}
