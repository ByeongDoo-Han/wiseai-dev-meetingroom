FROM eclipse-temurin:17-jre-focal

WORKDIR /app

ENV WIREMOCK_VERSION 3.13.1
RUN wget -O wiremock-standalone.jar "https://repo1.maven.org/maven2/org/wiremock/wiremock-standalone/${WIREMOCK_VERSION}/wiremock-standalone-${WIREMOCK_VERSION}.jar"
COPY mappings/ ./mappings/

EXPOSE 8080

CMD ["java", "-jar", "wiremock-standalone.jar", "--port", "8080", "--verbose", "--root-dir", "/app"]
