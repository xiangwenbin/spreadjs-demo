package com.zhonghui.spreadjs.bean.command;

import com.grapecity.documents.excel.Workbook;
import lombok.Data;

/**
 * @author xwb
 */
@Data
public abstract class BaseCommand {

    private String spreadCommand;

    private String serverCommand;

    private String cmd;

    private String docId;

    /**
     * gcexcel 执行命令接口
     * @param workbook
     */
    public abstract void execute(Workbook workbook);

}