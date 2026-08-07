# 基础镜像
FROM eclipse-temurin:17-jdk

# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone

# 环境变量（按照部署文档保留占位符，部署时由 .env 覆盖）
ENV OSS_ACCESS_KEY_ID=xxxxxxxxxxxxxxxxxxxx
ENV OSS_ACCESS_KEY_SECRET=xxxxxxxxxxxxxxxxxxxxxx
ENV OPENAI_API_KEY=xxxxxxxxxxxxxxxxxxxxxx

# 拷贝jar包
COPY xhzb-admin.jar /app.jar

# 暴露端口
EXPOSE 9000

# 入口
ENTRYPOINT ["java", "-jar", "/app.jar"]
