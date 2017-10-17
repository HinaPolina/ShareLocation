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

import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.model.User;

/**
 * Created by hinaikhan on 10/16/17.
 */

public class MessageAdapter extends ArrayAdapter<User> {

    private ImageView imgPhotoImageView;
    private TextView tvMessage;
    private TextView tvAuthorTextView;

    public MessageAdapter(Context context, int resource, List<User> mUsers) {
        super(context, resource, mUsers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        imgPhotoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        tvMessage = (TextView) convertView.findViewById(R.id.messageTextView);
        tvAuthorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

        User mUser = getItem(position);

        boolean isPhoto = mUser.getImageURI() != null;
        if(isPhoto){
            tvMessage.setVisibility(View.GONE);
            imgPhotoImageView.setVisibility(View.VISIBLE);
            Picasso.with(imgPhotoImageView.getContext()).load(mUser.getImageURI()).into(imgPhotoImageView);
        }else{
            tvMessage.setVisibility(View.VISIBLE);
            imgPhotoImageView.setVisibility(View.GONE);
            tvMessage.setText(mUser.getText());

        }

        tvAuthorTextView.setText(mUser.getName());


        return convertView;
    }





}
