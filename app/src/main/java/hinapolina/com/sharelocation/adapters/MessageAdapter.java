//package hinapolina.com.sharelocation.adapters;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toolbar;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.squareup.picasso.Picasso;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import hinapolina.com.sharelocation.R;
//import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
//import hinapolina.com.sharelocation.model.Message;
//import hinapolina.com.sharelocation.ui.Utils;
//
//import static hinapolina.com.sharelocation.R.drawable.user;
//
///**
// * Created by hinaikhan on 10/16/17.
// */
//
//public class MessageAdapter extends ArrayAdapter<Object> implements View.OnClickListener, View.OnLongClickListener{
//
//    private ImageView imgPhotoImageView;
//    private TextView tvMessage;
//    private TextView tvAuthorTextView;
//    private TextView tvMessageDateTime;
//    private List<Message> messages;
//    private String currentUserName;
//
//
//    public MessageAdapter(Context context, int resource, List<Message> messages) {
//        super(context, resource);
//        this.messages = messages;
//        SharedPreferences sharedPref = context.getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
//        currentUserName = sharedPref.getString(Utils.USER_NAME, " ");
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        if(convertView == null){
//            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
//        }
//
//        imgPhotoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
//        tvMessage = (TextView) convertView.findViewById(R.id.messageTextView);
//        tvAuthorTextView = (TextView) convertView.findViewById(R.id.nameTextView);
//        tvMessageDateTime = (TextView) convertView.findViewById(R.id.tv_message_datetime);
//
//
//        Message message = this.messages.get(position);
//
//        tvAuthorTextView.setText(message.getSender());
//
//
//        if (!TextUtils.isEmpty(message.getMessage())) {
//            tvMessage.setText(message.getMessage());
//            if (!currentUserName.equals(message.getSender())) {
//                tvMessage.setTextColor(Color.RED);
//            }
//        }
//
//        Picasso.with(getContext()).load(message.getUserProfileImg()) .resize(80, 80)
//                .transform(new GoogleLocationFragment.RoundTransformation()).into(imgPhotoImageView);
//
//        if (message.getTimeInMillis() > 0) {
//            tvMessageDateTime.setText(Utils.printableDateTime(message.getTimeInMillis()));
//        } else {
//            tvMessageDateTime.setText("");
//        }
//
//        if (!TextUtils.isEmpty(message.getImgUrl())) {
//            Log.d("", "Message image url: " + message.getImgUrl());
//            tvMessage.setVisibility(View.GONE);
//            imgPhotoImageView.setVisibility(View.VISIBLE);
//            Picasso.with(imgPhotoImageView.getContext()).load(message.getImgUrl()).into(imgPhotoImageView);
//        }
//
//        return convertView;
//    }
//
//    @Override
//    public int getCount() {
//        return messages != null ? messages.size() : 0;
//    }
//
//    /**
//     * Adds a new message to messages list
//     * @param message
//     */
//    public void addMessage(Message message) {
//        //Create a new list if messages list hasn't been initialized
//        if (messages == null) {
//            messages = new ArrayList<Message>();
//        }
//
//        //add message to list
//        messages.add(message);
//    }
//
//    @Override
//    public void onClick(View v) {
//
//    }
//
//    @Override
//    public boolean onLongClick(View v) {
//        return false;
//    }
//}
