package mom.com.adapter;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
//import com.tapadoo.alerter.Alerter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mom.com.R;
import mom.com.activities.CartActivity;
import mom.com.activities.MomItemDetailActivity;
import mom.com.network.response.MenuData;
import mom.com.utils.AppUser;
import mom.com.utils.LocalRepositories;
import mom.com.utils.Preferences;

public class MomItemDetailAdapter extends RecyclerView.Adapter<MomItemDetailAdapter.ViewHolder> {
    List<MenuData> responseList;
    Activity ctx;
    public Map<Integer, String> mQuantityState;
    //    public Map<Integer, Integer> mQuarterQuantity;
//    public Map<Integer, Integer> mHalfQuantity;
    public Map<Integer, Integer> mFullQuantity;
    public Map<Integer, MenuData> mItemDetail;
    public Map<Integer, Double> mPrice;
    Integer mInteger = 0;
    public static MomItemDetailAdapter context;
    AppUser appUser;
    View cart;
    int onlineStatus=0;

    public MomItemDetailAdapter(List<MenuData> responseList, Activity context,View cart, int onlineStatus) {
        this.context = this;
        this.responseList = responseList;
        this.ctx = context;
        mQuantityState = new HashMap();
        mFullQuantity = new HashMap();
        mItemDetail = new HashMap();
        mPrice = new HashMap();
        this.cart=cart;
        appUser= LocalRepositories.getAppUser(context);
        this.onlineStatus = onlineStatus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_mom_item_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        MenuData menuData = responseList.get(position);

        /*Glide.with(ctx)
                .load(menuData.getItemImage())
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .apply(RequestOptions.skipMemoryCacheOf(true))
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(viewHolder.itemImage);*/


        if(!StringUtils.isEmpty(menuData.getItemImage())) {
            Glide.with(ctx)
                    .load(menuData.getItemImage())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .apply(new RequestOptions().placeholder(R.drawable.default_food))
                    .into(viewHolder.itemImage);
        }else{
            viewHolder.itemImage.setImageDrawable(null);
        }


        viewHolder.itemName.setText(WordUtils.capitalizeFully(menuData.getItemName().toLowerCase().trim()));
        viewHolder.description.setText(StringUtils.capitalize(menuData.getItemDescription()));
        viewHolder.type.setText(StringUtils.capitalize(menuData.getFoodType()));
        viewHolder.price.setText(menuData.getFullPrice());
        mInteger = 0;
        if(onlineStatus ==0){
            viewHolder.addMenuLayout.setVisibility(View.INVISIBLE);
        }

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*Intent intent=new Intent(context, CartActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               context.startActivity(intent);*/
            }
        });

        Integer id = responseList.get(position).getId();
        state(viewHolder, id);
        amount(viewHolder, id);

        viewHolder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUser=LocalRepositories.getAppUser(ctx);
                if (!appUser.getMomMobile().equals(Preferences.getInstance(ctx).getMomMobile())){
                    if (appUser.getCartModels()!=null){
//                    alerter();
                        dialogPlusClear();
                        return;
                    }
                }
                addQuantity(id);
                putTypeQuantity(mFullQuantity, position);
                addPrice("full", position);
                mItemDetail.put(id, responseList.get(position));
                notifyDataSetChanged();
                showHideCart();
//                addDialog(id, position);
            }
        });

        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUser=LocalRepositories.getAppUser(ctx);
                if (!appUser.getMomMobile().equals(Preferences.getInstance(ctx).getMomMobile())){
                    if (appUser.getCartModels()!=null){
//                    alerter();
                        dialogPlusClear();
                        return;
                    }
                }
                addQuantity(id);
                putTypeQuantity(mFullQuantity, position);
                addPrice("full", position);
                mItemDetail.put(id, responseList.get(position));
                notifyDataSetChanged();
                showHideCart();
//                addDialog(id, position);
            }
        });

        viewHolder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (mQuarterQuantity.containsKey(id) && !mHalfQuantity.containsKey(id) && !mFullQuantity.containsKey(id)) {
                    removeQuantity(id);
                    removeTypeQuantity(mQuarterQuantity, position);
                    removePrice("quarter", position);
                } else if (!mQuarterQuantity.containsKey(id) && mHalfQuantity.containsKey(id) && !mFullQuantity.containsKey(id)) {
                    removeQuantity(id);
                    removeTypeQuantity(mHalfQuantity, position);
                    removePrice("half", position);
                } else if (!mQuarterQuantity.containsKey(id) && !mHalfQuantity.containsKey(id) && mFullQuantity.containsKey(id)) {
                    removeQuantity(id);
                    removeTypeQuantity(mFullQuantity, position);
                    removePrice("full", position);
                }*/

                if (mFullQuantity.containsKey(id)){
                    removeQuantity(id);
                    removeTypeQuantity(mFullQuantity, position);
                    removePrice("full", position);
                }
                else {
//                    minusDialog(id, position);
                }

                showHideCart();

            }
        });

        viewHolder.quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuantity(id);
                putTypeQuantity(mFullQuantity, position);
                addPrice("full", position);
                mItemDetail.put(id, responseList.get(position));
                notifyDataSetChanged();
                showHideCart();
//                addDialog(id, position);
            }
        });


    }


    void addQuantity(Integer id) {
        try {
            mInteger = Integer.parseInt(mQuantityState.get(id));
        } catch (Exception e) {
            mInteger = 0;
        }
        mInteger = mInteger + 1;
        mQuantityState.put(id, "" + mInteger);
        notifyDataSetChanged();
    }

    void removeQuantity(Integer id) {
        if (mQuantityState.get(id).equals("1")) {
            mQuantityState.remove(id);
        } else {
            try {
                mInteger = Integer.parseInt(mQuantityState.get(id));
            } catch (Exception e) {
                mInteger = 0;
            }
            mInteger = mInteger - 1;
            mQuantityState.put(id, "" + mInteger);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        try {
            return responseList.size();
        }catch (Exception e){
            return  0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        LinearLayout mainLayout;
        TextView price;

        TextView itemName;
        TextView description;
        TextView type;
        TextView plus;
        TextView minus;
        TextView quantity;
        TextView add;
        TextView amount;
        LinearLayout addMenuLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.mainLayout);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            type = itemView.findViewById(R.id.type);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            quantity = itemView.findViewById(R.id.quantity);
            add = itemView.findViewById(R.id.add);
            amount = itemView.findViewById(R.id.amount);
            addMenuLayout = itemView.findViewById(R.id.add_menu_layout);

        }
    }

    void state(ViewHolder viewHolder, Integer id) {
        if (mQuantityState.containsKey(id)) {
            if (mQuantityState.get(id).equals("0")) {
                viewHolder.add.setVisibility(View.VISIBLE);
                viewHolder.quantity.setVisibility(View.GONE);
                viewHolder.minus.setVisibility(View.GONE);
            } else {
                viewHolder.add.setVisibility(View.GONE);
                viewHolder.quantity.setVisibility(View.VISIBLE);
                viewHolder.minus.setVisibility(View.VISIBLE);
                viewHolder.quantity.setText("" + mQuantityState.get(id));
            }
        } else {
            viewHolder.add.setVisibility(View.VISIBLE);
            viewHolder.quantity.setVisibility(View.GONE);
            viewHolder.minus.setVisibility(View.GONE);
        }
    }

    void amount(ViewHolder viewHolder, Integer id) {
        if (mPrice.containsKey(id)) {
            viewHolder.amount.setText("" + mPrice.get(id));
        } else {
            viewHolder.amount.setText("0.0");
        }
    }


    void addDialog(Integer id, int position) {
        /*Dialog dialog = new Dialog(ctx);
        dialog.setTitle("Item Name");
        dialog.setContentView(R.layout.dialog_add_item2);
        dialog.setCancelable(true);
        Button submit = dialog.findViewById(R.id.submit);
        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        TextView quarterPrice = dialog.findViewById(R.id.quarterPrice);
        TextView halfPrice = dialog.findViewById(R.id.halfPrice);
        TextView fullPrice = dialog.findViewById(R.id.fullPrice);

        RadioButton quarterButton = dialog.findViewById(R.id.quarterButton);
        RadioButton halfButton = dialog.findViewById(R.id.halfButton);
        RadioButton fullButton = dialog.findViewById(R.id.fullButton);

        TextView quarterRupee = dialog.findViewById(R.id.quarterRupee);
        TextView halfRupee = dialog.findViewById(R.id.halfRupee);
        TextView fullRupee = dialog.findViewById(R.id.fullRupee);

        if (!responseList.get(position).getQuarter()) {
            quarterButton.setVisibility(View.GONE);
            quarterRupee.setVisibility(View.GONE);
            quarterPrice.setVisibility(View.GONE);
        } else if (!responseList.get(position).getHalf()) {
            halfButton.setVisibility(View.GONE);
            halfRupee.setVisibility(View.GONE);
            halfPrice.setVisibility(View.GONE);
        } else if (!responseList.get(position).getFull()) {
            fullButton.setVisibility(View.GONE);
            fullRupee.setVisibility(View.GONE);
            fullPrice.setVisibility(View.GONE);
        }

        quarterPrice.setText(responseList.get(position).getQuarterPrice());
        halfPrice.setText(responseList.get(position).getHalfPrice());
        fullPrice.setText(responseList.get(position).getFullPrice());


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quarterButton.isChecked()) {
                    addQuantity(id);
                    putTypeQuantity(mQuarterQuantity, position);
                    addPrice("quarter", position);
                } else if (halfButton.isChecked()) {
                    addQuantity(id);
                    putTypeQuantity(mHalfQuantity, position);
                    addPrice("half", position);
                } else if (fullButton.isChecked()) {
                    addQuantity(id);
                    putTypeQuantity(mFullQuantity, position);
                    addPrice("full", position);
                }
                mItemDetail.put(id, responseList.get(position));
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });


        dialog.show();*/
    }


    void minusDialog(Integer id, int position) {
       /* Dialog dialog = new Dialog(ctx);
        dialog.setTitle("Item Name");
        dialog.setContentView(R.layout.dialog_add_item2);
        dialog.setCancelable(true);
        Button submit = dialog.findViewById(R.id.submit);

        TextView quarterPrice = dialog.findViewById(R.id.quarterPrice);
        TextView halfPrice = dialog.findViewById(R.id.halfPrice);
        TextView fullPrice = dialog.findViewById(R.id.fullPrice);

        RadioButton quarterButton = dialog.findViewById(R.id.quarterButton);
        RadioButton halfButton = dialog.findViewById(R.id.halfButton);
        RadioButton fullButton = dialog.findViewById(R.id.fullButton);

        TextView quarterRupee = dialog.findViewById(R.id.quarterRupee);
        TextView halfRupee = dialog.findViewById(R.id.halfRupee);
        TextView fullRupee = dialog.findViewById(R.id.fullRupee);

        if (!mQuarterQuantity.containsKey(id)) {
            quarterButton.setVisibility(View.GONE);
            quarterRupee.setVisibility(View.GONE);
            quarterPrice.setVisibility(View.GONE);
        }
        if (!mHalfQuantity.containsKey(id)) {
            halfButton.setVisibility(View.GONE);
            halfRupee.setVisibility(View.GONE);
            halfPrice.setVisibility(View.GONE);
        }
        if (!mFullQuantity.containsKey(id)) {
            fullButton.setVisibility(View.GONE);
            fullRupee.setVisibility(View.GONE);
            fullPrice.setVisibility(View.GONE);
        }

        quarterPrice.setText(responseList.get(position).getQuarterPrice());
        halfPrice.setText(responseList.get(position).getHalfPrice());
        fullPrice.setText(responseList.get(position).getFullPrice());


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quarterButton.isChecked()) {
                    removeQuantity(id);
                    removeTypeQuantity(mQuarterQuantity, position);
                    removePrice("quarter", position);
                } else if (halfButton.isChecked()) {
                    removeQuantity(id);
                    removeTypeQuantity(mHalfQuantity, position);
                    removePrice("half", position);
                } else if (fullButton.isChecked()) {
                    removeQuantity(id);
                    removeTypeQuantity(mFullQuantity, position);
                    removePrice("full", position);
                }
                if (mItemDetail.containsKey(id)) {
                    mItemDetail.remove(id);
                }

//                amount(viewHolder,id);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });


        dialog.show();*/
    }

    void putTypeQuantity(Map<Integer, Integer> map, int position) {
        if (map.containsKey(responseList.get(position).getId())) {
            Integer integer = map.get(responseList.get(position).getId());
            map.put(responseList.get(position).getId(), (integer + 1));
        } else {
            map.put(responseList.get(position).getId(), 1);
        }
        System.out.println("http : add = " + map);
    }

    void addPrice(String type, int position) {
        MenuData menuData = responseList.get(position);
        if (mPrice.containsKey(menuData.getId())) {
            Double prevPrice = mPrice.get(menuData.getId());
            /*if (type.equals("quarter")) {
                mPrice.put(responseList.get(position).getId(), (prevPrice + Double.valueOf(menuData.getQuarterPrice())));
                setMainPrice("add", menuData.getQuarterPrice());
            } else if (type.equals("half")) {
                mPrice.put(responseList.get(position).getId(), (prevPrice + Double.valueOf(menuData.getHalfPrice())));
                setMainPrice("add", menuData.getHalfPrice());
            } else if (type.equals("full")) {
                mPrice.put(responseList.get(position).getId(), (prevPrice + Double.valueOf(menuData.getFullPrice())));
                setMainPrice("add", menuData.getFullPrice());
            }*/


            mPrice.put(responseList.get(position).getId(), (prevPrice + Double.valueOf(menuData.getFullPrice())));
            setMainPrice("add", menuData.getFullPrice());
        } else {
            /*if (type.equals("quarter")) {
                mPrice.put(responseList.get(position).getId(), (Double.valueOf(menuData.getQuarterPrice())));
                setMainPrice("add", menuData.getQuarterPrice());
            } else if (type.equals("half")) {
                mPrice.put(responseList.get(position).getId(), (Double.valueOf(menuData.getHalfPrice())));
                setMainPrice("add", menuData.getHalfPrice());
            } else if (type.equals("full")) {
                mPrice.put(responseList.get(position).getId(), (Double.valueOf(menuData.getFullPrice())));
                setMainPrice("add", menuData.getFullPrice());
            }*/


            mPrice.put(responseList.get(position).getId(), (Double.valueOf(menuData.getFullPrice())));
            setMainPrice("add", menuData.getFullPrice());
        }


        System.out.println("http : mPrice = " + mPrice);
    }


    void removePrice(String type, int position) {
        MenuData menuData = responseList.get(position);
        if (mPrice.containsKey(menuData.getId())) {
            Double prevPrice = mPrice.get(menuData.getId());
            /*if (type.equals("quarter")) {
                mPrice.put(responseList.get(position).getId(), (prevPrice - Double.valueOf(menuData.getQuarterPrice())));
                setMainPrice("minus", menuData.getQuarterPrice());
            } else if (type.equals("half")) {
                mPrice.put(responseList.get(position).getId(), (prevPrice - Double.valueOf(menuData.getHalfPrice())));
                setMainPrice("minus", menuData.getHalfPrice());
            } else if (type.equals("full")) {
                mPrice.put(responseList.get(position).getId(), (prevPrice - Double.valueOf(menuData.getFullPrice())));
                setMainPrice("minus", menuData.getFullPrice());
            }*/

            mPrice.put(responseList.get(position).getId(), (prevPrice - Double.valueOf(menuData.getFullPrice())));
            setMainPrice("minus", menuData.getFullPrice());
            if (mPrice.get(menuData.getId()) == 0.0) {
                mPrice.remove(menuData.getId());
            }
        }
        System.out.println("http : mPrice = " + mPrice);
    }


    void removeTypeQuantity(Map<Integer, Integer> map, int position) {
        if (map.containsKey(responseList.get(position).getId())) {
            Integer integer = map.get(responseList.get(position).getId());
            if (integer == 1) {
                map.remove(responseList.get(position).getId());
            } else {
                map.put(responseList.get(position).getId(), (integer - 1));
            }
        }
        System.out.println("http : remove = " + map);
    }


    void setMainPrice(String operation, String amount) {
        if (operation.equals("add")) {
            MomItemDetailActivity.context.setAmount("" + (MomItemDetailActivity.context.getAmount() + Double.valueOf(amount)));
        } else {
            MomItemDetailActivity.context.setAmount("" + (MomItemDetailActivity.context.getAmount() - Double.valueOf(amount)));
        }
        MomItemDetailActivity.context.setQuantity(mQuantityState);
    }


    void alerter(){
        /*Alerter.create(ctx)
       .setTitle("")
                .setBackgroundColorRes(R.color.colorPrimary)
                .setText("Complete your added order in the cart")
                . setIcon(R.drawable.cart_alert)
                .setIconColorFilter(0)
                .show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ctx.startActivity(new Intent(ctx, CartActivity.class));

            }
        },2000);*/

    }




    public void dialogPlusClear() {
        DialogPlus dialog = DialogPlus.newDialog(ctx)
                .setHeader(R.layout.clear_header2)
                .setContentHolder(new GridHolder(2))
//                .setCancelable(false)
                .setGravity(Gravity.CENTER)
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
//                            ctx.findViewById(R.id.icon_id).setVisibility(View.GONE);
                            appUser=LocalRepositories.getAppUser(ctx);
                            appUser.setCartModels(null);
                            appUser.setMomMobile("");
                            appUser.setAmount(0.0);
                            LocalRepositories.saveAppUser(ctx,appUser);

                        }else if (view.getId()==R.id.view){
                            ctx.startActivity(new Intent(ctx, CartActivity.class));
                        }
                        dialog.dismiss();
                    }
                })
                .setFooter(R.layout.cancel_footer3)
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
    }


    void showHideCart(){
        if (mFullQuantity.size()>0) {
            cart.setVisibility(View.VISIBLE);
        }else {
            cart.setVisibility(View.GONE);
        }
    }



}
