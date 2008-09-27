/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2008, DbUnit.org
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
package org.dbunit.util;

import java.io.File;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * Utility that provides some general methods for working with {@link File} objects.
 * 
 * @author gommma
 * @version $Revision$
 * @since 2.3.0
 */
public class FileHelper
{
	private static Logger logger = LoggerFactory.getLogger(FileHelper.class);

	private FileHelper(){
	}
	
	
	/**
	 * Recursively deletes the given directory
	 * @param directory The directory to delete
	 */
	public static void deleteDirectory(File directory)
	{
		if(!directory.isDirectory()) {
			logger.warn("The directory '" + directory + "' does not exist. Will return without delete.");
			return;
		}
		
		// First we must delete all files in the directory
		File[] containedFiles = directory.listFiles();
		for (int i = 0; i < containedFiles.length; i++) {
			File currentFile = containedFiles[i];
			if(currentFile.isDirectory()) {
				// First delete children recursively
				deleteDirectory(currentFile);
			}
			else {
				// Delete the file itself
				boolean success = currentFile.delete();
				if(!success){
					logger.warn("Failed to delete file '" + currentFile + "'");
				}
			}		
		}
		// Finally delete the directory itself
		boolean success = directory.delete();
		if(!success){
			logger.warn("Failed to delete file '" + directory + "'");
		}
	}

	public static InputSource createInputSource(File file) throws MalformedURLException
	{
        String uri = file/*.getAbsoluteFile()*/.toURI().toURL().toString();
        InputSource source = new InputSource(uri);
        return source;
	}
}
