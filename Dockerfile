FROM java:8
ENV HTTP_PORT 8080
ENV TZ Asia/Shanghai
ENV JAVA_OPTIONS ''
COPY target/srpeadjs-demo-*-SNAPSHOT.jar /urs/local/www/srpeadjs-demo.jar
WORKDIR /urs/local/www/
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir -p /home/temp/
VOLUME [ "/home/logs/spreadjs-demo"]
EXPOSE 8080
#容器启动时候启动命令
ENTRYPOINT java $JAVA_OPTIONS -Dspring.profiles.active=$PROFILE_ACTIVE -jar srpeadjs-demo.jar 
LABEL version="1.0.0" author="wbxiang@zhcpa.cn"
