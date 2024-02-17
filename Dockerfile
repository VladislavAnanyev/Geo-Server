FROM openjdk:17-oracle
ENV TZ="Europe/Moscow"
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY CA.pem /root/.postgresql/root.crt
RUN chmod 0600 ~/.postgresql/root.crt
EXPOSE 80
ENTRYPOINT ["java", "-jar", "/app.jar"]