# Utilizaremos o Multi-stage Build, ao invés de criarmos uma build do projeto (.jar)
# nós delegamos essa tarefa ao docker

FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY . .
# Quando tivermos testes vamos rodá-los
RUN ./mvnw clean package -DskipTests

# Agora criamos a imagem utilizando apenas a JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]