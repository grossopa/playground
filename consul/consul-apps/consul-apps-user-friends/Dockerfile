FROM openjdk:11
ARG JAR_FILE=target/consul-apps-user-friends-*.jar
COPY ${JAR_FILE} consul-apps-user-friends.jar
ENTRYPOINT ["java","-jar","/consul-apps-user-friends.jar","--environment=k8s"]
