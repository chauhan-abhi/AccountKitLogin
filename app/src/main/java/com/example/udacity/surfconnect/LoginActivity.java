package com.example.udacity.surfconnect;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    public static int APP_REQUEST_CODE =1;
    LoginButton fbloginButton;
    CallbackManager callbackManager;            //help us handle the result of our login attempt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));
        /*PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.example.udacity.surfconnect", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }*/
        fbloginButton = (LoginButton)findViewById(R.id.facebook_login_button);
        fbloginButton.setReadPermissions("email");
        //Login Button Callback registration
        callbackManager = CallbackManager.Factory.create();
        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
               // launchAccountActivity();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                String toastMessage = error.getMessage();
                Toast.makeText(LoginActivity.this, toastMessage, Toast.LENGTH_LONG).show();
            }
        });


        // Check for existing token
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if(accessToken!=null){
            launchAccountActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Forward result to the callback manager for Login Button
        callbackManager.onActivityResult(requestCode,resultCode,data);
        
        //confirm that this response matches your request
        if(requestCode==APP_REQUEST_CODE){
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if(loginResult.getError()!=null){
                //display login error
                String toastMessage = loginResult.getError().getErrorType().getMessage();
                Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();

            }else if(loginResult.getAccessToken()!=null){
                //on successful login proceed to account activity
                launchAccountActivity();
            }
        }
    }



    public void onLogin(final LoginType loginType){
        //create intent for Account Kit Activity
        final Intent intent = new Intent(this, AccountKitActivity.class);
        //configure login and response type
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new
                AccountKitConfiguration.AccountKitConfigurationBuilder(
                        loginType,
                        AccountKitActivity.ResponseType.TOKEN
        );
        final AccountKitConfiguration configuration = configurationBuilder.build();
        //launch account kit activity
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configuration);
        //track success for login via on ActivityResult
        startActivityForResult(intent,APP_REQUEST_CODE);

    }

    public void onPhoneLogin(View view){
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("onPhoneLogin");
        onLogin(LoginType.PHONE);
    }
    public void onEmailLogin(View view){
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent("onEmailLogin");
        onLogin(LoginType.EMAIL);}

    private void launchAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }

}
