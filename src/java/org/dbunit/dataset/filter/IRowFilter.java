package org.dbunit.dataset.filter;

import org.dbunit.dataset.IRowValueProvider;

/**
 * Interface to filter out rows by checking specific column values provided by the {@link IRowValueProvider}.
 * @author gommma
 * @since 2.3.0
 */
public interface IRowFilter {
	/**
	 * Checks if the current row should be accepted or not
	 * @param rowValueProvider provides arbitrary column values of the current row to be checked.
	 * @return True if the given value (and so that current row) should be accepted
	 */
	public boolean accept(IRowValueProvider rowValueProvider);
}