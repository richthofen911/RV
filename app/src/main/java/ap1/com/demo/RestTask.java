package ap1.com.demo;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by yuriyivanov on 15-01-08. See pp. 304-305
 */

/*
        HttpParams params = httpclient.getParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        params.setTcpNoDelay(params, true);
        params.setConnectionTimeout(httpParameters, 30000);
        params.setSoTimeout(httpParameters, 30000);
*/


public class RestTask extends AsyncTask<HttpUriRequest, Void, Object> {
    private static final String TAG = "RestTask";

    private String taskName = "";
    private String cookie = "";
    private int status = 0;

    public interface ResponseCallback {
            public void onRequestSuccess(String response);
            public void onRequestError(Exception error);
    }

    private AbstractHttpClient mClient;
    private WeakReference<ResponseCallback> mCallback;

    public RestTask()
    {
        this(new DefaultHttpClient());

        HttpParams params = mClient.getParams();
        HttpConnectionParams.setTcpNoDelay(params, true);   // YI - my change
        mClient.setParams(params);

    }

    public void setTaskName( String name) { taskName = name; }
    public String getCookie()
    {
        return cookie;
    }

    public int getIntStatus() { return status; }

    public RestTask(AbstractHttpClient client) {
        mClient = client;
    }

    public void setResponseCallback(ResponseCallback callback)
    {
        mCallback = new WeakReference<ResponseCallback>(callback);
    }

    @Override
    protected Object doInBackground(HttpUriRequest... params)
    {
        try {
            HttpUriRequest request = params[0];

            // Log.i(TAG, "doInBackground: before execute");
            HttpResponse serverResponse = mClient.execute(request);
            BasicResponseHandler handler = new BasicResponseHandler();

            /* if (serverResponse == null) {
                Log.i(TAG, "doInBackground: null HTTP response");
            }
            */

            // -- my addition
            StatusLine statusLine = serverResponse.getStatusLine();
            status = statusLine.getStatusCode();

            if ( status == 200 ) {
                Log.e(TAG, "doInBackground: code 200 OK");
            }
            else
            {
                Log.e(TAG, "doInBackground: HTTP response code: " + status);
                Log.e(TAG, "doInBackground: HTTP response: " + statusLine.toString());
            }
            // --

            String response = "";
            if ( taskName.equals(""))
            {
                response = handler.handleResponse(serverResponse);
                // Log.e(TAG, "doInBackground: response = ZZ" + response + "ZZ");
            }

            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return e;
        }
    }

    @Override
    protected void onPostExecute(Object result)
    {
        if ( mCallback != null && mCallback.get() != null )
        {
            final ResponseCallback callback = mCallback.get();

            if (result instanceof String)
            {
                callback.onRequestSuccess((String) result);
            }
            else if (result instanceof Exception)
            {
                callback.onRequestError((Exception) result);
            }
            else
            {
                callback.onRequestError(new IOException(
                    "Unknown Error Contacting Host") );
            }
        }
    }

}

