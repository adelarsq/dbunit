/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.dbunit.dataset.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dbunit.dataset.csv.handlers.*;

import java.io.*;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

public class CsvParserImpl implements CsvParser {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CsvParserImpl.class);

    private Pipeline pipeline;

    public CsvParserImpl() {
        resetThePipeline();
    }

    private void resetThePipeline() {
        logger.debug("resetThePipeline() - start");

        pipeline = new Pipeline();
        getPipeline().putFront(SeparatorHandler.ENDPIECE());
        getPipeline().putFront(EscapeHandler.ACCEPT());
        getPipeline().putFront(IsAlnumHandler.QUOTE());
        getPipeline().putFront(QuoteHandler.QUOTE());
        getPipeline().putFront(EscapeHandler.ESCAPE());
        getPipeline().putFront(WhitespacesHandler.IGNORE());
        getPipeline().putFront(TransparentHandler.IGNORE());
    }

    public List parse(String csv) throws PipelineException, IllegalInputCharacterException {
        logger.debug("parse(csv=" + csv + ") - start");

        getPipeline().resetProducts();
        CharacterIterator iterator = new StringCharacterIterator(csv);
        for (char c = iterator.first(); c != CharacterIterator.DONE; c = iterator.next()) {
            getPipeline().handle(c);
        }
        getPipeline().noMoreInput();
        getPipeline().thePieceIsDone();
        return getPipeline().getProducts();
    }

    public List parse(File file) throws IOException, CsvParserException {
        logger.debug("parse(file=" + file + ") - start");

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        return parse(reader, file.getAbsolutePath().toString());
    }
    
    public List parse(URL url) throws IOException, CsvParserException {
        logger.debug("parse(url=" + url + ") - start");

        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        return parse(reader, url.toString());
    }
    
    public List parse(Reader reader, String source) throws IOException, CsvParserException {
        logger.debug("parse(reader=" + reader + ", source=" + source + ") - start");

        LineNumberReader lineNumberReader = new LineNumberReader(reader);
        List rows = new ArrayList();
        List columnsInFirstLine = parseFirstLine(lineNumberReader, source, rows);
        parseTheData(columnsInFirstLine, lineNumberReader, rows);
        return rows;
    }

    private List parseFirstLine(LineNumberReader lineNumberReader, File file, List rows) throws IOException, CsvParserException {
        logger.debug("parseFirstLine(lineNumberReader=" + lineNumberReader + ", file=" + file + ", rows=" + rows
                + ") - start");

    	return parseFirstLine(lineNumberReader, file.getAbsolutePath().toString(), rows);
    }

    /** parse the first line of data from the given source */
    private List parseFirstLine(LineNumberReader lineNumberReader, String source, List rows) throws IOException, CsvParserException {
        logger.debug("parseFirstLine(lineNumberReader=" + lineNumberReader + ", source=" + source + ", rows=" + rows
                + ") - start");

        String firstLine = lineNumberReader.readLine();
        if (firstLine == null)
            throw new CsvParserException("The first line of " + source + " is null");

        final List columnsInFirstLine = parse(firstLine);
        rows.add(columnsInFirstLine);
        return columnsInFirstLine;
    }

    private void parseTheData(final List columnsInFirstLine, LineNumberReader lineNumberReader, List rows) throws IOException, CsvParserException {
        logger.debug("parseTheData(columnsInFirstLine=" + columnsInFirstLine + ", lineNumberReader=" + lineNumberReader
                + ", rows=" + rows + ") - start");

        int nColumns = columnsInFirstLine.size();
        List columns;
        while ((columns = collectExpectedNumberOfColumns(nColumns, lineNumberReader)) != null) {
            rows.add(columns);
        }
    }

    private List collectExpectedNumberOfColumns(int expectedNumberOfColumns, LineNumberReader lineNumberReader) throws IOException, CsvParserException  {
        logger.debug("collectExpectedNumberOfColumns(expectedNumberOfColumns=" + expectedNumberOfColumns
                + ", lineNumberReader=" + lineNumberReader + ") - start");

        List columns = null;
        int columnsCollectedSoFar = 0;
        StringBuffer buffer = new StringBuffer();
        String anotherLine = lineNumberReader.readLine();
        if(anotherLine == null)
            return null;
        boolean shouldProceed = false;
        while (columnsCollectedSoFar < expectedNumberOfColumns) {
            try {
                buffer.append(anotherLine);
                columns = parse(buffer.toString());
                columnsCollectedSoFar = columns.size();
            } catch (IllegalStateException e) {
                logger.error("collectExpectedNumberOfColumns()", e);

                resetThePipeline();
                anotherLine = lineNumberReader.readLine();
                if(anotherLine == null)
                    break;
                buffer.append("\n");
                shouldProceed = true;
            }
            if (!shouldProceed)
                break;
        }
        if (columnsCollectedSoFar != expectedNumberOfColumns) {
            String message = new StringBuffer("Expected ").append(expectedNumberOfColumns)
                    .append(" columns on line ").append(lineNumberReader.getLineNumber())
                    .append(", got ").append(columnsCollectedSoFar).append(". Offending line: ").append(buffer).toString();
            throw new CsvParserException(message);
        }
        return columns;
    }

    Pipeline getPipeline() {
        logger.debug("getPipeline() - start");

        return pipeline;
    }

    void setPipeline(Pipeline pipeline) {
        logger.debug("setPipeline(pipeline=" + pipeline + ") - start");

        this.pipeline = pipeline;
    }
}
