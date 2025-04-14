FROM eclipse-temurin:21-jdk
COPY build/libs/server.jar .
EXPOSE 8088
CMD ["java", "-jar", "server.jar"]