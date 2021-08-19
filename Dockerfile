FROM adoptopenjdk/openjdk11:alpine

RUN addgroup -S app && adduser -S app -G app
USER app:app

COPY target/*.jar metrics-manager.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/metrics-manager.jar"]
