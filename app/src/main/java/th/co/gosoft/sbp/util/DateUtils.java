package th.co.gosoft.sbp.util;

import java.util.Date;

/**
 * Created by manitkan on 27/06/16.
 */
public class DateUtils {

    public static String concatNewDate(String url){
        return url+"&date="+new Date();
    }

    public static String concatNewDateFirstParam(String url){
        return url+"?date="+new Date();
    }
}
