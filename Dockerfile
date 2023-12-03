FROM amazoncorretto:17.0.9

WORKDIR /app

COPY target/webstore-service-*.jar /app/webstore-service.jar

CMD ["java", "-jar", "webstore-service.jar"]