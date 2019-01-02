def reportFile = new File(basedir, 'target/clover/XWikiReport-20171222-1835-20181129-1122.html')
assert reportFile.exists()
// Verify ALL
assert reportFile.text.contains('<tr><td>ALL</td><td>68.6358</td><td>69.0465</td><td>0.4106</td><td>N/A</td></tr>')
// Verify when TPC diff is >= 0 (even though contribution is < 0)
assert reportFile.text.contains('<tr><td>xwiki-commons-velocity</td><td style="color:green;">77.9603</td><td style="color:green;">78.2276</td><td style="color:green;">0.2673</td><td style="color:green;">-0.0065</td></tr>')
// Verify when TPC diff is < 0
assert reportFile.text.contains('<tr><td>xwiki-platform-tag-api</td><td style="color:red;">71.9794</td><td style="color:red;">32.8244</td><td style="color:red;">-39.155</td><td style="color:red;">-0.1</td></tr>')
// Verify when TPC diff is < 0 and contribution is > 0
assert reportFile.text.contains('<tr><td>xwiki-platform-sheet-api</td><td style="color:red;">93.75</td><td style="color:red;">92.5</td><td style="color:red;">-1.25</td><td style="color:red;">0.0003</td></tr>')
// Verify when module is new and contribution is < 0
assert reportFile.text.contains('<tr><td>xwiki-platform-graphviz-plugin</td><td style="color:red;">N/A</td><td style="color:red;">0</td><td style="color:red;">N/A</td><td style="color:red;">-0.0737</td></tr>')
// Verify when module is new and contribution is > 0
assert reportFile.text.contains('<tr><td>xwiki-platform-date</td><td style="color:green;">N/A</td><td style="color:green;">100</td><td style="color:green;">N/A</td><td style="color:green;">0.001</td></tr>')
// Verify when module has been removed (even though contribution is < 0)
assert reportFile.text.contains('<tr><td>xwiki-rendering-syntax-tex</td><td style="color:green;">80.6451</td><td style="color:green;">N/A</td><td style="color:green;">N/A</td><td style="color:green;">-0.0023</td></tr>')
