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

package org.dbunit.ant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.ForwardOnlyResultSetTableFactory;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.IDataTypeFactory;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * <code>DbUnitTask</code> is the task definition for an Ant
 * interface to <code>DbUnit</code>.   DbUnit is a JUnit extension
 * which sets your database to a known state before executing your
 * tasks.
 *
 * @author Timothy Ruppert
 * @author Ben Cox
 * @version $Revision$
 * @since Jun 10, 2002
 * @see org.apache.tools.ant.Task
 */
public class DbUnitTask extends Task
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(DbUnitTask.class);

    /**
     * Database connection
     */
    private Connection conn = null;

    /**
     * DB driver.
     */
    private String driver = null;

    /**
     * DB url.
     */
    private String url = null;

    /**
     * User name.
     */
    private String userId = null;

    /**
     * Password
     */
    private String password = null;

    /**
     * DB schema.
     */
    private String schema = null;

    /**
     * Steps
     */
    private List steps = new ArrayList();

    private Path classpath;

    private AntClassLoader loader;

    /**
     * Flag for using the qualified table names.
     */
    private boolean useQualifiedTableNames = false;

    /**
     * Flag for using batched statements.
     */
    private boolean supportBatchStatement = false;

    /**
     * Flag for datatype warning.
     */
    private boolean datatypeWarning = true;

    private String escapePattern = null;

    private String dataTypeFactory = "org.dbunit.dataset.datatype.DefaultDataTypeFactory";

    /**
     * Set the JDBC driver to be used.
     */
    public void setDriver(String driver)
    {
        logger.debug("setDriver(driver=" + driver + ") - start");

        this.driver = driver;
    }

    /**
     * Set the DB connection url.
     */
    public void setUrl(String url)
    {
        logger.debug("setUrl(url=" + url + ") - start");

        this.url = url;
    }

    /**
     * Set the user name for the DB connection.
     */
    public void setUserid(String userId)
    {
        logger.debug("setUserid(userId=" + userId + ") - start");

        this.userId = userId;
    }

    /**
     * Set the password for the DB connection.
     */
    public void setPassword(String password)
    {
        logger.debug("setPassword(password=" + password + ") - start");

        this.password = password;
    }

    /**
     * Set the schema for the DB connection.
     */
    public void setSchema(String schema)
    {
        logger.debug("setSchema(schema=" + schema + ") - start");

        this.schema = schema;
    }

    /**
     * Set the flag for using the qualified table names.
     */
    public void setUseQualifiedTableNames(boolean useQualifiedTableNames)
    {
        logger.debug("setUseQualifiedTableNames(useQualifiedTableNames=" + useQualifiedTableNames + ") - start");

        this.useQualifiedTableNames = useQualifiedTableNames;
    }

    /**
     * Set the flag for supporting batch statements.
     * NOTE: This property cannot be used to force the usage of batch
     *       statement if your database does not support it.
     */
    public void setSupportBatchStatement(boolean supportBatchStatement)
    {
        logger.debug("setSupportBatchStatement(supportBatchStatement=" + supportBatchStatement + ") - start");

        this.supportBatchStatement = supportBatchStatement;
    }

    public void setDatatypeWarning(boolean datatypeWarning)
    {
        logger.debug("setDatatypeWarning(datatypeWarning=" + datatypeWarning + ") - start");

        this.datatypeWarning = datatypeWarning;
    }

    public void setDatatypeFactory(String datatypeFactory)
    {
        logger.debug("setDatatypeFactory(datatypeFactory=" + datatypeFactory + ") - start");

        this.dataTypeFactory = datatypeFactory;
    }

    public void setEscapePattern(String escapePattern)
    {
        logger.debug("setEscapePattern(escapePattern=" + escapePattern + ") - start");

        this.escapePattern = escapePattern;
    }

    /**
     * Set the classpath for loading the driver.
     */
    public void setClasspath(Path classpath)
    {
        logger.debug("setClasspath(classpath=" + classpath + ") - start");

        if (this.classpath == null)
        {
            this.classpath = classpath;
        }
        else
        {
            this.classpath.append(classpath);
        }
    }

    /**
     * Create the classpath for loading the driver.
     */
    public Path createClasspath()
    {
        logger.debug("createClasspath() - start");

        if (this.classpath == null)
        {
            this.classpath = new Path(project);
        }
        return this.classpath.createPath();
    }

    /**
     * Set the classpath for loading the driver using the classpath reference.
     */
    public void setClasspathRef(Reference r)
    {
        logger.debug("setClasspathRef(r=" + r + ") - start");

        createClasspath().setRefid(r);
    }

    /**
     * Gets the Steps.
     */
    public List getSteps()
    {
        logger.debug("getSteps() - start");

        return steps;
    }

    /**
     * Adds an Operation.
     */
    public void addOperation(Operation operation)
    {
        logger.debug("addOperation(operation) - start");

        steps.add(operation);
    }

    /**
     * Adds a Compare to the steps List.
     */
    public void addCompare(Compare compare)
    {
        logger.debug("addCompare(compare) - start");

        steps.add(compare);
    }

    /**
     * Adds an Export to the steps List.
     */
    public void addExport(Export export)
    {
        logger.debug("addExport(export) - start");

    	export.setParentTask(this);
        steps.add(export);
    }

    /**
     * Load the step and then execute it
     */
    public void execute() throws BuildException
    {
        logger.debug("execute() - start");

        try
        {
            IDatabaseConnection connection = createConnection();

            Iterator stepIter = steps.listIterator();
            while (stepIter.hasNext())
            {
                DbUnitTaskStep step = (DbUnitTaskStep)stepIter.next();
                log(step.getLogMessage(), Project.MSG_INFO);
                step.execute(connection);
            }
        }
        catch (DatabaseUnitException e)
        {
            logger.error("execute()", e);

            throw new BuildException(e, location);
        }
        catch (SQLException e)
        {
            logger.error("execute()", e);

            throw new BuildException(e, location);
        }
        finally
        {
            try
            {
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (SQLException e)
            {
                logger.error("execute()", e);
            }
        }
    }

    IDatabaseConnection createConnection() throws SQLException
    {
        logger.debug("createConnection() - start");

        if (driver == null)
        {
            throw new BuildException("Driver attribute must be set!", location);
        }
        if (userId == null)
        {
            throw new BuildException("User Id attribute must be set!", location);
        }
        if (password == null)
        {
            throw new BuildException("Password attribute must be set!", location);
        }
        if (url == null)
        {
            throw new BuildException("Url attribute must be set!", location);
        }
        if (steps.size() == 0)
        {
            throw new BuildException("Must declare at least one step in a <dbunit> task!");
        }

        // Instanciate JDBC driver
        Driver driverInstance = null;
        try
        {
            Class dc;
            if (classpath != null)
            {
                log("Loading " + driver + " using AntClassLoader with classpath " + classpath,
                        Project.MSG_VERBOSE);

                loader = new AntClassLoader(project, classpath);
                dc = loader.loadClass(driver);
            }
            else
            {
                log("Loading " + driver + " using system loader.", Project.MSG_VERBOSE);
                dc = Class.forName(driver);
            }
            driverInstance = (Driver)dc.newInstance();
        }
        catch (ClassNotFoundException e)
        {
            logger.error("createConnection()", e);

            throw new BuildException("Class Not Found: JDBC driver "
                    + driver + " could not be loaded", e, location);
        }
        catch (IllegalAccessException e)
        {
            logger.error("createConnection()", e);

            throw new BuildException("Illegal Access: JDBC driver "
                    + driver + " could not be loaded", e, location);
        }
        catch (InstantiationException e)
        {
            logger.error("createConnection()", e);

            throw new BuildException("Instantiation Exception: JDBC driver "
                    + driver + " could not be loaded", e, location);
        }

        log("connecting to " + url, Project.MSG_VERBOSE);
        Properties info = new Properties();
        info.put("user", userId);
        info.put("password", password);
        conn = driverInstance.connect(url, info);

        if (conn == null)
        {
            // Driver doesn't understand the URL
            throw new SQLException("No suitable Driver for " + url);
        }
        conn.setAutoCommit(true);

        IDatabaseConnection connection = new DatabaseConnection(conn, schema);
        DatabaseConfig config = connection.getConfig();
        config.setFeature(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, supportBatchStatement);
        config.setFeature(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, useQualifiedTableNames);
        config.setFeature(DatabaseConfig.FEATURE_DATATYPE_WARNING, datatypeWarning);
        config.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, escapePattern);
        config.setProperty(DatabaseConfig.PROPERTY_RESULTSET_TABLE_FACTORY,
                new ForwardOnlyResultSetTableFactory());

        // Setup data type factory
        try
        {
            IDataTypeFactory dataTypeFactory = (IDataTypeFactory)Class.forName(
                    this.dataTypeFactory).newInstance();
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
        }
        catch (ClassNotFoundException e)
        {
            logger.error("createConnection()", e);

            throw new BuildException("Class Not Found: DataType factory "
                    + driver + " could not be loaded", e, location);
        }
        catch (IllegalAccessException e)
        {
            logger.error("createConnection()", e);

            throw new BuildException("Illegal Access: DataType factory "
                    + driver + " could not be loaded", e, location);
        }
        catch (InstantiationException e)
        {
            logger.error("createConnection()", e);

            throw new BuildException("Instantiation Exception: DataType factory "
                    + driver + " could not be loaded", e, location);
        }

        return connection;
    }
}

