package mom.com.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mom.com.R;
import mom.com.activities.BaseFragment;
import mom.com.activities.MomItemDetailActivity;
import mom.com.network.response.VendorData;
import mom.com.utils.Preferences;

public class TopRatedAdapter extends RecyclerView.Adapter<TopRatedAdapter.ViewHolder> {

    List<VendorData> toprated_list;
    Context context;
    public static Map<String,Integer> map;
    BaseFragment fragment;

    public TopRatedAdapter(List<VendorData> toprated_list, BaseFragment fragment,Context context) {

        this.toprated_list = toprated_list;
        this.context=context;
        this.fragment=fragment;
        map=new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_horizontal2,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        VendorData vendorData=toprated_list.get(position);


        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vendorData.getOnlineStatus()==0){
                    fragment.getDialog("Sorry for inconvenience","Sorry "+vendorData.getFisrtName()+" "+vendorData.getMiddleName()+" "+vendorData.getLastName()+" is not serving any order now. Please select the another MOM the proceed.");
                    return;
                }

                Preferences.getInstance(context).setMomMobile(vendorData.getMobile());
                Preferences.getInstance(context).setMom_latitude(vendorData.getLatitude());
                Preferences.getInstance(context).setMom_longitude(vendorData.getLongitude());

                Intent intent=new Intent(context, MomItemDetailActivity.class);
                intent.putExtra("mobile",vendorData.getMobile());
                intent.putExtra("name",vendorData.getFisrtName()+" "+vendorData.getMiddleName()+" "+vendorData.getLastName());
                intent.putExtra("rating",vendorData.getRating());
                intent.putExtra("address",vendorData.getSpecialization());
                intent.putExtra("time",vendorData.getEstimateTime());
                intent.putExtra("image",vendorData.getImage());
                intent.putExtra("description",vendorData.getDescription());
                intent.putExtra("momId",""+vendorData.getId());
                if (vendorData.getFlag()==1){
                    intent.putExtra("favourite",true);
                }else {
                    intent.putExtra("favourite",false);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        if (position==toprated_list.size()-1){
            viewHolder.space.setVisibility(View.VISIBLE);
        }else {
            viewHolder.space.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(vendorData.getImage())
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .apply(RequestOptions.skipMemoryCacheOf(true))
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(viewHolder.image);

        if (map.get("")!=null){
            if (map.get("")==position){
                viewHolder.mainLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_primary));
            }else {
                viewHolder.mainLayout.setBackground(context.getResources().getDrawable(R.drawable.bg_white));
            }
        }


    }

    @Override
    public int getItemCount() {
        return toprated_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        LinearLayout mainLayout;
        LinearLayout space;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            mainLayout=itemView.findViewById(R.id.mainLayout);
            space=itemView.findViewById(R.id.space);

        }
    }

}
