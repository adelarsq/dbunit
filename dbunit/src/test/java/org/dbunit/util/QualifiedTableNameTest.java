/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2004-2008, DbUnit.org
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

import org.dbunit.database.DatabaseConfig;

import junit.framework.TestCase;

/**
 * @author gommma
 * @author Last changed by: $Author$
 * @version $Revision$ $Date$
 * @since 2.3.0
 */
public class QualifiedTableNameTest extends TestCase 
{

	public void testQualifiedTableNamePresent_PrecedesDefaultSchemaName()
	{
		QualifiedTableName qualifiedTableName = new QualifiedTableName("MYSCHEMA.MYTABLE", "DEFAULT_SCHEMA");
		assertEquals("MYSCHEMA", qualifiedTableName.getSchema());
		assertEquals("MYTABLE", qualifiedTableName.getTable());
		assertEquals("MYSCHEMA.MYTABLE", qualifiedTableName.getQualifiedName());
	}

	public void testQualifiedTableNameNotPresentUsingDefaultSchema()
	{
		QualifiedTableName qualifiedTableName = new QualifiedTableName("MYTABLE", "DEFAULT_SCHEMA");
		assertEquals("DEFAULT_SCHEMA", qualifiedTableName.getSchema());
		assertEquals("MYTABLE", qualifiedTableName.getTable());
		assertEquals("DEFAULT_SCHEMA.MYTABLE", qualifiedTableName.getQualifiedName());
	}

	public void testQualifiedTableNameNotPresentAndNoDefaultSchema()
	{
		QualifiedTableName qualifiedTableName = new QualifiedTableName("MYTABLE", null);
		assertEquals(null, qualifiedTableName.getSchema());
		assertEquals("MYTABLE", qualifiedTableName.getTable());
		assertEquals("MYTABLE", qualifiedTableName.getQualifiedName());
	}

	public void testQualifiedTableNameNotPresentAndEmptyDefaultSchema()
	{
		QualifiedTableName qualifiedTableName = new QualifiedTableName("MYTABLE", "");
		assertEquals("", qualifiedTableName.getSchema());
		assertEquals("MYTABLE", qualifiedTableName.getTable());
		assertEquals("MYTABLE", qualifiedTableName.getQualifiedName());
	}

	public void testGetQualifiedTableName()
	{
		String qualifiedName = new QualifiedTableName("MY_SCHEMA.MY_TABLE", null, "'?'").getQualifiedName();
		assertEquals("'MY_SCHEMA'.'MY_TABLE'", qualifiedName);
	}
	
	public void testGetQualifiedTableName_DefaultSchema()
	{
		String qualifiedName = new QualifiedTableName("MY_TABLE", "DEFAULT_SCHEMA", "'?'").getQualifiedName();
		assertEquals("'DEFAULT_SCHEMA'.'MY_TABLE'", qualifiedName);
	}
	
	public void testGetQualifiedTableName_DefaultSchema_FeatureEnabled()
	{
		DatabaseConfig config = new DatabaseConfig();
		config.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
		String qualifiedName = new QualifiedTableName("MY_TABLE", "DEFAULT_SCHEMA", null).getQualifiedNameIfEnabled(config);
		assertEquals("DEFAULT_SCHEMA.MY_TABLE", qualifiedName);
	}

	public void testGetQualifiedTableName_DefaultSchema_FeatureDisabled()
	{
		DatabaseConfig config = new DatabaseConfig();
		config.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, false);
		String qualifiedName = new QualifiedTableName("MY_TABLE", "DEFAULT_SCHEMA", null).getQualifiedNameIfEnabled(config);
		assertEquals("MY_TABLE", qualifiedName);
	}

	public void testGetQualifiedTableName_DefaultSchema_FeatureEnabled_Escaping()
	{
		DatabaseConfig config = new DatabaseConfig();
		config.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
		String qualifiedName = new QualifiedTableName("MY_TABLE", "DEFAULT_SCHEMA", "'?'").getQualifiedNameIfEnabled(config);
		assertEquals("'DEFAULT_SCHEMA'.'MY_TABLE'", qualifiedName);
	}

	public void testGetQualifiedTableName_DefaultSchema_FeatureDisabled_Escaping()
	{
		DatabaseConfig config = new DatabaseConfig();
		config.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, false);
		String qualifiedName = new QualifiedTableName("MY_TABLE", "DEFAULT_SCHEMA", "'?'").getQualifiedNameIfEnabled(config);
		assertEquals("'MY_TABLE'", qualifiedName);
	}
	
	public void testGetQualifiedTableName_DefaultSchema_FeatureEnabled_EscapingWithoutQuestionmark()
    {
        DatabaseConfig config = new DatabaseConfig();
        config.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
        String qualifiedName = new QualifiedTableName("MY_TABLE", "DEFAULT_SCHEMA", "'").getQualifiedNameIfEnabled(config);
        assertEquals("'DEFAULT_SCHEMA'.'MY_TABLE'", qualifiedName);
    }

	public void testConstructorWithNullTable()
	{
		try {
			new QualifiedTableName(null, "SCHEMA");
			fail("Should not be able to create object with null table");
		}
		catch(NullPointerException expected){
			assertEquals("The parameter 'tableName' must not be null", expected.getMessage());
		}
	}

}
