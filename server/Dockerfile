FROM maven:3-eclipse-temurin-21

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/gamesUP-0.0.1-SNAPSHOT.jar"]
