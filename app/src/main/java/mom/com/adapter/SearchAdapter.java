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

import mom.com.activities.BaseFragment;
import mom.com.activities.MomItemDetailActivity;
import mom.com.R;
import mom.com.network.response.MenuList;
import mom.com.network.response.VendorDataSearch;
import mom.com.utils.Preferences;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {
    public static List<VendorDataSearch> responseList;
    public static List<VendorDataSearch> filteredList;
    Activity context;
    BaseFragment fragment;

    public SearchAdapter(BaseFragment fragment,List<VendorDataSearch> responseList, Activity context) {
        this.responseList = responseList;
        this.filteredList = responseList;
//        this.responseList .addAll( responseList);
//        this.filteredList .addAll( responseList);
        this.context = context;
        this.fragment=fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.top_category_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        VendorDataSearch vendorData = filteredList.get(position);

        Glide.with(context)
                .load(vendorData.getImage())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(viewHolder.image);

        viewHolder.name.setText(vendorData.getFisrtName() + " " + vendorData.getMiddleName() + " " + vendorData.getLastName());
        viewHolder.tittle.setText(vendorData.getSpecialization());
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vendorData.getOnlineStatus()==0){
                    fragment.getDialog("Sorry for inconvenience","Sorry "+vendorData.getFisrtName()+" "+vendorData.getMiddleName()+" "+vendorData.getLastName()+" is not serving any order now. Please select the another MOM the proceed.");
                    return;
                }
                Preferences.getInstance(context).setMomMobile(vendorData.getMobile());
                Preferences.getInstance(context).setMom_latitude(vendorData.getLatitude());
                Preferences.getInstance(context).setMom_longitude(vendorData.getLongitude());

                Intent intent = new Intent(context, MomItemDetailActivity.class);
                intent.putExtra("mobile", vendorData.getMobile());
                intent.putExtra("name", vendorData.getFisrtName() + " " + vendorData.getMiddleName() + " " + vendorData.getLastName());
                intent.putExtra("rating", vendorData.getRating());
                intent.putExtra("address", vendorData.getSpecialization());
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


    }

    @Override
    public int getItemCount() {
        try {
            return filteredList.size();
        } catch (Exception e) {
            return 0;

        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        LinearLayout container;
        TextView name;
        TextView tittle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            tittle = itemView.findViewById(R.id.tittle);
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                List<VendorDataSearch> filteredList1 = new ArrayList<>();
                if (charString.isEmpty()) {
                    filteredList1 = responseList;
                } else {
                    for (VendorDataSearch vendorData : responseList) {
                        if (vendorData.getSpecialization() != null) {
                            if (vendorData.getSpecialization().toLowerCase().toLowerCase().startsWith(charString) || vendorData.getSpecialization().toLowerCase().contains(charString)) {
                                filteredList1.add(vendorData);
                            }if (vendorData.getFisrtName().toLowerCase().toLowerCase().startsWith(charString) || vendorData.getFisrtName().toLowerCase().contains(charString)) {
                                filteredList1.add(vendorData);
                            }if (vendorData.getMiddleName().toLowerCase().toLowerCase().startsWith(charString) || vendorData.getMiddleName().toLowerCase().contains(charString)) {
                                filteredList1.add(vendorData);
                            }if (vendorData.getLastName().toLowerCase().toLowerCase().startsWith(charString) || vendorData.getLastName().toLowerCase().contains(charString)) {
                                filteredList1.add(vendorData);
                            }
                        }

                        for (MenuList menuList : vendorData.getMenuList()) {
                            if (menuList.getItemName().toLowerCase().startsWith(charString) || menuList.getItemName().toLowerCase().contains(charString)) {
                                if (!filteredList1.contains(vendorData)){
                                    filteredList1.add(vendorData);
                                }
                            }
                            if (menuList.getFood_type().toLowerCase().toLowerCase().startsWith(charString) || menuList.getFood_type().toLowerCase().contains(charString)) {
                                if (!filteredList1.contains(vendorData)){
                                    filteredList1.add(vendorData);
                                }
                            }
                        }
                    }
                    SearchAdapter.filteredList = filteredList1;
                }
                System.out.println("http result " + filteredList);
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList1;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<VendorDataSearch>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
