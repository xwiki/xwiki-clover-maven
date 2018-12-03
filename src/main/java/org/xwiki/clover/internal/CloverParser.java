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

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Parses a {@code clover.xml} file produced by Clover and store some of its data in memory in a {@link XMLDataSet}
 * object.
 *
 * @version $Id$
 * @since 1.0
 */
public class CloverParser
{
    private static final String NAME = "name";

    private Log logger;

    /**
     * @param logger the Maven logger that we use to log
     */
    public CloverParser(Log logger)
    {
        this.logger = logger;
    }

    /**
     * @param xmlReport the {@code clover.xml} source data to parse
     * @return the parsed data
     * @throws Exception if there's an error during the parsing
     */
    public XMLDataSet parse(Reader xmlReport) throws Exception
    {
        XMLDataSet dataSet = new XMLDataSet();

        SAXReader reader = new SAXReader();
        Document document = reader.read(xmlReport);

        Iterator<Element> rootIterator = document.getRootElement().elementIterator();
        // <project>
        walkProjectElements(rootIterator.next(), dataSet);
        // <testproject>
        walkProjectElements(rootIterator.next(), dataSet);

        return dataSet;
    }

    private void walkProjectElements(Element project, XMLDataSet dataSet)
    {
        // First element is always <metrics>, discard
        Iterator<Element> iterator = project.elementIterator();
        iterator.next();

        // <package>
        while (iterator.hasNext()) {
            Element packageElement = iterator.next();
            walkFileElements(packageElement, dataSet);
        }
    }

    private void walkFileElements(Element packageElement, XMLDataSet dataSet)
    {
        String packageName = packageElement.attributeValue(NAME);

        // First element is package <metrics>, ignore.
        Iterator<Element> iterator = packageElement.elementIterator();
        iterator.next();

        // Next elements are <file>
        while (iterator.hasNext()) {
            Element file = iterator.next();
            String filePath = file.attributeValue("path");
            // Exclude tests
            // - test packages ("/test/")
            // - test modules (".*xwiki-.*-test.*")
            if (!(filePath.contains("/test/") || filePath.matches(".*xwiki-.*-test.*"))) {
                CloverMetrics fileMetrics = extractMetrics(file.elementIterator().next());
                // Save metrics for packages
                dataSet.addPackageMetrics(packageName, fileMetrics);
                // Save metrics for modules
                dataSet.addModuleMetrics(extractModuleName(filePath), fileMetrics);
                if (filePath.contains("test")) {
                    this.logger.warn(String.format("Potentially missing test file: [%s]", filePath));
                }
            } else {
                // Find failing tests and record them.
                walkTestElements(packageName, file, dataSet);
            }
        }
    }

    private void walkTestElements(String packageName, Element file, XMLDataSet dataSet)
    {
        // First element is package <metrics>, ignore.
        Iterator<Element> iterator = file.elementIterator();
        iterator.next();

        // Following elements are <class> or <line> elements.
        String testClassName = null;
        int classFailingTestCount = 0;
        int currentFailingTestCount = 0;
        List<String> failingTestSignatures = new ArrayList<>();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            if (element.getName().equals("class")) {
                // If we're already parsing a class ignore sub classes
                if (testClassName == null) {
                    testClassName = element.attributeValue(NAME);
                    String failureAsString = element.elementIterator().next().attributeValue("testfailures");
                    if (failureAsString == null || Integer.parseInt(failureAsString) == 0) {
                        break;
                    }
                    classFailingTestCount = Integer.parseInt(failureAsString);
                }
            } else if (element.getName().equals("line")) {
                // Record if we find a <line> with a "testsuccess" attribute set to "false"
                String successString = element.attributeValue("testsuccess");
                if (successString != null && successString.equals("false")) {
                    String failingTestName = String.format("%s.%s#%s", packageName, testClassName,
                        element.attributeValue("signature"));
                    failingTestSignatures.add(failingTestName);
                    currentFailingTestCount++;
                }
            }
        }

        // If we don't have any test signatures recorded or if the number of failing tests doesn't match the ones
        // specified in the <class> element then add an entry to explain that
        if (currentFailingTestCount != classFailingTestCount) {
            String failingTestName = String.format("%s.%s#? (%s tests not accounted for)", packageName,
                testClassName, (classFailingTestCount - currentFailingTestCount));
            failingTestSignatures.add(failingTestName);
        }

        dataSet.addTestFailures(failingTestSignatures);
    }

    private CloverMetrics extractMetrics(Element metrics)
    {
        CloverMetrics metric = new CloverMetrics();
        metric.setConditionals(Long.parseLong(metrics.attributeValue("conditionals")));
        metric.setCoveredConditionals(Long.parseLong(metrics.attributeValue("coveredconditionals")));
        metric.setMethods(Long.parseLong(metrics.attributeValue("methods")));
        metric.setCoveredMethods(Long.parseLong(metrics.attributeValue("coveredmethods")));
        metric.setStatements(Long.parseLong(metrics.attributeValue("statements")));
        metric.setCoveredStatements(Long.parseLong(metrics.attributeValue("coveredstatements")));
        return metric;
    }

    /**
     * Example input: {@code /home/hudsonagent/jenkins_root/workspace/Clover/xwiki-commons/xwiki-commons-core/
     * xwiki-commons-stability/src/main/java/org/xwiki/stability/Unstable.java}.
     * Returns: {@code xwiki-commons-stability}
     */
    private String extractModuleName(String path)
    {
        String before = StringUtils.substringBefore(path, "/src/");
        return StringUtils.substringAfterLast(before, "/");
    }
}
