package th.co.gosoft.sbp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import th.co.gosoft.sbp.model.RoomModel;
import th.co.gosoft.sbp.model.TopicModel;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setusename(String usename) {
        prefs.edit().putString("usename", usename).commit();
    }

    public String getusename() {
        String usename = prefs.getString("usename","");
        return usename;
    }

    public void setRoom(RoomModel roomModel) {
        prefs.edit().putString("room_id", roomModel.get_id()).commit();
        prefs.edit().putString("room_name", roomModel.getName()).commit();
    }

    public RoomModel getRoom() {
        RoomModel roomModel = new RoomModel();
        roomModel.set_id(prefs.getString("room_id", ""));
        roomModel.setName(prefs.getString("room_name", ""));
        return roomModel;
    }

    public void setTopic(TopicModel topicModel) {
        prefs.edit().putString("topic_id", topicModel.get_id()).commit();
    }

    public TopicModel getTopic() {
        TopicModel topicModel = new TopicModel();
        topicModel.set_id(prefs.getString("topic_id", ""));
        return topicModel;
    }
}