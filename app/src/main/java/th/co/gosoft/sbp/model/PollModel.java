package th.co.gosoft.sbp.model;

import java.util.List;

/**
 * Created by ASUS on 5/4/2560.
 */

public class PollModel {

    private String _id;
    private String _rev;
    private String topicId;
    private List<QuestionModel> questionMaster;
    private String date;
    private String updateDate;
    private String type;
    private List<String> empEmailPoll;

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

    public List<QuestionModel> getQuestionMaster() {
        return questionMaster;
    }

    public void setQuestionMaster(List<QuestionModel> questionMaster) {
        this.questionMaster = questionMaster;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getEmpEmailPoll() {
        return empEmailPoll;
    }

    public void setEmpEmailPoll(List<String> empEmailPoll) {
        this.empEmailPoll = empEmailPoll;
    }
}
