package mom.com.firebase_service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import mom.com.utils.AppUser;

//import timber.log.Timber;

/**
 * Created by NgocTri on 8/9/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseInsIDService";


    @Override
    public void onTokenRefresh() {
        //Get updated token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Timber.i("http : Token  " + refreshedToken);
        System.out.println("http : token "+refreshedToken);
        sendRegistrationToServer(refreshedToken);
        //You can save the token into third party server to do anything you want
    }

    private void sendRegistrationToServer(String token) {

    }
}
