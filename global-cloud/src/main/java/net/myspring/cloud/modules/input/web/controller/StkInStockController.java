package net.myspring.cloud.modules.input.web.controller;

import net.myspring.cloud.common.enums.KingdeeNameEnum;
import net.myspring.cloud.common.utils.RequestUtils;
import net.myspring.cloud.modules.input.dto.KingdeeSynDto;
import net.myspring.cloud.modules.input.service.StkInStockService;
import net.myspring.cloud.modules.input.web.form.StkInStockForm;
import net.myspring.cloud.modules.sys.domain.AccountKingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeSyn;
import net.myspring.cloud.modules.sys.dto.ProductDto;
import net.myspring.cloud.modules.sys.service.AccountKingdeeBookService;
import net.myspring.cloud.modules.sys.service.KingdeeBookService;
import net.myspring.cloud.modules.sys.service.KingdeeSynService;
import net.myspring.cloud.modules.sys.service.ProductService;
import net.myspring.common.exception.ServiceException;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.util.mapper.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 采购入库单
 * Created by lihx on 2017/6/14.
 */
@RestController
@RequestMapping(value = "input/stkInStock")
public class StkInStockController {
    @Autowired
    private StkInStockService stkInStockService;
    @Autowired
    private KingdeeBookService kingdeeBookService;
    @Autowired
    private ProductService productService;
    @Autowired
    private AccountKingdeeBookService accountKingdeeBookService;
    @Autowired
    private KingdeeSynService kingdeeSynService;

    @RequestMapping(value = "form")
    public StkInStockForm form (StkInStockForm stkInStockForm) {
        AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountIdAndCompanyName(RequestUtils.getAccountId(), RequestUtils.getCompanyName());
        if (accountKingdeeBook != null) {
            KingdeeBook kingdeeBook = kingdeeBookService.findOne(accountKingdeeBook.getKingdeeBookId());
            if (!KingdeeNameEnum.JXDJ.name().equals(kingdeeBook.getName())) {
                String returnOutId = productService.findReturnOutIdByCompanyName();
                ProductDto productDto = productService.findByOutIdAndCompanyName(returnOutId);
                String type = productDto == null ? "" : productDto.getName();
                stkInStockForm.getTypeList().add(type);
            }
            return stkInStockService.getForm(stkInStockForm,kingdeeBook);
        }else {
            throw new ServiceException("您没有金蝶账号，不能开单");
        }
    }

    @RequestMapping(value = "save")
    public RestResponse save(StkInStockForm stkInStockForm) {
        RestResponse restResponse;
        StringBuilder message = new StringBuilder();
        try {
            AccountKingdeeBook accountKingdeeBook = accountKingdeeBookService.findByAccountIdAndCompanyName(RequestUtils.getAccountId(),RequestUtils.getCompanyName());
            if (accountKingdeeBook != null) {
                KingdeeBook kingdeeBook = kingdeeBookService.findOne(accountKingdeeBook.getKingdeeBookId());
                List<KingdeeSynDto> kingdeeSynDtoList = stkInStockService.save(stkInStockForm, kingdeeBook, accountKingdeeBook);
                kingdeeSynService.save(BeanUtil.map(kingdeeSynDtoList, KingdeeSyn.class));
                for (KingdeeSynDto kingdeeSynDto : kingdeeSynDtoList) {
                    if (kingdeeSynDto.getSuccess()) {
                        message.append(kingdeeSynDto.getBillNo()+",");
                    }
                }
                restResponse = new RestResponse("采购入库成功：" + message, null, true);
            }else {
                restResponse = new RestResponse("您没有金蝶账号，不能开单", null, false);
            }
            return restResponse;
        }catch (Exception e){
            return new RestResponse(e.getMessage(), ResponseCodeEnum.invalid.name(), false);
        }
    }
}
