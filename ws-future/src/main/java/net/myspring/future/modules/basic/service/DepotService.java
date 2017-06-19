package net.myspring.future.modules.basic.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSFile;
import net.myspring.basic.common.util.CompanyConfigUtil;
import net.myspring.cloud.modules.report.dto.CustomerReceiveDto;
import net.myspring.cloud.modules.report.web.query.CustomerReceiveQuery;
import net.myspring.common.enums.CompanyConfigCodeEnum;
import net.myspring.common.exception.ServiceException;
import net.myspring.future.common.utils.CacheUtils;
import net.myspring.future.common.utils.RequestUtils;
import net.myspring.future.modules.basic.client.CloudClient;
import net.myspring.future.modules.basic.client.OfficeClient;
import net.myspring.future.modules.basic.domain.Depot;
import net.myspring.future.modules.basic.dto.DepotAccountDto;
import net.myspring.future.modules.basic.dto.DepotDto;
import net.myspring.future.modules.basic.manager.DepotManager;
import net.myspring.future.modules.basic.repository.ClientRepository;
import net.myspring.future.modules.basic.repository.DepotRepository;
import net.myspring.future.modules.basic.web.query.DepotAccountQuery;
import net.myspring.future.modules.basic.web.query.DepotQuery;
import net.myspring.future.modules.layout.repository.ShopDepositRepository;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.excel.ExcelUtils;
import net.myspring.util.excel.SimpleExcelBook;
import net.myspring.util.excel.SimpleExcelColumn;
import net.myspring.util.excel.SimpleExcelSheet;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.text.StringUtils;
import net.myspring.util.time.LocalDateUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class DepotService {
    @Autowired
    private DepotRepository depotRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private OfficeClient officeClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private DepotManager depotManager;
    @Autowired
    private ShopDepositRepository shopDepositRepository;
    @Autowired
    private CloudClient cloudClient;
    @Autowired
    private GridFsTemplate tempGridFsTemplate;


    public List<DepotDto> findShopList(DepotQuery depotQuery) {
        List<Depot> depotList = depotRepository.findByAccountId(RequestUtils.getAccountId());
        depotQuery.setOfficeIdList(officeClient.getOfficeFilterIds(RequestUtils.getRequestEntity().getOfficeId()));
        if(CollectionUtil.isNotEmpty(depotList)) {
            depotQuery.setDepotIdList(CollectionUtil.extractToList(depotList,"id"));
        }
        return depotRepository.findShopList(depotQuery);
    }

    public List<String> filterDepotIds(){
        return depotManager.filterDepotIds();
    }

    public List<DepotDto> findStoreList(DepotQuery depotQuery) {
        List<Depot> depotList = depotRepository.findByAccountId(RequestUtils.getAccountId());
        depotQuery.setOfficeIdList(officeClient.getOfficeFilterIds(RequestUtils.getRequestEntity().getOfficeId()));
        if(CollectionUtil.isNotEmpty(depotList)) {
            depotQuery.setDepotIdList(CollectionUtil.extractToList(depotList,"id"));
        }
        return depotRepository.findStoreList(depotQuery);
    }

    public List<DepotDto> findByIds(List<String> ids){
        List<Depot> depotList=depotRepository.findByEnabledIsTrueAndIdIn(ids);
        List<DepotDto> depotDtoList= BeanUtil.map(depotList,DepotDto.class);
        cacheUtils.initCacheInput(depotDtoList);
        return depotDtoList;
    }

    public DepotDto findOne(String id) {
        DepotDto depotDto;
        if(StringUtils.isBlank(id)) {
            depotDto = new DepotDto();
        } else {
            Depot depot = depotRepository.findOne(id);
            depotDto = BeanUtil.map(depot,DepotDto.class);
            cacheUtils.initCacheInput(depotDto);
        }
        return depotDto;
    }

    public Page<DepotAccountDto> findDepotAccountList(Pageable pageable, DepotAccountQuery depotAccountQuery) {

        if(depotAccountQuery.getDutyDateRange() == null || depotAccountQuery.getDutyDateStart()==null || LocalDate.now().minusDays(70).isAfter(depotAccountQuery.getDutyDateStart()) ){
            throw new ServiceException("查询条件请选择为70天以内");
        }

        Page<DepotAccountDto> page = depotRepository.findDepotAccountList(pageable, depotAccountQuery);
        cacheUtils.initCacheInput(page.getContent());

        CustomerReceiveQuery customerReceiveQuery = new CustomerReceiveQuery();
        customerReceiveQuery.setDateRange(depotAccountQuery.getDutyDateRange());
        customerReceiveQuery.setCustomerIdList(CollectionUtil.extractToList(page.getContent(), "clientOutId"));
        List<CustomerReceiveDto> customerReceiveDtoList = cloudClient.getCustomerReceiveList(customerReceiveQuery);
        Map<String, CustomerReceiveDto> customerReceiveDtoMap = CollectionUtil.extractToMap(customerReceiveDtoList, "customerId");

        for(DepotAccountDto depotAccountDto : page.getContent()){
            CustomerReceiveDto customerReceiveDto = customerReceiveDtoMap.get(depotAccountDto.getClientOutId());
            depotAccountDto.setQcys(customerReceiveDto.getBeginShouldGet());
            depotAccountDto.setQmys(customerReceiveDto.getEndShouldGet());
        }

        return page;
    }

    public String depotAccountExportDetail(DepotAccountQuery depotAccountQuery) {

        List<SimpleExcelSheet> sheets = new ArrayList<>();
        Workbook workbook = new SXSSFWorkbook(10000);
        List<SimpleExcelColumn> simpleExcelColumnList = Lists.newArrayList();

        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "officeName", "所属机构"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "name", "客户名称"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "qcys", "期初应收"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "qmys", "期末应收"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "scbzj", "市场保证金"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "xxbzj", "形象押金"));
        List<DepotAccountDto> depotAccountList = findDepotAccountList(new PageRequest(0,10000),  depotAccountQuery).getContent();

        sheets.add(new SimpleExcelSheet("应收报表", depotAccountList, simpleExcelColumnList));

        Set<String> sheetNames = Sets.newHashSet();
        for(DepotAccountDto depotAccountDto : depotAccountList){
            //TODO 增加去重判断
//            if (StringUtils.isBlank(depotAccountDto.getOutId()) || sheetNames.contains(depotAccountDto.getOutId())) {
//                continue;
//            }
                sheetNames.add(depotAccountDto.getName());

                List<SimpleExcelColumn> depotAccountColumnList = Lists.newArrayList();

                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "billType", "业务类型"));
                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "billNo", "单据编号"));
                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "date", "记账日期"));
                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "materialName", "名称"));
                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "qty", "数量"));
                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "price", "单价"));
                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "totalAmount", "金额"));
                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "receiveAmount", "预收"));
                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "shouldGet", "应收"));
                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "endShouldGet", "期末"));
                depotAccountColumnList.add(new SimpleExcelColumn(workbook, "remarks", "备注"));

                List<String> customerAccounts = new ArrayList<>();// k3cloudService.findCustomerAccount(AccountUtils.getCompany().getOutDbname(),depot.getOutId(), depot.getName(), dateStart, dateEnd);

                sheets.add(new SimpleExcelSheet(depotAccountDto.getName(), customerAccounts, depotAccountColumnList));
        }

        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook,"客户应收" + LocalDateUtils.format(depotAccountQuery.getDutyDateStart(), "yyyyMMdd") + "~" + LocalDateUtils.format(depotAccountQuery.getDutyDateEnd(), "yyyyMMdd")+".xlsx", sheets);
        ByteArrayInputStream byteArrayInputStream= ExcelUtils.doWrite(simpleExcelBook.getWorkbook(),simpleExcelBook.getSimpleExcelSheets());
        GridFSFile gridFSFile = tempGridFsTemplate.store(byteArrayInputStream,simpleExcelBook.getName(),"application/octet-stream; charset=utf-8", RequestUtils.getDbObject());
        return StringUtils.toString(gridFSFile.getId());

    }

    public String depotAccountExportConfirmation(DepotAccountQuery depotAccountQuery) {
        //TODO 写确认书需要增强excel的打印能力

        return null;
    }

    public String depotAccountExportAllDepots(DepotAccountQuery depotAccountQuery) {

        Workbook workbook = new SXSSFWorkbook(10000);
        List<SimpleExcelColumn> simpleExcelColumnList = Lists.newArrayList();

        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "areaName", "所属办事处"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "officeName", "所属机构"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "name", "客户名称"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "qcys", "期初应收"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "qmys", "期末应收"));

        List<DepotAccountDto> depotAccountList = findDepotAccountList(new PageRequest(0,10000),  depotAccountQuery).getContent();

        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("应收列表", depotAccountList, simpleExcelColumnList);
        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook,"应收列表"+LocalDateUtils.format(LocalDate.now()) +".xlsx" ,simpleExcelSheet);
        ByteArrayInputStream byteArrayInputStream= ExcelUtils.doWrite(simpleExcelBook.getWorkbook(),simpleExcelBook.getSimpleExcelSheets());
        GridFSFile gridFSFile = tempGridFsTemplate.store(byteArrayInputStream,simpleExcelBook.getName(),"application/octet-stream; charset=utf-8", RequestUtils.getDbObject());
        return StringUtils.toString(gridFSFile.getId());

    }

    public void scheduleSynArea(){
        List<Depot> depotList=depotRepository.findAll();
        List<DepotDto> depotDtoList=BeanUtil.map(depotList,DepotDto.class);
        cacheUtils.initCacheInput(depotDtoList);
        Map<String,DepotDto> depotDtoMap=CollectionUtil.extractToMap(depotDtoList,"id");
        for(Depot depot:depotList){
            depot.setAreaId(depotDtoMap.get(depot.getId()).getAreaId());
            depotRepository.save(depot);
        }
    }

    public void synArea(DepotQuery depotQuery){
      depotQuery.setOfficeIdList(officeClient.getOfficeFilterIds(RequestUtils.getRequestEntity().getOfficeId()));
      depotQuery.setDepotIdList(depotManager.filterDepotIds());
      List<Depot> depotList=depotRepository.findByFilter(depotQuery);
        List<DepotDto> depotDtoList=BeanUtil.map(depotList,DepotDto.class);
        cacheUtils.initCacheInput(depotDtoList);
        Map<String,DepotDto> depotDtoMap=CollectionUtil.extractToMap(depotDtoList,"id");
        for(Depot depot:depotList){
            depot.setAreaId(depotDtoMap.get(depot.getId()).getAreaId());
            depotRepository.save(depot);
        }
    }

    public List<DepotDto> findAdStoreDtoList() {
        String outGroupId = CompanyConfigUtil.findByCode(redisTemplate, RequestUtils.getCompanyId(), CompanyConfigCodeEnum.STORE_AD_GROUP_IDS.name()).getValue();
        List<DepotDto> adStoreDtoList = depotRepository.findAdStoreDtoList(RequestUtils.getCompanyId(), outGroupId);
        cacheUtils.initCacheInput(adStoreDtoList);
        return adStoreDtoList;

    }

}
