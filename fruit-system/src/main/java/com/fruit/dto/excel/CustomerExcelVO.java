package com.fruit.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
@ContentRowHeight(18)
@ColumnWidth(20)
public class CustomerExcelVO {

    @ExcelProperty(value = "客户姓名", index = 0)
    private String name;

    @ExcelProperty(value = "电话", index = 1)
    private String phone;

    @ExcelProperty(value = "地址", index = 2)
    @ColumnWidth(30)
    private String address;

    @ExcelProperty(value = "分类", index = 3)
    private String category;

    @ExcelProperty(value = "备注", index = 4)
    @ColumnWidth(30)
    private String remark;

    @ExcelProperty(value = "创建时间", index = 5)
    @ColumnWidth(25)
    private String createTime;
}
