package com.example.udacity.surfconnect;

import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class AccountActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView id;
    TextView infoLabel;
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));

        profilePic = (ImageView) findViewById(R.id.profile_image);
        id = (TextView) findViewById(R.id.id);
        infoLabel = (TextView) findViewById(R.id.info_label);
        info = (TextView) findViewById(R.id.info);

        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                //Get Account Kit Id
                String accountKitId = account.getId();
                id.setText(accountKitId);

                PhoneNumber phoneNumber = account.getPhoneNumber();
                if(phoneNumber!=null){
                    //If the phone number is available , display it
                    String formattedPhoneNumber = formatPhoneNumber(phoneNumber.toString());
                    info.setText(formattedPhoneNumber);
                    infoLabel.setText(R.string.phone_label);
                }else
                {
                    //If the email is available, display it
                    String emailString = account.getEmail();
                    info.setText(emailString);
                    infoLabel.setText(R.string.email_label);
                }
            }

            @Override
            public void onError(AccountKitError accountKitError) {
                String toastmessage =accountKitError.getErrorType().getMessage();
                Toast.makeText(AccountActivity.this, toastmessage, Toast.LENGTH_LONG).show();
                Log.i("error","failed to read account details");
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    public void onLogout(View view){
        AccountKit.logOut();
        launchLoginActivity();
    }
    private void displayProfileInfo(Profile profile) {
        // get Profile ID
        String profileId = profile.getId();
        id.setText(profileId);

        // display the Profile name
        String name = profile.getName();
        info.setText(name);
        infoLabel.setText(R.string.name_label);

        // display the profile picture
        Uri profilePicUri = profile.getProfilePictureUri(100, 100);
        displayProfilePic(profilePicUri);
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private String formatPhoneNumber(String phoneNumber) {
        // helper method to format the phone number for display
        try {
            PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber pn = pnu.parse(phoneNumber, Locale.getDefault().getCountry());
            phoneNumber = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        }
        catch (NumberParseException e) {
            e.printStackTrace();
        }
        return phoneNumber;
    }

    private void displayProfilePic(Uri uri) {
        // helper method to load the profile pic in a circular imageview
        Transformation transformation = (Transformation) new RoundedTransformationBuilder()
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        Picasso.with(AccountActivity.this)
                .load(uri)
                .transform((com.squareup.picasso.Transformation) transformation)
                .into(profilePic);
    }
}
