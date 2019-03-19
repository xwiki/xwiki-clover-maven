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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Serializes diff data into HTML.
 *
 * @version $Id$
 * @since 1.0
 */
public class HTMLDiffMetricsSerializer implements DiffMetricsSerializer
{
    private static final DecimalFormat FORMAT = new DecimalFormat("#.####");

    private static final String RED = "style=\"color:red;\"";

    private static final String GREEN = "style=\"color:green;\"";

    private static final String NA = "N/A";

    private static final String START_TABLE = "<table>";
    private static final String START_TR = "<tr>";
    private static final String START_TD = "<td>";
    private static final String START_TH = "<th>";
    private static final String START_THEAD = "<thead>";
    private static final String START_TBODY = "<tbody>";
    private static final String START_H2 = "<h2>";
    private static final String START_P = "<p>";
    private static final String START_H1 = "<h1>";

    private static final String STOP_TABLE = "</table>";
    private static final String STOP_TR = "</tr>";
    private static final String STOP_TD = "</td>";
    private static final String STOP_TH = "</th>";
    private static final String STOP_THEAD = "</thead>";
    private static final String STOP_TBODY = "</tbody>";
    private static final String STOP_H2 = "</h2>";
    private static final String STOP_P = "</p>";
    private static final String STOP_H1 = "</h1>";

    private static final String BR = "<br/>";

    /**
     * Sets the rounding strategy (we truncate numbers).
     */
    public HTMLDiffMetricsSerializer()
    {
        // Truncate
        FORMAT.setRoundingMode(RoundingMode.DOWN);
    }

    @Override
    public String serialize(DiffDataSet diffDataSet, XMLDataSet oldDataSet, XMLDataSet newDataSet,
        String oldReportId, String newReportId)
    {
        StringBuilder content = new StringBuilder();
        content.append(START_H1).append(String.format("Report - %s -> %s", oldReportId, newReportId)).append(STOP_H1);

        if (hasGlobalCoverageFailure(oldDataSet, newDataSet)) {
            content.append(START_P).append("ERROR: Global TPC has been lowered").append(STOP_P);
        }

        if (diffDataSet.hasFailures()) {
            content.append(START_P).append("FAILURE: There are modules lowering the global TPC").append(STOP_P);
        }

        generateTPCHTML(content, diffDataSet, oldDataSet, newDataSet);
        generateFailingTestHTML(content, oldDataSet, newDataSet);

        return content.toString();
    }

    private void generateTPCHTML(StringBuilder content, DiffDataSet diffDataSet, XMLDataSet oldDataSet,
        XMLDataSet newDataSet)
    {
        content.append(START_H2).append("Modules affecting TPC").append(STOP_H2);
        content.append(START_TABLE);
        addTableHead(content, "Module", "TPC Old", "TPC New", "TPC Diff", "Global TPC Contribution");
        content.append(START_TBODY);
        double oldGlobalTPC = oldDataSet.getModuleGlobalMetrics().getTPC();
        double newGlobalTPC = newDataSet.getModuleGlobalMetrics().getTPC();
        content.append(START_TR);
        content.append(START_TD).append("ALL").append(STOP_TD);
        content.append(START_TD).append(round(oldGlobalTPC)).append(STOP_TD);
        content.append(START_TD).append(round(newGlobalTPC)).append(STOP_TD);
        content.append(START_TD).append(round(newGlobalTPC - oldGlobalTPC)).append(STOP_TD);
        content.append(START_TD).append(NA).append(STOP_TD);
        content.append(STOP_TR);

        for (Map.Entry<String, DiffMetrics> entry : diffDataSet.getDiffData().entrySet()) {
            DiffMetrics metrics = entry.getValue();
            String css = getCSS(metrics);
            String oldTPC = displayDouble(metrics.getOldTPC());
            String newTPC = displayDouble(metrics.getNewTPC());
            String diffTPC = metrics.getOldTPC() != null && metrics.getNewTPC() != null
                ? round(metrics.getNewTPC() - metrics.getOldTPC()) : NA;
            content.append(START_TR);
            content.append(START_TD).append(entry.getKey()).append(STOP_TD);
            content.append(startTD(css)).append(oldTPC).append(STOP_TD);
            content.append(startTD(css)).append(newTPC).append(STOP_TD);
            content.append(startTD(css)).append(diffTPC).append(STOP_TD);
            content.append(startTD(css)).append(round(metrics.getContribution())).append(STOP_TD);
            content.append(STOP_TR);
        }
        content.append(STOP_TBODY);
        content.append(STOP_TABLE);
    }

    private String getCSS(DiffMetrics metrics)
    {
        // We display in RED modules that need to be fixed, i.e. modules that have a TPC diff < 0
        // Note that the global contribution could be < 0 and yet the TPC diff > 0 (in case some code was removed),
        // which is why checking for contribution < 0 is not a good metric.
        // For new modules, base it on the contribution though.
        // For removed modules, always consider it green
        boolean isNew = metrics.getOldTPC() == null && metrics.getNewTPC() != null;
        boolean isRemoved = metrics.getOldTPC() != null && metrics.getNewTPC() == null;
        String css;
        if (isNew) {
            css = metrics.getContribution() >= 0 ? GREEN : RED;
        } else if (isRemoved) {
            css = GREEN;
        } else if (metrics.getNewTPC() - metrics.getOldTPC() >= 0) {
            css = GREEN;
        } else {
            css = RED;
        }
        return css;
    }
    private void addTableHead(StringBuilder content, String... columnValues)
    {
        content.append(START_THEAD).append(START_TR);
        for (String value : columnValues) {
            content.append(START_TH).append(value).append(STOP_TH);
        }
        content.append(STOP_TR).append(STOP_THEAD);
    }

    private void generateFailingTestHTML(StringBuilder content, XMLDataSet oldDataSet, XMLDataSet newDataSet)
    {
        content.append(START_H2).append("Failing Tests").append(STOP_H2);

        // Tests failing in oldDataset and not in newDataSet
        List<String> oldFailing = oldDataSet.getTestFailures();
        List<String> newFailing = newDataSet.getTestFailures();
        content.append(START_P);
        content.append(String.format("Differences of failing tests between old (%s) and new (%s) reports.",
            oldFailing.size(), newFailing.size()));
        content.append(STOP_P);
        content.append(BR).append(BR);
        content.append(START_TABLE);
        addTableHead(content, "Old Failing", "New Failing");
        content.append(START_TBODY);
        for (String failing : oldFailing) {
            if (!newFailing.contains(failing)) {
                content.append(START_TR);
                content.append(START_TD).append(failing).append(STOP_TD);
                content.append(START_TD).append(NA).append(STOP_TD);
                content.append(STOP_TR);
            }
        }
        for (String failing : newFailing) {
            if (!oldFailing.contains(failing)) {
                content.append(START_TR);
                content.append(START_TD).append(NA).append(STOP_TD);
                content.append(START_TD).append(failing).append(STOP_TD);
                content.append(STOP_TR);
            }
        }
        content.append(STOP_TBODY);
        content.append(STOP_TABLE);
    }

    private String startTD(String css)
    {
        return String.format("<td %s>", css);
    }

    private String displayDouble(Double number)
    {
        return number != null ? round(number) : NA;
    }

    private String round(double number)
    {
        return FORMAT.format(number);
    }

    /**
     * @return true if the global TPC has been lowered
     */
    private boolean hasGlobalCoverageFailure(XMLDataSet oldDataSet, XMLDataSet newDataSet)
    {
        boolean failure = false;
        if (newDataSet.getModuleGlobalMetrics().getTPC() - oldDataSet.getModuleGlobalMetrics().getTPC() < 0) {
            failure = true;
        }
        return failure;
    }
}
