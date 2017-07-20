package com.example.udacity.surfconnect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.appevents.AppEventsLogger;

public class LoginActivity extends AppCompatActivity {

    public static int APP_REQUEST_CODE =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));
        // Check for existing token
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        if(accessToken!=null){
            launchAccountActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
