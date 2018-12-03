/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.clover.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set of {@code DiffMetrics}.
 *
 * @version $Id$
 * @since 1.0
 */
public class DiffDataSet
{
    private Map<String, DiffMetrics> diffData = new HashMap<>();

    /**
     * @return the TPC and contribution differences for all packages or modules
     */
    public Map<String, DiffMetrics> getDiffData()
    {
        return this.diffData;
    }

    /**
     * @param diffData see {@link #getDiffData()}
     */
    public void setDiffData(Map<String, DiffMetrics> diffData)
    {
        this.diffData = diffData;
    }

    /**
     * Record some new diff data.
     *
     * @param key the package name or module name for which to add diff metrics
     * @param metrics the diff metrics to add
     */
    public void addDiffMetrics(String key, DiffMetrics metrics)
    {
        getDiffData().put(key, metrics);
    }

    /**
     * @return true if there are some packages or modules that contribute negatively to the global TPC
     */
    public boolean hasFailures()
    {
        boolean failures = false;
        for (Map.Entry<String, DiffMetrics> entry : getDiffData().entrySet()) {
            DiffMetrics metrics = entry.getValue();
            if (metrics.getOldTPC() != null && metrics.getNewTPC() != null
                && (metrics.getNewTPC() - metrics.getOldTPC() < 0))
            {
                failures = true;
                break;
            }
        }
        return failures;
    }
}
