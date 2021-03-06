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

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Generates a Clover diff report between two Clover XML reports, excluding test classes and showing global TPC
 * contribution diffs for each Maven module with the goal of finding out which modules are negatively contributing
 * to the global TPC.
 *
 * @version $Id$
 * @since 1.0
 */
@Mojo(
    name = "report",
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    threadSafe = true
)
public class CloverReportMojo extends AbstractMojo
{
    @Parameter(required = true, property = "oldCloverXMLReport")
    private String oldCloverXMLReport;

    @Parameter(required = true, property = "oldReportId")
    private String oldReportId;

    @Parameter(required = true, property = "newCloverXMLReport")
    private String newCloverXMLReport;

    @Parameter(required = true, property = "newReportId")
    private String newReportId;

    @Parameter(defaultValue = "${project.build.directory}", property = "diffReportOutputDirectory")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try {
            // Parse clover.xml files
            CloverParser cloverParser = new CloverParser(getLog());
            XMLDataSet dataSet1 = parseCloverXMLReport(cloverParser, this.oldCloverXMLReport);
            dataSet1.computeTPCs();
            XMLDataSet dataSet2 = parseCloverXMLReport(cloverParser, this.newCloverXMLReport);
            dataSet2.computeTPCs();

            // Generate diff data
            DiffMetricsGenerator diffMetricsGenerator = new DiffMetricsGenerator();
            DiffDataSet diffDataSet = diffMetricsGenerator.generateForModules(dataSet1, dataSet2);

            // Convert diff data to HTML
            DiffMetricsSerializer diffMetricsSerializer = new HTMLDiffMetricsSerializer();
            String html = diffMetricsSerializer.serialize(diffDataSet, dataSet1, dataSet2, this.oldReportId,
                this.newReportId);

            // Save HTML to file
            File file = new File(this.outputDirectory, String.format("XWikiReport-%s-%s.html", this.oldReportId,
                this.newReportId));
            FileUtils.writeStringToFile(file, html, "UTF-8");
        } catch (Exception e) {
            throw new MojoExecutionException(String.format("Failed to generate the Clover diff report for old report "
                + "[%s] and new report [%s]", this.oldCloverXMLReport, this.newCloverXMLReport), e);
        }
    }

    private XMLDataSet parseCloverXMLReport(CloverParser cloverParser, String report) throws Exception
    {
        try (Reader reader = isURL(report)
            ? new InputStreamReader(new URL(report).openStream()) : new FileReader(new File(report)))
        {
            return cloverParser.parse(reader);
        }
    }

    private boolean isURL(String report)
    {
        try {
            new URL(report);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
