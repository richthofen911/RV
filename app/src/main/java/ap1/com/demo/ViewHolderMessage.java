package ap1.com.demo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

/**
 * Created by admin on 04/05/15.
 */
public class ViewHolderMessage extends RecyclerView.ViewHolder{
    public WebView wv_message;
    public Button btn_close;
    public int selfPosition;

    public ViewHolderMessage(View rootview){
        super(rootview);
        btn_close = (Button) rootview.findViewById(R.id.btn_close);
        wv_message = (WebView) rootview.findViewById(R.id.wv_a_message);
        wv_message.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataStore.messageUrls.remove(selfPosition);
                MainActivity.adapterMessages.notifyDataSetChanged();
            }
        });
    }
}
