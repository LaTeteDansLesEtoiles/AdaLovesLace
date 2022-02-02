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

# ./mvnw clean install

from the project root directory

- To run me within an IDE, add "target/classes/org/alienlabs/adaloveslace/" to the classpath (in order to load the images of the patterns in the toolbox)
