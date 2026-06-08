# ==================== 构建阶段 ====================
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /build

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 构建项目（跳过测试）
RUN mvn clean package -DskipTests

# ==================== 运行阶段 ====================
FROM openjdk:8-jdk-alpine

WORKDIR /app

# 从构建阶段复制 jar 包
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]