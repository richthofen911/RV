package ap1.com.demo;

import android.os.AsyncTask;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

/**
 * Created by yuriyivanov on 15-01-22.
 */

public class FPostTask extends AsyncTask<Session, Void, GraphUser>
        implements RestTask.ResponseCallback {

    private static final String TAG = "FPostTask";

    private String message = null;
    private String statusMessage = null;
    private String response = null;  // for onRequestSuccess(String response)
    private String step = "";

    public String getStep() { return step; }

    public String getMessage() { return message; }
    public void setMessage(String msg ) { this.message = msg; }

    public String getStatusMessage() { return statusMessage; }

    @Override
    protected void onPreExecute() {
    }

        @Override
    protected GraphUser doInBackground(final Session... session)
    {
        GraphUser user = null;
        final String statusUpdate = this.message; //  + new Date().toString();  // no need

        // Log.i(TAG, "doInBackground: start");

        com.facebook.Request request = Request.newStatusUpdateRequest(session[0], statusUpdate,
            new Request.Callback()
            {
                 @Override
                 public void onCompleted(Response response)
                 {
                     // GraphObject result = response.getGraphObject();
                     FacebookRequestError error = response.getError();

                     String title = null;
                     String alertMessage = null;
                     if ( error == null )
                     {
                         title = "SUCCESS:";
                         // String id = result.cast(GraphObjectWithId.class).getId();
                         alertMessage = "Posted to Facebook: \n" +  statusUpdate;   // + id;

                         statusMessage = "Post to Facebook is successful.";
                     }
                     else
                     {
                         title = "Error:";
                         alertMessage = error.getErrorMessage();
                         statusMessage = "Post to Facebook failed.";

                         /* if ( alertMessage != null )
                             Log.i(TAG, "doInBackground: onCompleted: " + title + " " + alertMessage);
                             */
                     }

                     // Log.i(TAG, "doInBackground: onCompleted: " + statusMessage);
                 }
            });

        // Intention: execute the request synchronously in the background
        // and return the response.

        request.executeAndWait();    // executeAsync();

        return user;
    }

    @Override
    protected void onPostExecute(GraphUser user)
    {
        //Log.i(TAG, "onPostExecute");
    }

    @Override
    public void onRequestSuccess(String response)
    {
        this.response = response;
    }

    @Override
    public void onRequestError(Exception error)
    {
        error.printStackTrace();
    }

}
