package ap1.com.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class ActivityLogin extends Activity implements View.OnClickListener{

    private ImageView iv_ap1Logo;
    private Button btn_signIn;
    private Button btn_facebookLogin;
    private Button btn_register;
    private EditText et_email;
    private EditText et_password;

    private Session session = null;

    private String url_login = "http://sto.apengage.io/filemaker/login.php";
    private String encodedUrl = null;

    private String mCID = null;
    private String mCEmail = null;
    private String TAG = "ActivityLogin";
    private final String fbMsg =
            "Facebook login failed. Please, sign in then press Facebook button in Register a Key window." ;

    private boolean isFBTaskFinished = false;
    private boolean isKeyOK = false;

    private Intent goToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "ap1.com.demo",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_signIn = (Button) findViewById(R.id.btn_signin);
        btn_facebookLogin = (Button) findViewById(R.id.btn_facebookSignin);
        btn_register = (Button) findViewById(R.id.btn_register);

        btn_signIn.setOnClickListener(this);
        btn_facebookLogin.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if ( v.getId() == R.id.btn_facebookSignin )
        {
            Log.w("ActivityLogin", "onCreate: openFacebookSession");
            openFacebookSession();
        }

        if ( v.getId() == R.id.btn_signin )
        {
            Log.w("ActivityLogin", "onCreate: signIn");
            //signIn();
        }

        if ( v.getId() == R.id.btn_register ) {
            goToRegister = new Intent(ActivityLogin.this, ActivityRegister.class);
            startActivity(goToRegister);
        }
    }

    private void openFacebookSession()
    {
        // 2nd argument - allowLoginUI if false, only sets the active session and opens it if it
        // does not require user interaction

        this.session = Session.openActiveSession(this, true, Arrays.asList("public_profile", "email"),
                new Session.StatusCallback()
                {
                    @Override
                    public void call(Session session, SessionState state, Exception exception)
                    {
                        if (session.isOpened())
                        {
                            Log.e("fb session is open", "");
                            FAccessTask asyncTask = new FAccessTask();
                            asyncTask.execute(session);

                            int sleepCnt = 0;

                            // sleep loop - decrease?
                            for ( sleepCnt = 0; sleepCnt < 9; sleepCnt++ )
                            {
                                try {
                                    Thread.sleep(1600);
                                    // Log.i(TAG, "call: sleep 1.6 sec");
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                                if ( asyncTask.getStatus() == AsyncTask.Status.FINISHED ) {
                                    ActivityLogin.this.isFBTaskFinished = true;
                                    break;
                                }

                                if ( asyncTask.getStep().equals("HTTP Finished"))
                                {
                                    ActivityLogin.this.isFBTaskFinished = true;
                                    break;
                                }

                            }   // end-for

                        }   // if session.isOpened()
                        else
                        {
                            Log.i("openFBSession", "call: session is not opened");
                        }
                    }       // call()
                });  // openActiveSession

        // last statement to synchronize FAccessTask - it is not used
        isFBTaskFinished = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

}
