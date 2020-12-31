package mom.com.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import mom.com.R;
import mom.com.activities.MainActivity;
import mom.com.event.EventResetPromo;
import mom.com.fragment.CartFragment;
import mom.com.model.CartModel;
import mom.com.utils.AppUser;
import mom.com.utils.LocalRepositories;

public class CartFragmentAdapter extends RecyclerView.Adapter<CartFragmentAdapter.ViewHolder> {
    List<CartModel> cartModelList;
    CartFragment context;
    Activity activity;
    AppUser appUser;

    public CartFragmentAdapter(List<CartModel> cartModelList, CartFragment context, Activity activity) {
        this.cartModelList = cartModelList;
        this.context = context;
        this.activity = activity;
        appUser=LocalRepositories.getAppUser(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_cart_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        CartModel cartModel = cartModelList.get(position);

        viewHolder.itemName.setText(cartModel.getItemName());
//        viewHolder.itemActualPrice.setText(/*"₹" +*/ cartModel.getItemDescription());
//        viewHolder.type.setText(cartModel.getType());
        viewHolder.itemDescription.setText(cartModel.getItemDescription());
        viewHolder.price.setText("" + cartModel.getPrice());
        viewHolder.quantity.setText("" + cartModel.getQuantity());

        Glide.with(activity)
                .load(cartModel.getImage())
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .apply(RequestOptions.skipMemoryCacheOf(true))
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(viewHolder.image);


        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventResetPromo(""));
                cartModel.setQuantity(cartModel.getQuantity() + 1);
                cartModel.setPrice(cartModel.getPrice() + Double.valueOf(cartModel.getItemActualPrice()));
                setCartMainPrice("add",cartModel.getItemActualPrice());
                notifyAppUser();
                notifyDataSetChanged();
            }
        });


        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartModel.getQuantity() == 1) {
                    if (cartModelList.size() == 1) {
                        dialogPlusClear(position);
                        return;
                    }
                    cartModelList.remove(position);
//
                } else {
                    cartModel.setQuantity(cartModel.getQuantity() - 1);
                    cartModel.setPrice(cartModel.getPrice() - Double.valueOf(cartModel.getItemActualPrice()));
                }
                setCartMainPrice("remove",cartModel.getItemActualPrice());
                MainActivity.context.CartIcon();
                notifyAppUser();
                notifyDataSetChanged();
                EventBus.getDefault().post(new EventResetPromo(""));
            }
        });

        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cartModelList.size() == 1) {
                    dialogPlusClear(position);
//                    context.getDialog("You can't remove");
                    return;
                }
                int qty=cartModel.getQuantity();
                for (int i = cartModel.getQuantity(); i > 0; i--) {
                    if (cartModel.getQuantity() == 1) {
                        cartModelList.remove(position);
                    }
                    cartModel.setQuantity(cartModel.getQuantity() - 1);
                    cartModel.setPrice(cartModel.getPrice() - Double.valueOf(cartModel.getItemActualPrice()));
                    notifyAppUser();
                    notifyDataSetChanged();
                }

                setCartMainPrice("remove",""+(qty*Double.valueOf(cartModel.getItemActualPrice())));
                MainActivity.context.CartIcon();
                notifyDataSetChanged();
                EventBus.getDefault().post(new EventResetPromo(""));

            }
        });


    }


    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName;
        //        TextView itemActualPrice;
//        TextView type;
        TextView price;
        TextView quantity;
        TextView minus;
        TextView plus;
        ImageView image;
        TextView itemDescription;
        CardView cancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
//            itemActualPrice = itemView.findViewById(R.id.itemActualPrice);
//            type = itemView.findViewById(R.id.type);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);
            minus = itemView.findViewById(R.id.minus);
            plus = itemView.findViewById(R.id.plus);
            image = itemView.findViewById(R.id.image);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            cancel = itemView.findViewById(R.id.cancel);

        }
    }


    void setCartMainPrice(String operation, String amount) {
        if (operation.equals("add")) {
            notifyAppUser();
            appUser=LocalRepositories.getAppUser(activity);
            appUser.setAmount(appUser.getAmount() + Double.valueOf(amount));
            LocalRepositories.saveAppUser(activity,appUser);
            CartFragment.context.setAmount((appUser.getAmount()));
//            CartActivity.context.setAmount((CartActivity.context.getAmount() + Double.valueOf(amount)));
        } else {
            notifyAppUser();
            appUser=LocalRepositories.getAppUser(activity);
            appUser.setAmount(appUser.getAmount() - Double.valueOf(amount));
            LocalRepositories.saveAppUser(activity,appUser);
            CartFragment.context.setAmount((appUser.getAmount()));
//            CartActivity.context.setAmount((CartActivity.context.getAmount() - Double.valueOf(amount)));
        }
    }


    void notifyAppUser(){
        appUser=LocalRepositories.getAppUser(activity);
        appUser.setCartModels(cartModelList);
        LocalRepositories.saveAppUser(activity,appUser);
    }



    public void dialogPlusClear(int position) {
        DialogPlus dialog = DialogPlus.newDialog(activity)
                .setHeader(R.layout.clear_header)
                .setContentHolder(new GridHolder(2))
                .setGravity(Gravity.TOP)
//                .setAdapter(new CancelDialogAdapter(this, 2))
                .setMargin(15, 100, 15, 20)
                .setPadding(0, 0, 0, 50)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

                    }
                }).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        Log.e(" inside", " dialog is dismiss issssssssssssssss");
                        view.findViewById(R.id.footer_close_button);
                        if (view.getId()==R.id.yes){
                            try {
                                CartModel cartModel=cartModelList.get(position);
                                for (int i = cartModel.getQuantity(); i > 0; i--) {
                                    if (cartModel.getQuantity() == 1) {
                                        cartModelList.remove(position);
                                    }
                                    cartModel.setQuantity(cartModel.getQuantity() - 1);
                                    cartModel.setPrice(cartModel.getPrice() - Double.valueOf(cartModel.getItemActualPrice()));
                                    notifyAppUser();
                                    notifyDataSetChanged();
                                    EventBus.getDefault().post(new EventResetPromo(""));
                                }
//                                removeOnNotify(MomItemDetailAdapter.context, cartModelList.get(position));
                            }catch (Exception e){

                            }
//                            cartModelList.remove(position);
//                            context.findViewById(R.id.icon_id).setVisibility(View.GONE);
                            appUser=LocalRepositories.getAppUser(activity);
                            appUser.setCartModels(null);
                            appUser.setMomMobile("");
                            appUser.setAmount(0.0);
                            LocalRepositories.saveAppUser(activity,appUser);
                            context.onResume();
                            MainActivity.context.CartIcon();
                        }else {

                        }
                        dialog.dismiss();
                    }
                })
                .setFooter(R.layout.cancel_footer)
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }

}
