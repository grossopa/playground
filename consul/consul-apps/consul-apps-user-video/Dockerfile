FROM openjdk:11
ARG JAR_FILE=target/consul-app-user-video-*.jar
COPY ${JAR_FILE} consul-app-user-video.jar
ENTRYPOINT ["java","-jar","/consul-app-user-video.jar","--environment=k8s"]
