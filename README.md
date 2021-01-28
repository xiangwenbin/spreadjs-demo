# 项目描述

## mac 修改src/main/resources/config/application.properties logging.path

## java启动 

* jvm 启动参数-Dspring.profiles.active=dev


## 前端脚本

* cd src/main/resources/public
* npm install
* npm run test
* 测试地址 http://localhost:8080/index/1111/5

## 前端说明
* 测试地址对应的前端脚本目录:src/main/resources/public/src/pages/index App.vue
* 编辑器监听事件 onCommandExecute websocket订阅消息执行 executeCommand
