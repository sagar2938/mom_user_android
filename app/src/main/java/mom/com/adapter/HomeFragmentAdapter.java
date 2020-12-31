package mom.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;

import mom.com.R;
import mom.com.activities.BaseFragment;
import mom.com.activities.MomItemDetailActivity;
import mom.com.helper.FusedLocation;
import mom.com.network.response.MomListResponse;
import mom.com.network.response.VendorData;
import mom.com.utils.Preferences;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder> {
    MomListResponse response;
    Context context;
    BaseFragment fragment;

    public HomeFragmentAdapter(MomListResponse response, BaseFragment fragment, Context context) {
        this.response = response;
        this.context = context;
        this.fragment=fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_mom_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        VendorData vendorData = response.getVendor_data().get(position);

        if(!StringUtils.isEmpty(vendorData.getImage())) {
            Glide.with(context)
                    .load(vendorData.getImage())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .apply(new RequestOptions().placeholder(R.drawable.default_food))
                    .into(viewHolder.momImage);
        }else{
            viewHolder.momImage.setImageDrawable(null);
        }

        if (vendorData.getImageName() != null && !vendorData.getImageName().equals("")) {

                Glide.with(context)
                        .load(vendorData.getImageName())
                  //  .transition(DrawableTransitionOptions.withCrossFade())
                   // .apply(RequestOptions.skipMemoryCacheOf(true))
                   // .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .apply(new RequestOptions().placeholder(R.drawable.user))
                        .into(viewHolder.momProfileImage);

                /*Glide.with(context)
                        .load(vendorData.getImageName())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                        .apply(new RequestOptions().placeholder(R.drawable.bg_circle))
                        .into(viewHolder.momProfileImage);*/

        }else{
            viewHolder.momProfileImage.setImageDrawable(context.getResources().getDrawable(R.drawable.user));
        }

        /*Glide.with(context)
                .load(vendorData.getImage())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(viewHolder.momProfileImage);*/

        viewHolder.momTittle.setText(WordUtils.capitalizeFully(vendorData.getFisrtName() + " " + vendorData.getMiddleName() + " " + vendorData.getLastName()));
        viewHolder.rating.setText(vendorData.getRating());
        viewHolder.address.setText(vendorData.getSpecialization());
        viewHolder.momDescription.setText(vendorData.getPromocode());
        if (vendorData.getOnlineStatus()==1){
            viewHolder.status.setText("OPEN");
            viewHolder.status.setBackgroundColor(context.getResources().getColor(R.color.green));
            viewHolder.status.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.status.setVisibility(View.VISIBLE);
            viewHolder.status.setText("CLOSED");
            viewHolder.status.setBackgroundColor(context.getResources().getColor(R.color.red));
        }

        try {

            Integer minute = Integer.valueOf(vendorData.getEstimateTime().toString());

            int day = minute / 24 / 60;
            int hour = minute / 60 % 24;
            int min = minute % 60;


            if (day == 0) {
                if (hour == 0) {
                    viewHolder.time.setText("" + min + " Min");
                } else {
                    viewHolder.time.setText(hour + " Hour " + min + " Min");
                }
            } else {
                viewHolder.time.setText(day + " Day " + hour + " Hour " + min + " Min");
            }


        } catch (Exception e) {
            viewHolder.time.setText("");
        }

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (vendorData.getOnlineStatus()==0){
                    fragment.getDialog("Sorry for inconvenience","Sorry "+vendorData.getFisrtName()+" "+vendorData.getMiddleName()+" "+vendorData.getLastName()+" is not serving any order now. Please select the another MOM the proceed.");
                    return;
                }*/
                Preferences.getInstance(context).setMomMobile(vendorData.getMobile());
                Preferences.getInstance(context).setMom_latitude(vendorData.getLatitude());
                Preferences.getInstance(context).setMom_longitude(vendorData.getLongitude());

                Intent intent = new Intent(context, MomItemDetailActivity.class);
                intent.putExtra("mobile", vendorData.getMobile());
                intent.putExtra("name", vendorData.getFisrtName() + " " + vendorData.getMiddleName() + " " + vendorData.getLastName());
                intent.putExtra("rating", vendorData.getRating());
                intent.putExtra("address", viewHolder.address.getText().toString());
                intent.putExtra("time", vendorData.getEstimateTime());
                intent.putExtra("image", vendorData.getImage());
                intent.putExtra("description",vendorData.getDescription());
                intent.putExtra("onlineStatus",vendorData.getOnlineStatus());
                if (vendorData.getFlag()==1){
                    intent.putExtra("favourite",true);
                }else {
                    intent.putExtra("favourite",false);
                }
                intent.putExtra("momId",""+vendorData.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });


        if (position == response.getPosition()) {
            /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            viewHolder.recyclerView.setLayoutManager(linearLayoutManager);
            TopRatedAdapter adapter = new TopRatedAdapter(response.getToprated_list(),fragment, context);
            viewHolder.recyclerView.setAdapter(adapter);
            viewHolder.recyclerView.setFocusable(false);
//            viewHolder.layout.setVisibility(View.VISIBLE);

            VendorData topRated = response.getToprated_list().get(0);
            viewHolder.shopName.setText(topRated.getFisrtName() + " " + topRated.getMiddleName() + " " + topRated.getLastName());
            viewHolder.addressTop.setText(topRated.getSpecialization());
            viewHolder.ratingBar.setRating(Float.parseFloat(topRated.getRating()));
            viewHolder.openTime.setText(topRated.getOpenTime());

            viewHolder.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    try {
                        TopRatedAdapter.map = new HashMap<>();
                        TopRatedAdapter.map.put("", linearLayoutManager.findFirstCompletelyVisibleItemPosition());
                        VendorData topRated = response.getToprated_list().get(linearLayoutManager.findFirstCompletelyVisibleItemPosition());
                        viewHolder.shopName.setText(topRated.getFisrtName() + " " + topRated.getMiddleName() + " " + topRated.getLastName());
                        viewHolder.addressTop.setText(topRated.getSpecialization());
                        viewHolder.ratingBar.setRating(Float.parseFloat(topRated.getRating()));
//                        viewHolder.openTime.setText(topRated.getOpenTime());
                        estimatedTime(viewHolder.openTime,topRated);
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {

                    }

                }
            });*/

        } else {
            viewHolder.layout.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return response.getVendor_data().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView momImage;
        ImageView momProfileImage;
        LinearLayout mainLayout;
        TextView address;
        TextView rating;
        TextView momTittle, time,status;
        LinearLayout layout;
        RecyclerView recyclerView;

        TextView shopName;
        TextView addressTop;
        RatingBar ratingBar;
        TextView openTime;
        TextView momDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.mainLayout);
            layout = itemView.findViewById(R.id.layout);
            momImage = itemView.findViewById(R.id.momImage);
            momProfileImage = itemView.findViewById(R.id.mom_profile_image2);
            momTittle = itemView.findViewById(R.id.momTittle);
            address = itemView.findViewById(R.id.address);
            time = itemView.findViewById(R.id.time);
            rating = itemView.findViewById(R.id.rating);
            status = itemView.findViewById(R.id.status);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            shopName = itemView.findViewById(R.id.shopName);
            addressTop = itemView.findViewById(R.id.addressTop);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            openTime = itemView.findViewById(R.id.openTime);
            momDescription = itemView.findViewById(R.id.momDescription);


        }
    }


    float getEstimatedTime(Double lat, Double lon) {
        int speedIs10MetersPerMinute = 10;
        float estimatedDriveTimeInMinutes = 0;
        Location location1 = new Location("");
        try {
            location1.setLatitude(FusedLocation.location.getLatitude());
            location1.setLongitude(FusedLocation.location.getLongitude());

            Location location2 = new Location("");
            location2.setLatitude(lat);
            location2.setLongitude(lon);
            float distanceInMeters = location1.distanceTo(location2);


            estimatedDriveTimeInMinutes = distanceInMeters / speedIs10MetersPerMinute;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return estimatedDriveTimeInMinutes;

    }

    String getEstimatedTime2(Double lat, Double lon) {

        String str = "http://maps.google.com/maps?saddr=" + FusedLocation.location.getLatitude() + "," + FusedLocation.location.getLongitude() + "&daddr=" + lat + "," + lon + "&ie=UTF8&0&om=0&output=kml";
        return str;

    }


    void estimatedTime(TextView textView,VendorData vendorData){
        try {

            Integer minute = Integer.valueOf(vendorData.getEstimateTime().toString());

            int day = minute / 24 / 60;
            int hour = minute / 60 % 24;
            int min = minute % 60;


            if (day == 0) {
                if (hour == 0) {
                    textView.setText("" + min + " Min");
                } else {
                    textView.setText(hour + " Hour " + min + " Min");
                }
            } else {
                textView.setText(day + " Day " + hour + " Hour " + min + " Min");
            }


        } catch (Exception e) {
            textView.setText("");
        }
    }
}
