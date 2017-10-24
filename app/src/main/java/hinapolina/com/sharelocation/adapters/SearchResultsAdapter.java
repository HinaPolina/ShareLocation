package hinapolina.com.sharelocation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.listener.MassageSenderListener;
import hinapolina.com.sharelocation.model.User;
import hinapolina.com.sharelocation.network.FirebaseHelper;

/**
 * Created by polina on 10/16/17.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>  {
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
        if(user.isFriend()) {
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
         }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            image = (ImageView)itemView.findViewById(R.id.userImage);
            button = (Button)itemView.findViewById(R.id.buttonAddUser);
            button.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
           String id = user.getId();

            firebaseHelper.removeAddUser(id, currentId, user.isFriend());
            button.setText(user.isFriend()?R.string.add_user:R.string.remove_friend);
            user.setFriend(!user.isFriend());

            if(context instanceof MassageSenderListener){
               if(user.isFriend()) {
                   ((MassageSenderListener) context).onSendMassageListener(user, "User " + user.getName() + " add you to friend");
               }
            }

        }
    }
}
