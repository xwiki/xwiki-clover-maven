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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the parsing of a Clover XML file ({@code clover.xml}.
 *
 * @version $Id$
 * @since 1.0
 */
public class XMLDataSet
{
    public static final String PACKAGE = "package";

    public static final String MODULE = "module";

    private Map<String, Map<String, CloverMetrics>> data = new HashMap<>();

    private Map<String, CloverMetrics> globalData = new HashMap<>();

    private List<String> testFailures = new ArrayList<>();

    public void addPackageMetrics(String metricKey, CloverMetrics metrics)
    {
        addMetrics(PACKAGE, metricKey, metrics);
    }

    public void addModuleMetrics(String metricKey, CloverMetrics metrics)
    {
        addMetrics(MODULE, metricKey, metrics);
    }

    public void addMetrics(String type, String metricKey, CloverMetrics metrics)
    {
        Map<String, CloverMetrics> dataPerType = this.data.get(type);
        if (dataPerType == null) {
            dataPerType = new HashMap<>();
            this.data.put(type, dataPerType);
        }
        sumMetrics(metricKey, dataPerType, metrics);
    }

    private void sumMetrics(String metricKey, Map<String, CloverMetrics> metricsData, CloverMetrics metrics)
    {
        CloverMetrics currentMetrics = metricsData.get(metricKey);
        if (currentMetrics == null) {
            CloverMetrics newMetrics = new CloverMetrics();
            newMetrics.addMetrics(metrics);
            metricsData.put(metricKey, newMetrics);
        } else {
            currentMetrics.addMetrics(metrics);
        }
    }

    public Map<String, CloverMetrics> getData(String type)
    {
        return this.data.get(type);
    }

    public Map<String, CloverMetrics> getPackageData()
    {
        return getData(PACKAGE);
    }

    public Map<String, CloverMetrics> getModuleData()
    {
        return getData(MODULE);
    }

    public XMLDataSet computeTPCs()
    {
        for (Map.Entry<String, Map<String, CloverMetrics>> typeEntry : this.data.entrySet()) {
            CloverMetrics globalMetrics = new CloverMetrics();
            for (CloverMetrics metrics : typeEntry.getValue().values()) {
                metrics.computeTPC();
                globalMetrics.addMetrics(metrics);
            }
            globalMetrics.computeTPC();
            this.globalData.put(typeEntry.getKey(), globalMetrics);
        }
        return this;
    }

    public CloverMetrics getGlobalMetrics(String type)
    {
        return this.globalData.get(type);
    }

    public CloverMetrics getModuleGlobalMetrics()
    {
        return this.globalData.get(MODULE);
    }

    public CloverMetrics getPackageGlobalMetrics()
    {
        return this.globalData.get(PACKAGE);
    }

    public void addTestFailures(Collection<String> testSignatures)
    {
        this.testFailures.addAll(testSignatures);
    }

    public List<String> getTestFailures()
    {
        return this.testFailures;
    }
}
