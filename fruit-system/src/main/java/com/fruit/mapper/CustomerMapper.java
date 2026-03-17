package com.fruit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fruit.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
