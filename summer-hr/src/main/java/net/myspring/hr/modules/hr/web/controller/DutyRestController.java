package net.myspring.hr.modules.hr.web;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import net.myspring.hr.modules.hr.service.DutyRestService;

@Controller
@RequestMapping(value = "hr/dutyRest")
public class DutyRestController {

    @Autowired
    private DutyRestService dutyRestService;

}
