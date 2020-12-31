package mom.com.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import mom.com.activities.BaseFragment;
import mom.com.R;
import mom.com.adapter.OnGoingFragmentAdapter;
import mom.com.network.ApiCallService;
import mom.com.network.ThisApp;
import mom.com.network.response.order.GetNewOrderResponse;
import mom.com.utils.CustomProgressDialog;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnGoingFragment extends BaseFragment {
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

        ThisApp.getApi(getContext()).getOnGoingOrder(map).enqueue(new Callback<GetNewOrderResponse>() {
            @Override
            public void onResponse(Call<GetNewOrderResponse> call, Response<GetNewOrderResponse> r) {
                GetNewOrderResponse response=r.body();
                customProgressDialog.dismiss();
                if (response.getSuccess()){
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(new OnGoingFragmentAdapter(getActivity(),response.getOrderData()));
                }else {
                    getDialog("Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<GetNewOrderResponse> call, Throwable t) {
                customProgressDialog.dismiss();
            }
        });
        ApiCallService.action2(getActivity(),map, ApiCallService.Action.ACTION_ONGOING_ORDER);
    }

    @Subscribe
    public void getNewLead(GetNewOrderResponse response){
        customProgressDialog.dismiss();
//        swipeRefreshLayout.setRefreshing(false);
        if (response.getSuccess()){
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new OnGoingFragmentAdapter(getActivity(),response.getOrderData()));
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
