package mom.com.activities;

import android.graphics.drawable.Animatable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import mom.com.R;

public class TestActivity extends AppCompatActivity {

    ImageView mImgCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mImgCheck = (ImageView) findViewById(R.id.imageView);
        ((Animatable) mImgCheck.getDrawable()).start();
    }
}
