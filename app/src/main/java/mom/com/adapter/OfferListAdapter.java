package mom.com.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import mom.com.R;
import mom.com.network.Offer_data;

import static android.app.Activity.RESULT_OK;

public class OfferListAdapter extends RecyclerView.Adapter<OfferListAdapter.ViewHolder> {

    List<Offer_data> offerList;
    Activity context;

    public OfferListAdapter(List<Offer_data> offerList, Activity context) {
        this.offerList = offerList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_offer,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        Offer_data offer_data=offerList.get(position);
        Glide.with(context)
                .load(offer_data.getImage())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(viewHolder.image);


        viewHolder.offerName.setText(offer_data.getOfferName());
        viewHolder.offerType.setText(offer_data.getOfferType());
        viewHolder.promocode.setText(offer_data.getPromocode());
        viewHolder.description.setText(offer_data.getDescription());

        viewHolder.apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = context.getIntent();
                intent.putExtra("promo", offer_data.getPromocode());
                intent.putExtra("id", offer_data.getId());
                context.setResult(RESULT_OK, intent);
                context.finish();

            }
        });


    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView offerName;
        TextView offerType;
        TextView promocode;
        TextView description;
        LinearLayout apply;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.image);
            offerName=itemView.findViewById(R.id.offerName);
            offerType=itemView.findViewById(R.id.offerType);
            promocode=itemView.findViewById(R.id.promocode);
            description=itemView.findViewById(R.id.description);
            apply=itemView.findViewById(R.id.apply);
        }
    }

}
