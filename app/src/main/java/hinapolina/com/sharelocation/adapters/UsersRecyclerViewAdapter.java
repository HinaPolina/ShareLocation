package hinapolina.com.sharelocation.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.squareup.picasso.Picasso;

import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.TalkListener;
import hinapolina.com.sharelocation.activities.message.ChatActivity;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.ui.BatteryStatus;
import hinapolina.com.sharelocation.ui.Utils;

/**
 * Created by hinaikhan on 10/16/17.
 */

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.MainViewHolder> {

    private final static String TAG = UsersRecyclerViewAdapter.class.getSimpleName();
    private static int imageResourceIndex =0;
    private Context context;
    private List<User> mUsers;
    private FragmentManager fragmentManager;
    private TalkListener talkListener;
    private static final int MESSAGE = 0;
    private static final int VIDEO = 1;

    private String currentUserId;
    private static int[] imageResources = new int[]{
            R.drawable.ic_chat,
            R.drawable.ic_video,

    };

    static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

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
        holder.bmb.clearBuilders();
        holder.bmb.setShadowRadius(10);
        holder.bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_2_1);
        holder.bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_2_1);
        for (int i = 0; i < holder.bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            final SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder()
                    .normalImageRes(getImageResource())
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            switch (index){
                                case MESSAGE:
                                    Intent intent = new Intent(context, ChatActivity.class);
                                    intent.putExtra(ChatActivity.TO_USER, user);
                                    context.startActivity(intent);
                                    break;
                                case VIDEO:
                                    talkListener.callUser(user.getName());
                                    break;
                            }

                            System.err.println("item " + index);
                        }
                    });

            holder.bmb.addBuilder(builder);
        }

        if(currentUserId.equals(user.getId())){
           holder.bmb.setVisibility(View.INVISIBLE);
        }
        BatteryStatus batteryStatus = user.getBatteryStatus();
        MainViewHolder mainViewHolder = (MainViewHolder) holder;
        mainViewHolder.tvUsersName.setText(user.getName());
        mainViewHolder.tvBatteryPercentage.setText(String.valueOf(user.getBattery()));


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

        protected ImageView imgUsersProfileImage, imgBatterIcon, imgBatteryCharging;
        protected TextView tvUsersName, tvBatteryPercentage, tvUnreadMessagesCount;
        protected BoomMenuButton bmb;

        public MainViewHolder(View itemView) {
            super(itemView);
            bmb = (BoomMenuButton) itemView.findViewById(R.id.bmb);
            imgUsersProfileImage = (ImageView) itemView.findViewById(R.id.users_img);
            imgBatterIcon = (ImageView) itemView.findViewById(R.id.img_battery);
            imgBatteryCharging = (ImageView) itemView.findViewById(R.id.img_battery_charging);
            tvUsersName = (TextView) itemView.findViewById(R.id.tv_users_name);
            tvBatteryPercentage = (TextView) itemView.findViewById(R.id.tv_battery_percentage);
            tvUnreadMessagesCount = (TextView) itemView.findViewById(R.id.tv_unread_messages_count);
        }
    }

}


