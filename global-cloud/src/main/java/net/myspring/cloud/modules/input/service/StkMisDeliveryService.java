package net.myspring.cloud.modules.input.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.common.enums.*;
import net.myspring.cloud.modules.kingdee.repository.BdDepartmentRepository;
import net.myspring.common.utils.HandsontableUtils;
import net.myspring.cloud.modules.input.dto.KingdeeSynDto;
import net.myspring.cloud.modules.input.dto.StkMisDeliveryDto;
import net.myspring.cloud.modules.input.dto.StkMisDeliveryFEntityDto;
import net.myspring.cloud.modules.input.manager.KingdeeManager;
import net.myspring.cloud.modules.input.web.form.StkMisDeliveryForm;
import net.myspring.cloud.modules.kingdee.domain.BdMaterial;
import net.myspring.cloud.modules.kingdee.domain.BdStock;
import net.myspring.cloud.modules.kingdee.repository.BdMaterialRepository;
import net.myspring.cloud.modules.kingdee.repository.BdStockRepository;
import net.myspring.cloud.modules.sys.domain.AccountKingdeeBook;
import net.myspring.cloud.modules.sys.domain.KingdeeBook;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.exception.ServiceException;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.json.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *其他出库单
 * Created by lihx on 2017/5/17.
 */
@Service
@KingdeeDataSource
@Transactional(readOnly = true)
public class StkMisDeliveryService {
    @Autowired
    private KingdeeManager kingdeeManager;
    @Autowired
    private BdStockRepository bdStockRepository;
    @Autowired
    private BdMaterialRepository bdMaterialRepository;
    @Autowired
    private BdDepartmentRepository bdDepartmentRepository;

    private KingdeeSynDto save(StkMisDeliveryDto stkMisDeliveryDto,KingdeeBook kingdeeBook){
        KingdeeSynDto kingdeeSynDto = new KingdeeSynDto(
                stkMisDeliveryDto.getExtendId(),
                stkMisDeliveryDto.getExtendType(),
                KingdeeFormIdEnum.STK_MisDelivery.name(),
                stkMisDeliveryDto.getJson(),
                kingdeeBook);
        kingdeeSynDto = kingdeeManager.save(kingdeeSynDto);
        if (!kingdeeSynDto.getSuccess()){
            throw new ServiceException("其他出库单失败："+kingdeeSynDto.getResult());
        }
        return kingdeeSynDto;
    }

    @Transactional
    public List<KingdeeSynDto> save(List<StkMisDeliveryDto> stkMisDeliveryDtoList, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook){
        List<KingdeeSynDto> kingdeeSynDtoList = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(stkMisDeliveryDtoList)) {
            Boolean isLogin = kingdeeManager.login(kingdeeBook.getKingdeePostUrl(),kingdeeBook.getKingdeeDbid(),accountKingdeeBook.getUsername(),accountKingdeeBook.getPassword());
            if(isLogin) {
                for (StkMisDeliveryDto misDelivery : stkMisDeliveryDtoList) {
                    //库存方向
                    if (StkMisDeliveryTypeEnum.出库.name().equals(misDelivery.getMisDeliveryType())) {
                        misDelivery.setFStockDirect(StkMisDeliveryTypeEnum.出库.getFNumber());
                    } else if(StkMisDeliveryTypeEnum.退货.name().equals(misDelivery.getMisDeliveryType())){
                        misDelivery.setFStockDirect(StkMisDeliveryTypeEnum.退货.getFNumber());
                    }
                    KingdeeSynDto kingdeeSynDto = save(misDelivery, kingdeeBook);
                    kingdeeSynDtoList.add(kingdeeSynDto);
                }
            }else{
                throw new ServiceException("登入金蝶系统失败，请检查您的账户密码是否正确");
            }
        }
        return kingdeeSynDtoList;
    }

    @Transactional
    public KingdeeSynDto save(StkMisDeliveryDto stkMisDeliveryDto, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook){
        if (stkMisDeliveryDto != null) {
            KingdeeSynDto kingdeeSynDto;
            Boolean isLogin = kingdeeManager.login(kingdeeBook.getKingdeePostUrl(),kingdeeBook.getKingdeeDbid(),accountKingdeeBook.getUsername(),accountKingdeeBook.getPassword());
            if(isLogin) {
                stkMisDeliveryDto.setCreator(accountKingdeeBook.getUsername());
                //库存方向
                if (StkMisDeliveryTypeEnum.出库.name().equals(stkMisDeliveryDto.getMisDeliveryType())) {
                    stkMisDeliveryDto.setFStockDirect(StkMisDeliveryTypeEnum.出库.getFNumber());
                }else if(StkMisDeliveryTypeEnum.退货.name().equals(stkMisDeliveryDto.getMisDeliveryType())){
                    stkMisDeliveryDto.setFStockDirect(StkMisDeliveryTypeEnum.退货.getFNumber());
                }
                kingdeeSynDto = save(stkMisDeliveryDto, kingdeeBook);
            }else{
                throw new ServiceException("登入金蝶系统失败，请检查您的账户密码是否正确");
            }
            return kingdeeSynDto;
        }
        return null;
    }

    @Transactional
    public KingdeeSynDto saveForWS(StkMisDeliveryDto stkMisDeliveryDto, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook){
        if (stkMisDeliveryDto != null) {
            KingdeeSynDto kingdeeSynDto;
            stkMisDeliveryDto.setCreator(accountKingdeeBook.getUsername());
            //领料部门
            if (KingdeeNameEnum.WZOPPO.name().equals(kingdeeBook.getName())) {
                stkMisDeliveryDto.setFDeptIdNumber(DefaultBdDepartmentEnum.综合.getFNumber());
            }else if(KingdeeNameEnum.JXDJ.name().equals(kingdeeBook.getName())){
                stkMisDeliveryDto.setFDeptIdNumber(DefaultBdDepartmentEnum.电教.getFNumber());
            }else {
                stkMisDeliveryDto.setFDeptIdNumber(DefaultBdDepartmentEnum.省公司.getFNumber());
            }
            kingdeeSynDto = save(stkMisDeliveryDto, kingdeeBook,accountKingdeeBook);
            return kingdeeSynDto;
        }
        return null;
    }

    @Transactional
    public List<KingdeeSynDto> save(StkMisDeliveryForm stkMisDeliveryForm, KingdeeBook kingdeeBook, AccountKingdeeBook accountKingdeeBook) {
        Map<String, StkMisDeliveryDto> misDeliveryMap = Maps.newLinkedHashMap();
        String departmentNumber = stkMisDeliveryForm.getDepartmentNumber();
        LocalDate billDate = stkMisDeliveryForm.getBillDate();
        String json = HtmlUtils.htmlUnescape(stkMisDeliveryForm.getJson());
        List<List<Object>> data = ObjectMapperUtils.readValue(json, ArrayList.class);
        List<String> stockNameList = Lists.newArrayList();
        List<String> materialNameList = Lists.newArrayList();
        for (List<Object> row : data) {
            materialNameList.add(HandsontableUtils.getValue(row, 1));
            stockNameList.add(HandsontableUtils.getValue(row, 2));
        }
        Map<String, String> stockNameNumMap = bdStockRepository.findByNameList(stockNameList).stream().collect(Collectors.toMap(BdStock::getFName,BdStock::getFNumber));
        Map<String, String> materialNameNumMap = bdMaterialRepository.findByNameList(materialNameList).stream().collect(Collectors.toMap(BdMaterial::getFName,BdMaterial::getFNumber));
        for (List<Object> row : data) {
            String materialName = HandsontableUtils.getValue(row, 1);
            String stockName = HandsontableUtils.getValue(row, 2);
            Integer qty = Integer.valueOf(HandsontableUtils.getValue(row, 3));
            String type = HandsontableUtils.getValue(row, 4);
            String remarks = HandsontableUtils.getValue(row, 5);
            StkMisDeliveryDto misDelivery = new StkMisDeliveryDto();
            misDelivery.setExtendType(ExtendTypeEnum.其他出库单_K3.name());
            misDelivery.setCreator(accountKingdeeBook.getUsername());
            misDelivery.setDate(billDate);
            misDelivery.setFDeptIdNumber(departmentNumber);
            String billKey = materialName + CharConstant.COMMA + stockName + CharConstant.COMMA + qty + CharConstant.COMMA + remarks + CharConstant.COMMA + type;
            if (!misDeliveryMap.containsKey(billKey)) {
                StkMisDeliveryFEntityDto misDeliveryFEntityDto = new StkMisDeliveryFEntityDto();
                misDeliveryFEntityDto.setMaterialNumber(materialNameNumMap.get(materialName));
                misDeliveryFEntityDto.setStockNumber(stockNameNumMap.get(stockName));
                misDeliveryFEntityDto.setQty(qty);
                misDeliveryFEntityDto.setFEntryNote(remarks);
                misDelivery.setMisDeliveryType(type);
                misDelivery.getStkMisDeliveryFEntityDtoList().add(misDeliveryFEntityDto);
                misDeliveryMap.put(billKey, misDelivery);
            } else {
                misDeliveryMap.get(billKey).getStkMisDeliveryFEntityDtoList().get(0).setQty(qty+qty);
            }
        }
        List<StkMisDeliveryDto> billList = Lists.newArrayList(misDeliveryMap.values());
        return save(billList,kingdeeBook,accountKingdeeBook);
    }

    public StkMisDeliveryForm getForm(){
        StkMisDeliveryForm stkMisDeliveryForm = new StkMisDeliveryForm();
        Map<String,Object> map = Maps.newHashMap();
        map.put("stockNameList",bdStockRepository.findAll().stream().map(BdStock::getFName).collect(Collectors.toList()));
        //外购（1）类物料
        List<BdMaterial> materialList = bdMaterialRepository.findByErpCleId("1");
        map.put("materialNameList",materialList.stream().map(BdMaterial::getFName).collect(Collectors.toList()));
        map.put("materialNumberList",materialList.stream().map(BdMaterial::getFNumber).collect(Collectors.toList()));
        map.put("stkMisDeliveryTypeEnums",StkMisDeliveryTypeEnum.values());
        map.put("departmentList",bdDepartmentRepository.findAll());
        stkMisDeliveryForm.setExtra(map);
        return stkMisDeliveryForm;
    }
}
