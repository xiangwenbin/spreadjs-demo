package com.zhonghui.spreadjs.bean.command;

import com.grapecity.documents.excel.IWorksheet;
import com.grapecity.documents.excel.Workbook;
import lombok.Data;

/**
 * @author xwb
 */
@Data
public abstract class BaseSheetCommand extends  BaseCommand{

    private String sheetName;

    public IWorksheet getWorksheet(Workbook workbook){
        return workbook.getWorksheets().get(this.getSheetName());
    }

}