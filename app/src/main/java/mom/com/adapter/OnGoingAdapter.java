package mom.com.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import mom.com.R;
import mom.com.activities.MomItemDetailActivity;
import mom.com.network.response.VendorData;

public class OnGoingAdapter extends RecyclerView.Adapter<OnGoingAdapter.ViewHolder>  implements Filterable {
    public static List<VendorData> responseList;
    public static List<VendorData> filteredList;
    Activity context;

    public OnGoingAdapter(List<VendorData> responseList, Activity context) {
        this.responseList = responseList;
        this.filteredList = responseList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.top_category_item_layout,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        VendorData vendorData=responseList.get(position);

        Glide.with(context)
                .load(vendorData.getImageName())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(viewHolder.image);

        viewHolder.name.setText(vendorData.getFisrtName()+" "+vendorData.getMiddleName()+" "+vendorData.getLastName());
       viewHolder.container.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(context, MomItemDetailActivity.class);
               intent.putExtra("mobile",vendorData.getMobile());
               intent.putExtra("name",vendorData.getFisrtName()+" "+vendorData.getMiddleName()+" "+vendorData.getLastName());
               intent.putExtra("rating",vendorData.getRating());
               intent.putExtra("address",vendorData.getSpecialization());
               intent.putExtra("time",vendorData.getEstimateTime());
               intent.putExtra("image",vendorData.getImageName());
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               context.startActivity(intent);
           }
       });


    }

    @Override
    public int getItemCount() {
        return responseList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        LinearLayout container;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container=itemView.findViewById(R.id.container);
            image=itemView.findViewById(R.id.image);
            name=itemView.findViewById(R.id.name);
        }
    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    filteredList = responseList;
                } else {
                    ArrayList<VendorData> filteredList = new ArrayList<>();
                    for (VendorData vendorData : responseList) {
                        if (vendorData.getFisrtName().toLowerCase().toLowerCase().startsWith(charString) || vendorData.getFisrtName().toLowerCase().contains(charString)) {
                            filteredList.add(vendorData);
                        }if (vendorData.getMiddleName().toLowerCase().toLowerCase().startsWith(charString) || vendorData.getMiddleName().toLowerCase().contains(charString)) {
                            filteredList.add(vendorData);
                        }if (vendorData.getLastName().toLowerCase().toLowerCase().startsWith(charString) || vendorData.getLastName().toLowerCase().contains(charString)) {
                            filteredList.add(vendorData);
                        }
                    }
                    OnGoingAdapter.filteredList = filteredList;
                }
                System.out.println("http result "+filteredList);
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<VendorData>) filterResults.values;
                responseList = filteredList;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
