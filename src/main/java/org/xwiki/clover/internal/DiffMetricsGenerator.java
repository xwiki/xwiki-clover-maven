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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DiffMetricsGenerator
{
    /**
     * @return the diff metrics by package or module, sorted by contributions (lowest to highest)
     */
    public DiffDataSet generate(String type, XMLDataSet oldDataSet, XMLDataSet newDataSet)
    {
        DiffDataSet diffDataSet = new DiffDataSet();

        CloverMetrics newGlobalCloverMetrics = newDataSet.getGlobalMetrics(type);
        Map<String, CloverMetrics> oldCloverMetrics = oldDataSet.getData(type);
        Map<String, CloverMetrics> newCloverMetrics = newDataSet.getData(type);

        // Process the new added modules + the modified ones
        for (Map.Entry<String, CloverMetrics> entry : newCloverMetrics.entrySet()) {
            String metricKey = entry.getKey();
            CloverMetrics metrics = entry.getValue();
            // New modules
            if (!oldCloverMetrics.containsKey(metricKey)) {
                DiffMetrics diffMetrics = new DiffMetrics();
                diffMetrics.setContribution(newGlobalCloverMetrics.getTPC()
                    - computeGlobalTPCWithoutModule(newGlobalCloverMetrics, metrics, null));
                diffMetrics.setNewTPC(metrics.getTPC());
                diffDataSet.addDiffMetrics(metricKey, diffMetrics);
            } else {
                // Modified modules
                if (oldCloverMetrics.get(metricKey) != null) {
                    double oldTPC = oldCloverMetrics.get(metricKey).getTPC();
                    double newTPC = metrics.getTPC();
                    // Don't add packages/modules that have the same TPC since we're only interested in those that
                    // gained or lost global TPC contributions.
                    if (newTPC != oldTPC) {
                        DiffMetrics diffMetrics = new DiffMetrics();
                        diffMetrics.setContribution(newGlobalCloverMetrics.getTPC()
                            - computeGlobalTPCWithoutModule(newGlobalCloverMetrics, oldCloverMetrics.get(metricKey),
                            metrics));
                        diffMetrics.setNewTPC(newTPC);
                        diffMetrics.setOldTPC(oldTPC);
                        diffDataSet.addDiffMetrics(metricKey, diffMetrics);
                    }
                }
            }
        }

        // Process removed modules
        for (Map.Entry<String, CloverMetrics> entry : oldCloverMetrics.entrySet()) {
            String metricKey = entry.getKey();
            if (!newCloverMetrics.containsKey(metricKey)) {
                CloverMetrics metrics = entry.getValue();
                DiffMetrics diffMetrics = new DiffMetrics();
                diffMetrics.setContribution(computeGlobalTPCWithoutModule(newGlobalCloverMetrics, metrics, null)
                    - newGlobalCloverMetrics.getTPC());
                diffMetrics.setOldTPC(metrics.getTPC());
                diffDataSet.addDiffMetrics(metricKey, diffMetrics);
            }
        }

        diffDataSet.setDiffData(sortByContribution(diffDataSet.getDiffData()));

        return diffDataSet;
    }

    public DiffDataSet generateForModules(XMLDataSet oldDataSet, XMLDataSet newDataSet)
    {
        return generate(XMLDataSet.MODULE, oldDataSet, newDataSet);
    }

    private double computeGlobalTPCWithoutModule(CloverMetrics newGlobalCloverMetrics, CloverMetrics metrics1,
        CloverMetrics metrics2)
    {
        CloverMetrics diff = metrics2 == null ? newGlobalCloverMetrics.substract(metrics1)
            : newGlobalCloverMetrics.substract(metrics2.substract(metrics1));
        diff.computeTPC();
        return diff.getTPC();
    }

    private Map<String, DiffMetrics> sortByContribution(Map<String, DiffMetrics> diffMetrics)
    {
        // 1. Convert Map to List of Map
        List<Map.Entry<String, DiffMetrics>> list = new LinkedList<>(diffMetrics.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        Collections.sort(list, new Comparator<Map.Entry<String, DiffMetrics>>()
        {
            public int compare(Map.Entry<String, DiffMetrics> dm1, Map.Entry<String, DiffMetrics> dm2)
            {
                Double contribution1 = dm1.getValue().getContribution();
                Double contribution2 = dm2.getValue().getContribution();
                return contribution1.compareTo(contribution2);
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, DiffMetrics> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, DiffMetrics> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
