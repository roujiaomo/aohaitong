package com.aohaitong.utils.offshore.message;


/*
 * 修改群名语句
 */
public class OffshoreCommunicationMessage30 extends OffshoreCommunicationAccountMessage {

    private static final long serialVersionUID = -3827992608888302478L;

    /*
     *群组ID
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
    public OffshoreCommunicationMessage30() {
        super(30);
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
