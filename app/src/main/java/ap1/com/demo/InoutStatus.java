package ap1.com.demo;

/**
 * Created by admin on 23/04/15.
 */
public class InoutStatus {
    private static boolean checkedInAlready = false;

    public static boolean getInoutStatus(){
        return checkedInAlready;
    }

    public static void setInoutStatus(boolean status){
        checkedInAlready = status;
    }
}
