package ap1.com.demo;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;


public class FireNotification extends ActionBarActivity {
    ImageView image_bottom2;
    WebView wv_top2;
    Button btn_close2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_notification);

        image_bottom2 = (ImageView) findViewById(R.id.image_bottom2);
        wv_top2 = (WebView) findViewById(R.id.wv_top2);
        btn_close2 = (Button) findViewById(R.id.btn_close2);

        image_bottom2.setImageResource(R.drawable.ap1_logo);
        wv_top2.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wv_top2.loadUrl(DataStore.getMessageUrl());
        btn_close2.setVisibility(View.VISIBLE);

    }

    public void onCloseClick2(View view){
        wv_top2.setVisibility(View.INVISIBLE);
        btn_close2.setVisibility(View.INVISIBLE);
    }
}
