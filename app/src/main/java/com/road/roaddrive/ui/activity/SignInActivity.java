package com.road.roaddrive.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.road.roaddrive.R;

import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
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


import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private TextInputLayout phoneNumberInput;
    private TextView signInState;
    private Button signInButton;
    private TextInputLayout otp_view;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private String mobile;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        phoneNumberInput=findViewById(R.id.phoneNumberInput);
        signInState=findViewById(R.id.signInState);
        signInButton=findViewById(R.id.signInButton);
        otp_view=findViewById(R.id.otp_view);

        Objects.requireNonNull(otp_view.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==6)
                {
                    verifyPhoneNumberWithCode(mVerificationId,s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileText= Objects.requireNonNull(phoneNumberInput.getEditText()).getText().toString().trim();
                Log.d("PhoneNumber",mobileText);
                if(mobileText.isEmpty())
                {
                    phoneNumberInput.setErrorEnabled(true);
                    phoneNumberInput.setError("Enter Your Phone Number");
                }
                else {
                    mobile = "+88" + mobileText;
                    startPhoneNumberVerification(mobile);
                }

            }
        });
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to contact or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                // [START_EXCLUDE silent]

                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                // [END_EXCLUDE]
                String code = credential.getSmsCode();
                if(code!=null) {
                    otp_view.getEditText().setText(code);
                    verifyPhoneNumberWithCode(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    signInState.setError("Invalid phone number");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    signInState.setError("Quota exceeded");
                    // [END_EXCLUDE]
                }
                else if (e instanceof FirebaseNetworkException)
                {
                    signInState.setError("Network Error!");
                }

                // Show a message and update the UI
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                phoneNumberInput.setVisibility(View.GONE);
                otp_view.setVisibility(View.VISIBLE);
                signInButton.setVisibility(View.GONE);
                // [START_EXCLUDE]
                // Update UI

                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(Objects.requireNonNull(phoneNumberInput.getEditText()).getText().toString());
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }
    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }
    ProgressDialog progressDialog;
    private void verifyPhoneNumberWithCode(@NonNull String verificationId, @NonNull String code) {
        // [START verify_with_code]
        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing In!");
            progressDialog.setCancelable(false);
            progressDialog.show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            // [END verify_with_code
            signInWithPhoneAuthCredential(credential);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // [START resend_verification]
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("DriverProfile").child(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid());
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {
                                        startActivity(new Intent(SignInActivity.this,MainActivity.class));
                                    }
                                    else
                                    {
                                        Intent intent=new Intent(SignInActivity.this,SignUpActivity.class);
                                        intent.putExtra("mobile",mobile);
                                        startActivity(intent);
                                    }
                                    progressDialog.dismiss();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    progressDialog.dismiss();
                                }
                            });


                            // [START_EXCLUDE]
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                signInState.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI

                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private boolean validatePhoneNumber() {
        String phoneNumber = Objects.requireNonNull(phoneNumberInput.getEditText()).getText().toString();
        if (phoneNumber.isEmpty()) {
            signInState.setError("Invalid phone number.");
            return false;
        }

        return true;
    }
}

