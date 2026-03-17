package com.fruit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruit.entity.InboundOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InboundOrderMapper extends BaseMapper<InboundOrder> {
}
