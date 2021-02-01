package com.zhonghui.spreadjs.vo;

import com.grapecity.documents.excel.Workbook;
import lombok.Data;

/**
 * @author xwb
 */
@Data
public  class CommandFrameBodyVo {

    private String spreadCommand;

    private String serverCommand;

    private String cmd;

    private String sheetName;

    private String docId;


}