package com.example.leanh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.leanh.Sqlite.SQLiteHelper;
import com.example.leanh.model.User;
import com.example.leanh.view.ToastCustom;

/**
 * this class is sign up activity check user input and check user existed in database or not
 * add new one if not
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    /*Edit text user's name, password and confirm password*/
    private EditText mUserName, mPassWord, mConfirmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // initialize view
        initView();
    }

    /**
     * initialize view for activity
     */
    private void initView() {
        Button buttonSignUp = findViewById(R.id.sign_up_btn);
        TextView textView = findViewById(R.id.sign_in_tv);
        mUserName = findViewById(R.id.user_name);
        mPassWord = findViewById(R.id.input_password);
        mConfirmPassword = findViewById(R.id.confirm_password);

        buttonSignUp.setOnClickListener(this);
        textView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.sign_up_btn:
                String userNameString = mUserName.getText().toString();
                String passwordString = mPassWord.getText().toString();
                String passwordConfirmString = mConfirmPassword.getText().toString();
                boolean error = checkInputData(userNameString, passwordString, passwordConfirmString);
                if (!error) {
                    // get SQLiteHelper instance
                    SQLiteHelper helper = SQLiteHelper.getInstance(this);
                    // check if user exist
                    int exist = helper.isUserExist(userNameString, passwordString);
                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                    switch (exist) {
                        case -1:
                            // add new user to database if user not existed
                            User user = new User(userNameString, passwordConfirmString);
                            long add = helper.addUser(user);
                            if (add != -1) {
                                // successfully add user
                                ToastCustom.Toast(this, getLayoutInflater(),
                                        getString(R.string.announcement_add_account_complete));
                            }
                            // move to SignInActivity
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            finish();
                            break;
                        case 1:
                            // if mUserName exist require make another one
                            ToastCustom.Toast(this, getLayoutInflater(),
                                    getString(R.string.user_name_exist));
                            break;
                        case 2:
                            // if user existed in the database show this
                            ToastCustom.Toast(this, getLayoutInflater(),
                                    getString(R.string.announcement_account_registered));
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            finish();
                            break;
                    }

                }
                break;
            case R.id.sign_in_tv:
                // start SignInActivity
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
                break;
        }
    }

    /**
     * check data user input
     */
    private boolean checkInputData(String userName, String password, String passwordConfirm) {
        boolean error = false;
        // if the mUserName is an email, toast sign in by google in sign in activity
        if (Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
            error = true;
            ToastCustom.Toast(this, getLayoutInflater(),
                    getString(R.string.hint_click_sign_in));
        }

        // all field are require
        if (userName.length() == 0 || password.length() == 0 || passwordConfirm.length() == 0) {
            error = true;
            ToastCustom.Toast(this, getLayoutInflater(),
                    getString(R.string.error_information));
        }
        // check password is the same
        if (passwordConfirm.compareTo(password) != 0) {
            error = true;
            ToastCustom.Toast(this, getLayoutInflater(),
                    getString(R.string.error_password_not_same));
        }
        return error;
    }

}
