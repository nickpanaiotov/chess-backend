FROM openjdk:latest
ARG JAR_FILE=api/target/*.jar
COPY ${JAR_FILE} app.jar
COPY api/src/main/resources/engines/linux /engines
RUN chmod -R +x /engines
ENTRYPOINT ["java","-jar","/app.jar"]
