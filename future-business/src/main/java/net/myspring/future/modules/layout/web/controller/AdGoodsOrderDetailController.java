package net.myspring.future.modules.layout.web.controller;

import net.myspring.future.modules.layout.service.AdGoodsOrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "crm/adGoodsOrderDetail")
public class AdGoodsOrderDetailController {

    @Autowired
    private AdGoodsOrderDetailService adGoodsOrderDetailService;

}