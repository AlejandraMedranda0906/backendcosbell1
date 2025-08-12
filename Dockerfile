FROM openjdk:17-jdk-slim
WORKDIR /app

# Copia sólo lo imprescindible para construir
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Genera el .jar
RUN chmod +x mvnw \
 && ./mvnw clean package -DskipTests \
 && cp target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]