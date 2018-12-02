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
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(
    name = "report",
    defaultPhase = LifecyclePhase.VERIFY,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    threadSafe = true
)
public class CloverReportMojo extends AbstractMojo
{
    @Parameter(required = true)
    private File oldCloverXMLReport;

    @Parameter(required = true)
    private String oldReportId;

    @Parameter(required = true)
    private File newCloverXMLReport;

    @Parameter(required = true)
    private String newReportId;

    @Parameter(defaultValue = "${project.build.directory}")
    private File outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try {
            // Parse clover.xml files
            CloverParser cloverParser = new CloverParser(getLog());
            XMLDataSet dataSet1 = cloverParser.parse(new FileReader(this.oldCloverXMLReport)).computeTPCs();
            XMLDataSet dataSet2 = cloverParser.parse(new FileReader(this.newCloverXMLReport)).computeTPCs();

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
}
