Rationale
=========

Maven plugin that we execute in our
[Clover CI pipeline](https://github.com/xwiki/xwiki-jenkins-pipeline/blob/master/src/org/xwiki/jenkins/Clover.groovy)
to verify that we don't lower the global coverage. It's indeed possible that at the module level the coverage stays 
flat or increases (as measure by Jacoco), while we reduce the global coverage:
* If some modules with a high coverage are moved to Contrib for example the global TPC will decrease.
* If a new module with a coverage lower than the global one is introduce, it'll lower the global TPC.
* If some refactoring are done in a given module and that module was tested by functional tests, and the refactoring
  causes different code to be exercised, then the global coverage can be reduced.   

Links:
* Original trigger: http://massol.myxwiki.org/xwiki/bin/view/Blog/ComparingCloverReports
* Discussed strategy: http://markmail.org/message/owtyhkmrz4tcbymn

Usage
=====

Configuration inside a `pom.xml`:

```
<plugin>
  <groupId>org.xwiki.clover</groupId>
  <artifactId>xwiki-clover-maven</artifactId>
  <version>1.0</version>
  <configuration>
    <oldReportId>20171222-1835</oldReportId>
    <newReportId>20181129-1122</newReportId>
    <oldCloverXMLReport>reports/clover-20171222-1835.xml</oldCloverXMLReport>
    <newCloverXMLReport>reports/clover-20181129-1122.xml</newCloverXMLReport>
  </configuration>
  <executions>
    <execution>
      <id>report</id>
      <goals>
        <goal>report</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

And then `mvn clean xwiki-clover:report`.

Directly from the command line:

```
mvn clean xwiki-clover:report \
  -DoldCloverXMLReport=reports/clover-20171222-1835.xml -DoldReportId=20171222-1835 \
  -DnewCloverXMLReport=reports/clover-20181129-1122.xml -DnewReportId=20181129-1122
```

Options
=======

* It's possible to pass the reports either as file locations or as URLs.
* The output directory is configurable with the `outputDirectory` configuration property in the `pom.xml` or by 
  using the `diffReportOutputDirectory` command line system property.

Examples
========

* [Example report 1](http://maven.xwiki.org/site/clover/20181129/XWikiReport-20171222-1835-20181129-1122.html)
