package com.aohaitong.utils.offshore.message;


/*
 * A创建群组请求语句
 */
public class OffshoreCommunicationMessage14 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = -1209415392556121994L;

    /*
     *群组Id
     */
    private Long groupId;

    /*
     *群组名称
     */

    private String groupName;

    /*
     *备用
     */

    private Integer spare;

    /*
     *二进制数据
     */

    private String data;

    /**
     * 构造方法。
     */
    public OffshoreCommunicationMessage14() {
        super(14);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getSpare() {
        return spare;
    }

    public void setSpare(Integer spare) {
        this.spare = spare;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
