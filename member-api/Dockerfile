FROM openjdk:11
CMD ["./mvnw", "clean", "package"]
ARG JAR_FILE_PATH=build/libs/foryoufamily-*-SNAPSHOT.jar
COPY ${JAR_FILE_PATH} foryoufamily.jar
ENTRYPOINT ["java", "-jar", "foryoufamily.jar"]