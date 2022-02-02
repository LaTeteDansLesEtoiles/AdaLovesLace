- Project generated with:

# mvn archetype:generate -DarchetypeGroupId=org.openjfx -DarchetypeArtifactId=javafx-archetype-simple -DarchetypeVersion=0.0.3 -DgroupId=org.alienlabs.adaloveslace -DartifactId=adaloveslace -Dversion=0.0.1 -Djavafx-version=11

- Maven wrapper generated with:

# mvn -N io.takari:maven:wrapper -Dmaven=3.8.4

Feel free to run it again if Java changes

./mvnw -version

- Build project with:

https://github.com/jgneff/javafx-graphics

# ./mvnw clean install

./mvnw clean install -Dmodule-path=/usr/lib/jvm/java-17-openjdk-amd64/lib/javafx.base.jar;/usr/lib/jvm/java-17-openjdk-amd64/lib/javafx.controls.jar;/usr/lib/jvm/java-17-openjdk-amd64/lib/javafx.graphics.jar -Dadd-modules javafx.base,javafx.controls,javafx.graphics


from the project root directory

- for mvnw to run OK, you need to set JAVA_HOME to a JDK 17 modified according to:
  
https://github.com/jgneff/javafx-graphics

- To run me within an IDE, add "target/classes/org/alienlabs/adaloveslace/" to the classpath (in order to load the images of the patterns in the toolbox)
