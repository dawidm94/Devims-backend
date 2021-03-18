FROM openjdk:11-jdk-alpine
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8882,server=y,suspend=n
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]