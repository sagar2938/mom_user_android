package mom.com.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mom.com.R;
import mom.com.activities.address.AddAddressActivity;
import mom.com.network.ThisApp;
import mom.com.network.response.AddressData;
import mom.com.network.response.SucessResponse;
import mom.com.utils.CustomProgressDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    List<AddressData> addressDataList;
    Activity context;

    public AddressAdapter(Activity context, List<AddressData> addressDataList) {
        this.addressDataList = addressDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_address, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        AddressData addressData = addressDataList.get(position);

        viewHolder.name.setText(addressData.getName());
//        viewHolder.itemActualPrice.setText(/*"₹" +*/ addressData.getItemDescription());
//        viewHolder.type.setText(addressData.getType());
        viewHolder.phone_number.setText(addressData.getPhone_number());
        viewHolder.address.setText("" + addressData.getAddress());

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddAddressActivity.class);
                intent.putExtra("for", "edit");
                intent.putExtra("name", addressData.getName());
                intent.putExtra("phone_number", addressData.getPhone_number());
                intent.putExtra("address", addressData.getAddress());
                intent.putExtra("lat", addressData.getLatitude());
                intent.putExtra("long", addressData.getLongitude());
                intent.putExtra("id", addressData.getId());
                context.startActivity(intent);
            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete?")
                        .setMessage("Do you really want to delete?")
                        .setCancelable(true)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                CustomProgressDialog.getInstance(context).show();
                                Map map=new HashMap();
                                map.put("id",addressData.getId());
                                ThisApp.getApi(context).deleteAddress(map).enqueue(new Callback<SucessResponse>() {
                                    @Override
                                    public void onResponse(Call<SucessResponse> call, Response<SucessResponse> response) {
                                        CustomProgressDialog.setDismiss();
                                        addressDataList.remove(position);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onFailure(Call<SucessResponse> call, Throwable t) {
                                        CustomProgressDialog.setDismiss();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setIcon(R.mipmap.ic_launcher)
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return addressDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView phone_number;
        TextView address;
        RelativeLayout edit;
        RelativeLayout delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            phone_number = itemView.findViewById(R.id.phone_number);
            address = itemView.findViewById(R.id.address);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);

        }
    }


    void setCartMainPrice(String operation, String amount) {
        /*MomItemDetailAdapter.context.setMainPrice(operation, amount);
        if (operation.equals("add")) {
            CartActivity.context.setAmount((CartActivity.context.getAmount() + Double.valueOf(amount)));
        } else {
            CartActivity.context.setAmount((CartActivity.context.getAmount() - Double.valueOf(amount)));
        }*/
    }
}
