package ap1.com.demo;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuriyivanov on 15-01-13.
 */

public class FAccessTask extends AsyncTask<Session, Void, GraphUser>
        implements RestTask.ResponseCallback
{
    private static final String TAG = "FAccessTask";

    private static String loginUrl = "http://sto.apengage.io/filemaker/login.php";

    private String cID = null;
    private String oldCID = "";
    private String cEmail = null;
    private String cError = null;
    private String cKey = null;
    private String cDate = null;

    String fbUserId = null;
    String fbEmail = null;
    String fbFname = null;
    String fbLname = null;
    String fbGender = null;
    String fbPicture = null;

    JSONObject userFBObject = null;

    private String response = null;  // for onRequestSuccess(String response)
    private String step = null;
    private String scriptName = "login.php";
    private String loginStatus = "";

    public String getCID() { return cID; }
    public String getOldCID() { return oldCID; }
    public void setOldCID(String old) { this.oldCID = old; }

    public String getCEmail() { return cEmail; }
    public String getCError() { return cError; }
    public String getCKey() { return cKey; }
    public String getLoginStatus(){ return loginStatus; }
    public String getStep() { return step; }
    public String getFbUserId() { return fbUserId ;}

    public void setScriptName(String  name) { this.scriptName = name ; }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected GraphUser doInBackground(final Session... session)
    {
        GraphUser user = null;

        // Log.i(TAG, "doInBackground: start");

        com.facebook.Request request = Request.newMeRequest(session[0], new Request.GraphUserCallback(){

            @Override
            public void onCompleted(GraphUser user, Response response)
            {
                // Log.i(TAG, "doInBackground: onCompleted");
                step = "FB onCompleted";

                if (user != null) {
                    Log.e("facebook obj: ", user.toString());

                    userFBObject = user.getInnerJSONObject();
                    Log.e("userFBObj", userFBObject.toString());
                    fbUserId = user.getId();
                    fbFname = user.getFirstName();
                    fbLname = user.getLastName();
                    fbPicture = "http://graph.facebook.com/" + fbUserId + "/picture?type=large";
                    try{
                        fbEmail = userFBObject.getString("email");
                        fbGender = userFBObject.getString("gender");
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    afterLoginPost();
                }
                else
                {
                    // Log.i("doInBackground: onCompleted", "user is null");
                }

                if (response.getError() != null)
                {
                }

            }   // onCompleted
        });


        request.executeAndWait();    // executeAsync();

        return user;
   }

   private void afterLoginPost()
   {
       if ( fbUserId != null && fbEmail != null)
       {
           try {
               step = "HTTP";
               HttpClient httpclient = new DefaultHttpClient();

               HttpPost postRequest = new HttpPost(new URI(loginUrl));

               List<NameValuePair> parameters = new ArrayList<NameValuePair>();
               parameters.add(new BasicNameValuePair("registration", "fb"));
               parameters.add(new BasicNameValuePair("gender", fbEmail));
               parameters.add(new BasicNameValuePair("email", fbEmail));
               parameters.add(new BasicNameValuePair("fbID", fbUserId));
               parameters.add(new BasicNameValuePair("fName", fbFname));
               parameters.add(new BasicNameValuePair("lName", fbLname));
               parameters.add(new BasicNameValuePair("img", fbPicture));

               postRequest.setEntity(new UrlEncodedFormEntity(parameters));

               // Log.i("afterLoginPost", "postRequest = " + postRequest);

               HttpResponse httpResponse = httpclient.execute(postRequest);

               Log.e("send req already", "");
               // Log.i("afterLoginPost after execute", httpResponse.toString());
               step = "HTTP after execute";

               StatusLine statusLine = httpResponse.getStatusLine();
               int statusCode = statusLine.getStatusCode();
               if (statusCode == 200)
               {
                   step = "HTTP status 200";
                   StringBuilder builder = new StringBuilder();
                   HttpEntity responseEntity = httpResponse.getEntity();
                   InputStream contentStream = responseEntity.getContent();
                   BufferedReader reader = new BufferedReader(new InputStreamReader(contentStream));
                   String line;
                   while ((line = reader.readLine()) != null) {
                       builder.append(line);
                   }

                   String responseStr = builder.toString();
                   // Log.i("afterLoginPost: responseStr =", responseStr);

                   responseEntity.consumeContent();
                   reader.close();
                   contentStream.close();

                   JSONObject jsonResp = new JSONObject(responseStr);

                   Log.e("json resp: ", jsonResp.toString());

               }
               else
               {
                   Log.e("afterLoginPost", "HTTP Response code = " + statusCode);
               }
           }
           catch (Exception e)
           {
               e.printStackTrace();
           }
       }
   }

   @Override
   protected void onPostExecute(GraphUser user)
   {
   }

    @Override
    public void onRequestSuccess(String response)
    {
        // Log.i(TAG, "onRequestSuccess: response = " + response);
        this.response = response;
    }

    @Override
    public void onRequestError(Exception error)
    {
        error.printStackTrace();
    }
}
