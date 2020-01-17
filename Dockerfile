FROM openjdk:8-alpine
COPY target/AuthorizerSecurity-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "AuthorizerSecurity-0.0.1-SNAPSHOT.jar"]