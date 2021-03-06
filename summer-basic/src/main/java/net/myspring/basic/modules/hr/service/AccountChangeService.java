package net.myspring.basic.modules.hr.service;

import com.google.common.collect.Lists;
import net.myspring.basic.common.enums.AccountChangeTypeEnum;
import net.myspring.basic.common.enums.EmployeeStatusEnum;
import net.myspring.basic.common.enums.JointTypeEnum;
import net.myspring.basic.common.utils.CacheUtils;
import net.myspring.basic.common.utils.RequestUtils;
import net.myspring.basic.modules.hr.domain.Account;
import net.myspring.basic.modules.hr.domain.AccountChange;
import net.myspring.basic.modules.hr.domain.Employee;
import net.myspring.basic.modules.hr.domain.Position;
import net.myspring.basic.modules.hr.dto.AccountChangeDto;
import net.myspring.basic.modules.hr.repository.AccountChangeRepository;
import net.myspring.basic.modules.hr.repository.AccountRepository;
import net.myspring.basic.modules.hr.repository.EmployeeRepository;
import net.myspring.basic.modules.hr.repository.PositionRepository;
import net.myspring.basic.modules.hr.web.form.AccountChangeBatchForm;
import net.myspring.basic.modules.hr.web.form.AccountChangeForm;
import net.myspring.basic.modules.hr.web.query.AccountChangeQuery;
import net.myspring.basic.modules.sys.client.ActivitiClient;
import net.myspring.basic.modules.sys.client.FolderFileClient;
import net.myspring.basic.modules.sys.domain.Office;
import net.myspring.basic.modules.sys.manager.OfficeManager;
import net.myspring.basic.modules.sys.repository.OfficeRepository;
import net.myspring.common.constant.CharConstant;
import net.myspring.common.response.ResponseCodeEnum;
import net.myspring.common.response.RestResponse;
import net.myspring.general.modules.sys.dto.FolderFileFeignDto;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.myspring.util.excel.ExcelUtils.doRead;

@Service
@Transactional(readOnly = true)
public class AccountChangeService {

    @Autowired
    private AccountChangeRepository accountChangeRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private OfficeRepository officeRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private ActivitiClient activitiClient;
    @Autowired
    private OfficeManager officeManager;
    @Autowired
    private FolderFileClient folderFileClient;


    public AccountChange findOne(String id) {
        AccountChange accountChange = accountChangeRepository.findOne(id);
        return accountChange;
    }

    public AccountChangeForm getForm(AccountChangeQuery accountChangeQuery) {
        AccountChangeForm accountChangeForm = new AccountChangeForm();
        if (StringUtils.isNotBlank(accountChangeQuery.getId()) || StringUtils.isNotBlank(accountChangeQuery.getAccountId())) {
            accountChangeForm = accountChangeRepository.getForm(accountChangeQuery);
            if (StringUtils.isNotBlank(accountChangeQuery.getId())) {
                AccountChange accountChange = accountChangeRepository.findOne(accountChangeQuery.getId());
                accountChangeForm.setType(accountChange.getType());
                accountChangeForm.setNewValue(accountChange.getNewValue());
                accountChangeForm.setRemarks(accountChange.getRemarks());
            }
            cacheUtils.initCacheInput(accountChangeForm);
        }
        return accountChangeForm;
    }

    @Transactional
    public void audit(String id, boolean pass, String comment) {
        AccountChange accountChange = accountChangeRepository.findOne(id);
        if (pass) {
            accountChange.setProcessStatus("已通过");
            Account account = accountRepository.findOne(accountChange.getAccountId());
            Employee employee = employeeRepository.findOne(account.getEmployeeId());
            if (accountChange.getType().equals(AccountChangeTypeEnum.部门.toString())) {
                String officeId=account.getOfficeId();
                account.setOfficeId(accountChange.getNewValue());
                if(!officeId.equals(accountChange.getNewValue())){
                    account.setOfficeIds(CharConstant.COMMA+account.getOfficeId()+CharConstant.COMMA);
                }
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.岗位.toString())) {
                String positionId=account.getPositionId();
                account.setPositionId(accountChange.getNewValue());
                List<String> positionIdList=StringUtils.getSplitList(account.getPositionIds(),CharConstant.COMMA);
                if(positionIdList.size()==1){
                    account.setPositionIds(CharConstant.COMMA+account.getPositionId()+CharConstant.COMMA);
                }else if(positionIdList.contains(positionId)){
                    account.setPositionIds(account.getPositionIds().replace(CharConstant.COMMA+positionId+CharConstant.COMMA,CharConstant.COMMA+account.getPositionId()+CharConstant.COMMA));
                }
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.上级.toString())) {
                account.setLeaderId(accountChange.getNewValue());
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.手机.toString())) {
                employee.setMobilePhone(accountChange.getNewValue());
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.身份证.toString())) {
                employee.setIdcard(accountChange.getNewValue());
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.银行卡号.toString())) {
                employee.setBankNumber(accountChange.getNewValue());
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.转正.toString())) {
                employee.setRegularDate(LocalDateUtils.parse(accountChange.getNewValue()));
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.离职.toString())) {
                employee.setLeaveDate(LocalDateUtils.parse(accountChange.getNewValue()));
                if (StringUtils.isNotBlank(accountChange.getNewValue())) {
                    employee.setStatus(EmployeeStatusEnum.离职.name());
                } else {
                    employee.setStatus(EmployeeStatusEnum.在职.name());
                }
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.入职.name())) {
                employee.setEntryDate(LocalDateUtils.parse(accountChange.getNewValue()));
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.底薪.name())) {
                employee.setSalary(new BigDecimal(accountChange.getNewValue()));
            } else if (accountChange.getType().equals(AccountChangeTypeEnum.功能岗位.name())) {
                if(StringUtils.isNotBlank(accountChange.getNewValue())){
                    account.setPositionIds(accountChange.getNewValue());
                    List<String> positionIdList=StringUtils.getSplitList(account.getPositionIds(),CharConstant.COMMA);
                    if(positionIdList.size()==1){
                        account.setPositionId(positionIdList.get(0));
                    }
                }
            }
            accountRepository.save(account);
            employeeRepository.save(employee);
        } else {
            accountChange.setProcessStatus("未通过");
        }
        accountChangeRepository.save(accountChange);
    }

    @Transactional
    public AccountChange save(AccountChangeForm accountChangeForm) throws Exception{
        Account account = accountRepository.findOne(accountChangeForm.getAccountId());
        Employee employee = employeeRepository.findOne(account.getEmployeeId());
        Office office = officeRepository.findOne(account.getOfficeId());
        Office area = officeRepository.findOne(office.getAreaId());
        boolean pass = false;
        if ((accountChangeForm.getType().equals(AccountChangeTypeEnum.离职.toString()) && employee.getLeaveDate() == null) || JointTypeEnum.代理.name().equals(area.getJointType())) {
            pass = true;
        }
        AccountChange accountChange = getAccountChange(accountChangeForm, account, employee);
        save(accountChange, pass);
        return accountChange;
    }

    @Transactional
    public AccountChange save(AccountChange accountChange, boolean pass) {
        accountChangeRepository.save(accountChange);
        if (pass) {
            audit(accountChange.getId(), true, null);
        }
        return accountChange;
    }

    private AccountChange getAccountChange(AccountChangeForm accountChangeForm, Account account, Employee employee) throws Exception{
        AccountChange accountChange = new AccountChange();
        accountChange.setAccountId(accountChangeForm.getAccountId());
        accountChange.setNewValue(accountChangeForm.getNewValue());
        accountChange.setType(accountChangeForm.getType());
        accountChange.setRemarks(accountChangeForm.getRemarks());
        if (accountChange.getType().equals(AccountChangeTypeEnum.部门.toString())) {
            if (StringUtils.isNotBlank(account.getOfficeId())) {
                Office office = officeRepository.findOne(account.getOfficeId());
                accountChange.setOldValue(office.getId());
                accountChange.setOldLabel(office.getName());
            }
            Office newOffice = officeRepository.findByIdOrName(accountChange.getNewValue());
            accountChange.setNewValue(newOffice.getId());
            accountChange.setNewLabel(newOffice.getName());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.岗位.toString())) {
            if (StringUtils.isNotBlank(account.getPositionId())) {
                Position position = positionRepository.findOne(account.getPositionId());
                accountChange.setOldValue(position.getId());
                accountChange.setOldLabel(position.getName());
            }
            Position newPosition = positionRepository.findByIdOrName(accountChange.getNewValue());
            accountChange.setNewLabel(newPosition.getName());
            accountChange.setNewValue(newPosition.getId());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.上级.toString())) {
            if (StringUtils.isNotBlank(account.getLeaderId())) {
                Account leader = accountRepository.findOne(account.getLeaderId());
                accountChange.setOldValue(leader.getId());
                accountChange.setOldLabel(leader.getLoginName());
            }
            Account newLeader = accountRepository.findByIdOrName(accountChange.getNewValue());
            accountChange.setNewLabel(newLeader.getLoginName());
            accountChange.setNewValue(newLeader.getId());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.手机.toString())) {
            accountChange.setOldLabel(employee.getMobilePhone());
            accountChange.setOldValue(employee.getMobilePhone());
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.身份证.toString())) {
            accountChange.setOldLabel(employee.getIdcard());
            accountChange.setOldValue(employee.getIdcard());
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.银行卡号.toString())) {
            accountChange.setOldLabel(employee.getBankNumber());
            accountChange.setOldValue(employee.getBankNumber());
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.底薪.toString())) {
            accountChange.setOldLabel(employee.getSalary() != null ? employee.getSalary().toString() : null);
            accountChange.setOldValue(employee.getSalary() != null ? employee.getSalary().toString() : null);
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.转正.toString())) {
            if (employee.getRegularDate() != null) {
                accountChange.setOldLabel(LocalDateUtils.format(employee.getRegularDate()));
                accountChange.setOldValue(LocalDateUtils.format(employee.getRegularDate()));
            }
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.离职.toString())) {
            if (employee.getLeaveDate() != null) {
                accountChange.setOldLabel(LocalDateUtils.format(employee.getLeaveDate()));
                accountChange.setOldValue(LocalDateUtils.format(employee.getLeaveDate()));
            }
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.入职.name())) {
            if (employee.getEntryDate() != null) {
                accountChange.setOldLabel(LocalDateUtils.format(employee.getEntryDate()));
                accountChange.setOldValue(LocalDateUtils.format(employee.getEntryDate()));
            }
            accountChange.setNewLabel(accountChangeForm.getNewValue());
        } else if (accountChange.getType().equals(AccountChangeTypeEnum.功能岗位.name())) {
            List<String> positionIdList = StringUtils.getSplitList(account.getPositionIds(), CharConstant.COMMA);
            List<Position> positionList = positionRepository.findByIdInOrNameIn(positionIdList);
            accountChange.setOldValue(account.getPositionIds());
            accountChange.setOldLabel(StringUtils.join(CollectionUtil.extractToList(positionList, "name"), CharConstant.COMMA));

            List<String> list = StringUtils.getSplitList(accountChangeForm.getNewValue(), CharConstant.COMMA);
            List<Position> positions = positionRepository.findByIdInOrNameIn(list);
            accountChange.setNewValue(CharConstant.COMMA+StringUtils.join(CollectionUtil.extractToList(positions, "id"), CharConstant.COMMA)+CharConstant.COMMA);
            accountChange.setNewLabel(StringUtils.join(CollectionUtil.extractToList(positions, "name"), CharConstant.COMMA));
        }
        accountChange.setProcessStatus("省公司人事审核");
        return accountChange;
    }


    public Page<AccountChangeDto> findPage(Pageable pageable, AccountChangeQuery accountChangeQuery) {
        Page<AccountChangeDto> page = accountChangeRepository.findPage(pageable, accountChangeQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    @Transactional
    public void batchPass(String[] ids, boolean pass) {
        List<String> idList = Arrays.asList(ids);
        for (String id : idList) {
            audit(id, pass, "批量审核");
        }
    }

    @Transactional
    public void pass(String id, boolean pass) {
        audit(id, pass, "审核");
    }

    @Transactional
    public void logicDelete(String id) {
        accountChangeRepository.logicDelete(id);
    }

    public SimpleExcelBook findSimpleExcelSheet() {
        Workbook workbook = new SXSSFWorkbook(10000);
        List<SimpleExcelColumn> simpleExcelColumnList = Lists.newArrayList();
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "", "登录名"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "", "调整项"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "", "调整后"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "", "备注"));
        SimpleExcelSheet simpleExcelSheet = new SimpleExcelSheet("员工调整批量修改导入模版", null, simpleExcelColumnList);
        ExcelUtils.doWrite(workbook, simpleExcelSheet);
        SimpleExcelBook simpleExcelBook = new SimpleExcelBook(workbook, "员工调整批量修改导入模版" + UUID.randomUUID() + ".xls", simpleExcelSheet);
        return simpleExcelBook;
    }

    @Transactional
    public RestResponse batchSave(String folderFileId) {
        RestResponse restResponse = new RestResponse("保存成功", null);
        FolderFileFeignDto folderFileFeignDto = folderFileClient.findById(folderFileId);
        Workbook workbook = ExcelUtils.getWorkbook(new File(folderFileFeignDto.getUploadPath(RequestUtils.getCompanyName())));
        List<SimpleExcelColumn> simpleExcelColumnList = Lists.newArrayList();
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "loginName", "登录名"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "type", "调整项"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "newValue", "调整后"));
        simpleExcelColumnList.add(new SimpleExcelColumn(workbook, "remarks", "备注"));
        StringBuilder sb = new StringBuilder();
        if (workbook != null) {
            List<AccountChangeBatchForm> list = doRead(workbook.getSheetAt(0), simpleExcelColumnList, AccountChangeBatchForm.class);
            List<Account> accountList = accountRepository.findByLoginNameList(CollectionUtil.extractToList(list, "loginName"), RequestUtils.getOfficeIdList());
            Map<String, Account> accountMap = CollectionUtil.extractToMap(accountList, "loginName");
            List<String> positionNameList=CollectionUtil.extractToList(positionRepository.findByEnabledIsTrue(),"name");
            List<String> officeNameList=CollectionUtil.extractToList(officeManager.findHasPermissionOffice(),"name");
            List<Account> leaderList = accountRepository.findByLoginNameList(CollectionUtil.extractToList(list, "newValue"), RequestUtils.getOfficeIdList());
            List<String> leaderName = CollectionUtil.extractToList(leaderList, "loginName");
            List<String> typeList = AccountChangeTypeEnum.getList();
            for (AccountChangeBatchForm accountChangeBatchForm : list) {
                if (StringUtils.isNotBlank(accountChangeBatchForm.getLoginName())) {
                    if (!typeList.contains(accountChangeBatchForm.getType())) {
                        sb.append(accountChangeBatchForm.getLoginName() + accountChangeBatchForm.getType() + "调整项不正确\n");
                    }
                    if (!accountMap.containsKey(accountChangeBatchForm.getLoginName())) {
                        sb.append(accountChangeBatchForm.getLoginName() + "不存在或者不在你的管辖范围\n");
                    }
                    if(AccountChangeTypeEnum.岗位.name().equals(accountChangeBatchForm.getType())&&!positionNameList.contains(accountChangeBatchForm.getNewValue())){
                        sb.append(accountChangeBatchForm.getLoginName() + "的修改后岗位在系统中不存在\n");
                    }
                    if(AccountChangeTypeEnum.部门.name().equals(accountChangeBatchForm.getType())&&!officeNameList.contains(accountChangeBatchForm.getNewValue())){
                        sb.append(accountChangeBatchForm.getLoginName() + "的修改后部门不存在或者不在管辖范围\n");
                    }
                    if(AccountChangeTypeEnum.上级.name().equals(accountChangeBatchForm.getType())&&!leaderName.contains(accountChangeBatchForm.getNewValue())){
                        sb.append(accountChangeBatchForm.getLoginName() + "的修改后上级不存在或者不在管辖范围\n");
                    }
                }
            }
            if (StringUtils.isBlank(sb.toString())) {
                Map<String, Employee> employeeMap = employeeRepository.findMap(CollectionUtil.extractToList(accountList, "employeeId"));
                Map<String, Office> officeMap = officeRepository.findMap(CollectionUtil.extractToList(accountList, "officeId"));
                Map<String, Office> areaMap = officeRepository.findMap(CollectionUtil.extractToList(officeMap.values(), "areaId"));
                for (AccountChangeBatchForm accountChangeBatchForm : list) {
                    if (StringUtils.isNotBlank(accountChangeBatchForm.getLoginName())) {
                        AccountChangeForm accountChangeForm = BeanUtil.map(accountChangeBatchForm, AccountChangeForm.class);
                        Account account = accountMap.get(accountChangeBatchForm.getLoginName());
                        if (accountChangeBatchForm.getNewValue() instanceof Date) {
                            Date date = (Date) (accountChangeBatchForm.getNewValue());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            accountChangeForm.setNewValue(sdf.format(date));
                        }
                        Employee employee = employeeMap.get(account.getEmployeeId());
                        accountChangeForm.setAccountId(account.getId());
                        Office office = officeMap.get(account.getOfficeId());
                        Office area = areaMap.get(office.getAreaId());
                        boolean pass = false;
                        if ((accountChangeForm.getType().equals(AccountChangeTypeEnum.离职.toString()) && employee.getLeaveDate() == null) || JointTypeEnum.代理.name().equals(area.getJointType())) {
                            pass = true;
                        }
                        AccountChange accountChange = null;
                        try {
                            accountChange = getAccountChange(accountChangeForm, account, employee);
                            save(accountChange, pass);
                        } catch (Exception e) {
                            return new RestResponse("保存失败,Excel有错误", ResponseCodeEnum.invalid.name(), false);
                        }
                    }
                }
            } else {
                restResponse = new RestResponse(sb.toString(), null, false);
            }
        } else {
            restResponse = new RestResponse("保存失败,导入Excel不能为空", null, false);
        }
        return restResponse;
    }
}
