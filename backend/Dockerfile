FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S trace360 && adduser -S trace360 -G trace360
COPY --from=builder /app/target/*.jar app.jar
RUN chown trace360:trace360 app.jar
USER trace360
EXPOSE 10000
ENTRYPOINT ["java", "-Xms128m", "-Xmx384m", "-XX:+UseSerialGC", "-jar", "app.jar"]
