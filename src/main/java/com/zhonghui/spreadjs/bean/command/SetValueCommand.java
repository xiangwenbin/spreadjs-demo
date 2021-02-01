package com.zhonghui.spreadjs.bean.command;

import com.grapecity.documents.excel.Workbook;
import lombok.Data;

/**
 * @author xwb
 */
@Data
public class SetValueCommand extends BaseCellCommand {

    private Object newValue;

    @Override
    public void execute(Workbook workbook) {
        workbook.getWorksheets().get(this.getSheetName()).getRange(this.getRow(), this.getColumn()).setValue(this.getNewValue());
    }
}
