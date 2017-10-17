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
import hinapolina.com.sharelocation.model.User;

/**
 * Created by polina on 10/16/17.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder>  {
    List<User> users;
    Context context;


    public SearchResultsAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
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
            ((Button)itemView.findViewById(R.id.buttonAddUser)).setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
           String id = user.getId();
        }
    }
}
