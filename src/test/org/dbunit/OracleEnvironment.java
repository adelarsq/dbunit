/*
 * OracleEnvironment.java   May 2, 2002
 *
 * Copyright 2002 Freeborders Canada Corp. All Rights Reserved.
 *
 * This software is the proprietary information of Karat Software Corp.
 * Use is subject to license terms.
 *
 */

package org.dbunit;

import org.dbunit.dataset.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class OracleEnvironment extends DatabaseEnvironment
{
    public OracleEnvironment(DatabaseProfile profile) throws Exception
    {
        super(profile);
    }

    public IDataSet getInitDataSet() throws Exception
    {
        ITable[] extraTables = {
            new DefaultTable("CLOB_TABLE"),
            new DefaultTable("BLOB_TABLE"),
        };

        return new CompositeDataSet(super.getInitDataSet(),
                new DefaultDataSet(extraTables));
    }
}
