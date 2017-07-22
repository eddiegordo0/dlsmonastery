package net.myspring.tool.modules.future.service;

import net.myspring.tool.common.dataSource.annotation.FutureDataSource;
import net.myspring.tool.modules.future.repository.FutureDemoPhoneRepository;
import net.myspring.tool.modules.vivo.domain.SProductItemLend;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@FutureDataSource
public class FutureDemoPhoneService {
    @Autowired
    private FutureDemoPhoneRepository futureDemoPhoneRepository;


    public List<SProductItemLend> getDemoPhonesData(String date){
        if (StringUtils.isBlank(date)){
            date = LocalDateUtils.format(LocalDate.now());
        }
        String dateStart = date;
        String dateEnd = LocalDateUtils.format(LocalDateUtils.parse(date).plusDays(1));
        return futureDemoPhoneRepository.findDemoPhones(dateStart,dateEnd);
    }


}
