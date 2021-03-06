package net.myspring.cloud.modules.input.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.common.enums.KingdeeFormIdEnum;
import net.myspring.cloud.common.enums.KingdeeNameEnum;
import net.myspring.cloud.common.enums.SalOutStockBillTypeEnum;
import net.myspring.cloud.modules.input.repository.SalOutStockRepository;
import net.myspring.cloud.modules.kingdee.repository.BdStockRepository;
import net.myspring.common.utils.HandsontableUtils;
import net.myspring.cloud.modules.input.dto.KingdeeSynExtendDto;
import net.myspring.cloud.modules.input.dto.SalOutStockDto;
import net.myspring.cloud.modules.input.dto.SalOutStockFEntityDto;
import net.myspring.cloud.modules.input.manager.KingdeeManager;
import net.myspring.cloud.modules.input.web.form.SalStockForm;
import net.myspring.cloud.modules.kingdee.domain.BdCustomer;
import net.myspring.cloud.modules.kingdee.domain.BdDepartment;
import net.myspring.cloud.modules.kingdee.domain.BdMaterial;
import net.myspring.cloud.modules.kingdee.repository.BdCustomerRepository;
import net.myspring.cloud.modules.kingdee.repository.BdDepartmentRepository;
import net.myspring.cloud.modules.kingdee.repository.BdMaterialRepository;
import net.myspring.cloud.modules.sys.domain.AccountKingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.exception.ServiceException;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.json.ObjectMapperUtils;
import net.myspring.util.text.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 销售出库单
 * Created by liuj on 2017/5/11.
 */
@Service
@KingdeeDataSource
@Transactional(readOnly = true)
public class SalOutStockService {
    @Autowired
    private KingdeeManager kingdeeManager;
    @Autowired
    private BdCustomerRepository bdCustomerRepository;
    @Autowired
    private BdDepartmentRepository bdDepartmentRepository;
    @Autowired
    private BdMaterialRepository bdMaterialRepository;
    @Autowired
    private BdStockRepository bdStockRepository;
    @Autowired
    private SalOutStockRepository salOutStockRepository;

    private KingdeeSynExtendDto save(SalOutStockDto salOutStockDto,KingdeeBook kingdeeBook) {
        KingdeeSynExtendDto kingdeeSynExtendDto = new KingdeeSynExtendDto(
                salOutStockDto.getExtendId(),
                salOutStockDto.getExtendType(),
                KingdeeFormIdEnum.SAL_OUTSTOCK.name(),
                salOutStockDto.getJson(),
                kingdeeBook,
                KingdeeFormIdEnum.AR_receivable.name());
        kingdeeSynExtendDto = kingdeeManager.save(kingdeeSynExtendDto);
        if (!kingdeeSynExtendDto.getSuccess()){
            throw new ServiceException("销售出库单："+kingdeeSynExtendDto.getResult());
        }
        return kingdeeSynExtendDto;
    }

    @Transactional
    public List<KingdeeSynExtendDto> save (List<SalOutStockDto> salOutStockDtoList, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook){
        List<KingdeeSynExtendDto> kingdeeSynExtendDtoList = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(salOutStockDtoList)) {
            Boolean isLogin = kingdeeManager.login(kingdeeBook.getKingdeePostUrl(),kingdeeBook.getKingdeeDbid(),accountKingdeeBook.getUsername(),accountKingdeeBook.getPassword());
            if(isLogin) {
                for (SalOutStockDto salOutStockDto : salOutStockDtoList) {
                    if(SalOutStockBillTypeEnum.标准销售出库单.name().equals(salOutStockDto.getBillType())) {
                        salOutStockDto.setFBillTypeIdNumber(SalOutStockBillTypeEnum.标准销售出库单.getFNumber());
                    }else if (SalOutStockBillTypeEnum.现销出库单.name().equals(salOutStockDto.getBillType())){
                        salOutStockDto.setFBillTypeIdNumber(SalOutStockBillTypeEnum.现销出库单.getFNumber());
                    }
                    KingdeeSynExtendDto kingdeeSynExtendDto = save(salOutStockDto,kingdeeBook);
                    kingdeeSynExtendDtoList.add(kingdeeSynExtendDto);
                }
            }else{
                throw new ServiceException("登入金蝶系统失败，请检查您的账户密码是否正确");
            }
        }
        return kingdeeSynExtendDtoList;
    }

    @Transactional
    public List<KingdeeSynExtendDto> save (SalStockForm salStockForm, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook) {
        String stockNumber = salStockForm.getStockNumber();
        LocalDate date = salStockForm.getBillDate();
        String json = HtmlUtils.htmlUnescape(salStockForm.getJson());
        List<List<Object>> data = ObjectMapperUtils.readValue(json, ArrayList.class);
        List<String> customerNameList = Lists.newArrayList();
        for (List<Object> row : data){
            customerNameList.add(HandsontableUtils.getValue(row,1));
        }
        Map<String, String> customerNumMap = Maps.newHashMap();
        Map<String, String> customerDepartmentMap = Maps.newHashMap();

        List<String> departmentIdList = Lists.newArrayList();
        List<BdCustomer> bdCustomerList = bdCustomerRepository.findByNameList(customerNameList);
        for (BdCustomer bdCustomer : bdCustomerList) {
            customerNumMap.put(bdCustomer.getFName(), bdCustomer.getFNumber());
            customerDepartmentMap.put(bdCustomer.getFName(), bdCustomer.getFSalDeptId());
            departmentIdList.add(bdCustomer.getFSalDeptId());
        }
        List<BdDepartment> bdDepartmentList = bdDepartmentRepository.findByIdList(departmentIdList);
        Map<String,BdDepartment> bdDepartmentMap = bdDepartmentList.stream().collect(Collectors.toMap(BdDepartment::getFDeptId, bdDepartment -> bdDepartment));
        Map<String, SalOutStockDto> billMap = Maps.newLinkedHashMap();
        for (List<Object> row : data) {
            String materialNumber = HandsontableUtils.getValue(row,0);
            String customerName = HandsontableUtils.getValue(row,1);
            String priceStr = HandsontableUtils.getValue(row, 3);
            BigDecimal price = StringUtils.isEmpty(priceStr) ? BigDecimal.ZERO : new BigDecimal(priceStr);
            Integer qty = Integer.valueOf(HandsontableUtils.getValue(row,4));
            String remarks = HandsontableUtils.getValue(row,5);
            String billType = HandsontableUtils.getValue(row,6);

            SalOutStockFEntityDto salOutStockFEntityDto = new SalOutStockFEntityDto();
            salOutStockFEntityDto.setStockNumber(stockNumber);
            salOutStockFEntityDto.setMaterialNumber(materialNumber);
            salOutStockFEntityDto.setPrice(price);
            salOutStockFEntityDto.setQty(qty);
            salOutStockFEntityDto.setEntryNote(remarks);

            String billKey = customerNumMap.get(customerName) + CharConstant.COMMA + billType;
            if (!billMap.containsKey(billKey)) {
                SalOutStockDto salOutStockDto = new SalOutStockDto();
                salOutStockDto.setCreator(accountKingdeeBook.getUsername());
                salOutStockDto.setDate(date);
                salOutStockDto.setDepartmentNumber(bdDepartmentMap.get(customerDepartmentMap.get(customerName)).getFNumber());
                salOutStockDto.setBillType(billType);
                salOutStockDto.setCustomerNumber(customerNumMap.get(customerName));
                salOutStockDto.setNote(remarks);
                billMap.put(billKey, salOutStockDto);
            }
            billMap.get(billKey).getSalOutStockFEntityDtoList().add(salOutStockFEntityDto);
        }

        List<SalOutStockDto> salOutStockDtoList = Lists.newArrayList(billMap.values());
        return save(salOutStockDtoList,kingdeeBook,accountKingdeeBook);
    }

    @Transactional
    public List<KingdeeSynExtendDto> saveForXSCKD (List<SalOutStockDto> salOutStockDtoList,KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook){
        List<String> customerNumberList = Lists.newArrayList();
        for (SalOutStockDto salOutStockDto  : salOutStockDtoList){
            customerNumberList.add(salOutStockDto.getCustomerNumber());
        }
        Map<String, String> customerDepartmentMap = Maps.newHashMap();
        List<String> departmentIdList = Lists.newArrayList();
        List<BdCustomer> bdCustomerList = bdCustomerRepository.findByNumberList(customerNumberList);
        for (BdCustomer bdCustomer : bdCustomerList) {
            customerDepartmentMap.put(bdCustomer.getFNumber(), bdCustomer.getFSalDeptId());
            departmentIdList.add(bdCustomer.getFSalDeptId());
        }
        List<BdDepartment> bdDepartmentList = bdDepartmentRepository.findByIdList(departmentIdList);
        Map<String,BdDepartment> bdDepartmentMap = bdDepartmentList.stream().collect(Collectors.toMap(BdDepartment::getFDeptId, bdDepartment -> bdDepartment));
        for (SalOutStockDto salOutStockDto : salOutStockDtoList) {
            salOutStockDto.setCreator(accountKingdeeBook.getUsername());
            salOutStockDto.setBillType(SalOutStockBillTypeEnum.标准销售出库单.name());
            salOutStockDto.setDepartmentNumber(bdDepartmentMap.get(customerDepartmentMap.get(salOutStockDto.getCustomerNumber())).getFNumber());
        }
        return save(salOutStockDtoList,kingdeeBook,accountKingdeeBook);
    }

    public SalStockForm getForm(KingdeeBook kingdeeBook){
        SalStockForm salStockForm = new SalStockForm();
        Map<String,Object> map = Maps.newHashMap();
        if (KingdeeNameEnum.WZOPPO.name().equals(kingdeeBook.getName())){
            map.put("outStockBillTypeEnums",SalOutStockBillTypeEnum.values());
        }else{
            map.put("outStockBillTypeEnums",SalOutStockBillTypeEnum.标准销售出库单);
        }
        map.put("bdCustomerNameList",bdCustomerRepository.findAll().stream().map(BdCustomer::getFName).collect(Collectors.toList()));
        map.put("bdMaterialNameList",bdMaterialRepository.findAll().stream().map(BdMaterial::getFName).collect(Collectors.toList()));
        map.put("stockList",bdStockRepository.findAll());
        salStockForm.setExtra(map);
        return salStockForm;
    }

    public List<String> findNoPushDown(){
        return salOutStockRepository.findNoPushDown();
    }

}
