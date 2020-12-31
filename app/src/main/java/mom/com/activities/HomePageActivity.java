package mom.com.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Scroller;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import mom.com.R;

public class HomePageActivity extends AppCompatActivity {


    LinearLayout login;
    LinearLayout sign_up;
    ViewPager mPager;
    RadioGroup mRadioGroup;

    TextView terms;

    MyPagerAdapter adapter;
    int currentPage = 0;
    int NUM_PAGES = 5;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    Handler handler;
    LinearLayout skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);
        login=findViewById(R.id.login);
        sign_up=findViewById(R.id.sign_up);
        mPager=findViewById(R.id.viewpager);
        mRadioGroup=findViewById(R.id.radiogroup);
        skip=findViewById(R.id.skip);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
        adapter=new MyPagerAdapter();

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        mRadioGroup.check(R.id.radioButton);
                        break;
                    case 1:
                        mRadioGroup.check(R.id.radioButton2);
                        break;
                    case 2:
                        mRadioGroup.check(R.id.radioButton3);
                        break;
                    case 3:
                        mRadioGroup.check(R.id.radioButton4);
                        break;
                    case 4:
                        mRadioGroup.check(R.id.radioButton5);
                        break;
                    case 5:
                        mRadioGroup.check(R.id.radioButton6);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mPager.setAdapter(adapter);
        changePagerScroller();

        handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES - 1) {
                    currentPage = 0;
                    changePagerScroller();
                }
                mPager.setCurrentItem(currentPage++, true);

            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                timer.cancel();
                return false;
            }
        });
    }

    public void changePagerScroller() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(mPager.getContext());
            mScroller.set(mPager, scroller);
        } catch (Exception e) {
        }
    }

    class MyPagerAdapter extends PagerAdapter {

        public int getCount() {
            return 6;
        }

        public Object instantiateItem(View collection, int position) {

            LayoutInflater inflater = (LayoutInflater) collection.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.layout.layout_fragment_page1;
                    break;
                case 1:
                    resId = R.layout.layout_fragment_page2;
                    break;
                case 2:
                    resId = R.layout.layout_fragment_page3;
                    break;
                case 3:
                    resId = R.layout.layout_fragment_page4;
                    break;
                case 4:
                    resId = R.layout.layout_fragment_page5;
                    break;
                case 5:
                    resId = R.layout.layout_fragment_page6;
                    break;
            }

            View view = inflater.inflate(resId, null);

            ((ViewPager) collection).addView(view, 0);

            return view;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);

        }


        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }

    class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 1500;

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mScrollDuration);
        }


    }
}
