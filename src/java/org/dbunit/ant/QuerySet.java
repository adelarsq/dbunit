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

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FilterSet;

/**
 * This element is a container for Queries. It facilitates reuse
 * through references. Using Ant 1.6 and greater, references can be
 * defined in a single build file and <i>import</i>ed into many others.
 * An example of where this is useful follows:
 * <p>
 * In our database
 * we have INDIVIDUALS which must have an associated NAME_INFO and
 * at least one IND_ADDRESS. The developer creating a dataset for
 * his/her tests probably won't know all the details of what relationships are
 * expected, and if he did, its an error prone and repetitive task
 * to create the correct SQL for entities in each dataset.
 * Missing a related table, not only creates invalid data for your tests,
 * but also is likely to cause DBUnit setUp() failures due to foreign key
 * constraint errors.
 * (ex. If a previous test had inserted INDIVIDUALS
 * and NAME_INFO and my test tries to delete only the INDIVIDUALS, the
 * NAME_INFO.IND_ID constraint would prevent it)
 * <p>
 * Usage:
 *
 * <pre>
 * &lt;!-- ======== Define the reusable reference ========== --&gt;
 *
 * &lt;queryset id="individuals"&gt;
 *    &lt;query name="INDIVIDUALS" sql="
 *      SELECT * FROM APS_DATA.INDIVIDUALS WHERE IND_ID IN (@subQuery@)"/&gt;
 *
 *    &lt;query name="NAME_INFO" sql="
 *      SELECT B.* FROM APS_DATA.INDIVIDUALS A, APS_DATA.NAME_INFO B
 *      WHERE A.IND_ID IN (@subQuery@)
 *      AND B.IND_ID = A.IND_ID"/&gt;
 *
 *    &lt;query name="IND_ADDRESSES" sql="
 *      SELECT B.* FROM APS_DATA.INDIVIDUALS A, APS_DATA.IND_ADDRESSES B
 *      WHERE A.IND_ID IN (@subQuery@)
 *      AND B.IND_ID = A.IND_ID"/&gt;
 * &lt;/queryset&gt;
 *
 * &lt;!-- ========= Use the reference ====================== --&gt;
 *
 * &lt;dbunit dest="@{destDir}" driver="${jdbcDriver}"
 *     url="${jdbcURL}" userid="${jdbcUser}" password="${jdbcPassword}"&gt;
 *   &lt;export dest="someDir"&gt;
 *   &lt;queryset refid="individuals"&gt;
 *      &lt;filterset&gt;
 *        &lt;filter token="subQuery" value="
 *          SELECT IND_ID FROM APS_DATA.INDIVIDUALS WHERE USER_NAME = 'UNKNOWN'"/&gt;
 *      &lt;/filterset&gt;
 *   &lt;/queryset&gt;
 *
 *   &lt;queryset&gt;
 *      &lt;query name="MAN_EVENT_TYPE"
 *        sql="SELECT * FROM MANUSCRIPTS.MAN_EVENT_TYPE"/&gt;
 *      &lt;query name="JOURNAL" sql="SELECT * FROM MANUSCRIPTS.JOURNAL"/&gt;
 *   &lt;/queryset&gt;
 *   &lt;/export&gt;
 * &lt;/dbunit&gt;
 *
 * </pre>
 *
 * @author Lenny Marks lenny@aps.org
 * @version $Revision$
 * @since Sep. 13 2004
 */
public class QuerySet {

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(QuerySet.class);

	private String id;
	private String refid;
	private List queries = new ArrayList();
	private List filterSets = new ArrayList();

	private static String ERR_MSG =
		"Cannot specify 'id' and 'refid' attributes together in queryset.";

	public QuerySet() {
		super();
	}

	public void addQuery(Query query) {
        logger.debug("addQuery(query=" + query + ") - start");

		queries.add(query);
	}

	public void addFilterSet(FilterSet filterSet) {
        logger.debug("addFilterSet(filterSet=" + filterSet + ") - start");

		filterSets.add(filterSet);
	}

	public String getId() {
        logger.debug("getId() - start");

		return id;
	}

	public String getRefid() {
        logger.debug("getRefid() - start");

		return refid;
	}

	public void setId(String string) {
        logger.debug("setId(string=" + string + ") - start");

		if(refid != null) throw new BuildException(ERR_MSG);
		id = string;
	}

	public void setRefid(String string) {
        logger.debug("setRefid(string=" + string + ") - start");

		if(id != null) throw new BuildException(ERR_MSG);
		refid = string;
	}

	protected List getQueries() {
        logger.debug("getQueries() - start");

		Iterator i = queries.iterator();
		while(i.hasNext()) {
			Query query = (Query)i.next();
			replaceTokens(query);
		}

		return queries;

	}

	private void replaceTokens(Query query) {
        logger.debug("replaceTokens(query=" + query + ") - start");

		Iterator i = filterSets.iterator();
		while(i.hasNext()) {
			FilterSet filterSet = (FilterSet)i.next();
			query.setSql(filterSet.replaceTokens(query.getSql()));
		}
	}


	public void copyQueriesFrom(QuerySet referenced) {
        logger.debug("copyQueriesFrom(referenced=" + referenced + ") - start");

		Iterator i = referenced.queries.iterator();
		while(i.hasNext()) {
			addQuery((Query)i.next());
		}
	}


}