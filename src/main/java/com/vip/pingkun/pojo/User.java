package com.vip.pingkun.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String openId;
    private String header;
    private int sex;
    private String phone;
    private String nickName;
    private String commpanyName;
    private String jobName;
    //主营产品
    private String mainProject;
    private String email;
    //关注领域
    private String noticeArea;

    private int grade;//用户等级

    private Date becomeGradeTime; //成为会员日期

    private Date endGradeTime;//会员结束日期

    private Date createTime;
    private Date updateTime;

    private int state;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Date getBecomeGradeTime() {
        return becomeGradeTime;
    }

    public void setBecomeGradeTime(Date becomeGradeTime) {
        this.becomeGradeTime = becomeGradeTime;
    }

    public Date getEndGradeTime() {
        return endGradeTime;
    }

    public void setEndGradeTime(Date endGradeTime) {
        this.endGradeTime = endGradeTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCommpanyName() {
        return commpanyName;
    }

    public void setCommpanyName(String commpanyName) {
        this.commpanyName = commpanyName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getMainProject() {
        return mainProject;
    }

    public void setMainProject(String mainProject) {
        this.mainProject = mainProject;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNoticeArea() {
        return noticeArea;
    }

    public void setNoticeArea(String noticeArea) {
        this.noticeArea = noticeArea;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
