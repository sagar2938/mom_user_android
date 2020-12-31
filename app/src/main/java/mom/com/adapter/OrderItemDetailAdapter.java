package mom.com.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import mom.com.R;
import mom.com.network.response.order.ProductList;
import mom.com.utils.Preferences;


public class OrderItemDetailAdapter extends RecyclerView.Adapter<OrderItemDetailAdapter.ViewHolder> {

    Context context;
    List<ProductList> response;
    public OrderItemDetailAdapter(Context mContext, List<ProductList> orderDataList) {
        response=orderDataList;
        context=mContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public OrderItemDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_item, parent, false);
        return new OrderItemDetailAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final OrderItemDetailAdapter.ViewHolder viewHolder, int position) {

        ProductList productList=response.get(position);
        viewHolder.itemName.setText(productList.getFoodItem());
        viewHolder.itemQuantity.setText(productList.getQuantity());
        viewHolder.itemAmount.setText(""+(Double.valueOf(productList.getQuantity())*Double.valueOf(productList.getItemActualPrice())));

        Glide.with(context)
                .load(response.get(position).getImage())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(viewHolder.image);

    }

    @Override
    public int getItemCount() {
        return response.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        TextView itemQuantity;
        TextView itemAmount;
        ImageView image;

        public ViewHolder(View view) {
            super(view);
            itemName=view.findViewById(R.id.itemName);
            itemQuantity=view.findViewById(R.id.itemQuantity);
            itemAmount=view.findViewById(R.id.itemAmount);
            image=view.findViewById(R.id.image);

        }

    }



}
