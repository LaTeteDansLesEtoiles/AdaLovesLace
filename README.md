- Project generated with:

# mvn archetype:generate -DarchetypeGroupId=org.openjfx -DarchetypeArtifactId=javafx-archetype-simple -DarchetypeVersion=0.0.3 -DgroupId=org.alienlabs.adaloveslace -DartifactId=adaloveslace -Dversion=0.0.1 -Djavafx-version=11

- for mvnw (see below) to run OK, you need to set JAVA_HOME to a JDK 17 modified according to:

https://github.com/jgneff/javafx-graphics

- Maven wrapper generated with:

# mvn -N io.takari:maven:wrapper -Dmaven=3.8.4

Feel free to run it again if Java changes

- Build project with:

https://github.com/jgneff/javafx-graphics

then:

# ./mvnw clean package

or:

# ./mvnw clean jacoco:prepare-agent package jacoco:report pmd:pmd pmd:cpd-check spotbugs:spotbugs

from the project root directory

- To run me within an IDE, add "target/classes/org/alienlabs/adaloveslace/" to the classpath (in order to load the images of the patterns in the toolbox)

- In case of UnsupportedOperationException in Jenkins & testFX: https://github.com/TestFX/TestFX/issues/731

=> apt-get install libgtk3.0-cil libgtk3.0-cil-dev libgtk-3-0 libgtk-3-bin libgtk-3-dev 



This is Free Software, under Affero GPL V3 license. © 2022 Zala GOUPIL

This software comes with ABSOLUTELY NO GUARANTEE, to the extent permitted by applicable law.
