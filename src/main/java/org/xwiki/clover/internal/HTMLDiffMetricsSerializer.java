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

public class HTMLDiffMetricsSerializer implements DiffMetricsSerializer
{
    private static final DecimalFormat FORMAT = new DecimalFormat("#.####");

    private static final String RED = "style=\"color:red;\"";

    private static final String GREEN = "style=\"color:green;\"";

    private static final String NA = "N/A";

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
        content.append("<h1>Report - " + oldReportId + " -> " + newReportId + "</h1>");

        if (diffDataSet.hasFailures()) {
            content.append("<p>FAILURE: There are modules having lowered the global TPC.</p>");
        }

        content.append("<h2>Modules affecting TPC</h2>");
        content.append("<table><thead><tr>");
        content.append("<th>Module</th><th>TPC Old</th><th>TPC New</th><th>TPC Diff</th>"
            + "<th>Global TPC Contribution</th>");
        content.append("</tr></thead><tbody>");
        double oldGlobalTPC = oldDataSet.getModuleGlobalMetrics().getTPC();
        double newGlobalTPC = newDataSet.getModuleGlobalMetrics().getTPC();
        content.append("<tr><td>ALL</td><td>" + round(oldGlobalTPC) + "</td><td>" + round(newGlobalTPC) + "</td>");
        content.append("<td>" + round(newGlobalTPC - oldGlobalTPC) + "</td><td>N/A</td></tr>");

        for (Map.Entry<String, DiffMetrics> entry : diffDataSet.getDiffData().entrySet()) {
            DiffMetrics metrics = entry.getValue();
            String css = metrics.getContribution() < 0 ? RED : GREEN;
            String oldTPC = displayDouble(metrics.getOldTPC());
            String newTPC = displayDouble(metrics.getNewTPC());
            String diffTPC = metrics.getOldTPC() != null && metrics.getNewTPC() != null
                ? round(metrics.getNewTPC() - metrics.getOldTPC()) : NA;
            String cssDiff = metrics.getContribution() < 0 || (metrics.getOldTPC() != null
                && metrics.getNewTPC() != null && metrics.getNewTPC() - metrics.getOldTPC() < 0) ? RED: GREEN;
            content.append("<tr><td>" + entry.getKey() + "</td><td " + css + ">" + oldTPC + "</td><td " + css + ">"
                + newTPC + "</td>");
            content.append("<td " + cssDiff + ">" + diffTPC + "</td><td " + css + ">"
                + round(metrics.getContribution()) + "</td></tr>");
        }
        content.append("</tbody></table>");

        content.append("<h2>Failing Tests</h2>");

        // Tests failing in oldDataset and not in newDataSet
        List<String> oldFailing = oldDataSet.getTestFailures();
        List<String> newFailing = newDataSet.getTestFailures();
        content.append("<p>Differences of failing tests between old (" + oldFailing.size() + ") and new ("
            + newFailing.size() + ") reports.<br/><br/>");
        content.append("<table><thead><tr>");
        content.append("<th>Old Failing</th><th>New Failing</th>");
        content.append("</tr></thead><tbody>");
        for (String failing : oldFailing) {
            if (!newFailing.contains(failing)) {
                content.append("<tr><td>" + failing + "</td><td>N/A</td></tr>");
            }
        }
        for (String failing : newFailing) {
            if (!oldFailing.contains(failing)) {
                content.append("<tr><td>N/A</td><td>" + failing + "</td></tr>");
            }
        }
        content.append("</tbody></table>");

        return content.toString();
    }

    private String displayDouble(Double number)
    {
        return number != null ? round(number) : NA;
    }

    private String round(double number)
    {
        return FORMAT.format(number);
    }
}
