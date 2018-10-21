package com.wheresmybus.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.wheresmybus.MainActivity;
import com.wheresmybus.R;
import com.wheresmybus.helpers.PreferenceHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String IS_SIGNED_IN = "is_signed_in";
    private final int RC_SIGN_IN = 11;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.google_signin).setOnClickListener(this);
        findViewById(R.id.signin).setOnClickListener(this);
    }

    private void signIn() {
        final String email = ((EditText) findViewById(R.id.email_input)).getText().toString();
        final String password = ((EditText) findViewById(R.id.password_input)).getText().toString();
        if (email.replaceAll(" ", "").equalsIgnoreCase("") || !isValidEmail(email)) {
            Snackbar.make(findViewById(R.id.login_parent), "Enter a valid email address", Snackbar.LENGTH_SHORT).show();
        } else if (password.replaceAll(" ", "").equalsIgnoreCase("")) {
            Snackbar.make(findViewById(R.id.login_parent), "Enter password", Snackbar.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                signedIn(user);
                            } else {
                                mAuth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    signedIn(user);
                                                } else {
                                                    signInFailed();
                                                }
                                            }
                                        });

                            }
                        }
                    });
        }

    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                signInFailed();
                Log.w("error", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            signedIn(user);
                        } else {
                            signInFailed();
                            Log.w("error", task.getException());
                        }
                    }
                });
    }

    private void signInFailed() {
        Snackbar.make(findViewById(R.id.login_parent), "Sign in failed", Snackbar.LENGTH_SHORT).show();
        PreferenceHelper.getInstance(this).writeBoolean(IS_SIGNED_IN, false);
    }

    private void signedIn(FirebaseUser user) {
        PreferenceHelper.getInstance(this).writeBoolean(IS_SIGNED_IN, true);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signin:
                signIn();
                break;
            case R.id.google_signin:
                googleSignIn();
                break;
        }
    }
}
