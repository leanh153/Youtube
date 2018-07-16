package com.example.leanh.activity;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.leanh.Sqlite.SQLiteHelper;
import com.example.leanh.connector.GoogleConnector;
import com.example.leanh.model.User;
import com.example.leanh.ultil.Constant;
import com.example.leanh.view.ToastCustom;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * this class is SignInActivity contain sign in By google button
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SignInActivity.class.getSimpleName();

    /*Edit text user's name and password*/
    private EditText mUserName, mPassword;

    /*manipulate with database*/
    private SQLiteHelper mHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.sign_in_btn:
                if (isDeviceOnline()) {
                    String userInputString = mUserName.getText().toString();
                    String passwordInputString = mPassword.getText().toString();
                    boolean error = checkInputData(userInputString, passwordInputString);
                    if (!error) {
                        int exist = mHelper.isUserExist(userInputString, passwordInputString);
                        switch (exist) {
                            case -1:
                                ToastCustom.Toast(this, getLayoutInflater(),
                                        getString(R.string.error_incorrect_password_or_username));
                                break;
                            case 1:
                                ToastCustom.Toast(this, getLayoutInflater(),
                                        getString(R.string.error_password));
                                break;
                            case 2:
                                updateUI(null, userInputString);
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                finish();
                                break;

                        }
                    } else {
                        ToastCustom.Toast(this, getLayoutInflater(), getString(R.string.error_invalid_account));
                    }
                } else {
                    ToastCustom.Toast(this, getLayoutInflater(),
                            getString(R.string.error_internet));
                }
                break;
            case R.id.sign_up_tv:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;

            case R.id.sign_in_google:
                if (isDeviceOnline()) {
                    signInByGoogle();
                } else {
                    ToastCustom.Toast(this, getLayoutInflater(),
                            getString(R.string.error_internet));
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account, null);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == Constant.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * init view for activity
     */
    private void initView() {
        Button signInButton = findViewById(R.id.sign_in_btn);
        TextView signUpTv = findViewById(R.id.sign_up_tv);

        mUserName = findViewById(R.id.user_name);
        mPassword = findViewById(R.id.input_password);

        SignInButton signInByGoogle = findViewById(R.id.sign_in_google);

        signInButton.setOnClickListener(this);
        signUpTv.setOnClickListener(this);
        signInByGoogle.setOnClickListener(this);
        mHelper = SQLiteHelper.getInstance(this);
    }

    /**
     * check data input
     */
    private boolean checkInputData(String userName, String password) {
        boolean error = false;
        if (userName.length() == 0 || password.length() == 0) {
            error = true;

        }
        if (Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
            error = true;
            ToastCustom.Toast(this, getLayoutInflater(),
                    getString(R.string.hint_sign_in_by_google));
        }
        return error;
    }

    /**
     * sign in by google
     */
    private void signInByGoogle() {
        // get google client sign in account
        GoogleSignInClient mGoogleSignInClient = new GoogleConnector(this).getGoogleSignInClient();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        // start activity to get account result
        startActivityForResult(signInIntent, Constant.RC_SIGN_IN);
    }

    /**
     * handle the account return
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            //get account user choose
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            updateUI(account, null);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /**
     * start HomeActivity by the account send email and user name to get video history
     * or start by userName
     */
    private void updateUI(GoogleSignInAccount account, String user) {
        Intent intent = new Intent(this, HomeActivity.class);
        if (account != null) {
            //check database about user
            int exist = mHelper.isUserExist(account.getEmail(), "");
            if (exist == -1) {
                // add new user to database if user's not existed yet
                long add = mHelper.addUser(new User(account.getEmail(), ""));
                if (add != -1) {
                    // the add method return -1 which mean add new user false
                    ToastCustom.Toast(this, getLayoutInflater(),
                            getString(R.string.announcement_add_account_complete));
                }
            }
            // start HomeActivity
            // put userName to display
            intent.putExtra("userName", account.getDisplayName());
            // put email to manipulate database
            intent.putExtra("email", account.getEmail());
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            finish();

        } else if (user != null) {
            // start HomeActivity
            // put userName to display
            intent.putExtra("userName", user);
            startActivity(intent);
            // use this animation
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            finish();
        }
    }

    /**
     * check if device online, this require permission in AndroidManifest
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     */
    private boolean isDeviceOnline() {
        // check NetWork information
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // connect if networkInfo not null and connected state
        return (networkInfo != null && networkInfo.isConnected());
    }

}