package com.example.leanh.connector;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * this class connect google sign in, Get GoogleSinInClient use to sign in and log out
 */
public class GoogleConnector {
    private final GoogleSignInClient mGoogleSignInClient;

    public GoogleConnector(Context context) {
        //  google sign in option
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()// request email this contain email, userName
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    // get GoogleSignInClient
    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }
}
