package net.myspring.future.modules.crm.service;

import net.myspring.future.modules.crm.mapper.ProductMonthPriceDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductMonthPriceDetailService {

    @Autowired
    private ProductMonthPriceDetailMapper productMonthPriceDetailMapper;

}