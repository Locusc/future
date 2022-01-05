package cn.locusc.mybatis.lagos.action.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Jay
 * 用户表
 * 2021/8/19
 */
@Table(name = "sys_user")
public class TkUser implements Serializable {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 账户名称
     */
    private String account_name;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像地址
     */
    private String avatar_url;

    /**
     * 用户类型
     */
    private Integer user_type;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 创建人
     */
    private Long create_by;

    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 更新人
     */
    private Long update_by;

    /**
     * 删除标识
     */
    private Integer is_del;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public Integer getUser_type() {
        return user_type;
    }

    public void setUser_type(Integer user_type) {
        this.user_type = user_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Long getCreate_by() {
        return create_by;
    }

    public void setCreate_by(Long create_by) {
        this.create_by = create_by;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public Long getUpdate_by() {
        return update_by;
    }

    public void setUpdate_by(Long update_by) {
        this.update_by = update_by;
    }

    public Integer getIs_del() {
        return is_del;
    }

    public void setIs_del(Integer is_del) {
        this.is_del = is_del;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", account_name='" + account_name + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", user_type=" + user_type +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", create_time=" + create_time +
                ", create_by=" + create_by +
                ", update_time=" + update_time +
                ", update_by=" + update_by +
                ", is_del=" + is_del +
                '}';
    }
}