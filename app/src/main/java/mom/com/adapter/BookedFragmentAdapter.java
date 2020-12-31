package mom.com.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mom.com.R;
import mom.com.activities.BaseActivity;
import mom.com.activities.BaseFragment;
import mom.com.activities.TrackingActivity;
import mom.com.network.ApiCallService;
import mom.com.network.ThisApp;
import mom.com.network.request.EventPushRequest;
import mom.com.network.response.PushNotificationResponse;
import mom.com.network.response.SucessResponse;
import mom.com.network.response.SucessResponse;
import mom.com.network.response.order.OrderDatum;
import mom.com.utils.CustomProgressDialog;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BookedFragmentAdapter extends RecyclerView.Adapter<BookedFragmentAdapter.ViewHolder> {

    Activity context;
    List<OrderDatum> response;
    BaseFragment baseFragment;

    public BookedFragmentAdapter(Activity mContext, BaseFragment baseFragment, List<OrderDatum> orderDataList) {
        this.context = mContext;
        response = orderDataList;
        this.baseFragment = baseFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public BookedFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_booked, parent, false);
        return new BookedFragmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookedFragmentAdapter.ViewHolder viewHolder, int position) {
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.recyclerView.setAdapter(new OrderItemDetailAdapter(context, response.get(position).getProductList()));

        viewHolder.name.setText(response.get(position).getName());
        viewHolder.waitingFor.setText("Waiting for "+response.get(position).getMom_name()+" to accept the order    ");
        viewHolder.address.setText(response.get(position).getLocation());
        viewHolder.totalAmount.setText("₹" + response.get(position).getTotalPrice());
        viewHolder.time.setText(response.get(position).getCreatedAt());
        viewHolder.orderId.setText(response.get(position).getOrderId());


        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (response.get(position).getOrderStatus() == 0) {
                    getDialog(position);
                }
            }
        });

        if (response.get(position).getOrderStatus() == 0) {
            viewHolder.deleteCart.setCardBackgroundColor(context.getResources().getColor(R.color.red));
        } else {
            viewHolder.deleteCart.setCardBackgroundColor(context.getResources().getColor(R.color.divider_menu));
        }

        viewHolder.note.setText(response.get(position).getNote());
        if (response.get(position).getNote().trim().equals("")) {
            viewHolder.note.setVisibility(View.GONE);
        } else {
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
        TextView waitingFor;
        TextView address;
        TextView totalAmount;
        TextView time;
        TextView orderId;
        LinearLayout track;
        ImageView image;
        ProgressBar progressBar;
        TextView note;
        CardView deleteCart;
        LinearLayout cancel;

        public ViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.recyclerView);
            name = view.findViewById(R.id.name);
            waitingFor = view.findViewById(R.id.waitingFor);
            address = view.findViewById(R.id.address);
            totalAmount = view.findViewById(R.id.totalAmount);
            time = view.findViewById(R.id.time);
            orderId = view.findViewById(R.id.orderId);
            track = view.findViewById(R.id.track);
            image = view.findViewById(R.id.image);
            progressBar = view.findViewById(R.id.progressBar);
            note = view.findViewById(R.id.note);
            deleteCart = view.findViewById(R.id.deleteCart);
            cancel = view.findViewById(R.id.cancel);

        }

    }


    public void getDialog(Integer position) {
        new AlertDialog.Builder(context)
                .setTitle("Cancel?")
                .setMessage("Do you want to cancel?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CustomProgressDialog.getInstance(context).show();
                        Map map = new HashMap();
                        map.put("orderId", response.get(position).getOrderId());
                        ThisApp.getApi(context).deleteOrder(map).enqueue(new Callback<SucessResponse>() {
                            @Override
                            public void onResponse(Call<SucessResponse> call, Response<SucessResponse> response) {
                                CustomProgressDialog.setDismiss();
                                if (response.code()==200){
                                    if (response.body().getResponse().getConfirmation() == 1) {
                                        try {
                                            EventPushRequest request = new EventPushRequest();
                                            request.setTo(Preferences.getInstance(context).getMomMobile());
                                            request.setMessage("" + BookedFragmentAdapter.this.response.get(position).getId() + " has canceled by " + Preferences.getInstance(context).getName());
//                                        ApiCallService.action2(context, request, ApiCallService.Action.ACTION_PUSH_NOTIFICATION);
                                            ThisApp.getApi(context).pushNotification(request).enqueue(new Callback<PushNotificationResponse>() {
                                                @Override
                                                public void onResponse(Call<PushNotificationResponse> call, Response<PushNotificationResponse> response) {

                                                }

                                                @Override
                                                public void onFailure(Call<PushNotificationResponse> call, Throwable t) {

                                                }
                                            });
                                        } catch (Exception e) {
                                        }
                                        BookedFragmentAdapter.this.response.remove(position);
                                        BookedFragmentAdapter.this.notifyDataSetChanged();
//                                    baseFragment.getDialog(response.body().getResponse().getMessage());
                                        map.put("mobile", Preferences.getInstance(context).getMobile());
//                                        ApiCallService.action2(context, map, ApiCallService.Action.ACTION_BOOKED_ORDER);
                                    } else {
                                        baseFragment.getDialog(response.body().getResponse().getMessage());
                                    }
                                }else {
                                   getDialog("Some thing went wrong "+response.code());
                               }
                            }

                            @Override
                            public void onFailure(Call<SucessResponse> call, Throwable t) {
                                CustomProgressDialog.setDismiss();
                                baseFragment.getDialog(t.getMessage());
                            }
                        });

                    }
                })
                .setNegativeButton("No", null)
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    public void getDialog( String message) {
        new AlertDialog.Builder(context)
                .setTitle("Sorry")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
//                .setNegativeButton("Cancel", null)
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }



}
