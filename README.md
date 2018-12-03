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