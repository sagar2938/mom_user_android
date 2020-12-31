package mom.com.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import mom.com.R;
import mom.com.activities.BaseFragment;
import mom.com.adapter.BookedFragmentAdapter;
import mom.com.adapter.OnGoingFragmentAdapter;
import mom.com.network.ApiCallService;
import mom.com.network.ThisApp;
import mom.com.network.response.order.GetBookedResponse;
import mom.com.network.response.order.GetNewOrderResponse;
import mom.com.utils.CustomProgressDialog;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookedFragment extends BaseFragment {
    RecyclerView recyclerView;
    CustomProgressDialog customProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ongoing, container, false);
        recyclerView=view.findViewById(R.id.recyclerView);
        return  view;

    }

    @Override
    public void onResume() {
        super.onResume();
        Map map=new HashMap();
        map.put("mobile", Preferences.getInstance(getActivity()).getMobile());
        customProgressDialog= CustomProgressDialog.getInstance(getActivity());
        customProgressDialog.show();
//        ApiCallService.action2(getActivity(),map, ApiCallService.Action.ACTION_BOOKED_ORDER);
        ThisApp.getApi(getContext()).bookedOrder(map).enqueue(new Callback<GetBookedResponse>() {
            @Override
            public void onResponse(Call<GetBookedResponse> call, Response<GetBookedResponse> r) {
                GetBookedResponse response=r.body();
                customProgressDialog.dismiss();
                if (response.getSuccess()){
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(new BookedFragmentAdapter(getActivity(),BookedFragment.this,response.getOrderData()));
                }else {
                    getDialog("Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<GetBookedResponse> call, Throwable t) {
                getDialog(t.getMessage());
                customProgressDialog.dismiss();
            }
        });
    }

    @Subscribe
    public void getNewLead(GetBookedResponse response){
        customProgressDialog.dismiss();
//        swipeRefreshLayout.setRefreshing(false);
        if (response.getSuccess()){
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new BookedFragmentAdapter(getActivity(),this,response.getOrderData()));
        }else {
            getDialog("Something went wrong");
        }
    }


    @Subscribe
    public void timeOut(String msg) {
        customProgressDialog.dismiss();
        getDialog("Failed", msg);
    }






}
