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
@ColumnWidth(20)
public class StatisticsExcelVO {

    @ExcelProperty(value = "统计类型", index = 0)
    private String type;

    @ExcelProperty(value = "入库金额(元)", index = 1)
    private BigDecimal inboundAmount;

    @ExcelProperty(value = "出库金额(元)", index = 2)
    private BigDecimal outboundAmount;

    @ExcelProperty(value = "利润(元)", index = 3)
    private BigDecimal profit;

    @ExcelProperty(value = "待收欠款(元)", index = 4)
    private BigDecimal pendingDebt;

    @ExcelProperty(value = "统计时间", index = 5)
    @ColumnWidth(25)
    private String statisticsTime;
}
