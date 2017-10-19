package hinapolina.com.sharelocation.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.model.Message;
import hinapolina.com.sharelocation.model.User;

/**
 * Created by hinaikhan on 10/16/17.
 */

public class MessageAdapter extends ArrayAdapter<Object> {

    private ImageView imgPhotoImageView;
    private TextView tvMessage;
    private TextView tvAuthorTextView;
    private List<Message> messages;

    public MessageAdapter(Context context, int resource, List<Message> messages) {
        super(context, resource);
        this.messages = messages;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        imgPhotoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        tvMessage = (TextView) convertView.findViewById(R.id.messageTextView);
        tvAuthorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

        Message message = this.messages.get(position);

//        boolean isPhoto = mUser.getImageURI() != null;
//        if(isPhoto){
//            tvMessage.setVisibility(View.GONE);
//            imgPhotoImageView.setVisibility(View.VISIBLE);
//            Picasso.with(imgPhotoImageView.getContext()).load(mUser.getImageURI()).into(imgPhotoImageView);
//        }else{
//            tvMessage.setVisibility(View.VISIBLE);
//            imgPhotoImageView.setVisibility(View.GONE);
//            tvMessage.setText(mUser.getText());
//
//        }

        tvAuthorTextView.setText(message.getSender());
        tvMessage.setText(message.getMessage());
        Picasso.with(getContext()).load(message.getUserProfileImg()).into(imgPhotoImageView);


        return convertView;
    }

    @Override
    public int getCount() {
        return messages != null ? messages.size() : 0;
    }

    /**
     * Adds a new message to messages list
     * @param message
     */
    public void addMessage(Message message) {
        //Create a new list if messages list hasn't been initialized
        if (messages == null) {
            messages = new ArrayList<Message>();
        }

        //add message to list
        messages.add(message);
    }

}
