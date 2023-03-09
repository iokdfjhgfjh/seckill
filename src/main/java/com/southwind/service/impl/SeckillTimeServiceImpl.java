package com.southwind.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.southwind.entity.Orders;
import com.southwind.entity.SeckillTime;
import com.southwind.mapper.OrdersMapper;
import com.southwind.mapper.SeckillTimeMapper;
import com.southwind.service.SeckillTimeService;
import org.springframework.stereotype.Service;

@Service
public class SeckillTimeServiceImpl extends ServiceImpl<SeckillTimeMapper, SeckillTime> implements SeckillTimeService {
}
