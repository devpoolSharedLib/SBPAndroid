package th.co.gosoft.sbp.model;

public class ChoiceTransactionModel {

    private String _id;
    private String _rev;
    private String empEmail;
    private String pollId;
    private String questionId;
    private String choiceKey;
    private String type;
    private String date;
    
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
    public String getEmpEmail() {
        return empEmail;
    }
    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }
    public String getQuestionId() {
        return questionId;
    }
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    public String getChoiceKey() {
        return choiceKey;
    }
    public void setChoiceKey(String choiceKey) {
        this.choiceKey = choiceKey;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getPollId() {
        return pollId;
    }
    public void setPollId(String pollId) {
        this.pollId = pollId;
    }
    
}
