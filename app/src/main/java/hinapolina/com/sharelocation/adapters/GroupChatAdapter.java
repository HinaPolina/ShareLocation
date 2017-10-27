package hinapolina.com.sharelocation.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.model.Message;
import hinapolina.com.sharelocation.ui.Utils;

/**
 * Created by polina on 10/26/17.
 */

public class GroupChatAdapter  extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {

        private List<Message> mMessages;
        private List<String> mMessageKeys;
        private String currentUserName;
        private Context context;


    public GroupChatAdapter(Context context) {
            super();
            this.context = context;
            SharedPreferences sharedPref = context.getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
            currentUserName = sharedPref.getString(Utils.USER_NAME, " ");
            mMessageKeys = new ArrayList<String>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = parent;
            switch (viewType){
               case 0:
                   view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_chat, parent, false);
                   break;
                case 1:
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_chat_me, parent, false);
                    break;

           }

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        if (!currentUserName.equals(message.getSender())) {
            return 0;
        } else {
           return 1;
        }

    }

    @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Message message = mMessages.get(position);

            holder.message = message;


            holder.tvAuthorTextView.setText(message.getSender());

                holder.tvMessage.setText(message.getMessage());

            Picasso.with(context).load(message.getUserProfileImg()).centerCrop().resize(80, 80)
                    .transform(new GoogleLocationFragment.RoundTransformation()).into(holder.imgPhotoImageView);

            if (message.getTimeInMillis() > 0) {
                holder.tvMessageDateTime.setText(Utils.getDate(message.getTimeInMillis()));
            }

            if (!TextUtils.isEmpty(message.getImgUrl())) {
                holder.imgReceiveImgView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(message.getImgUrl()).placeholder(R.mipmap.ic_launcher).centerCrop().resize(600, 350).into(holder.imgReceiveImgView);
            } else {
                holder.imgReceiveImgView.setVisibility(View.GONE);
            }



        }

        /**
         * Adds a new message to messages list
         * @param message
         */
    public void addMessage(Message message, String msgKey) {
        //Create a new list if messages list hasn't been initialized
        if (mMessages == null) {
            mMessages = new ArrayList<Message>();
        }


        if (!mMessageKeys.contains(msgKey)) {
            //add message to list
            mMessages.add(0,message);
            mMessageKeys.add(msgKey);
            this.notifyItemInserted(mMessages.size()-1);
        }
    }


    @Override
    public int getItemCount() {
        return mMessages != null ? mMessages.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgPhotoImageView, imgReceiveImgView;
        private TextView tvMessage;
        private TextView tvAuthorTextView;
        private TextView tvMessageDateTime;
        Message message;

        public ViewHolder(View itemView) {

            super(itemView);
            imgPhotoImageView = (ImageView) itemView.findViewById(R.id.photoImageView);
            tvMessage = (TextView) itemView.findViewById(R.id.messageTextView);
            tvAuthorTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            tvMessageDateTime = (TextView) itemView.findViewById(R.id.tv_message_datetime);
            imgReceiveImgView = (ImageView) itemView.findViewById(R.id.receive_image_view);
            imgReceiveImgView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(message.getLng()!=null) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=" + message.getLat() + "," + message.getLng()));
                context.startActivity(intent);
            }

        }
    }


}
