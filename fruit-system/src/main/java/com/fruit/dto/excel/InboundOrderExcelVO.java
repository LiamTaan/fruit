package com.fruit.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.math.BigDecimal;

@Data
@HeadRowHeight(20)
@ContentRowHeight(18)
@ColumnWidth(18)
public class InboundOrderExcelVO {

    @ExcelProperty(value = "入库单号", index = 0)
    @ColumnWidth(22)
    private String orderNo;

    @ExcelProperty(value = "果品名称", index = 1)
    private String productName;

    @ExcelProperty(value = "品种", index = 2)
    private String variety;

    @ExcelProperty(value = "等级", index = 3)
    private String grade;

    @ExcelProperty(value = "重量(kg)", index = 4)
    private BigDecimal weight;

    @ExcelProperty(value = "单价(元)", index = 5)
    private BigDecimal unitPrice;

    @ExcelProperty(value = "总金额(元)", index = 6)
    private BigDecimal totalAmount;

    @ExcelProperty(value = "产地", index = 7)
    private String origin;

    @ExcelProperty(value = "备注", index = 8)
    @ColumnWidth(25)
    private String remark;

    @ExcelProperty(value = "入库时间", index = 9)
    @ColumnWidth(22)
    private String createTime;
}
