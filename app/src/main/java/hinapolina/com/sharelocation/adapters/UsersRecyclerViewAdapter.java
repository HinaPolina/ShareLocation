package hinapolina.com.sharelocation.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.TalkListener;
import hinapolina.com.sharelocation.activities.message.ChatActivity;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.ui.BatteryStatus;
import hinapolina.com.sharelocation.ui.Utils;

import static com.android.volley.Request.Method.HEAD;

/**
 * Created by hinaikhan on 10/16/17.
 */

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.MainViewHolder> {

    private final static String TAG = UsersRecyclerViewAdapter.class.getSimpleName();
    private Context context;
    private List<User> mUsers;
    private FragmentManager fragmentManager;
    private TalkListener talkListener;
    private String currentUserId;

    public UsersRecyclerViewAdapter(Context context, TalkListener talkListener, List<User> mUsers) {
        this.context = context;
        this.mUsers = mUsers;
        this.talkListener = talkListener;

        SharedPreferences sharedPref = context.getSharedPreferences( Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);
        currentUserId = sharedPref.getString(Utils.USER_ID, "") ;
    }

    public void addUser(User user) {
        this.mUsers.add(user);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_user_battery_information, parent, false);
        MainViewHolder viewHolder = new MainViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {


        final User user = mUsers.get(position);
        if(currentUserId.equals(user.getId())){
            holder.imgMessage.setVisibility(View.INVISIBLE);
            holder.imgTalk.setVisibility(View.INVISIBLE);
        }
        BatteryStatus batteryStatus = user.getBatteryStatus();
        MainViewHolder mainViewHolder = (MainViewHolder) holder;
        mainViewHolder.tvUsersName.setText(user.getName());
        mainViewHolder.tvBatteryPercentage.setText(String.valueOf(user.getBattery()));

        //for video conference
        mainViewHolder.imgTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talkListener.callUser(user.getName());
            }
        });

        //for individual text messages
        mainViewHolder.imgMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                //intent.putExtra(ChatActivity.TO_USER, user.getId());
                //intent.putExtra(ChatActivity.TO_USER_TOKEN, user.getToken());
                intent.putExtra(ChatActivity.TO_USER, user);
                v.getContext().startActivity(intent);
            }
        });

        Picasso.with(holder.itemView.getContext()).load(user.getImageURI()) .centerCrop().resize(80, 80)
                .transform(new GoogleLocationFragment.RoundTransformation()).into(mainViewHolder.imgUsersProfileImage);

        if(batteryStatus != null && (batteryStatus.isCharging() || (batteryStatus.isUsbCharge())
                || batteryStatus.isAcCharge())) {
            mainViewHolder.imgBatterIcon.setImageResource(R.drawable.icon_battery_charging);
        }else{
            mainViewHolder.imgBatterIcon.setImageResource(R.drawable.icon_battery);

        }

        //setup unread message count
        if (user.getUndreadMessagesCount() > 0) {
            mainViewHolder.tvUnreadMessagesCount.setText(Integer.parseInt(user.getUndreadMessagesCount() +  ""));
            mainViewHolder.tvUnreadMessagesCount.setVisibility(View.VISIBLE);

        }else{
            mainViewHolder.tvUnreadMessagesCount.setVisibility(View.GONE);
        }

    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imgUsersProfileImage, imgBatterIcon, imgBatteryCharging, imgTalk, imgMessage;
        protected TextView tvUsersName, tvBatteryPercentage, tvUnreadMessagesCount;

        public MainViewHolder(View itemView) {
            super(itemView);
            imgUsersProfileImage = (ImageView) itemView.findViewById(R.id.users_img);
            imgBatterIcon = (ImageView) itemView.findViewById(R.id.img_battery);
            imgBatteryCharging = (ImageView) itemView.findViewById(R.id.img_battery_charging);
            tvUsersName = (TextView) itemView.findViewById(R.id.tv_users_name);
            tvBatteryPercentage = (TextView) itemView.findViewById(R.id.tv_battery_percentage);
            imgTalk = (ImageView) itemView.findViewById(R.id.img_talk);
            imgMessage = (ImageView) itemView.findViewById(R.id.img_message);
            tvUnreadMessagesCount = (TextView) itemView.findViewById(R.id.tv_unread_messages_count);
        }
    }

}


