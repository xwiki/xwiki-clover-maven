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

public class DiffDataSet
{
    private Map<String, DiffMetrics> diffData = new HashMap<>();

    public Map<String, DiffMetrics> getDiffData()
    {
        return this.diffData;
    }

    public void setDiffData(Map<String, DiffMetrics> diffData)
    {
        this.diffData = diffData;
    }

    public void addDiffMetrics(String key, DiffMetrics metrics)
    {
        getDiffData().put(key, metrics);
    }

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
