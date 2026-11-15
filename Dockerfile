FROM eclipse-temurin:27-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/
RUN sed -i 's/\r$//' mvnw && sh mvnw -B -DskipTests package

FROM eclipse-temurin:27-jre
WORKDIR /app
COPY --from=build /app/target/platform-1.0.0.jar app.jar

ENV SERVER_PORT=10000
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "app.jar"]
