package mom.com.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import mom.com.R;
import mom.com.helper.FusedLocation;
import mom.com.network.response.AddressData;
import mom.com.utils.Preferences;

import static android.app.Activity.RESULT_OK;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.ViewHolder> {
    List<AddressData> addressDataList;
    Activity context;

    public AddressListAdapter(Activity context, List<AddressData> addressDataList) {
        this.addressDataList = addressDataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_address_list, viewGroup, false);
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


        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double distance = distance(Double.valueOf(Preferences.getInstance(context).getMom_latitude()), Double.valueOf(Preferences.getInstance(context).getMom_longitude()),
                        Double.valueOf(addressData.getLatitude()), Double.valueOf(addressData.getLongitude()));

                if (distance > 6.0){
                    getDialog("This MOM is not serving in this location");
                    return;
                }

                Intent intent=new Intent();
                intent.putExtra("name",addressData.getName());
                intent.putExtra("latitude",addressData.getLatitude());
                intent.putExtra("longitude",addressData.getLongitude());
                intent.putExtra("address",addressData.getAddress());
                intent.putExtra("mobile",addressData.getPhone_number());
                Preferences.getInstance(context).setAddressLatitude(addressData.getLatitude());
                Preferences.getInstance(context).setAddressLongitude(addressData.getLongitude());
                context.setResult(RESULT_OK, intent);
                context.finish();
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
        LinearLayout mainLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            phone_number = itemView.findViewById(R.id.phone_number);
            address = itemView.findViewById(R.id.address);
            mainLayout = itemView.findViewById(R.id.mainLayout);

        }
    }


    void setCartMainPrice(String operation, String amount) {
       /* MomItemDetailAdapter.context.setMainPrice(operation, amount);
        if (operation.equals("add")) {
            CartActivity.context.setAmount((CartActivity.context.getAmount() + Double.valueOf(amount)));
        } else {
            CartActivity.context.setAmount((CartActivity.context.getAmount() - Double.valueOf(amount)));
        }*/
    }




    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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
