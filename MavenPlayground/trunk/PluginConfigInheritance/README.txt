


To test whether <configuration> is inherited partially or overwritten by e.g. profile's <conf> completely.

WAR plugin config used for this.

May it vary per plugin?



Usage:


mvn clean package
mvn clean package -Pchild


The latter should give you a WAR in `target/test-child`,
but still using `target/test` for intermediate files.
  
  
