/*
 * DbUnitTask.java    Mar 24, 2002
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Timothy Ruppert && Ben Cox
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

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * <code>DbUnitTask</code> is the task definition for an Ant
 * interface to <code>DbUnit</code>.   DbUnit is a JUnit extension
 * which sets your database to a known state before executing your
 * tasks.
 *
 * @author Timothy Ruppert && Ben Cox
 * @version $Revision$
 * @see org.apache.tools.ant.Task
 */
public class DbUnitTask extends Task
{

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
     * Flag for using botched statements.
     */
    private boolean supportBatchStatement = true;

    /**
     * Set the JDBC driver to be used.
     */
    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    /**
     * Set the DB connection url.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * Set the user name for the DB connection.
     */
    public void setUserid(String userId)
    {
        this.userId = userId;
    }

    /**
     * Set the password for the DB connection.
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Set the schema for the DB connection.
     */
    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    /**
     * Set the flag for using the qualified table names.
     */
    public void setUseQualifiedTableNames(boolean useQualifiedTableNames)
    {
        this.useQualifiedTableNames = useQualifiedTableNames;
    }

    /**
     * Set the flag for supporting batch statements.
     * NOTE: This property cannot be used to force the usage of batch
     *       statement if your database does not support it.
     */
    public void setSupportBatchStatement(boolean supportBatchStatement)
    {
        this.supportBatchStatement = supportBatchStatement;
    }

    /**
     * Set the classpath for loading the driver.
     */
    public void setClasspath(Path classpath)
    {
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
        createClasspath().setRefid(r);
    }

    /**
     * Gets the Steps.
     */
    public List getSteps()
    {
        return steps;
    }

    /**
     * Adds an Operation.
     */
    public void addOperation(Operation operation)
    {
        steps.add(operation);
    }

    /**
     * Adds a Composite to the steps List.
     */
    public void addComposite(Composite composite)
    {
        steps.add(composite);
    }

    /**
     * Adds an Export to the steps List.
     */
    public void addExport(Export export)
    {
        steps.add(export);
    }

    /**
     * Load the step and then execute it
     */
    public void execute() throws BuildException
    {
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

        if (useQualifiedTableNames)
        {
            System.setProperty("dbunit.qualified.table.names", "true");
        }
        if (!supportBatchStatement)
        {
            System.setProperty("dbunit.database.supportBatchStatement", "false");
        }

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
            throw new BuildException("Class Not Found: JDBC driver "
                    + driver + " could not be loaded", e, location);
        }
        catch (IllegalAccessException e)
        {
            throw new BuildException("Illegal Access: JDBC driver "
                    + driver + " could not be loaded", location);
        }
        catch (InstantiationException e)
        {
            throw new BuildException("Instantiation Exception: JDBC driver "
                    + driver + " could not be loaded", location);
        }

        try
        {
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
            throw new BuildException(e, location);
        }
        catch (SQLException e)
        {
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
            }
        }
    }
}

