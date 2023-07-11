# 使用支持Maven打包的Java 11环境的基础镜像
FROM maven:3.8.4-openjdk-11 AS build

# 设置工作目录
WORKDIR /app

# 拉取项目代码
COPY src/ ./src/
COPY pom.xml .

# 使用Maven进行构建和打包
RUN mvn clean package

# 使用一个仅包含JRE 17的运行时镜像
FROM openjdk:11-jre-slim

# 设置工作目录
WORKDIR /app

# 从构建镜像中复制打包好的jar文件到运行镜像中
COPY --from=build /app/target/tvbox-mv-0.0.1-SNAPSHOT.jar /app/tvbox-mv-0.0.1-SNAPSHOT.jar

# 暴露端口
EXPOSE 7777

# 运行应用程序
CMD ["java", "-jar", "tvbox-mv-0.0.1-SNAPSHOT.jar"]