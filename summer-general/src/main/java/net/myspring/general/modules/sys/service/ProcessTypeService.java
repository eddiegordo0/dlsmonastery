package net.myspring.general.modules.sys.service;

import com.google.common.collect.Lists;
import net.myspring.common.constant.CharConstant;
import net.myspring.general.common.utils.CacheUtils;
import net.myspring.general.modules.sys.domain.ProcessFlow;
import net.myspring.general.modules.sys.domain.ProcessType;
import net.myspring.general.modules.sys.dto.ProcessFlowDto;
import net.myspring.general.modules.sys.dto.ProcessTypeDto;
import net.myspring.general.modules.sys.repository.ProcessFlowRepository;
import net.myspring.general.modules.sys.repository.ProcessTypeRepository;
import net.myspring.general.modules.sys.web.form.ProcessFlowForm;
import net.myspring.general.modules.sys.web.form.ProcessTypeForm;
import net.myspring.general.modules.sys.web.query.ProcessTypeQuery;
import net.myspring.util.collection.CollectionUtil;
import net.myspring.util.mapper.BeanUtil;
import net.myspring.util.text.StringUtils;
import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProcessTypeService {


    @Autowired
    private ProcessTypeRepository processTypeRepository;
    @Autowired
    private ProcessFlowRepository processFlowRepository;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private CacheUtils cacheUtils;

    public ProcessTypeDto findByName(String name) {
        ProcessType processType = processTypeRepository.findByName(name);
        return BeanUtil.map(processType, ProcessTypeDto.class);
    }

    public List<ProcessTypeDto> findEnabledAuditFileType() {
        List<ProcessType> processTypeList = processTypeRepository.findEnabledAuditFileType();
        return BeanUtil.map(processTypeList, ProcessTypeDto.class);
    }

    public ProcessTypeDto findOne(ProcessTypeDto processTypeDto) {
        if (!processTypeDto.isCreate()) {
            ProcessType processType = processTypeRepository.findOne(processTypeDto.getId());
            processTypeDto = BeanUtil.map(processType, ProcessTypeDto.class);
            List<ProcessFlow> processFlowList = processFlowRepository.findByProcessTypeId(processTypeDto.getId());
            processTypeDto.setProcessFlowList(BeanUtil.map(processFlowList, ProcessFlowDto.class));
            cacheUtils.initCacheInput(processTypeDto);
        }
        return processTypeDto;
    }

    public void logicDelete(String id) {
        ProcessType processType = processTypeRepository.findOne(id);
        processType.setEnabled(false);
        processType.setName(processType.getName() + "removed(" + System.currentTimeMillis() + ")");
        processTypeRepository.save(processType);
    }

    public void save(ProcessTypeForm processTypeForm) {
        for(ProcessFlowForm processFlow:processTypeForm.getProcessFlowList()){
            if(!processTypeForm.getCreatePositionIds().contains(processFlow.getPositionId())){
                processTypeForm.setCreatePositionIds(processTypeForm.getCreatePositionIds()+ processFlow.getPositionId()+CharConstant.COMMA);
            }
            if(!processTypeForm.getViewPositionIds().contains(processFlow.getPositionId())){
                processTypeForm.setViewPositionIds(processTypeForm.getViewPositionIds()+ processFlow.getPositionId()+CharConstant.COMMA);
            }
        }
        if (processTypeForm.isCreate()) {
            for (int i = processTypeForm.getProcessFlowList().size() - 1; i >= 0; i--) {
                ProcessFlowForm processFlow = processTypeForm.getProcessFlowList().get(i);
                if (StringUtils.isBlank(processFlow.getName())) {
                    processTypeForm.getProcessFlowList().remove(i);
                }
            }
            if (CollectionUtil.isEmpty(processTypeForm.getProcessFlowList())) {
                return;
            }
            ProcessType processType = BeanUtil.map(processTypeForm, ProcessType.class);
            processTypeRepository.save(processType);
            for (ProcessFlowForm processFlow : processTypeForm.getProcessFlowList()) {
                processFlow.setProcessTypeId(processType.getId());
            }
            processFlowRepository.save(BeanUtil.map(processTypeForm.getProcessFlowList(), ProcessFlow.class));
            // 部署流程
            String processId = "process_type_" + processType.getId();
            BpmnModel model = new BpmnModel();
            Process process = new Process();
            process.setId(processId);
            process.setName(processType.getName());
            model.addProcess(process);

            // 起始节点
            StartEvent startEvent = new StartEvent();
            startEvent.setId("start");
            process.addFlowElement(startEvent);
            List<ProcessFlow> processFlows = processFlowRepository.findByProcessTypeId(processType.getId());
            for (int i = 0; i < processFlows.size(); i++) {
                ProcessFlow processFlow = processFlows.get(i);
                List<String> candidataGroups = Lists.newArrayList();
                candidataGroups.add(processFlow.getPositionId());
                UserTask userTask = new UserTask();
                userTask.setName(processFlow.getName());
                userTask.setId("task_" + processFlow.getId());
                userTask.setCandidateGroups(candidataGroups);
                process.addFlowElement(userTask);
                if (i != processFlows.size() - 1) {
                    ExclusiveGateway exclusivegateway = new ExclusiveGateway();
                    exclusivegateway.setId("exclusivegateway_" + processFlow.getId());
                    process.addFlowElement(exclusivegateway);
                }
            }
            // 结束节点
            EndEvent endEvent = new EndEvent();
            endEvent.setId("end");
            process.addFlowElement(endEvent);

            SequenceFlow flow = new SequenceFlow();
            flow.setSourceRef("start");
            flow.setTargetRef("task_" + processFlows.get(0).getId());
            process.addFlowElement(flow);

            for (int i = 0; i < processFlows.size() - 1; i++) {
                ProcessFlow processFlow = processFlows.get(i);
                flow = new SequenceFlow();
                flow.setSourceRef("task_" + processFlow.getId());
                flow.setTargetRef("exclusivegateway_" + processFlow.getId());
                process.addFlowElement(flow);

                flow = new SequenceFlow();
                flow.setSourceRef("exclusivegateway_" + processFlow.getId());
                flow.setTargetRef("task_" + processFlows.get(i + 1).getId());
                flow.setName("同意");
                flow.setConditionExpression("${task_" + processFlow.getId() + "_Pass}");
                process.addFlowElement(flow);

                flow = new SequenceFlow();
                flow.setSourceRef("exclusivegateway_" + processFlow.getId());
                flow.setTargetRef("end");
                flow.setName("不同意");
                flow.setConditionExpression("${!task_" + processFlow.getId() + "_Pass}");
                process.addFlowElement(flow);
            }
            flow = new SequenceFlow();
            flow.setSourceRef("task_" + processFlows.get(processFlows.size() - 1).getId());
            flow.setTargetRef("end");
            process.addFlowElement(flow);

            // 2. Generate graphical information
            new BpmnAutoLayout(model).execute();
            // 3. Deploy the process to the engine
            repositoryService.createDeployment().addBpmnModel(processId + ".bpmn", model).name(processType.getName()).deploy();
        }else {
            ProcessType processType=processTypeRepository.findOne(processTypeForm.getId());
            processType.setCreatePositionIds(processTypeForm.getCreatePositionIds());
            processType.setViewPositionIds(processTypeForm.getViewPositionIds());
            processType.setRemarks(processTypeForm.getRemarks());
            processTypeRepository.save(processType);
        }
    }

    public Page<ProcessTypeDto> findPage(Pageable pageable, ProcessTypeQuery processTypeQuery) {
        Page<ProcessTypeDto> page = processTypeRepository.findPage(pageable, processTypeQuery);
        cacheUtils.initCacheInput(page.getContent());
        return page;
    }

    public List<ProcessTypeDto> findAll() {
        List<ProcessType> processTypeList = processTypeRepository.findAll();
        return BeanUtil.map(processTypeList, ProcessTypeDto.class);
    }

    public List<ProcessTypeDto> findByCreatePositionId(List<String> positionIdList) {
        List<ProcessType> processTypeList = processTypeRepository.findByCreatePositionIdsLike(positionIdList);
        return BeanUtil.map(processTypeList, ProcessTypeDto.class);
    }

    public List<ProcessTypeDto> findByViewPositionId(List<String> positionIdList) {
        List<ProcessType> processTypeList = processTypeRepository.findByViewPositionIdsLike(positionIdList);
        return BeanUtil.map(processTypeList, ProcessTypeDto.class);
    }

    public List<ProcessTypeDto> findByAuditFileTypeIsTrue() {
        List<ProcessType> processTypeList = processTypeRepository.findByAuditFileTypeIsTrueAndEnabledIsTrue();
        return BeanUtil.map(processTypeList, ProcessTypeDto.class);
    }
}
