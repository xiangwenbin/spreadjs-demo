package com.zhonghui.spreadjs.util;

import java.io.File;

public class CommonUtil {
    public static String joinFilePath(String... dirs){
        String path="";
        for(int i=0;i<dirs.length;i++){
            path+=dirs[i]+ ((i==dirs.length-1)?"":File.separator);
        }
        return path;
    }
}