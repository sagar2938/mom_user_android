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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import mom.com.R;
import mom.com.activities.PromoDetailActivity;
import mom.com.network.Offer_data;

public class OfferHorizontalAdapter extends RecyclerView.Adapter<OfferHorizontalAdapter.ViewHolder> {

    List<Offer_data> toprated_list;
    Context context;

    public OfferHorizontalAdapter(List<Offer_data> toprated_list, Context context) {

        this.toprated_list = toprated_list;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_horizontal,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        Offer_data vendorData=toprated_list.get(position);
        Glide.with(context)
                .load(vendorData.getImage())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(viewHolder.image);

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromoDetailActivity.offer_data=vendorData;
                context.startActivity(new Intent(context,PromoDetailActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return toprated_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        LinearLayout mainLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            mainLayout=itemView.findViewById(R.id.mainLayout);
        }
    }

}
