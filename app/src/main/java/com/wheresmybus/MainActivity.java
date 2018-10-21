package com.wheresmybus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.wheresmybus.helpers.PreferenceHelper;
import com.wheresmybus.login.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean signedIn = PreferenceHelper.getInstance(this).readBoolean(LoginActivity.IS_SIGNED_IN);
        if (!signedIn) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        findViewById(R.id.signout).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signout:
                PreferenceHelper.getInstance(MainActivity.this).writeBoolean(LoginActivity.IS_SIGNED_IN, false);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }
    }
}
