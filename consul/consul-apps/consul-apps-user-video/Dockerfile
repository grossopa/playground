FROM openjdk:11
ARG JAR_FILE=target/consul-apps-user-video-*.jar
COPY ${JAR_FILE} consul-apps-user-video.jar
ENTRYPOINT ["java","-jar","/consul-apps-user-video.jar","--environment=k8s"]
