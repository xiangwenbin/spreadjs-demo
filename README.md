# 项目描述



## mac 修改src/main/resources/config/application.properties logging.path

## java启动 
* jvm 启动参数-Dspring.profiles.active=dev


## 前端脚本
* cd src/main/resources/public
* npm install
* npm run build

## 前端说明
* 测试地址对应的前端脚本目录:src/main/resources/public/src/pages/index App.vue

## 问题描述，禁用 菜单项 CellType,CellsInsert,InsertShape ,在初始化配置config里，单元格类型这个菜单禁灰效果是延后的
* 相关代码  src/main/resources/public/src/pages/index App.vue  line:89->94 117->118
* 测试地址 http://localhost:8080/index/1111/5


