package net.myspring.basic.modules.hr.web.form;


import net.myspring.basic.modules.hr.domain.Job;
import net.myspring.mybatis.form.BaseForm;

/**
 * Created by admin on 2017/4/5.
 */

public class JobForm extends BaseForm<Job> {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
