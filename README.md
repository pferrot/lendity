# Lendity

Lendity is (was) a social network for people to share anything.

This project is dead since a few years, but I am finally making the source code available on GitHub.
Maybe someone will want to resurect Lendity.
Or maybe some code snippets will be useful for other projects.

Main technologies:
* Maven
* Java 
* Spring
* JSF
* MySQL 
* Hibernate

I may add more documentation than the few instructions you can find below if I ever find the time to do it. In the meantime, you will need to dig into the code to know how things work.

Enjoy :-)

## Building
Some instructions to help get started.

### Private dependencies
Install the following dependencies locally with `mvn install`:
* [core](https://github.com/pferrot/core)
* [security](https://github.com/pferrot/security)
* [email-sender](https://github.com/pferrot/email-sender)

### Other dependencies
You will need some extra Maven repositories in order to build Lendity. E.g. configure ~/.m2/settings.xml as follows:
```
<?xml version="1.0" encoding="UTF-8"?>
<settings>
   <profiles>
      <profile>
         <id>standard-extra-repos</id>
         <activation>
            <activeByDefault>true</activeByDefault>
         </activation>
         <repositories>
            <repository>
               <id>jboss</id>
               <url>http://repository.jboss.com</url>
               <releases>
                  <enabled>true</enabled>
               </releases>
               <snapshots>
                  <enabled>false</enabled>
               </snapshots>
            </repository>
         </repositories>
         <pluginRepositories>
            <pluginRepository>
               <id>jboss-snapshot-plugins</id>
               <url>http://snapshots.jboss.org/maven2</url>
               <releases>
                  <enabled>true</enabled>
               </releases>
               <snapshots>
                  <enabled>true</enabled>
               </snapshots>
            </pluginRepository>
            <pluginRepository>
               <id>codehaus-plugins-snapshots</id>
               <url>http://snapshots.repository.codehaus.org/</url>
               <snapshots>
                  <enabled>true</enabled>
               </snapshots>
               <releases>
                  <enabled>false</enabled>
               </releases>
            </pluginRepository>
         </pluginRepositories>
      </profile>
   </profiles>
</settings>
```

**If adding the above repositories is  still not sufficient to build, you will need to configure other repositories and/or manually install
the missing JARs in you local Maven repository as documented [here](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html).**

### DDL
Generate the DDL to create the necessary tables with `mvn process-classes` (see the files `schema-create.ddl` and `schema-drop.ddl` that get generated in the `target` directory).

### Package
Create the WAR file with `mvn package`.
