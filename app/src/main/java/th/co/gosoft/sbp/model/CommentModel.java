package th.co.gosoft.sbp.model;

import java.util.Date;

public class CommentModel {
    
    private String _id;
    private String _rev;
    private String topicId;
    private String user;
    private String subject;
    private String content;
    private Date date;
    private String type;
    
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
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
    public String getTopicId() {
        return topicId;
    }
    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString(){
        return "_id : "+_id+", _rev : "+_rev+", topicId : "+topicId+" subject : "+subject+", content : "+content+", user : "+user+", date : "+date+"\n";
    }


}
