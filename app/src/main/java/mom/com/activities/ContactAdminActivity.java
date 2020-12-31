package mom.com.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import mom.com.R;


public class ContactAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_admin);
//        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000'>Contact Admin</font>"));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        call(findViewById(R.id.calluser_iv),"9008241090");
        addContact(findViewById(R.id.addcontact_iv),"MotherOnMission","9008241090");
        email(findViewById(R.id.sendmessage_tv),"mom@motheronmission.com");
        openWhatsApp(findViewById(R.id.whatupsend_tv),"9008241090");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }


    void call(View view,String mobile) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:0" + mobile));
                if (ActivityCompat.checkSelfPermission(ContactAdminActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                }
                startActivity(callIntent);
            }
        });

    }


    void addContact(View view,String name,String mobile) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                contactIntent
                        .putExtra(ContactsContract.Intents.Insert.NAME, name)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, mobile);
                startActivityForResult(contactIntent, 1);

            }
        });

    }


    void sms(View view,String str) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", str);
                smsIntent.putExtra("sms_body", "School Name");
                startActivity(smsIntent);
            }
        });
    }


    void email(View view,String str) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(Intent.ACTION_SEND);
                String[] recipients={"mailto@gmail.com"};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT,"Subject text here...");
                intent.putExtra(Intent.EXTRA_TEXT,"Body of the content here...");
                intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));*/
                startActivity(new Intent(getApplicationContext(),EmailActivity.class));
            }
        });
    }




    void openWhatsApp(View view, String number) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smsNumber = "91" + number;
                boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
                if (isWhatsappInstalled) {

                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(smsNumber) + "@s.whatsapp.net");  //phone number without "+" prefix

                    startActivity(sendIntent);
                } else {

                    Uri uri = Uri.parse("market://details?id=com.whatsapp");
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    Toast.makeText(ContactAdminActivity.this, "WhatsApp not Installed",
                            Toast.LENGTH_SHORT).show();
                    startActivity(goToMarket);
                }

            }
        });
    }


    boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = this.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


}
