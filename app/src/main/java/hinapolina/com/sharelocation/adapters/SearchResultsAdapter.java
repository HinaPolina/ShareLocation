package hinapolina.com.sharelocation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanks.htextview.line.LineTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.listener.MassageSenderListener;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.FirebaseHelper;

/**
 * Created by polina on 10/16/17.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    List<User> users;
    Context context;
    String currentId;
    FirebaseHelper firebaseHelper;
    String token;
    String currentName;


    public SearchResultsAdapter(List<User> users, Context context, String id, FirebaseHelper f, String token, String name) {
        this.users = users;
        this.context = context;
        currentId = id;
        firebaseHelper = f;
        this.token = token;
        currentName = name;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.user_item,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        holder.setUser(user);
        if (user.isFriend()) {
            holder.button.setText(R.string.remove_friend);
        } else {
            holder.button.setText(R.string.add_user);
        }


        Picasso.with(context)
                .load(user.getImageURI().replaceAll("large", "small"))
                .centerCrop()
                .resize(80, 80)
                .transform(new GoogleLocationFragment.RoundTransformation())
                .into(holder.image);
        holder.name.setText(user.getName());
//        holder.name.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

        //added RotateAnimation for user image profile
        RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);

        // Start animating the image
        holder.image.startAnimation(anim);

        // Later.. stop the animation
        holder.image.setAnimation(null);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView image;
        Button button;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        User user;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.userName);
            image = (ImageView) itemView.findViewById(R.id.userImage);
            button = (Button) itemView.findViewById(R.id.buttonAddUser);
            button.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            String id = user.getId();

            firebaseHelper.removeAddUser(id, currentId, user.isFriend());

            if (user.isFriend()) {
                setButtonUserUnFriend();
            } else if (!user.isFriend()) {
                setButtonUserFriend();
            }

//            button.setText(user.isFriend()?R.string.add_user:R.string.remove_friend);
            user.setFriend(!user.isFriend());

            if (context instanceof MassageSenderListener) {
                if (user.isFriend()) {
                    ((MassageSenderListener) context).onSendMassageListener(user, "User " + user.getName() + " add you to friend");
                }
            }

        }

        private void setButtonUserFriend() {
            SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            alertDialog.setTitleText("Great!")
                    .setContentText("Your Friend has been Added!")
                    .setCustomImage(R.drawable.custom_img)
                    .show();
        }

        private void setButtonUserUnFriend() {
            SweetAlertDialog sd = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
            sd.setTitleText("Are you sure?")
                    .setContentText("You want to delete this Friend")
                    .setConfirmText("Yes,delete it!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            // reuse previous dialog instance
                            sDialog.setTitleText("Deleted!")
                                    .setContentText("Your Friend has been deleted!")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    })
                    .show();

        }
    }
}
