Uncategorized code I've created. Mostly Java, but also Groovy, Bash, Maven POMs, XSLT, and other.

## JawaBot moved ##
JawaBot finally moved to http://code.google.com/p/jawabot/ .

## Mavenized: ##

  * IRC Appender 0.5
  * wicket-contrib-accordion 1.3.0
  * wicketstuff-dojo 1.3.0-beta
  * wicketstuff-dojo 1.4-ondrazizka-aeff23b
  * wicketstuff-dojo-api 1.4-ondrazizka-aeff23b
  * wicketstuff-dojo-resources-standard-1.4
  * javax.persistence:persistence-api:jar:2.0
  * javax.transaction:jta:jar:1.1
  * javax.xml:jaxb-impl:jar:2.1
  * FreeTTS / MBrola libraries with MANIFEST.MF fixed and KevinVoiceDirectory recompiled.
  * [PJL Compressing Filter](https://sourceforge.net/projects/pjl-comp-filter/) 1.7 by srowen
  * MySQL JDBC driver 5.1.12 for Maven – mavenized Connector/J
  * PircBot 1.5.0
  * Axis2 1.4.1
  * Sun JMX - jmxri and jmxtools
  * Ralph's CSV library (you have to use Saxon 8.7, Saxon 9 is not mavenized)


```
<project>

  <!-- Dependencies -->
  <dependencies>

    <dependency>
       <groupId>cz.dynawest.third.pircbot</groupId>
       <artifactId>PircBot</artifactId>
       <version>1.5.0</version>
    </dependency>

  </dependencies>

  <repositories>
    <repository>
      <id>ondrazizka</id>
      <url>http://ondrazizka.googlecode.com/svn/maven</url>
    </repository>
  </repositories>
</project>
```