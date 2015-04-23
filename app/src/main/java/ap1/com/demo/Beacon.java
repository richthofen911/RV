package ap1.com.demo;

/**
 * Created by admin on 21/04/15.
 */
public class Beacon {
    private String uuid;
    private String major;
    private String minor;
    private String companyId;
    private String umm;

    public Beacon(String uuid, String major, String minor, String companyId){
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.companyId = companyId;
        this.umm = uuid + major + minor;
    }

    public String getUuid(){
        return uuid;
    }

    public String getMajor(){
        return major;
    }

    public String getMinor(){
        return minor;
    }

    public String getCompanyId(){
        return companyId;
    }

    public String getUmm(){
        return umm;
    }
}
