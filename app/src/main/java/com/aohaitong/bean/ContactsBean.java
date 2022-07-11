package com.aohaitong.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 联系人实体类
 */
@Entity
public class ContactsBean {

    //联系人姓名
    private String name;
    //联系人手机号
    @Id
    private String telephone;

    @Generated(hash = 306855278)
    public ContactsBean(String name, String telephone) {
        this.name = name;
        this.telephone = telephone;
    }

    @Generated(hash = 747317112)
    public ContactsBean() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }


}
