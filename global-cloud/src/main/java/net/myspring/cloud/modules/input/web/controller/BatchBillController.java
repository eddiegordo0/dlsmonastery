package net.myspring.cloud.modules.input.web.controller;

import net.myspring.cloud.modules.input.service.BatchBillService;
import net.myspring.cloud.modules.input.web.query.BatchBillQuery;
import net.myspring.common.response.RestResponse;
import net.myspring.util.json.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lihx on 2017/4/25.
 */
@RestController
@RequestMapping(value = "input/batchBill")
public class BatchBillController {
    @Autowired
    private BatchBillService batchBillService;

    @RequestMapping(value = "form")
    public BatchBillQuery form (BatchBillQuery batchBillQuery) {
        batchBillQuery = batchBillService.getFormProperty(batchBillQuery);
        return batchBillQuery;
    }

    @RequestMapping(value = "save")
    public RestResponse save(String data, String storeCode, String billDateBTW) {
        data = HtmlUtils.htmlUnescape(data);
        List<List<Object>> datas = ObjectMapperUtils.readValue(data, ArrayList.class);
        List<String> codeList = batchBillService.save(datas, storeCode, LocalDate.parse(billDateBTW));
        return new RestResponse("批量开单成功：" + codeList,null,true);
    }
}
