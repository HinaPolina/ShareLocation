package hinapolina.com.sharelocation.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.activities.message.ChatActivity;
import hinapolina.com.sharelocation.activities.videotalk.VideoTalkActivity;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.model.Message;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.ui.BatteryStatus;

import static hinapolina.com.sharelocation.R.drawable.user;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> mMessageModel;
    private Context context;
    private LayoutInflater mInflater;

    public ChatRecyclerViewAdapter(List<Message> mMessageModel, Context context) {
        super();
        this.mMessageModel = mMessageModel;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContentHolder(mInflater.inflate(R.layout.chat_items, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Message item = mMessageModel.get(position);
        ContentHolder contentHolder = (ContentHolder) viewHolder;


        contentHolder.tvTextMessage.setText(item.getMessage());
        contentHolder.tvUserName.setText(item.getSender());
        Picasso.with(contentHolder.itemView.getContext()).load((item.getUserProfileImg())) .resize(80, 80)
                .transform(new GoogleLocationFragment.RoundTransformation()).into(contentHolder.imgProfileImage);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount () {
        return mMessageModel.size ();
    }

    private class ContentHolder extends RecyclerView.ViewHolder  {

        TextView tvUserName, tvTextMessage, tvMessageCount,tvMessageTime;
        ImageView imgProfileImage;


        ContentHolder(View view) {
            super(view);
            tvUserName = (TextView) view.findViewById(R.id.tv_username);
            tvTextMessage = (TextView) view.findViewById(R.id.tv_message);
            imgProfileImage = (ImageView) view.findViewById(R.id.tv_user_pic);
            tvMessageCount = (TextView) view.findViewById(R.id.tv_new_message_count);
            tvMessageTime = (TextView) view.findViewById(R.id.tv_time);
//            v.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            UsersModel item = mUsersModel.get(this.getLayoutPosition() - 1);
//            switch (v.getId()) {
//                default:
//                    break;
//            }
        }

    public void addMessage(Message message) {
        //Create a new list if messages list hasn't been initialized
        if (mMessageModel == null) {
            mMessageModel = new ArrayList<Message>();
        }

        //add message to list
        mMessageModel.add(message);
    }
}


