package net.myspring.cloud.modules.input.web.form;


/**
 * Created by lihx on 2017/4/25.
 */
public class BatchBillForm {
    private String storeNumber;
    private String billDate;
    private String data;

    public String getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
