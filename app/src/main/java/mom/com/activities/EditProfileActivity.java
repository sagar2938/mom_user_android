package mom.com.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mom.com.R;
import mom.com.network.ApiCallService;
import mom.com.network.response.SucessResponse;
import mom.com.utils.Helper;
import mom.com.utils.Preferences;
import mom.com.utils.UploadEvent;
import mom.com.utils.Validation;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;


public class EditProfileActivity extends BaseActivity {


    TextView edit_profile;
    EditText name, email, mobile;
    ImageView image;
    Button submit;
    RelativeLayout upload;
    ProgressBar progressBar;


    String url = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
//        getSupportActionBar().setTitle("Edit Profile");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        url=Preferences.getInstance(getApplicationContext()).getProfileImage();

        edit_profile = findViewById(R.id.edit_profile);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        image = findViewById(R.id.image);
        mobile = findViewById(R.id.mobile);
        submit = findViewById(R.id.submit);
        upload = findViewById(R.id.upload);
        progressBar = findViewById(R.id.progressBar);




        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkPermissions()) {
                    imageName = Helper.getRandom();
                    dialogPlus();
                } else {
                    requestPermission();
                }


            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobile.getText().toString().trim().isEmpty()){
                    getDialog("Enter Mobile Number");
                    return;
                } if (mobile.getText().toString().length()!=10){
                    getDialog("Enter 10 digit Mobile Number");
                    return;
                }if (!email.getText().toString().isEmpty()){
                    if (!Validation.isEmailValid(email.getText().toString())){
                        getDialog("Enter valid email");
                        return;
                    }
                }

                Map map = new HashMap();
                map.put("mobile", mobile.getText().toString());
                map.put("name", name.getText().toString());
                map.put("email", email.getText().toString());
                map.put("profileImage", url);
                ApiCallService.action(EditProfileActivity.this, map, ApiCallService.Action.ACTION_UPDATE_RESPONSE);
            }
        });

        if (!Preferences.getInstance(getApplicationContext()).getProfileImage().equals("")) {
            Glide.with(this)
                    .load(Preferences.getInstance(getApplicationContext()).getProfileImage())
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .apply(RequestOptions.skipMemoryCacheOf(true))
//                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                    .apply(new RequestOptions().placeholder(R.drawable.user))
                    .into(image);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Preferences.getInstance(getApplicationContext()).getProfileImage().equals("")) {



           /*
            Glide.with(this)
                    .load(Preferences.getInstance(getApplicationContext()).getProfileImage())
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .apply(RequestOptions.skipMemoryCacheOf(true))
//                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
//                    .apply(new RequestOptions().placeholder(R.drawable.user))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(image);*/
        }


        name.setText(Preferences.getInstance(getApplicationContext()).getName());
        mobile.setText(Preferences.getInstance(getApplicationContext()).getMobile());
        email.setText(Preferences.getInstance(getApplicationContext()).getEmail());

       /* if (!Preferences.getInstance(getApplicationContext()).getEmail().equals("")) {
            email.setText(Preferences.getInstance(getApplicationContext()).getEmail());
        } else {
            email.setHint("Enter email");
        }*/

        Map map = new HashMap();
        map.put("mobile", Preferences.getInstance(getApplicationContext()).getMobile());
//        ApiCallService.action(this,map,ApiCallService.Action.ACTION_GET_ADDRESS_LIST);
    }


    @Subscribe
    public void uploadEvent(UploadEvent event) {
        url = event.getUrl();
    }

    @Subscribe
    public void updateProfile(SucessResponse response) {
        if (response.getResponse().getConfirmation() == 1) {
            Preferences.getInstance(getApplicationContext()).setProfileImage(url);
            Preferences.getInstance(getApplicationContext()).setEmail(email.getText().toString());
            Preferences.getInstance(getApplicationContext()).setName(name.getText().toString());
            Preferences.getInstance(getApplicationContext()).setMobile(mobile.getText().toString());
            getDialogSuccess(response.getResponse().getMessage());
        } else {
            getDialog(response.getResponse().getMessage());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {

                if (requestCode == REQUEST_CAMERA) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    image.setImageURI(fileUri);
                    fileUri = Uri.fromFile(Helper.getOutputMediaFile(MEDIA_TYPE_IMAGE, imageName));
                    String str = compressImage(this.fileUri.toString());
                    Uri uri = Uri.fromFile(new File(str));
                    uploadFile(imageName, uri);
//                    uploadFile(imageName, fileUri);
                }
                if (requestCode == REQUEST_GALLERY) {
                    try {
                        fileUri = data.getData();
                        String[] projection = {MediaStore.Images.Media.DATA};
                        String picturePath = "";
                        try {
                            Cursor cursor = getContentResolver().query(fileUri, projection, null, null, null);
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(projection[0]);
                            picturePath = cursor.getString(columnIndex);
                            cursor.close();
                        } catch (Exception e) {
                        }
//                        Bitmap photo = ImagePicker.getImageFromResult(this, resultCode, data);
                        image.setImageURI(fileUri);
                        String str = compressImage(fileUri.toString());
                        Uri uri = Uri.fromFile(new File(str));
                        uploadFile(imageName, uri);

//                        uploadFile(imageName, fileUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(EditProfileActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                permissions, 100);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EditProfileActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


}
