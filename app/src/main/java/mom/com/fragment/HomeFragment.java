package mom.com.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnClickListener;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import mom.com.activities.AddressPickerActivity2;
import mom.com.activities.BaseActivity;
import mom.com.activities.BaseFragment;
import mom.com.activities.kt.PlacePickerActivity;
import mom.com.adapter.FavouriteMomAdapter;
import mom.com.adapter.HomeFragmentAdapter;
import mom.com.adapter.OfferHorizontalAdapter;
import mom.com.helper.FusedLocation;
import mom.com.R;
import mom.com.network.ApiCallService;
import mom.com.network.ThisApp;
import mom.com.network.response.AddResponse;
import mom.com.network.response.GetFavouriteResponse;
import mom.com.network.response.GetOfferResponse;
import mom.com.network.response.MomListResponse;
import mom.com.network.response.Mom_data;
import mom.com.network.response.order.GetNewOrderResponse2;
import mom.com.network.response.order.OrderDatum;
import mom.com.utils.CustomProgressDialog;
import mom.com.utils.Helper;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends BaseFragment {
    RecyclerView recyclerView;
    RecyclerView recyclerViewOffer;
    RecyclerView recyclerViewFav;
    ProgressBar progressBar;
    CardView cardView;
    EditText search;
    TextView location;
    LinearLayout noRecord;

    CustomProgressDialog offerProgressDialog;
    CustomProgressDialog favProgressDialog;

    TextView momTittle;
    TextView address;
    TextView time;
    RatingBar rating;

    LinearLayoutManager layoutManager;
    LinearLayoutManager momListLayoutManager;
    NestedScrollView nestedScrollview;

    GetFavouriteResponse response;
    FavouriteMomAdapter adapter;
    Map map;


    int pastVisibleItems, visibleItemCount, totalItemCount;
    boolean loading = true;
    int pageNo = 0;

    public static boolean updateList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        offerProgressDialog = new CustomProgressDialog(getActivity());
        favProgressDialog = new CustomProgressDialog(getActivity());
        favProgressDialog.show();
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        momTittle = view.findViewById(R.id.momTittle);
        progressBar = view.findViewById(R.id.progressBar);
        address = view.findViewById(R.id.address);
        time = view.findViewById(R.id.time);
        rating = view.findViewById(R.id.rating);
        nestedScrollview = view.findViewById(R.id.nestedScrollview);

        cardView = view.findViewById(R.id.cardView);
        recyclerViewFav = view.findViewById(R.id.recyclerViewFav);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerViewOffer = view.findViewById(R.id.recyclerViewOffer);
        search = view.findViewById(R.id.search);
        location = view.findViewById(R.id.location);
        noRecord = view.findViewById(R.id.noRecord);
        cardView.setVisibility(View.GONE);


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateList = false;
                startActivityForResult(new Intent(getActivity(), PlacePickerActivity.class), 1);
            }
        });

        recyclerViewFav.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                try {
                    FavouriteMomAdapter.map.put("", layoutManager.findFirstCompletelyVisibleItemPosition());
                    Mom_data mom_data = response.getMom_data().get(layoutManager.findFirstCompletelyVisibleItemPosition());
                    momTittle.setText(mom_data.getFirstName() + " " + mom_data.getMiddleName() + " " + mom_data.getLastName());
                    address.setText(mom_data.getSpecialization());
                    rating.setRating(mom_data.getRating());
//                    time.setText(mom_data.getOpenTime());
                    adapter.notifyDataSetChanged();
                } catch (Exception ignored) {

                }

            }
        });


        Map map = new HashMap();
        map.put("mobile", Preferences.getInstance(getContext()).getMobile());
        map.put("start_date", Helper.getDate(-4));
        map.put("end_date", Helper.getCurrentDate());
        if (Preferences.getInstance(getActivity()).isLogin()) {
            ThisApp.getApi(getContext()).getHistory(map).enqueue(new Callback<GetNewOrderResponse2>() {
                @Override
                public void onResponse(Call<GetNewOrderResponse2> call, Response<GetNewOrderResponse2> r) {
                    GetNewOrderResponse2 response = r.body();
                    try {
                        if (response.getSuccess()) {
                            for (int i = 0; i < response.getOrderData().size(); i++) {
                                if (response.getOrderData().get(i).getRatingStatus() == 0) {
                                    ratingDialog(response.getOrderData().get(i));
                                    return;
                                }
                            }
                        } else {
                            getDialog("Something went wrong");
                        }
                    } catch (Exception e) {
//                        getDialog("Some thing went wrong");
                    }

                }

                @Override
                public void onFailure(Call<GetNewOrderResponse2> call, Throwable t) {
                    Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }



        if (Preferences.getInstance(getActivity()).getAdd().equals("")){
            FusedLocation.ADDRESS2 = FusedLocation.ADDRESS2.replace("null", "");
            FusedLocation.ADDRESS2 = FusedLocation.ADDRESS2.replace("null,", "");
            FusedLocation.ADDRESS2 = FusedLocation.ADDRESS2.replace(",null", "");
            FusedLocation.ADDRESS2 = FusedLocation.ADDRESS2.replace(",null,", "");
            Preferences.getInstance(getActivity()).setAdd(FusedLocation.ADDRESS2);
        }
        location.setText(Preferences.getInstance(getActivity()).getAdd());

        map = new HashMap();

        // latitude = 12.9678728;
        //  longitude = 77.7110936;


        if (Preferences.getInstance(getActivity()).getLat().equals("")) {
            Preferences.getInstance(getActivity()).setLat("" + FusedLocation.location.getLatitude());
            Preferences.getInstance(getActivity()).setLong("" + FusedLocation.location.getLongitude());
        }

        map.put("mobile", Preferences.getInstance(getContext()).getMobile());
        map.put("latitude", "" + Preferences.getInstance(getActivity()).getLat());
        map.put("longitude", "" + Preferences.getInstance(getActivity()).getLong());


        map.put("pageNo", "" + pageNo);
        ApiCallService.action(getActivity(), map, ApiCallService.Action.ACTION_MOM_LIST);
        ApiCallService.action2(getActivity(), map, ApiCallService.Action.ACTION_GET_FAVOURITE);
        ApiCallService.action2(getActivity(), map, ApiCallService.Action.ACTION_GET_OFFER_LIST);

        nestedScrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    {
                        visibleItemCount = momListLayoutManager.getChildCount();
                        totalItemCount = momListLayoutManager.getItemCount();
                        pastVisibleItems = momListLayoutManager.findFirstCompletelyVisibleItemPosition();
                        if (loading) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                loading = false;
                                pageNo++;
                                Map map = new HashMap();
                                map.put("mobile", Preferences.getInstance(getContext()).getMobile());
                                map.put("latitude", "" + Preferences.getInstance(getActivity()).getLat());
                                map.put("longitude", "" + Preferences.getInstance(getActivity()).getLong());
                                map.put("pageNo", "" + pageNo);
                                progressBar.setVisibility(View.VISIBLE);
                                ApiCallService.action2(getActivity(), map, ApiCallService.Action.ACTION_MOM_LIST);
                            }

                        }
                    }

                }
            }
        });


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        return view;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        map = new HashMap();
        try {
            latitude = FusedLocation.location.getLatitude();
            longitude = FusedLocation.location.getLongitude();
            if (latitude != null && longitude != null) {
                map.put("mobile", Preferences.getInstance(getContext()).getMobile());
                map.put("latitude",""+ latitude);
                map.put("longitude",""+ longitude);
            } else {
                map.put("mobile", Preferences.getInstance(getContext()).getMobile());
                map.put("latitude", Preferences.getInstance(getContext()).getLatitude());
                map.put("longitude", Preferences.getInstance(getContext()).getLongitude());
            }

        } catch (Exception e) {
            map.put("mobile", Preferences.getInstance(getContext()).getMobile());
            map.put("latitude", Preferences.getInstance(getContext()).getLatitude());
            map.put("longitude", Preferences.getInstance(getContext()).getLongitude());
        }
        map.put("pageNo",""+pageNo);
//        ApiCallService.action2(getActivity(), map, ApiCallService.Action.ACTION_GET_FAVOURITE);
    }*/

    MomListResponse momListResponse;
    HomeFragmentAdapter homeFragmentAdapter;

    @Subscribe
    public void getMomList(MomListResponse response) {
        progressBar.setVisibility(View.GONE);
        if (pageNo == 0) {
            if (response.getVendor_data().size() > 0) {
                noRecord.setVisibility(View.GONE);
                recyclerViewOffer.setVisibility(View.VISIBLE);
            } else {
                noRecord.setVisibility(View.VISIBLE);
                recyclerViewOffer.setVisibility(View.GONE);
            }
            this.momListResponse = response;
            momListLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(momListLayoutManager);
            homeFragmentAdapter = new HomeFragmentAdapter(response, this, getContext());
            recyclerView.setAdapter(homeFragmentAdapter);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setFocusable(false);
        } else {
            this.momListResponse.getVendor_data().addAll(response.getVendor_data());
            homeFragmentAdapter.notifyDataSetChanged();
            if (response.getVendor_data().size() > 0) {
                loading = true;
            } else {
                loading = false;
            }
        }

    }


    @Subscribe
    public void getOffer(GetOfferResponse response) {
        offerProgressDialog.dismiss();
        if (response.isSuccess()) {
            recyclerViewOffer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerViewOffer.setAdapter(new OfferHorizontalAdapter(response.getOffer_data(), getContext()));
            recyclerViewOffer.setNestedScrollingEnabled(false);
            recyclerViewOffer.setFocusable(false);
        }
    }

    @Subscribe
    public void getFavourite(GetFavouriteResponse response) {
        favProgressDialog.dismiss();
        if (response.isSuccess()) {
            this.response = response;
            if (response.getMom_data().size() > 0) {
                cardView.setVisibility(View.VISIBLE);
                layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerViewFav.setLayoutManager(layoutManager);
                adapter = new FavouriteMomAdapter(response.getMom_data(), this, getContext());
                recyclerViewFav.setAdapter(adapter);
                recyclerViewFav.setNestedScrollingEnabled(false);
                recyclerViewFav.setFocusable(false);
            } else {
                cardView.setVisibility(View.GONE);
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Map map = new HashMap();
                Preferences.getInstance(getActivity()).setLat("" + data.getStringExtra("lat"));
                Preferences.getInstance(getActivity()).setLong("" + data.getStringExtra("lon"));

                map.put("mobile", Preferences.getInstance(getContext()).getMobile());
                map.put("latitude", "" + Preferences.getInstance(getActivity()).getLat());
                map.put("longitude", "" + Preferences.getInstance(getActivity()).getLong());
                pageNo = 0;
                loading = true;
                map.put("pageNo", "" + pageNo);
                Preferences.getInstance(getActivity()).setAdd(data.getStringExtra("address"));
                location.setText(data.getStringExtra("address"));
                ApiCallService.action2(getActivity(), map, ApiCallService.Action.ACTION_GET_FAVOURITE);
                ApiCallService.action(getActivity(), map, ApiCallService.Action.ACTION_MOM_LIST);

            }
        }
    }



    /*@Subscribe
    public void LocationEvent(LocationEventHome event){
        latitude=event.getLatitude();
        longitude=event.getLongitude();
        location.setText(event.getAddress());
    }*/


    void ratingDialog(OrderDatum orderDatum) {
        DialogPlus dialogPlus = null;
        DialogPlusBuilder dialogPlusBuilder = DialogPlus.newDialog(getActivity());
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
                CustomProgressDialog.getInstance(getActivity()).show();
                ThisApp.getApi(getActivity()).rating(map).enqueue(new Callback<AddResponse>() {
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
                    Toast.makeText(getContext(), "Please rate the MOMCHEFF", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rateDeliveryBoy.getRating() == 0) {
                    Toast.makeText(getContext(), "Please rate the Delivery boy", Toast.LENGTH_SHORT).show();
                    return;
                }
                finalDialogPlus.dismiss();
                Map map = new Hashtable();
                map.put("orderId", orderDatum.getOrderId());
                map.put("rating", "" + ratingBar.getRating());
                map.put("deliver_rating", "" + rateDeliveryBoy.getRating());
                CustomProgressDialog.getInstance(getActivity()).show();
//                ApiCallService.action(this,map,ApiCallService.Action.ACTION_RATING);

                ThisApp.getApi(getActivity()).rating(map).enqueue(new Callback<AddResponse>() {
                    @Override
                    public void onResponse(Call<AddResponse> call, Response<AddResponse> response) {
                        CustomProgressDialog.setDismiss();
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


    @Override
    public void onResume() {
        super.onResume();
        if (updateList) {
            Map map = new HashMap();
            try {
                map.put("mobile", Preferences.getInstance(getContext()).getMobile());
                map.put("latitude", FusedLocation.location.getLatitude());
                map.put("longitude", FusedLocation.location.getLongitude());
            } catch (Exception e) {
                map.put("mobile", Preferences.getInstance(getContext()).getMobile());
                map.put("latitude", FusedLocation.location.getLatitude());
                map.put("longitude", FusedLocation.location.getLongitude());
            }
            pageNo = 0;
            loading = true;
            map.put("pageNo", "" + pageNo);
            ApiCallService.action2(getActivity(), map, ApiCallService.Action.ACTION_GET_FAVOURITE);
        }

    }
}
