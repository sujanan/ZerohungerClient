package com.zerohunger.zerohungerclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.zerohunger.zerohungerclient.R;
import com.zerohunger.zerohungerclient.ui.model.User;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText textFirstName;
    private TextInputEditText textLastName;
    private FloatingActionButton fabNext;
    private boolean textFirstNameEmpty = false;
    private boolean textLastNameEmpty = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        textFirstName = findViewById(R.id.textRegisterFirstName);
        textLastName = findViewById(R.id.textRegisterLastName);
        fabNext = findViewById(R.id.fabRegisterNext);

        textFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textFirstNameEmpty = s.toString().trim().isEmpty();
                boolean b = !textFirstNameEmpty && !textLastNameEmpty;
                if (b) {
                    fabNext.setVisibility(View.VISIBLE);
                } else {
                    fabNext.setVisibility(View.GONE);
                }
            }
        });
        textLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textLastNameEmpty = s.toString().trim().isEmpty();
                boolean b = !textFirstNameEmpty && !textLastNameEmpty;
                if (b) {
                    fabNext.setVisibility(View.VISIBLE);
                } else {
                    fabNext.setVisibility(View.GONE);
                }
            }
        });
        textLastName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean b1 = event.getAction() == KeyEvent.ACTION_DOWN;
                boolean b2 = keyCode == KeyEvent.KEYCODE_ENTER;
                boolean b3 = !textFirstNameEmpty && !textLastNameEmpty;
                if (b1 && b2 && b3) {
                    validate();
                    return true;
                }
                return false;
            }
        });
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void validate() {
        String firstName = textFirstName.getText().toString().trim();
        String lastName = textLastName.getText().toString().trim();
        User user = new User(firstName, lastName, "customer");
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId != null) {
            FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userId)
                    .setValue(user);
            SharedPreferences userPref = RegisterActivity.this.getSharedPreferences(
                    getString(R.string.preference_user_file_key),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = userPref.edit();
            editor.putString(
                    getString(R.string.saved_profile_name),
                    user.firstName + " " + user.lastName);
            editor.apply();
            finish();
            PhoneNumberActivity.phoneNumberActivity.finish();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
