package mom.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnClickListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import mom.com.R;
import mom.com.activities.CartActivity;
import mom.com.model.CartMainModel;
import mom.com.model.CartModel;
import mom.com.network.ThisApp;
import mom.com.network.response.AddResponse;
import mom.com.network.response.order.OrderDatum;
import mom.com.network.response.order.ProductList;
import mom.com.utils.AppUser;
import mom.com.utils.CustomProgressDialog;
import mom.com.utils.LocalRepositories;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HistoryFragmentAdapter extends RecyclerView.Adapter<HistoryFragmentAdapter.ViewHolder> {

    Activity context;
    List<OrderDatum> response;

    public HistoryFragmentAdapter(Activity mContext, List<OrderDatum> orderDataList) {
        this.context = mContext;
        response = orderDataList;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public HistoryFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_history, parent, false);
        return new HistoryFragmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HistoryFragmentAdapter.ViewHolder viewHolder, int position) {
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.recyclerView.setAdapter(new OrderItemDetailAdapter(context, response.get(position).getProductList()));

        OrderDatum orderDatum = response.get(position);
        viewHolder.name.setText(response.get(position).getName());
        viewHolder.address.setText(response.get(position).getLocation());
        viewHolder.totalAmount.setText("₹" + response.get(position).getTotalPrice());
        viewHolder.time.setText(response.get(position).getCreatedAt());
        viewHolder.orderId.setText(response.get(position).getOrderId());
        viewHolder.note.setText(response.get(position).getNote());
        if (response.get(position).getNote()==null){
            viewHolder.note.setVisibility(View.GONE);
        }else {
            if (response.get(position).getNote().trim().equals("")){
                viewHolder.note.setVisibility(View.GONE);
            }else {
                viewHolder.note.setVisibility(View.VISIBLE);
            }
        }

        if (orderDatum.getRatingStatus() == 0) {
            viewHolder.rateTxt.setTextColor(context.getResources().getColor(R.color.default_text));
            viewHolder.rateTxt.setText("Rate Us");
            ImageViewCompat.setImageTintList(viewHolder.rateIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_light)));
        }else if (orderDatum.getRatingStatus() == 2) {
            viewHolder.rateTxt.setTextColor(context.getResources().getColor(R.color.default_text));
            viewHolder.rateTxt.setText("Rate Us");
            ImageViewCompat.setImageTintList(viewHolder.rateIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_light)));
        } else {
            viewHolder.rateTxt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            viewHolder.rateTxt.setText(orderDatum.getCustomerRating());
            ImageViewCompat.setImageTintList(viewHolder.rateIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
        }

        if (orderDatum.getFavourate() == 0) {
            viewHolder.favouriteTxt.setTextColor(context.getResources().getColor(R.color.default_text));
            ImageViewCompat.setImageTintList(viewHolder.favouriteIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_light)));
        } else {
            viewHolder.favouriteTxt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            ImageViewCompat.setImageTintList(viewHolder.favouriteIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
        }
        if (orderDatum.getOrderStatus() == 4) {
            viewHolder.status.setText("Cancelled");
            viewHolder.favouriteTxt.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            viewHolder.status.setText("Delivered");
            viewHolder.favouriteTxt.setTextColor(context.getResources().getColor(R.color.green));
        }

        if (!Preferences.getInstance(context).getProfileImage().equals("")) {
            Glide.with(context)
                    .load(Preferences.getInstance(context).getProfileImage())
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .apply(RequestOptions.skipMemoryCacheOf(true))
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
//                .apply(new RequestOptions().placeholder(R.drawable.user))
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
        /*viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map map=new HashMap();
                map.put("orderId",response.get(position).getOrderId());
                ApiCallService.action(context, map, ApiCallService.Action.ACTION_ACCEPT_ORDER);
            }
        });*/

        viewHolder.rateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.rateTxt.getText().toString().equals("Rate Us")) {
                    ratingDialog(orderDatum);
                }
            }
        });


        viewHolder.repeatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ProductList> productList = orderDatum.getProductList();
                CartMainModel cartMainModel = new CartMainModel();
                List<CartModel> product_list = new ArrayList<>();
                Preferences.getInstance(context).setMomMobile(orderDatum.getMomMobile());
                double amt=0.0;
                for (int i = 0; i < productList.size(); i++) {
                    CartModel cartModel = new CartModel();
                    cartModel.setQuantity(Integer.valueOf(productList.get(i).getQuantity()));
                    cartModel.setItem_id(Integer.valueOf(productList.get(i).getItemId()));
                    cartModel.setFood_item(productList.get(i).getFoodItem());
                    cartModel.setItemActualPrice(productList.get(i).getItemActualPrice());
                    cartModel.setType(productList.get(i).getPType());
                    cartModel.setItemDescription(productList.get(i).getItemDescription());
                    cartModel.setImage(productList.get(i).getImage());
                    cartModel.setPrice(cartModel.getQuantity() * Double.valueOf(cartModel.getItemActualPrice()));
                    product_list.add(cartModel);
                    amt=amt+Integer.valueOf(productList.get(i).getQuantity())*Double.valueOf(productList.get(i).getItemActualPrice());
                }
                cartMainModel.setProduct_list(product_list);
                AppUser appUser = LocalRepositories.getAppUser(context);
                appUser.setCartModels(product_list);
                appUser.setAmount(amt);
                LocalRepositories.saveAppUser(context, appUser);

//                cartMainModel.setMom_mobile(orderDatum.getMomMobile());
//                cartMainModel.setLocation(orderDatum.getLocation());
//                cartMainModel.setLatitude(orderDatum.getLatitude());
//                cartMainModel.setLongitude(orderDatum.getLongitude());
//                cartMainModel.setTotal_price(orderDatum.getTotalPrice());
////                cartMainModel.setNote(orderDatum.getnote);
//                cartMainModel.setNote("");
//                cartMainModel.setName(orderDatum.getName());
//                cartMainModel.setMobile(orderDatum.getMobile());
////                cartMainModel.setPaymentMode(Preferences.getInstance(context).getMode());
//                cartMainModel.setPaymentMode("");

                context.startActivity(new Intent(context, CartActivity.class));


            }
        });

        viewHolder.favouriteLayout.setVisibility(View.GONE);

        viewHolder.favouriteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomProgressDialog.getInstance(context).show();
                Map map = new Hashtable();
                map.put("mobile", Preferences.getInstance(context).getMobile());
                map.put("momId", orderDatum.getMomId());
                ThisApp.getApi(context).favourite(map).enqueue(new Callback<AddResponse>() {
                    @Override
                    public void onResponse(Call<AddResponse> call, Response<AddResponse> response) {
                        CustomProgressDialog.setDismiss();
                        if (orderDatum.getFavourate() == 0) {
                            orderDatum.setFavourate(1);
                        } else {
                            orderDatum.setFavourate(0);
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<AddResponse> call, Throwable t) {
                        CustomProgressDialog.setDismiss();
                    }
                });

            }
        });


       /* viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog("Cancel","Are you sure you want to cancel this order?",position);
            }
        });*/
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
        LinearLayout cancel;
        ImageView image;
        ProgressBar progressBar;
        TextView status;
        LinearLayout rateLayout;
        ImageView rateIcon;
        TextView rateTxt;

        LinearLayout favouriteLayout;
        ImageView favouriteIcon;
        TextView favouriteTxt;

        LinearLayout repeatLayout;
        TextView note;

        public ViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.recyclerView);
            name = view.findViewById(R.id.name);
            address = view.findViewById(R.id.address);
            totalAmount = view.findViewById(R.id.totalAmount);
            time = view.findViewById(R.id.time);
            orderId = view.findViewById(R.id.orderId);
            track = view.findViewById(R.id.track);
            cancel = view.findViewById(R.id.cancel);
            image = view.findViewById(R.id.image);
            progressBar = view.findViewById(R.id.progressBar);
            status = view.findViewById(R.id.status);
            rateLayout = view.findViewById(R.id.rateLayout);
            rateIcon = view.findViewById(R.id.rateIcon);
            rateTxt = view.findViewById(R.id.rateTxt);
            favouriteLayout = view.findViewById(R.id.favouriteLayout);
            favouriteIcon = view.findViewById(R.id.favouriteIcon);
            favouriteTxt = view.findViewById(R.id.favouriteTxt);
            repeatLayout = view.findViewById(R.id.repeatLayout);
            note = view.findViewById(R.id.note);

        }

    }


    void ratingDialog(OrderDatum orderDatum) {
        DialogPlus dialogPlus = null;
        DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(context);
        dialogPlusBuilder.setHeader(R.layout.rating_dialog);
        dialogPlusBuilder.setContentHolder(new GridHolder(1));
        dialogPlusBuilder.setGravity(Gravity.BOTTOM);
        dialogPlusBuilder.setCancelable(true);

        dialogPlusBuilder.setMargin(5, 300, 5, 160);
        dialogPlusBuilder.setPadding(0, 0, 0, 10);

//        dialogPlusBuilder.setFooter(R.layout.footer);
        dialogPlusBuilder.setExpanded(false); // This will enable the expand feature, (similar to android L share dialog)
        dialogPlus = dialogPlusBuilder.create();
        Button button = (Button) dialogPlus.findViewById(R.id.submit);
        RatingBar ratingBar = (RatingBar) dialogPlus.findViewById(R.id.rating);
        RatingBar rateDeliveryBoy = (RatingBar) dialogPlus.findViewById(R.id.rateDeliveryBoy);
        LinearLayout cancel = (LinearLayout) dialogPlus.findViewById(R.id.cancel);
        TextView mom = (TextView) dialogPlus.findViewById(R.id.mom);
        TextView delivery = (TextView) dialogPlus.findViewById(R.id.delivery);

        mom.setText(orderDatum.getMom_name());
        delivery.setText(orderDatum.getDelivery_name());

        DialogPlus finalDialogPlus = dialogPlus;
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map map = new Hashtable();
                map.put("orderId", "" + orderDatum.getOrderId());
                map.put("rating", "0");
                map.put("deliver_rating", "0");
                CustomProgressDialog.getInstance(context).show();
                ThisApp.getApi(context).rating(map).enqueue(new Callback<AddResponse>() {
                    @Override
                    public void onResponse(Call<AddResponse> call, Response<AddResponse> response) {
                        CustomProgressDialog.setDismiss();
                    }

                    @Override
                    public void onFailure(Call<AddResponse> call, Throwable t) {
                        CustomProgressDialog.setDismiss();
                    }
                });
                finalDialogPlus.dismiss();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() == 0) {
                    Toast.makeText(context, "Please rate the MOMCHEFF", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rateDeliveryBoy.getRating() == 0) {
                    Toast.makeText(context, "Please rate the Delivery boy", Toast.LENGTH_SHORT).show();
                    return;
                }
                finalDialogPlus.dismiss();
                Map map = new Hashtable();
                map.put("orderId", orderDatum.getOrderId());
                map.put("rating", "" + ratingBar.getRating());
                map.put("deliver_rating", "" + rateDeliveryBoy.getRating());
                CustomProgressDialog.getInstance(context).show();
//                ApiCallService.action(this,map,ApiCallService.Action.ACTION_RATING);

                ThisApp.getApi(context).rating(map).enqueue(new Callback<AddResponse>() {
                    @Override
                    public void onResponse(Call<AddResponse> call, Response<AddResponse> response) {
                        CustomProgressDialog.setDismiss();
                        orderDatum.setRatingStatus(1);
                        orderDatum.setCustomerRating(String.valueOf(ratingBar.getRating()));
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<AddResponse> call, Throwable t) {
                        CustomProgressDialog.setDismiss();
                    }
                });
            }
        });

        dialogPlusBuilder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(DialogPlus dialog, View view) {
                dialog.dismiss();
            }
        });

        dialogPlus.show();
    }


    /*public void getDialog(String tittle, String message,Integer position) {
        new AlertDialog.Builder(context)
                .setTitle(tittle)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Map map=new HashMap();
                        map.put("orderId",response.get(position).getOrderId());
                        ApiCallService.action(context, map, ApiCallService.Action.ACTION_CANCEL_ORDER);

                    }
                })
                .setNegativeButton("No", null)
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }*/

}
