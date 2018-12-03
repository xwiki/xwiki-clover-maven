def reportFile = new File(basedir, 'target/XWikiReport-20171222-1835-20181129-1122.html')
assert reportFile.exists()
assert reportFile.text.contains('<tr><td>ALL</td><td>68.6358</td><td>69.0465</td><td>0.4106</td><td>N/A</td></tr>')