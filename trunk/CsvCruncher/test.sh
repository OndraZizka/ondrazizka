
if [ "" == "$1" ] ; then mvn clean install assembly:assembly; fi

java -jar target/CsvCruncher-1.0-SNAPSHOT-dist.jar src/test/data/csv-test.csv target/foo.csv 'SELECT * FROM indata'