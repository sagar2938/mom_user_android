package mom.com.adapter;

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
import mom.com.activities.CartActivity;
import mom.com.event.EventResetPromo;
import mom.com.model.CartModel;
import mom.com.utils.AppUser;
import mom.com.utils.LocalRepositories;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    List<CartModel> cartModelList;
    CartActivity context;
    AppUser appUser;

    public CartAdapter(List<CartModel> cartModelList, CartActivity context) {
        this.cartModelList = cartModelList;
        this.context = context;
        appUser=LocalRepositories.getAppUser(context);
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

        Glide.with(context)
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
                try {
                    addOnNotify(MomItemDetailAdapter.context, cartModel);
                }catch (Exception e){
                    e.printStackTrace();
                }
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
//                        context.getDialog("You can't remove");
                        return;
                    }
                    cartModelList.remove(position);
//                    MomItemDetailAdapter.context.mPrice.remove(cartModel.getId());
                } else {
                    cartModel.setQuantity(cartModel.getQuantity() - 1);
                    cartModel.setPrice(cartModel.getPrice() - Double.valueOf(cartModel.getItemActualPrice()));
                }
                try {

                }catch (Exception e){
                    removeOnNotify(MomItemDetailAdapter.context, cartModel);
                }
                removeOnNotify(MomItemDetailAdapter.context, cartModel);
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

                for (int i = cartModel.getQuantity(); i > 0; i--) {
                    if (cartModel.getQuantity() == 1) {
                        cartModelList.remove(position);
                    }
                    cartModel.setQuantity(cartModel.getQuantity() - 1);
                    cartModel.setPrice(cartModel.getPrice() - Double.valueOf(cartModel.getItemActualPrice()));
                    removeOnNotify(MomItemDetailAdapter.context, cartModel);
                    notifyAppUser();
                    notifyDataSetChanged();
                }

                notifyDataSetChanged();
                EventBus.getDefault().post(new EventResetPromo(""));

            }
        });


    }

    void removeOnNotify(MomItemDetailAdapter context, CartModel cartModel) {
        try {
            context.mPrice.put(cartModel.getId(), (context.mPrice.get(cartModel.getId()) - Double.valueOf(cartModel.getItemActualPrice())));
            context.mQuantityState.put(cartModel.getId(), "" + (Integer.valueOf(context.mQuantityState.get(cartModel.getId())) - 1));
            /*if (cartModel.getType().equals("Quarter")) {
                if (context.mQuarterQuantity.get(cartModel.getId()) != null) {
                    if (context.mQuarterQuantity.get(cartModel.getId()) == 1) {
                        context.mQuarterQuantity.remove(cartModel.getId());
                    } else {
                        context.mQuarterQuantity.put(cartModel.getId(), (context.mQuarterQuantity.get(cartModel.getId()) - 1));
                    }
                }
            }else if (cartModel.getType().equals("Half")) {
                if (context.mHalfQuantity.get(cartModel.getId()) != null) {
                    if (context.mHalfQuantity.get(cartModel.getId()) == 1) {
                        context.mHalfQuantity.remove(cartModel.getId());
                    } else {
                        context.mHalfQuantity.put(cartModel.getId(), (context.mHalfQuantity.get(cartModel.getId()) - 1));
                    }
                }
            } else*/ if (cartModel.getType().equals("Full")) {
                if (context.mFullQuantity.get(cartModel.getId()) != null) {
                    if (context.mFullQuantity.get(cartModel.getId()) == 1) {
                        context.mFullQuantity.remove(cartModel.getId());
                    } else {
                        context.mFullQuantity.put(cartModel.getId(), (context.mFullQuantity.get(cartModel.getId()) - 1));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        setCartMainPrice("remove", cartModel.getItemActualPrice());
    }

    void addOnNotify(MomItemDetailAdapter context, CartModel cartModel) {
        try {
            context.mPrice.put(cartModel.getId(), (context.mPrice.get(cartModel.getId()) + Double.valueOf(cartModel.getItemActualPrice())));
            context.mQuantityState.put(cartModel.getId(), "" + (Integer.valueOf(context.mQuantityState.get(cartModel.getId())) + 1));
            /*if (cartModel.getType().equals("Quarter")) {
                context.mQuarterQuantity.put(cartModel.getId(), (context.mQuarterQuantity.get(cartModel.getId()) + 1));
            } else if (cartModel.getType().equals("Half")) {
                context.mHalfQuantity.put(cartModel.getId(), (context.mHalfQuantity.get(cartModel.getId()) + 1));
            } else*/
            if (cartModel.getType().equals("Full")) {
                context.mFullQuantity.put(cartModel.getId(), (context.mFullQuantity.get(cartModel.getId()) + 1));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        setCartMainPrice("add", cartModel.getItemActualPrice());
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
        try {
            MomItemDetailAdapter.context.setMainPrice(operation, amount);
        }catch (Exception e){

        }
        if (operation.equals("add")) {
            notifyAppUser();
            appUser=LocalRepositories.getAppUser(context);
            appUser.setAmount(appUser.getAmount() + Double.valueOf(amount));
            LocalRepositories.saveAppUser(context,appUser);
            CartActivity.context.setAmount((appUser.getAmount()));
//            CartActivity.context.setAmount((CartActivity.context.getAmount() + Double.valueOf(amount)));
        } else {
            notifyAppUser();
            appUser=LocalRepositories.getAppUser(context);
            appUser.setAmount(appUser.getAmount() - Double.valueOf(amount));
            LocalRepositories.saveAppUser(context,appUser);
            CartActivity.context.setAmount((appUser.getAmount()));
//            CartActivity.context.setAmount((CartActivity.context.getAmount() - Double.valueOf(amount)));
        }
    }


    void notifyAppUser(){
        appUser=LocalRepositories.getAppUser(context);
        appUser.setCartModels(cartModelList);
        LocalRepositories.saveAppUser(context,appUser);
    }



    public void dialogPlusClear(int position) {
        DialogPlus dialog = DialogPlus.newDialog(context)
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
                                    removeOnNotify(MomItemDetailAdapter.context, cartModel);
                                    notifyAppUser();
                                    notifyDataSetChanged();
                                    EventBus.getDefault().post(new EventResetPromo(""));
                                }
//                                removeOnNotify(MomItemDetailAdapter.context, cartModelList.get(position));
                            }catch (Exception e){

                            }
//                            cartModelList.remove(position);
//                            context.findViewById(R.id.icon_id).setVisibility(View.GONE);
                            appUser=LocalRepositories.getAppUser(context);
                            appUser.setCartModels(null);
                            appUser.setMomMobile("");
                            appUser.setAmount(0.0);
                            LocalRepositories.saveAppUser(context,appUser);
                            context.onResume();
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
