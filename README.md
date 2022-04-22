Ada Loves Lace: A tatting lace patterns creation software 

Ada Aime la Dentelle : un logiciel de création de diagrammes de dentelle

This is Free Software, under [Affero GPL V3 license](license.md)

© 2022 Zala GOUPIL

This software comes with ABSOLUTELY NO GUARANTEE, to the extent permitted by applicable law.


--------------------------------------------------------------------------------------------

- This project uses Java JDK 17 (because it is an LTS JDK) & JavaFX 17.0.2

- Project generated with:


    mvn archetype:generate -DarchetypeGroupId=org.openjfx -DarchetypeArtifactId=javafx-archetype-simple -DarchetypeVersion=0.0.3 -DgroupId=org.alienlabs.adaloveslace -DartifactId=adaloveslace -Dversion=0.0.1 -Djavafx-version=11


- for mvnw (see below) to run OK, you need to set JAVA_HOME to a JDK 17 modified according to:


    https://github.com/jgneff/javafx-graphics


- Maven wrapper generated with:


    mvn -N io.takari:maven:wrapper -Dmaven=3.8.4

Feel free to run the Maven wrapper generation command again if Java version used changes


--------------------------------------------------------------------------------------------

- Build project with:


    https://github.com/jgneff/javafx-graphics


then:

    ./mvnw clean package

or:

    ./mvnw clean jacoco:prepare-agent package jacoco:report pmd:pmd pmd:cpd-check spotbugs:spotbugs

from the project root directory


--------------------------------------------------------------------------------------------
Create a local branch:

    git checkout -b 4-undoredoclear


Then, commit. On first push, create remote counterpart:

    git push -u origin 4-undoredoclear


When creating a branch, if you wish to set tracking information for this branch you can do so with:

    git branch --set-upstream-to=origin/4-undoredoclear 4-undoredoclear
    => Branch '4-undoredoclear' set up to track remote branch '4-undoredoclear' from 'origin'.


--------------------------------------------------------------------------------------------

- To run me within an IDE, add "target/classes/org/alienlabs/adaloveslace/" to the classpath (in order to load the images of the patterns in the toolbox)


- In case of UnsupportedOperationException in Jenkins & testFX:


    https://github.com/TestFX/TestFX/issues/731

    => apt-get install libgtk3.0-cil libgtk3.0-cil-dev libgtk-3-0 libgtk-3-bin libgtk-3-dev 


- When upgrading Jenkins Docker container:


    docker exec -u root -it jenkins-alienlabs bash

    apt-get update

    apt-get install libgtk3.0-cil libgtk3.0-cil-dev libgtk-3-0 libgtk-3-bin libgtk-3-dev vim git xvfb


- Jenkins logs? 


    docker logs -f jenkins-alienlabs


- Jenkins container update:


    docker pull jenkins/jenkins:lts

    docker container rm jenkins-alienlabs.old

    docker container rename jenkins-alienlabs jenkins-alienlabs.old

    docker network create jenkins

    docker run --name jenkins-alienlabs --detach --network jenkins --env DOCKER_HOST=tcp://docker:2376 --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=0 --publish 8780:8780 --publish 50800:50800 --volume /home/change_myuser/docker/jenkins-data:/var/jenkins_home jenkins/jenkins:lts


- Does Jenkins fail with java.lang.IllegalArgumentException: Invalid refspec refs/heads/** Error ?


    https://stackoverflow.com/questions/46684972/jenkins-throws-java-lang-illegalargumentexception-invalid-refspec-refs-heads/58348530#58348530


--------------------------------------------------------------------------------------------
