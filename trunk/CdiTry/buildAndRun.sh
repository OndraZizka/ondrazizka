mvn clean install
mvn dependency:copy-dependencies
cd target
java -cp CdiTry-1.0-SNAPSHOT.jar:dependency/weld-se-1.1.1.Final.jar org.jboss.qa.test.cditry.AppWeld
