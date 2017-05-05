package net.myspring.general.modules.sys.form;

import org.springframework.util.StringUtils;

/**
 * Created by wangzm on 2017/4/25.
 */
public class ActivitiCompleteForm {
    private String id;
    private String name;
    private String processInstanceId;
    private String processTypeId;
    private String comment;
    private boolean pass;

    public ActivitiCompleteForm(){};
    public ActivitiCompleteForm(String id, String name, String processInstanceId, String processTypeId, String comment, boolean pass){
        this.pass=pass;
        this.id=id;
        this.name=name;
        this.processInstanceId=processInstanceId;
        this.processTypeId=processTypeId;
        this.comment=comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(String processTypeId) {
        this.processTypeId = processTypeId;
    }

    public String getComment() {
        if(StringUtils.isEmpty(comment)){
            this.comment="";
        }
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean getPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }
}