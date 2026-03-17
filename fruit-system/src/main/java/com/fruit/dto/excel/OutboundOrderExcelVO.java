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
public class OutboundOrderExcelVO {

    @ExcelProperty(value = "出库单号", index = 0)
    @ColumnWidth(22)
    private String orderNo;

    @ExcelProperty(value = "客户姓名", index = 1)
    private String customerName;

    @ExcelProperty(value = "果品名称", index = 2)
    private String productName;

    @ExcelProperty(value = "品种", index = 3)
    private String variety;

    @ExcelProperty(value = "等级", index = 4)
    private String grade;

    @ExcelProperty(value = "重量(kg)", index = 5)
    private BigDecimal weight;

    @ExcelProperty(value = "单价(元)", index = 6)
    private BigDecimal unitPrice;

    @ExcelProperty(value = "总金额(元)", index = 7)
    private BigDecimal totalAmount;

    @ExcelProperty(value = "成本金额(元)", index = 8)
    private BigDecimal costAmount;

    @ExcelProperty(value = "利润(元)", index = 9)
    private BigDecimal profit;

    @ExcelProperty(value = "收款方式", index = 10)
    private String paymentType;

    @ExcelProperty(value = "备注", index = 11)
    @ColumnWidth(25)
    private String remark;

    @ExcelProperty(value = "出库时间", index = 12)
    @ColumnWidth(22)
    private String createTime;
}
