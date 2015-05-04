package ap1.com.demo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.ArrayList;

/**
 * Created by admin on 04/05/15.
 */
public class AdapterMessages extends RecyclerView.Adapter<ViewHolderMessage>{

    public ArrayList<String> messageURLs = new ArrayList<>();

    public AdapterMessages(ArrayList<String> webViews){
        messageURLs = webViews;
    }

    @Override
    public ViewHolderMessage onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.a_message,viewGroup,false);
        //ViewHolder vh = new ViewHolder(view);
        return new ViewHolderMessage(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderMessage viewHolder, int position) {
        viewHolder.wv_message.loadUrl(messageURLs.get(position));
        viewHolder.selfPosition = position;
    }

    @Override
    public int getItemCount() {
        return messageURLs.size();
    }

}
