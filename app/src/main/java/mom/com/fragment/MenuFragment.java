package mom.com.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.HashMap;

import mom.com.activities.AddressBookActivity;
import mom.com.activities.ContactAdminActivity;
import mom.com.activities.HomePageActivity;
import mom.com.activities.LoginActivity;
import mom.com.activities.ProfileActivity;
import mom.com.R;
import mom.com.foodorders.OrderHistory;
import mom.com.network.ThisApp;
import mom.com.network.response.profile.GetProfileResponse;
import mom.com.utils.AppUser;
import mom.com.utils.LocalRepositories;
import mom.com.utils.Preferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragment extends Fragment {
    CardView loginSignUp;
    CardView profile;
    RelativeLayout share, logout, yourOrder, addressBook;
    TextView name, email, mobile;
    ImageView image;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Menu");

        profile = view.findViewById(R.id.profile);
        loginSignUp = view.findViewById(R.id.loginSignUp);
        logout = view.findViewById(R.id.logout);

        share = view.findViewById(R.id.share);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        image = view.findViewById(R.id.image);
        mobile = view.findViewById(R.id.mobile);
        yourOrder = view.findViewById(R.id.yourOrder);
        progressBar = view.findViewById(R.id.progressBar);
        addressBook = view.findViewById(R.id.addressBook);

        name.setText(Preferences.getInstance(getContext()).getName());
        mobile.setText(Preferences.getInstance(getContext()).getMobile());
        if (!Preferences.getInstance(getContext()).getEmail().equals("")) {
            email.setText(Preferences.getInstance(getContext()).getEmail());
        } else {
            email.setHint("Not found");

        }



        view.findViewById(R.id.rateOnStore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://play.google.com/store/apps/details?id=mom.com"));
                startActivity(i);
            }
        });

        view.findViewById(R.id.aboutUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://delhi-1018.appspot.com/#!/main/about"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.contactUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(getActivity(), ContactAdminActivity.class);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.tnc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://delhi-1018.appspot.com/#!/main/terms-con"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://delhi-1018.appspot.com/#!/main/privacy-policy"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });

        addressBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddressBookActivity.class));
            }
        });

        yourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OrderHistory.class));
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sent from MOM app");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Signup now on MOM and order delicious MOM COOKED FOOD. Use this link and get up to 50% off. https://play.google.com/store/apps/details?id=mom.com");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
        loginSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomePageActivity.class));
            }
        });


        if (Preferences.getInstance(getContext()).isLogin()) {
            loginSignUp.setVisibility(View.GONE);
        } else {
            profile.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.getInstance(getActivity()).setLogin(false);
                Preferences.getInstance(getActivity()).setAddress("");
                Preferences.getInstance(getActivity()).setMobile("");
                AppUser appUser = LocalRepositories.getAppUser(getActivity());
                appUser.setCartModels(null);
                appUser.setMomMobile("");
                appUser.setAmount(0.0);
                LocalRepositories.saveAppUser(getActivity(), appUser);

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        HashMap map=new HashMap();
        map.put("mobile",Preferences.getInstance(getContext()).getMobile());

        ThisApp.getApi(getContext()).getProfile(map).enqueue(new Callback<GetProfileResponse>() {
            @Override
            public void onResponse(Call<GetProfileResponse> call, Response<GetProfileResponse> r) {
                GetProfileResponse response=r.body();
                if (response.getResponse().getConfirmation()==1){
                    Preferences.getInstance(getContext()).setProfileImage(response.getResponse().getProfile_image());
                    if (!Preferences.getInstance(getContext()).getProfileImage().equals("")) {
                        try {
                            Glide.with(getActivity())
                                    .load(Preferences.getInstance(getContext()).getProfileImage())
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            image.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.user));
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .into(image);
                        }catch (Exception e){
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }

                    }
                }

            }

            @Override
            public void onFailure(Call<GetProfileResponse> call, Throwable t) {

            }
        });




    }
}
