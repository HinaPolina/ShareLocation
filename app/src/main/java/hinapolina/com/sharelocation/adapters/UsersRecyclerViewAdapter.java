package hinapolina.com.sharelocation.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.ui.BatteryStatus;

/**
 * Created by hinaikhan on 10/16/17.
 */

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.MainViewHolder> {

    private final static String TAG = UsersRecyclerViewAdapter.class.getSimpleName();
    private Context context;
    private List<User> mUsers;
    private FragmentManager fragmentManager;

    private FirebaseAuth mAuth;

    public UsersRecyclerViewAdapter(Context context, List<User> mUsers) {
        this.context = context;
        this.mUsers = mUsers;
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
        mAuth = FirebaseAuth.getInstance();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {


        final User user = mUsers.get(position);
        BatteryStatus batteryStatus = user.getBatteryStatus();
        MainViewHolder mainViewHolder = (MainViewHolder) holder;
        mainViewHolder.tvUsersName.setText(user.getName());
        mainViewHolder.tvBatteryPercentage.setText(String.valueOf(user.getBattery()));
        Picasso.with(holder.itemView.getContext()).load(user.getImageURI()) .resize(80, 80)
                .transform(new GoogleLocationFragment.RoundTransformation()).into(mainViewHolder.imgUsersProfileImage);

        if(batteryStatus != null && (batteryStatus.isCharging() || (batteryStatus.isUsbCharge())
                || batteryStatus.isAcCharge())) {
            mainViewHolder.imgBatterIcon.setImageResource(R.drawable.icon_battery_charging);
        }else{
            mainViewHolder.imgBatterIcon.setImageResource(R.drawable.icon_battery);

        }


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imgUsersProfileImage, imgBatterIcon, imgBatteryCharging;
        protected TextView tvUsersName, tvBatteryPercentage;

        public MainViewHolder(View itemView) {
            super(itemView);
            imgUsersProfileImage = (ImageView) itemView.findViewById(R.id.users_img);
            imgBatterIcon = (ImageView) itemView.findViewById(R.id.img_battery);
            imgBatteryCharging = (ImageView) itemView.findViewById(R.id.img_battery_charging);
            tvUsersName = (TextView) itemView.findViewById(R.id.tv_users_name);
            tvBatteryPercentage = (TextView) itemView.findViewById(R.id.tv_battery_percentage);



        }
    }


}


