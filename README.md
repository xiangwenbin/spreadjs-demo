# 项目描述

## jvm 启动参数-Dspring.profiles.active=dev

## 当前版本1.0.0

## 使用versions-maven-plugin 插件管理整个项目版本 
*  生成镜像
* 1. mvn versions:set -DnewVersion=1.0.0-SNAPSHOT
* 2. mvn  clean install -U  -D maven.test.skip=true build-helper:parse-version dockerfile:build dockerfile:push


## delete tag
* 1. git tag -d v1.0.0
* 2. git push origin :refs/tags/v1.0.0

## add tag
* 1. git tag  v1.0.0 -am 'version:1.0.0'
* 2. git push origin v1.0.0

## add all tag
* git push --tags
