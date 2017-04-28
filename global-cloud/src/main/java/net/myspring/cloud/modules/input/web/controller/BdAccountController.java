package net.myspring.cloud.modules.input.web.controller;

import net.myspring.cloud.modules.input.service.BdAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lihx on 2017/4/6.
 */
@RestController
@RequestMapping(value = "input/bdAccount")
public class BdAccountController {
    @Autowired
    private BdAccountService bdAccountService;

    @RequestMapping(value = "getFNumberByName", method = RequestMethod.GET)
    public String getFNumberByName(String bankName){
        return bdAccountService.findFNumberByName(bankName);
    }
}