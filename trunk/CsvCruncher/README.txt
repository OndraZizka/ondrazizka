
This is a test of ways to parse and process a CSV file.
The winner is HSQL with it's CSV-file-based tables. See HsqlCsvTest.java.

Usage:

  crunch [-in] <inCSV> [-out] <outCSV> [-sql] <SQL>"

Example:

  crunch input.csv output.csv "SELECT AVG(duration) AS durAvg FROM (SELECT * FROM indata ORDER BY duration LIMIT 2 OFFSET 6)"

Example:

   ## jobName, buildNumber, config, ar, arFile, deployDur, warmupDur, scale
   'eap-5.1.0-perf-deployers', 355,'production','testdata/war/hellothere.war','hellothere.war',10282,14804,1000
   'eap-5.1.0-perf-deployers', 355,'production','testdata/ear/EarWithWar-Counter.ear','EarWithWar-Counter.ear',11005,18904,1000
   'eap-5.1.0-perf-deployers', 355,'production','testdata-own/war/war-big-1.0.war','war-big-1.0.war',1966,14800,100
   ...

   SELECT jobName, buildNumber, config, ar, arFile, deployDur, warmupDur, scale,"
								+ " CAST(warmupDur AS DOUBLE) / CAST(deployDur AS DOUBLE) AS warmupSlower FROM indata ORDER BY deployDur

   'eap-5.1.0-perf-deployers',355,'production','testdata/war/hellothere.war','hellothere.war',10282,14804,1000,1.4397977047267068E0
   'eap-5.1.0-perf-deployers',355,'production','testdata/ear/EarWithWar-Counter.ear','EarWithWar-Counter.ear',11005,18904,1000,1.7177646524307133E0
   'eap-5.1.0-perf-deployers',355,'production','testdata-own/war/war-big-1.0.war','war-big-1.0.war',1966,14800,100,7.527975584944048E0

(For the `crunch` see target/appassabler/bin .)

***  Revision 9727 was tested. Successive may be messy :) ***