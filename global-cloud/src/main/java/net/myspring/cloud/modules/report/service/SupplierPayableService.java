package net.myspring.cloud.modules.report.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.myspring.cloud.common.dataSource.annotation.KingdeeDataSource;
import net.myspring.cloud.common.enums.BillTypeEnum;
import net.myspring.cloud.modules.kingdee.domain.BdMaterial;
import net.myspring.cloud.modules.kingdee.domain.BdSupplier;
import net.myspring.cloud.modules.kingdee.repository.BdMaterialRepository;
import net.myspring.cloud.modules.kingdee.repository.BdSupplierRepository;
import net.myspring.cloud.modules.report.dto.SupplierPayableDetailDto;
import net.myspring.cloud.modules.report.dto.SupplierPayableDto;
import net.myspring.cloud.modules.report.repository.SupplierPayableRepository;
import net.myspring.cloud.modules.report.web.query.SupplierPayableQuery;
import net.myspring.common.exception.ServiceException;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 供应商-应付
 * Created by lihx on 2017/6/29.
 */
@Service
@KingdeeDataSource
@Transactional(readOnly = true)
public class SupplierPayableService {
    @Autowired
    private SupplierPayableRepository supplierPayableRepository;
    @Autowired
    private BdSupplierRepository bdSupplierRepository;
    @Autowired
    private BdMaterialRepository bdMaterialRepository;

    public List<SupplierPayableDto> findSupplierPayableDtoList(SupplierPayableQuery supplierPayableQuery) {
        LocalDate dateStart = supplierPayableQuery.getDateStart();
        LocalDate dateEnd = supplierPayableQuery.getDateEnd();
        List<String> supplierIdList = supplierPayableQuery.getSupplierIdList();
        if (supplierIdList.size()>0 && dateStart != null && dateEnd != null ) {
            List<SupplierPayableDto> supplierPayableDtoList = Lists.newArrayList();
            if (supplierIdList.size() > 0) {
                //期初结余：采购入库单-采购退料单+应付单+其他应付单-付款单+付款退款单
                List<SupplierPayableDto> startPayList = supplierPayableRepository.findEndPayable(dateStart, supplierIdList);
                Map<String, SupplierPayableDto> startPayKeyMap = Maps.newHashMap();
                for (SupplierPayableDto startPay : startPayList) {
                    String key = startPay.getSupplierId();
                    startPayKeyMap.put(key, startPay);
                }
                //期末结余
                List<SupplierPayableDto> endPayList = supplierPayableRepository.findEndPayable(dateEnd.plusDays(1), supplierIdList);
                Map<String, SupplierPayableDto> endPayKeyMap = Maps.newHashMap();
                for (SupplierPayableDto endPay : endPayList) {
                    String key = endPay.getSupplierId();
                    endPayKeyMap.put(key, endPay);
                    SupplierPayableDto supplierPayable = new SupplierPayableDto();
                    supplierPayable.setSupplierId(endPay.getSupplierId());
                    supplierPayable.setEndAmount(endPay.getBeginAmount());
                    if (startPayKeyMap.containsKey(key)) {
                        supplierPayable.setBeginAmount(startPayKeyMap.get(key).getBeginAmount());
                    } else {
                        supplierPayable.setBeginAmount(BigDecimal.ZERO);
                    }
                    supplierPayableDtoList.add(supplierPayable);
                }
                for (Map.Entry<String, SupplierPayableDto> startPayMap : startPayKeyMap.entrySet()) {
                    if (!endPayKeyMap.containsKey(startPayMap.getKey())) {
                        SupplierPayableDto supplierPayable = new SupplierPayableDto();
                        supplierPayable.setSupplierId(startPayMap.getValue().getSupplierId());
                        supplierPayable.setBeginAmount(startPayMap.getValue().getBeginAmount());
                        supplierPayable.setEndAmount(BigDecimal.ZERO);
                        supplierPayableDtoList.add(supplierPayable);
                    }
                }
                //应付金额：采购入库单-采购退料单+其他应付单+应付单
                List<SupplierPayableDto> payableList = supplierPayableRepository.findShouldPay(supplierPayableQuery);
                Map<String, BigDecimal> payableKeyMap = Maps.newHashMap();
                for (SupplierPayableDto supplierPayableDto : payableList) {
                    String key = supplierPayableDto.getSupplierId();
                    payableKeyMap.put(key, supplierPayableDto.getBeginAmount());
                }
                //实付金额：付款单-付款退款单
                List<SupplierPayableDto> actualPayList = supplierPayableRepository.findActualPay(supplierPayableQuery);
                Map<String, BigDecimal> actualPayKeyMap = Maps.newHashMap();
                for (SupplierPayableDto supplierPayableDto : actualPayList) {
                    String key = supplierPayableDto.getSupplierId();
                    actualPayKeyMap.put(key, supplierPayableDto.getBeginAmount());
                }
                Map<String, BdSupplier> bdSupplierIdMap;
                if (supplierIdList.size() > 0) {
                    bdSupplierIdMap = bdSupplierRepository.findIncludeForbidBySupplierIdList(supplierIdList).stream().collect(Collectors.toMap(BdSupplier::getFSupplierId, BdSupplier -> BdSupplier));
                } else {
                    bdSupplierIdMap = bdSupplierRepository.findAllIncludeForbid().stream().collect(Collectors.toMap(BdSupplier::getFSupplierId, BdSupplier -> BdSupplier));
                }
                for (SupplierPayableDto supplierPayable : supplierPayableDtoList) {
                    String key = supplierPayable.getSupplierId();
                    supplierPayable.setSupplierName(bdSupplierIdMap.get(supplierPayable.getSupplierId()).getFName());
                    supplierPayable.setPayableAmount(payableKeyMap.get(key));
                    supplierPayable.setActualPayAmount(actualPayKeyMap.get(key));
                    if (supplierPayableQuery.getQueryDetail()) {
                        Map<String, List<SupplierPayableDetailDto>> supplierPayableDetailDtoMap = findSupplierPayableDetailDtoMap(supplierPayableQuery);
                        supplierPayable.setSupplierPayableDetailDtoList(supplierPayableDetailDtoMap.get(key));
                    }
                }
                return supplierPayableDtoList;
            }
        }
        return null;
    }

    public List<SupplierPayableDetailDto> findSupplierPayableDetailDtoList(SupplierPayableQuery supplierPayableQuery) {
        List<SupplierPayableDetailDto> detailDtoList = Lists.newArrayList();
        List<String> supplierIdList = supplierPayableQuery.getSupplierIdList();
        if (supplierIdList.size()>0 && supplierPayableQuery.getDateStart() != null && supplierPayableQuery.getDateEnd() != null ) {
            Map<String, List<SupplierPayableDetailDto>> map = findSupplierPayableDetailDtoMap(supplierPayableQuery);
            if (map.size() > 0) {
                for (String supplierId : supplierIdList) {
                    String key = supplierId;
                    detailDtoList.addAll(map.get(key));
                }
            }
            return detailDtoList;
        }
        return null;
    }

    //一个supplierId+departmentId对应List<CustomerReceiveDetailDto>
    public Map<String, List<SupplierPayableDetailDto>> findSupplierPayableDetailDtoMap(SupplierPayableQuery supplierPayableQuery) {
        LocalDate dateStart = supplierPayableQuery.getDateStart();
        List<String> supplierIdList = supplierPayableQuery.getSupplierIdList();
        //期初应收
        List<SupplierPayableDto> beginList = supplierPayableRepository.findEndPayable(dateStart,supplierIdList);
        //根据key组织成map
        Map<String,BigDecimal> keyToBeginAmountMap = Maps.newHashMap();
        for (SupplierPayableDto supplierPayableDto : beginList){
            String key = supplierPayableDto.getSupplierId();
            keyToBeginAmountMap.put(key,supplierPayableDto.getBeginAmount());
        }
        //主单--采购入库单sum+采购退料单sum+应付单sum+付款单+付款退款单+其他应付单
        List<SupplierPayableDetailDto> detailForBillList = supplierPayableRepository.findMainList(supplierPayableQuery);
        //根据key组织成map
        Map<String, List<SupplierPayableDetailDto>> keyToMainBillMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(detailForBillList)) {
            for (SupplierPayableDetailDto supplierPayableDetailDto: detailForBillList) {
                String key = supplierPayableDetailDto.getSupplierId();
                if (!keyToMainBillMap.containsKey(key)) {
                    keyToMainBillMap.put(key, new ArrayList<>());
                }
                keyToMainBillMap.get(key).add(supplierPayableDetailDto);
            }
        }
        //有物料的
        List<SupplierPayableDetailDto> detailForMaterialList = supplierPayableRepository.findDetailList(supplierPayableQuery);
        //根据BillNo组织成map
        Map<String, List<SupplierPayableDetailDto>> billNoToDetailBillMap = Maps.newHashMap();
        List<String> materialIdList = Lists.newArrayList();
        if (CollectionUtil.isNotEmpty(detailForMaterialList)) {
            for (SupplierPayableDetailDto supplierPayableDetailDto: detailForMaterialList) {
                String billNo = supplierPayableDetailDto.getBillNo();
                if (!billNoToDetailBillMap.containsKey(billNo)) {
                    billNoToDetailBillMap.put(billNo, new ArrayList<>());
                }
                billNoToDetailBillMap.get(billNo).add(supplierPayableDetailDto);
                materialIdList.add(supplierPayableDetailDto.getMaterialId());
            }
        }
        Map<String,BdSupplier> bdSupplierIdMap;
        if (CollectionUtil.isNotEmpty(supplierIdList)){
            bdSupplierIdMap = bdSupplierRepository.findIncludeForbidBySupplierIdList(supplierIdList).stream().collect(Collectors.toMap(BdSupplier::getFSupplierId,BdSupplier->BdSupplier));
        }else {
            bdSupplierIdMap = bdSupplierRepository.findAllIncludeForbid().stream().collect(Collectors.toMap(BdSupplier::getFSupplierId,BdSupplier->BdSupplier));
        }
        Map<String,BdMaterial> materialIdMap = Maps.newHashMap();
        if (materialIdList.size()>0){
            materialIdMap = bdMaterialRepository.findByMasterIdList(materialIdList).stream().collect(Collectors.toMap(BdMaterial::getFMasterId,BdMaterial->BdMaterial));
        }
        Map<String,List<SupplierPayableDetailDto>> result = Maps.newHashMap();
        if (keyToMainBillMap.size()>0) {
            for (String key : keyToMainBillMap.keySet()) {
                if(!result.containsKey(key)) {
                    result.put(key,Lists.newArrayList());
                }
                List<SupplierPayableDetailDto> list = result.get(key);
                BigDecimal beginAmount = keyToBeginAmountMap.get(key);
                if (beginAmount == null){
                    beginAmount = BigDecimal.ZERO;
                }
                int index = 0;
                SupplierPayableDetailDto supplierPayableDetailDto = new SupplierPayableDetailDto();
                supplierPayableDetailDto.setBillType(bdSupplierIdMap.get(key).getFName());
                supplierPayableDetailDto.setIndex(index++);
                list.add(supplierPayableDetailDto);

                supplierPayableDetailDto = new SupplierPayableDetailDto();
                supplierPayableDetailDto.setBillType("期初应付");
                supplierPayableDetailDto.setEndAmount(beginAmount);
                supplierPayableDetailDto.setIndex(index++);
                list.add(supplierPayableDetailDto);
                BigDecimal payableAmount = BigDecimal.ZERO;
                BigDecimal actualPayAmount = BigDecimal.ZERO;
                for (int i = 0; i < detailForBillList.size(); i++) {
                    SupplierPayableDetailDto main = detailForBillList.get(i);
                    supplierPayableDetailDto = new SupplierPayableDetailDto();
                    supplierPayableDetailDto.setBillType(main.getBillType());
                    supplierPayableDetailDto.setDate(main.getDate());
                    supplierPayableDetailDto.setBillNo(main.getBillNo());
                    supplierPayableDetailDto.setNote(main.getNote());
                    supplierPayableDetailDto.setDocumentStatus(main.getDocumentStatus());
                    supplierPayableDetailDto.setIndex(index++);
                    //应付
                    if (main.getBillType().equals(BillTypeEnum.标准采购入库.name())) {
                        supplierPayableDetailDto.setPayableAmount(main.getAmount());
                        beginAmount = beginAmount.add(supplierPayableDetailDto.getPayableAmount());
                        supplierPayableDetailDto.setEndAmount(beginAmount);
                        list.add(supplierPayableDetailDto);
                    } else if (main.getBillType().equals(BillTypeEnum.标准退料单.name())) {
                        supplierPayableDetailDto.setPayableAmount(BigDecimal.ZERO.subtract(main.getAmount()));
                        beginAmount = beginAmount.add(supplierPayableDetailDto.getPayableAmount());
                        supplierPayableDetailDto.setEndAmount(beginAmount);
                        list.add(supplierPayableDetailDto);
                    } else if (main.getBillType().equals(BillTypeEnum.标准应付单.name())) {
                        supplierPayableDetailDto.setPayableAmount(main.getAmount());
                        beginAmount = beginAmount.add(supplierPayableDetailDto.getPayableAmount());
                        supplierPayableDetailDto.setEndAmount(beginAmount);
                        list.add(supplierPayableDetailDto);
                    } else if (main.getBillType().equals(BillTypeEnum.其他应付单.name())) {
                        supplierPayableDetailDto.setMaterialName(main.getMaterialId());//费用名称
                        supplierPayableDetailDto.setQty(main.getQty());//费用编码
                        supplierPayableDetailDto.setPayableAmount(main.getAmount());
                        beginAmount = beginAmount.add(supplierPayableDetailDto.getPayableAmount());
                        supplierPayableDetailDto.setEndAmount(beginAmount);
                        list.add(supplierPayableDetailDto);
                    //实付
                    } else if (main.getBillType().equals(BillTypeEnum.采购业务付款单.name())) {
                        supplierPayableDetailDto.setActualPayAmount(main.getAmount());
                        beginAmount = beginAmount.subtract(supplierPayableDetailDto.getActualPayAmount());
                        supplierPayableDetailDto.setEndAmount(beginAmount);
                        list.add(supplierPayableDetailDto);
                    } else if (main.getBillType().equals(BillTypeEnum.采购业务退款单.name())) {
                        supplierPayableDetailDto.setActualPayAmount(BigDecimal.ZERO.subtract(main.getAmount()));
                        beginAmount = beginAmount.subtract(supplierPayableDetailDto.getActualPayAmount());
                        supplierPayableDetailDto.setEndAmount(beginAmount);
                        list.add(supplierPayableDetailDto);
                    }
                    if (supplierPayableDetailDto.getPayableAmount() != null){
                        payableAmount = payableAmount.add(supplierPayableDetailDto.getPayableAmount());
                    }
                    if (supplierPayableDetailDto.getActualPayAmount() != null) {
                        actualPayAmount = actualPayAmount.add(supplierPayableDetailDto.getActualPayAmount());
                    }
                    if (billNoToDetailBillMap.containsKey(main.getBillNo())) {
                        for (SupplierPayableDetailDto detailMaterial : billNoToDetailBillMap.get(main.getBillNo())) {
                            supplierPayableDetailDto = new SupplierPayableDetailDto();
                            supplierPayableDetailDto.setBillNo(detailMaterial.getBillNo());
                            supplierPayableDetailDto.setMaterialName(materialIdMap.get(detailMaterial.getMaterialId()).getFName());
                            if (detailMaterial.getQty() != null && detailMaterial.getQty().intValue() != 0) {
                                supplierPayableDetailDto.setPrice(detailMaterial.getAmount().divide(detailMaterial.getQty(), 2, BigDecimal.ROUND_HALF_UP));
                                supplierPayableDetailDto.setQty(detailMaterial.getQty());
                            }
                            supplierPayableDetailDto.setAmount(detailMaterial.getAmount());
                            supplierPayableDetailDto.setNote(detailMaterial.getNote());
                            supplierPayableDetailDto.setDocumentStatus(detailMaterial.getDocumentStatus());
                            supplierPayableDetailDto.setIndex(index);
                            list.add(supplierPayableDetailDto);
                        }
                    }
                }
                supplierPayableDetailDto = new SupplierPayableDetailDto();
                supplierPayableDetailDto.setBillType("期末应付");
                supplierPayableDetailDto.setActualPayAmount(actualPayAmount);
                supplierPayableDetailDto.setPayableAmount(payableAmount);
                supplierPayableDetailDto.setEndAmount(beginAmount);
                supplierPayableDetailDto.setIndex(index++);
                list.add(supplierPayableDetailDto);
            }
        }
        return result;
    }

    public SupplierPayableQuery getQuery(){
        return null;
    }

    public SimpleExcelBook export(SupplierPayableQuery supplierPayableQuery){
        supplierPayableQuery.setQueryDetail(true);
        List<SupplierPayableDto> supplierPayableDtoList = findSupplierPayableDtoList(supplierPayableQuery);
        Workbook workbook = new SXSSFWorkbook(10000);
        List<SimpleExcelSheet> simpleExcelSheetList = Lists.newArrayList();
        List<SimpleExcelColumn> simpleExcelColumnList = Lists.newArrayList();
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"supplierName", "供应商名称"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"beginAmount", "期初应付"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"payableAmount", "应付金额"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"actualPayAmount", "实付金额"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook,"endAmount", "期末应付"));
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("应付汇总", supplierPayableDtoList, simpleExcelColumnList);
        simpleExcelSheetList.add(simpleExcelSheet);
        for(SupplierPayableDto supplierPayableDto : supplierPayableDtoList){
            List<SupplierPayableDetailDto> supplierPayableDetailDtoList = supplierPayableDto.getSupplierPayableDetailDtoList();
            if (CollectionUtil.isNotEmpty(supplierPayableDetailDtoList)){
                List<SimpleExcelColumn> columnList = Lists.newArrayList();
                CellStyle cellStyleRed = ExcelUtils.getCellStyleMap(workbook).get(ExcelCellStyle.RED.name());
                columnList.add(new SimpleExcelColumn(workbook,"billType", "业务类型"));
                columnList.add(new SimpleExcelColumn(workbook,"billNo", "单据编号"));
                columnList.add(new SimpleExcelColumn(workbook,"date", "单据日期"));
                columnList.add(new SimpleExcelColumn(workbook,"materialName", "商品名称"));
                columnList.add(new SimpleExcelColumn(workbook,"qty", "数量"));
                columnList.add(new SimpleExcelColumn(workbook,"price", "单价"));
                columnList.add(new SimpleExcelColumn(workbook,"amount", "金额"));
                columnList.add(new SimpleExcelColumn(workbook,"payableAmount", "应付"));
                columnList.add(new SimpleExcelColumn(workbook,"actualPayAmount", "实付"));
                columnList.add(new SimpleExcelColumn(workbook,"endAmount", "期末"));
                columnList.add(new SimpleExcelColumn(workbook,"note", "摘要"));
                SupplierPayableDetailDto supplierPayableDetailDto = supplierPayableDetailDtoList.get(0);
                SimpleExcelSheet excelSheet = new SimpleExcelSheet(supplierPayableDetailDto.getBillType(), supplierPayableDetailDtoList, columnList);
                simpleExcelSheetList.add(excelSheet);
            }
        }
        ExcelUtils.doWrite(workbook, simpleExcelSheetList);
        return new SimpleExcelBook(workbook,"应付款对账报表"+ supplierPayableQuery.getDateStart()+"-"+supplierPayableQuery.getDateEnd()+".xlsx",simpleExcelSheetList);
    }

    public SimpleExcelBook exportDetailOne(SupplierPayableQuery supplierPayableQuery){
        Workbook workbook = new SXSSFWorkbook(10000);
        List<SimpleExcelSheet> simpleExcelSheetList = Lists.newArrayList();
        List<SupplierPayableDetailDto> supplierPayableDetailDtoList = findSupplierPayableDetailDtoList(supplierPayableQuery);
        if (CollectionUtil.isNotEmpty(supplierPayableDetailDtoList)){
            List<SimpleExcelColumn> columnList = Lists.newArrayList();
            CellStyle cellStyleRed = ExcelUtils.getCellStyleMap(workbook).get(ExcelCellStyle.RED.name());
            columnList.add(new SimpleExcelColumn(workbook,"billType", "业务类型"));
            columnList.add(new SimpleExcelColumn(workbook,"billNo", "单据编号"));
            columnList.add(new SimpleExcelColumn(workbook,"date", "单据日期"));
            columnList.add(new SimpleExcelColumn(workbook,"materialName", "商品名称"));
            columnList.add(new SimpleExcelColumn(workbook,"qty", "数量"));
            columnList.add(new SimpleExcelColumn(workbook,"price", "单价"));
            columnList.add(new SimpleExcelColumn(workbook,"amount", "金额"));
            columnList.add(new SimpleExcelColumn(workbook,"payableAmount", "应付"));
            columnList.add(new SimpleExcelColumn(workbook,"actualPayAmount", "实付"));
            columnList.add(new SimpleExcelColumn(workbook,"endAmount", "期末"));
            columnList.add(new SimpleExcelColumn(workbook,"note", "摘要"));
            SupplierPayableDetailDto supplierPayableDetailDto = supplierPayableDetailDtoList.get(0);
            SimpleExcelSheet excelSheet = new SimpleExcelSheet(supplierPayableDetailDto.getBillType(), supplierPayableDetailDtoList, columnList);
            simpleExcelSheetList.add(excelSheet);
        }
        ExcelUtils.doWrite(workbook, simpleExcelSheetList);
        return new SimpleExcelBook(workbook,"应付款对账报表"+ supplierPayableQuery.getDateStart()+"-"+supplierPayableQuery.getDateEnd()+".xlsx",simpleExcelSheetList);
    }
}
