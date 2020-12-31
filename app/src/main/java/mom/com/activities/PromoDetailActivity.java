package mom.com.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import mom.com.R;
import mom.com.network.Offer_data;

public class PromoDetailActivity extends AppCompatActivity {

    ImageView image;
    TextView offerName;
    TextView offerType;
    TextView promocode;
    TextView description;
    public static Offer_data offer_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_detail);

        image=findViewById(R.id.image);
        offerName=findViewById(R.id.offerName);
        offerType=findViewById(R.id.offerType);
        promocode=findViewById(R.id.promocode);
        description=findViewById(R.id.description);

        offerName.setText(offer_data.getOfferName());
        offerType.setText(offer_data.getOfferType());
        promocode.setText(offer_data.getPromocode());
        description.setText(offer_data.getDescription());


        Glide.with(this)
                .load(offer_data.getImage())
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .apply(new RequestOptions().placeholder(R.drawable.default_food))
                .into(image);




    }
}
