package mom.com.fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import java.util.HashMap;
import java.util.Map;
import mom.com.activities.BaseFragment;
import mom.com.activities.kt.PlacePickerActivity;
import mom.com.adapter.SearchAdapter;
import mom.com.helper.FusedLocation;
import mom.com.R;
import mom.com.network.ApiCallService;
import mom.com.network.response.MomListResponse2;
import mom.com.utils.Preferences;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends BaseFragment {
    RecyclerView recyclerView;
    EditText search;
    LinearLayout search_btn;
    LinearLayout close_btn;
    MomListResponse2 response;
    SearchAdapter searchAdapter;
    ProgressBar progressBar;

    Double latitude;
    Double longitude;

    TextView location;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Search");
        recyclerView = view.findViewById(R.id.recyclerView);
        search=view.findViewById(R.id.search);
        search_btn=view.findViewById(R.id.search_btn);
        close_btn=view.findViewById(R.id.close_btn);
        location = view.findViewById(R.id.location);
        progressBar = view.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);


        if (Preferences.getInstance(getActivity()).getAdd().equals("")){
            FusedLocation.ADDRESS2 = FusedLocation.ADDRESS2.replace("null", "");
            FusedLocation.ADDRESS2 = FusedLocation.ADDRESS2.replace("null,", "");
            FusedLocation.ADDRESS2 = FusedLocation.ADDRESS2.replace(",null", "");
            FusedLocation.ADDRESS2 = FusedLocation.ADDRESS2.replace(",null,", "");
            Preferences.getInstance(getActivity()).setAdd(FusedLocation.ADDRESS2);
        }
        location.setText(Preferences.getInstance(getActivity()).getAdd());


        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), PlacePickerActivity.class), 1);
            }
        });


        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });



        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    search_btn.setVisibility(View.VISIBLE);
                    close_btn.setVisibility(View.GONE);
                }else {
                    search_btn.setVisibility(View.GONE);
                    close_btn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
//                    SearchAdapter.filteredList = response.getVendor_data();
//                    SearchAdapter.responseList = response.getVendor_data();
//                    searchAdapter.getFilter().filter(s.toString());

                    if (s.toString().length()>1){
                        progressBar.setVisibility(View.VISIBLE);
                        Map map=new HashMap();
                        map.put("mobile", Preferences.getInstance(getContext()).getMobile());
                        map.put("latitude", "" + Preferences.getInstance(getActivity()).getLat());
                        map.put("longitude", "" + Preferences.getInstance(getActivity()).getLong());
                        map.put("key", "" + s.toString());
                        ApiCallService.action2(getActivity(),map,ApiCallService.Action.ACTION_SEARCH_WITH_KEY);
                    }

                } catch (Exception e) {

                }

            }
        });
        return  view;

    }


    @Override
    public void onResume() {
        super.onResume();

        Map map=new HashMap();
        map.put("mobile", Preferences.getInstance(getContext()).getMobile());
        map.put("latitude", "" + Preferences.getInstance(getActivity()).getLat());
        map.put("longitude", "" + Preferences.getInstance(getActivity()).getLong());
        ApiCallService.action(getActivity(),map,ApiCallService.Action.ACTION_SEARCH);


    }

    @Subscribe
    public void getMomList(MomListResponse2 response){
        progressBar.setVisibility(View.GONE);
        this.response=response;
        setAdapter(response);
    }


    void setAdapter(MomListResponse2 response){
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        searchAdapter = new SearchAdapter(this,response.getVendor_data(),getActivity());
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setFocusable(false);
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
                Preferences.getInstance(getActivity()).setAdd(data.getStringExtra("address"));
                location.setText(data.getStringExtra("address"));
                ApiCallService.action(getActivity(), map, ApiCallService.Action.ACTION_SEARCH);
            }
        }
    }


    @Subscribe
    public void timeOut(String msg) {
        progressBar.setVisibility(View.GONE);
        getDialog("Failed", msg);
    }

    /*@Subscribe
    public void LocationEvent(LocationEventHome event){
        latitude=event.getLatitude();
        longitude=event.getLongitude();
        location.setText(event.getAddress());
    }*/

}
