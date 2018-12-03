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
    private Map<DataType, Map<String, CloverMetrics>> data = new HashMap<>();

    private Map<DataType, CloverMetrics> globalData = new HashMap<>();

    private List<String> testFailures = new ArrayList<>();

    /**
     * Record a new set of metrics for a package.
     *
     * @param metricKey the name of the package
     * @param metrics the metrics to record
     */
    public void addPackageMetrics(String metricKey, CloverMetrics metrics)
    {
        addMetrics(DataType.PACKAGE, metricKey, metrics);
    }

    /**
     * Record a new set of metrics for a module.
     *
     * @param metricKey the name of the module
     * @param metrics the metrics to record
     */
    public void addModuleMetrics(String metricKey, CloverMetrics metrics)
    {
        addMetrics(DataType.MODULE, metricKey, metrics);
    }

    /**
     * @return the recorded metrics for all packages
     */
    public Map<String, CloverMetrics> getPackageData()
    {
        return getData(DataType.PACKAGE);
    }

    /**
     * @return the recorded metrics for all modules
     */
    public Map<String, CloverMetrics> getModuleData()
    {
        return getData(DataType.MODULE);
    }

    /**
     * @param type the type of data to return (package or modules)
     * @return the metrics for the passed type
     */
    public Map<String, CloverMetrics> getData(DataType type)
    {
        return this.data.get(type);
    }

    /**
     * Computes the TPCs for all packages and all modules.
     */
    public void computeTPCs()
    {
        for (Map.Entry<DataType, Map<String, CloverMetrics>> typeEntry : this.data.entrySet()) {
            CloverMetrics globalMetrics = new CloverMetrics();
            for (CloverMetrics metrics : typeEntry.getValue().values()) {
                metrics.computeTPC();
                globalMetrics.addMetrics(metrics);
            }
            globalMetrics.computeTPC();
            this.globalData.put(typeEntry.getKey(), globalMetrics);
        }
    }

    /**
     * @return the global TPC for all modules
     */
    public CloverMetrics getModuleGlobalMetrics()
    {
        return getGlobalMetrics(DataType.MODULE);
    }

    /**
     * @return the global TPC for all packages
     */
    public CloverMetrics getPackageGlobalMetrics()
    {
        return getGlobalMetrics(DataType.PACKAGE);
    }

    /**
     * @param type the type of data to return (package or modules)
     * @return the global TPC for the passed type
     */
    public CloverMetrics getGlobalMetrics(DataType type)
    {
        return this.globalData.get(type);
    }

    /**
     * Record some failing tests.
     *
     * @param testSignatures the String signatures of the failing tests
     */
    public void addTestFailures(Collection<String> testSignatures)
    {
        this.testFailures.addAll(testSignatures);
    }

    /**
     * @return the failing test signatures
     */
    public List<String> getTestFailures()
    {
        return this.testFailures;
    }

    private void addMetrics(DataType type, String metricKey, CloverMetrics metrics)
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
}
