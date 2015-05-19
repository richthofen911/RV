package ap1.com.demo;

import java.util.ArrayList;

/**
 * Created by admin on 23/04/15.
 */
public class DataStore {
    private static boolean checkedInAlready = false;
    private static String messageUrl;
    public static ArrayList<String> messageUrls = new ArrayList<>();

    public static boolean getInoutStatus(){
        return checkedInAlready;
    }
    public static String getMessageUrl() {
        return messageUrl;
    }

    public static void setInoutStatus(boolean status){
        checkedInAlready = status;
    }
    public static void setMessageUrl(String url){
        messageUrl = url;
    }
}
