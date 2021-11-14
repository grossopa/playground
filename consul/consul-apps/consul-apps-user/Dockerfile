FROM openjdk:11
ARG JAR_FILE=target/consul-apps-user-*.jar
COPY ${JAR_FILE} consul-apps-user.jar
ENTRYPOINT ["java","-jar","/consul-apps-user.jar","--environment=k8s"]
