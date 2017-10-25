package hinapolina.com.sharelocation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import hinapolina.com.sharelocation.R;
import hinapolina.com.sharelocation.fragments.GoogleLocationFragment;
import hinapolina.com.sharelocation.model.Place;

/**
 * Created by polina on 10/25/17.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ItemsHolder> {

    List<Place> placeList;
    Context context;

    public PlaceAdapter(List<Place> placeList, Context context) {
        this.placeList = placeList;
        this.context = context;
    }

    @Override
    public ItemsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.place_item, parent, false);
        ItemsHolder viewHolder = new ItemsHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemsHolder holder, int position) {
        Place place = placeList.get(position);
        holder.setPlace(place);
        holder.name.setText(place.getName());
        System.err.println("image:" + place.getUrl());
        Picasso.with(holder.itemView.getContext()).load(place.getUrl()) .centerCrop().resize(80, 80)
                .transform(new GoogleLocationFragment.RoundTransformation()).placeholder(R.mipmap.ic_location).into(holder.image);


    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class ItemsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageView image;
        Place place;
        public ItemsHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.placeName);
            image = (ImageView) itemView.findViewById(R.id.placeImage);
        }

        @Override
        public void onClick(View v) {

        }

        public Place getPlace() {
            return place;
        }

        public void setPlace(Place place) {
            this.place = place;
        }
    }
}
