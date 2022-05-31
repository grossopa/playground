FROM openjdk:11
ARG JAR_FILE=target/service-mesh-example-modern-user-*.jar
COPY ${JAR_FILE} service-mesh-example-modern-user.jar
ENTRYPOINT ["java","-jar","/service-mesh-example-modern-user.jar"]
