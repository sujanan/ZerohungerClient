package com.zerohunger.zerohungerclient.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerohunger.zerohungerclient.R;
import com.zerohunger.zerohungerclient.ui.model.User;

import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity {

    static Activity phoneNumberActivity;

    private static final int FROM_PHONE_NUMBER_TO_REGISTER = 1;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthCallbacks;

    private FloatingActionButton fabNext;
    private ProgressBar pbAuth;
    private TextInputEditText textPhoneNumber;
    private TextInputEditText textCountry;
    private TextInputEditText textPhoneCode;
    private boolean textPhoneNumberEmpty = false;
    private boolean textCountryEmpty = false;
    private boolean textPhoneCodeEmpty = false;
    private boolean authenticating = false;
    private String userId;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        phoneNumberActivity = PhoneNumberActivity.this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        fabNext = findViewById(R.id.fabPhoneNumberNext);
        pbAuth = findViewById(R.id.pbPhoneNumberAuth);
        textPhoneNumber = findViewById(R.id.textPhoneNumberPhoneNumber);
        textCountry = findViewById(R.id.textPhoneNumberCountry);
        textPhoneCode = findViewById(R.id.textPhoneNumberPhoneCode);

        fabNext.setVisibility(View.GONE);
        pbAuth.setVisibility(View.GONE);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        textPhoneNumber.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean b1 = event.getAction() == KeyEvent.ACTION_DOWN;
                boolean b2 = keyCode == KeyEvent.KEYCODE_ENTER;
                boolean b3 = !textCountryEmpty && !textPhoneCodeEmpty && !textPhoneNumberEmpty;
                if (b1 && b2 && b3 && !authenticating) {
                    validate();
                    return true;
                }
                return false;
            }
        });
        textPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textPhoneNumberEmpty = s.toString().trim().isEmpty();
                boolean b = !textCountryEmpty && !textPhoneCodeEmpty && !textPhoneNumberEmpty;
                if (b) {
                    fabNext.setVisibility(View.VISIBLE);
                } else {
                    fabNext.setVisibility(View.GONE);
                }
            }
        });
        textCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textCountryEmpty = s.toString().trim().isEmpty();
                boolean b = !textCountryEmpty && !textPhoneCodeEmpty && !textPhoneNumberEmpty;
                if (b) {
                    fabNext.setVisibility(View.VISIBLE);
                } else {
                    fabNext.setVisibility(View.GONE);
                }
            }
        });
        textPhoneCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textPhoneCodeEmpty = s.toString().trim().isEmpty();
                boolean b = !textCountryEmpty && !textPhoneCodeEmpty && !textPhoneNumberEmpty;
                if (b) {
                    fabNext.setVisibility(View.VISIBLE);
                } else {
                    fabNext.setVisibility(View.GONE);
                }
            }
        });
        phoneAuthCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast toast = Toast.makeText(
                            PhoneNumberActivity.this,
                            R.string.phone_number_invalid_request,
                            Toast.LENGTH_LONG);
                    toast.show();
                    authenticating = false;
                    resetUI();
                } else if (e instanceof FirebaseTooManyRequestsException){
                    Toast toast = Toast.makeText(
                            PhoneNumberActivity.this,
                            R.string.phone_number_sms_quota,
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        };
    }

    private void resetUI() {
        fabNext.setEnabled(true);
        fabNext.setImageDrawable(ContextCompat.getDrawable(
                getApplicationContext(), R.drawable.ic_arrow_forward_white_24dp));
        pbAuth.setVisibility(View.GONE);
        textCountry.setEnabled(true);
        textPhoneCode.setEnabled(true);
        textPhoneNumber.setEnabled(true);
    }

    private void validate() {
        fabNext.setEnabled(false);
        fabNext.setImageDrawable(null);
        pbAuth.setVisibility(View.VISIBLE);
        textCountry.setEnabled(false);
        textPhoneCode.setEnabled(false);
        textPhoneNumber.setEnabled(false);
        authenticating = true;
        verifyPhoneNumber();
    }

    private void verifyPhoneNumber() {
        String phoneCode = textPhoneCode.getText().toString();
        phoneNumber = phoneCode + textPhoneNumber.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                phoneAuthCallbacks
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userId = task.getResult().getUser().getUid();
                            checkWhetherUserExist();
                        }
                    }
                });
    }

    private void checkWhetherUserExist() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences userPref = PhoneNumberActivity.this.getSharedPreferences(
                        getString(R.string.preference_user_file_key),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userPref.edit();
                editor.putString(
                        getString(R.string.saved_profile_phone_number),
                        phoneNumber);
                if (!dataSnapshot.child(userId).exists()) {
                    finish();
                    Intent intent = new Intent(PhoneNumberActivity.this,
                            RegisterActivity.class);
                    startActivity(intent);
                } else {
                    User user = dataSnapshot.child(userId).getValue(User.class);
                    String name = user.firstName + " " + user.lastName;
                    editor.putString(
                            getString(R.string.saved_profile_name),
                            name);
                    setResult(1);
                    finish();
                }
                editor.apply();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast toast = Toast.makeText(
                        PhoneNumberActivity.this,
                        "Database read error",
                        Toast.LENGTH_LONG);
                toast.show();
                authenticating = false;
                resetUI();
            }
        });
    }

    @Override
    public void finish() {
        setResult(1);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
