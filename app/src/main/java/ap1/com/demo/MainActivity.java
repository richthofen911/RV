package ap1.com.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.app.PendingIntent;
import android.app.NotificationManager;

import com.firebase.client.Firebase;
import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECOProximity;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements RECOServiceConnectListener, RECORangingListener{

    String url_beaconList = "http://sto.apengage.io/index.php/beacons/global";
    String url_company_prefix = "http://sto.apengage.io/index.php/api/v2/beacons/";
    String url_company = "";
    String beaconId;
    String TOBEFOUND_UUID = "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0";

    ImageView image_bottom;
    WebView wv_top;
    Button btn_close;
    HTTPClient httpClient;

    GetBeaconList getBeaconList;
    WifiManager wifiManager;
    String macAddress;

    protected RECOBeaconManager mRecoManager;
    protected ArrayList<RECOBeaconRegion> mRegions;

    public static final boolean DISCONTINUOUS_SCAN = false;

    private HashMap<String, RECOProximity> beacons = new HashMap<>();
    private Map<String, String> inout = new HashMap<>();
    private Map<String, String> oneBeacon = new HashMap<>();

    Firebase rootRef;

    private String proximity;

    private boolean readyToRange = false;
    private boolean isAppActive = false;

    private Beacon aBeacon;

    NotificationCompat.Builder notifyBuilder;
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), false, false);
        mRegions = this.generateBeaconRegion();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mRecoManager.setRangingListener(this);
        mRecoManager.bind(this);
        Firebase.setAndroidContext(this);
        rootRef = new Firebase("https://salesdemo.firebaseio.com/");
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        macAddress = getMacAddress(getApplicationContext(), wifiManager);
        inout.put(macAddress, "out");

        image_bottom = (ImageView) findViewById(R.id.image_bottom);
        wv_top = (WebView) findViewById(R.id.wv_top);
        btn_close = (Button) findViewById(R.id.btn_close);

        image_bottom.setImageResource(R.drawable.ap1_logo);
        wv_top.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });


        getBeaconList = new GetBeaconList();
        getBeaconList.execute(url_beaconList);
    }

    public String getMacAddress(Context context, WifiManager wifiManager){
        String macStr;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo.getMacAddress() != null){
            macStr = wifiInfo.getMacAddress();
        }else{
            macStr = "not find the beacon";
        }
        return macStr;
    }

    public PendingIntent getDefaultIntent(){
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(getApplicationContext(), FireNotification.class), PendingIntent.FLAG_CANCEL_CURRENT);
        return pendingIntent;
    }

    public void simpleNotification(){

        notifyBuilder = new NotificationCompat.Builder(this)
                .setContentIntent(getDefaultIntent())
                .setContentTitle("New Messages")
                .setContentText("You Received a New Message")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);
        notificationManager.notify(100, notifyBuilder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //rootRef.child(beaconId).child(macAddress).removeValue();
        //this.stop(mRegions);
        //this.unbind();
    }

    @Override
    public void onStart(){
        super.onStart();
        isAppActive = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        isAppActive = false;
    }

    @Override
    public void onServiceConnect() {
        Log.e("RangingActivity", "onServiceConnect()");
        mRecoManager.setDiscontinuousScan(this.DISCONTINUOUS_SCAN);
        this.start(mRegions);
        //Write the code when RECOBeaconManager is bound to RECOBeaconService
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoRegion) {
        if(!recoBeacons.isEmpty()){
            synchronized (recoBeacons){
                for(RECOBeacon recoBeacon: recoBeacons){
                    beacons.put("" + recoBeacon.getProximityUuid() + recoBeacon.getMajor() + recoBeacon.getMinor(), recoBeacon.getProximity());
                }
                if(readyToRange){
                    if(beacons.containsKey(aBeacon.getUmm()) && (beacons.get(aBeacon.getUmm()) == RECOProximity.RECOProximityImmediate || beacons.get(aBeacon.getUmm()) == RECOProximity.RECOProximityNear)){
                        if(!DataStore.getInoutStatus()){
                            DataStore.setInoutStatus(true);
                            Log.e("load message with url: ", url_company);
                            wv_top.setVisibility(View.VISIBLE);
                            wv_top.loadUrl(url_company);
                            btn_close.setVisibility(View.VISIBLE);
                            inout.put(macAddress, "in");
                            if(beaconId != null){
                                rootRef.child(beaconId).child(macAddress).setValue("in");
                            }

                            if(!isAppActive){
                                simpleNotification();
                            }

                            Log.e("put a check in ---", "");

                        }else{
                            Log.e("checked in already", "");
                        }

                    }else{
                        if(DataStore.getInoutStatus()){
                            DataStore.setInoutStatus(false);
                            inout.put(macAddress, "out");
                            rootRef.child(beaconId).child(macAddress).setValue("out");
                            Log.e("put a check out --- ", "");
                        }else{
                            Log.e("checked out already", "");
                        }
                    }
                }
                else{
                    rootRef.child(beaconId).child(macAddress).setValue("out");
                    Log.e("no beacon found", "send an out");
                }
            }
        }
    }

    protected void start(ArrayList<RECOBeaconRegion> regions) {
        for(RECOBeaconRegion region : regions) {
            try {
                Log.e("a region: ", region.getProximityUuid());
                mRecoManager.startRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                Log.i("RECORangingActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RECORangingActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }

    protected void stop(ArrayList<RECOBeaconRegion> regions) {
        for(RECOBeaconRegion region : regions) {
            try {
                mRecoManager.stopRangingBeaconsInRegion(region);
            } catch (RemoteException e) {
                Log.i("RECORangingActivity", "Remote Exception");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.i("RECORangingActivity", "Null Pointer Exception");
                e.printStackTrace();
            }
        }
    }

    private void unbind() {
        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {
            Log.i("RECORangingActivity", "Remote Exception");
            e.printStackTrace();
        }
    }

    //rootRef.OnDisconnect()

    @Override
    public void onServiceFail(RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed.
        //See the RECOErrorCode in the documents.
        return;
    }

    @Override
    public void rangingBeaconsDidFailForRegion(RECOBeaconRegion region, RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed to range beacons in the region.
        //See the RECOErrorCode in the documents.
        return;
    }

    private ArrayList<RECOBeaconRegion> generateBeaconRegion() {
        ArrayList<RECOBeaconRegion> regions = new ArrayList<RECOBeaconRegion>();

        RECOBeaconRegion recoRegion;
        recoRegion = new RECOBeaconRegion(this.TOBEFOUND_UUID, "RECO Sample Region");
        regions.add(recoRegion);

        return regions;
    }

    public void onCloseClick(View view){
        wv_top.setVisibility(View.INVISIBLE);
        btn_close.setVisibility(View.INVISIBLE);
    }

    public class GetBeaconList extends AsyncTask<String, String, String> {
        HTTPClient httpClient = new HTTPClient();
        @Override
        protected String doInBackground(String... params) {
            String receive = httpClient.getRequest(params[0]);
            Log.e("result: ", receive);
            return receive;
        }
        protected void onPostExecute(String result){
            if(!result.equals("[]")){
                httpClient.parseJSON(result);
                Log.e("beacon in httpclient ", httpClient.getBeacons().toString());
                aBeacon = httpClient.getBeacons().get(0);
                beaconId = aBeacon.getBeaconId();
                url_company = url_company_prefix + aBeacon.getCompanyId() + "/" + aBeacon.getUuid() +"/"+ aBeacon.getMajor() + "/" + aBeacon.getMinor();
                DataStore.setMessageUrl(url_company);
                readyToRange = true;
            }
        }
    }
}
