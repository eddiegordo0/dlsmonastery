package net.myspring.hr.modules.hr.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import net.myspring.hr.modules.hr.mapper.OfficeChangeMapper;

@Service
public class OfficeChangeService {

    @Autowired
    private OfficeChangeMapper officeChangeMapper;

}
