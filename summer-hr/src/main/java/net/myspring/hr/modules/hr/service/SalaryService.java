package net.myspring.hr.modules.hr.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import net.myspring.hr.modules.hr.mapper.SalaryMapper;

@Service
public class SalaryService {

    @Autowired
    private SalaryMapper salaryMapper;

}
