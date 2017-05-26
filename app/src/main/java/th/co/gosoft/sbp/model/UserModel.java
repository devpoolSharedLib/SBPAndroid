package th.co.gosoft.sbp.model;

/**
 * Created by manitkan on 12/06/16.
 */
public class UserModel {

    private String _id;
    private String _rev;
    private String accountId;
    private String empName;
    private String empEmail;
    private String avatarName;
    private String avatarPic;
    private String token;
    private boolean activate;
    private String type;
    private String birthday;

    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String get_rev() {
        return _rev;
    }
    public void set_rev(String _rev) {
        this._rev = _rev;
    }
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public String getEmpName() {
        return empName;
    }
    public void setEmpName(String empName) {
        this.empName = empName;
    }
    public String getEmpEmail() {
        return empEmail;
    }
    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }
    public String getAvatarName() {
        return avatarName;
    }
    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }
    public String getAvatarPic() {
        return avatarPic;
    }
    public void setAvatarPic(String avatarPic) {
        this.avatarPic = avatarPic;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public boolean isActivate() {
        return activate;
    }
    public void setActivate(boolean activate) {
        this.activate = activate;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
