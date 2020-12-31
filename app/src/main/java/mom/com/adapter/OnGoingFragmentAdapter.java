package mom.com.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mom.com.R;
import mom.com.activities.TrackingActivity;
import mom.com.network.ApiCallService;
import mom.com.network.ThisApp;
import mom.com.network.response.SucessResponse;
import mom.com.network.response.order.OrderDatum;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OnGoingFragmentAdapter extends RecyclerView.Adapter<OnGoingFragmentAdapter.ViewHolder> {

    Activity context;
    List<OrderDatum> response;

    public OnGoingFragmentAdapter(Activity mContext, List<OrderDatum> orderDataList) {
        this.context = mContext;
        response = orderDataList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public OnGoingFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_new_on_going, parent, false);
        return new OnGoingFragmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OnGoingFragmentAdapter.ViewHolder viewHolder, int position) {
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.recyclerView.setAdapter(new OrderItemDetailAdapter(context, response.get(position).getProductList()));

        viewHolder.name.setText(response.get(position).getName());
        viewHolder.address.setText(response.get(position).getLocation());
        viewHolder.totalAmount.setText("₹" + response.get(position).getTotalPrice());
        viewHolder.time.setText(response.get(position).getCreatedAt());
        viewHolder.orderId.setText(response.get(position).getOrderId());


        viewHolder.track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TrackingActivity.class);
//                Intent intent=new Intent(context, SimpleDirectionActivity.class);
                intent.putExtra("latitude", response.get(position).getLatitude());
                intent.putExtra("longitude", response.get(position).getLongitude());
                intent.putExtra("orderId", response.get(position).getOrderId());
                intent.putExtra("name", response.get(position).getDeliveryBoyName());
                intent.putExtra("mobile", response.get(position).getDeliver_number());
                intent.putExtra("url", response.get(position).getDeliver_number());
                context.startActivity(intent);
            }
        });

        if (response.get(position).getDeliver_number()==null){
            viewHolder.trackLayout.setVisibility(View.GONE);
            viewHolder.waitingLayout.setVisibility(View.VISIBLE);
        }else {
            viewHolder.trackLayout.setVisibility(View.VISIBLE);
            viewHolder.waitingLayout.setVisibility(View.GONE);
        }



        viewHolder.note.setText(response.get(position).getNote());
        if (response.get(position).getNote().trim().equals("")){
            viewHolder.note.setVisibility(View.GONE);
        }else {
            viewHolder.note.setVisibility(View.VISIBLE);
        }

        if (!Preferences.getInstance(context).getProfileImage().equals("")) {
            Glide.with(context)
                    .load(Preferences.getInstance(context).getProfileImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(viewHolder.image);
        }

    }

    @Override
    public int getItemCount() {
        return response.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView name;
        TextView address;
        TextView totalAmount;
        TextView time;
        TextView orderId;
        LinearLayout track;
        ImageView image;
        ProgressBar progressBar;
        TextView note;
        CardView trackLayout;
        CardView waitingLayout;

        public ViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.recyclerView);
            name = view.findViewById(R.id.name);
            address = view.findViewById(R.id.address);
            totalAmount = view.findViewById(R.id.totalAmount);
            time = view.findViewById(R.id.time);
            orderId = view.findViewById(R.id.orderId);
            track = view.findViewById(R.id.track);
            image = view.findViewById(R.id.image);
            progressBar = view.findViewById(R.id.progressBar);
            note = view.findViewById(R.id.note);
            trackLayout = view.findViewById(R.id.trackLayout);
            waitingLayout = view.findViewById(R.id.waitingLayout);

        }

    }



}
