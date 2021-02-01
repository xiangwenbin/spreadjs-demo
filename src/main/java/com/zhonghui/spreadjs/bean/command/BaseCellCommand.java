package com.zhonghui.spreadjs.bean.command;

import lombok.Data;

/**
 * @author xwb
 */
@Data
public abstract class BaseCellCommand extends  BaseSheetCommand {

    private String range;

    private int row;

    private int column;

}
