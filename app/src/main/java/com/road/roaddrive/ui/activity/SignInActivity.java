package com.road.roaddrive.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.road.roaddrive.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    ProgressDialog progressDialog;
    // [END declare_auth]
    private TextInputLayout emailInput;
    private TextInputLayout passwordInput1;
    private TextInputLayout passwordInput2;
    private CardView emailPassLayout;
    private CardView cardView;
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
        emailPassLayout=findViewById(R.id.emailPassLayout);
        emailInput=findViewById(R.id.emailInput);
        passwordInput1=findViewById(R.id.passwordInput1);
        passwordInput2=findViewById(R.id.passwordInput2);
        cardView=findViewById(R.id.cardView);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Creating Your Profile!");
        progressDialog.setCancelable(true);
//        Objects.requireNonNull(otp_view.getEditText()).addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(s.toString().length()==6)
//                {
//                    verifyPhoneNumberWithCode(mVerificationId,s.toString().trim());
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });


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
                otp_view.getEditText().setText(code);
                phoneNumberInput.setVisibility(View.GONE);
                otp_view.setVisibility(View.VISIBLE);
                verifyPhoneNumberWithCode(credential);
                //signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]
                progressDialog.dismiss();
                Toast.makeText(SignInActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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
                progressDialog.setMessage("Retrieving Code!");
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
    private void verifyPhoneNumberWithCode(PhoneAuthCredential credential) {
        // [START verify_with_code]
        try {
            // [END verify_with_code
            signInState.setText("Phone Verified! Create Email & Password!");
            cardView.setVisibility(View.GONE);
            emailPassLayout.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            signInButton.setText("Create Account");
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInWithEmailPass(credential);
                }
            });
            //signInWithPhoneAuthCredential(credential);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            signInState.setText(e.getLocalizedMessage());
        }
    }

    private void signInWithEmailPass(PhoneAuthCredential credential) {
        String email=emailInput.getEditText().getText().toString();
        String pass=passwordInput1.getEditText().getText().toString();
        String confirmPass=passwordInput2.getEditText().getText().toString();
        if (email.isEmpty())
        {
            emailInput.setErrorEnabled(true);
            emailInput.setError("Enter Your Email Please!");
        }
        else if (pass.isEmpty())
        {
            passwordInput1.setErrorEnabled(true);
            passwordInput1.setError("Enter Your Password Please!");
        }
        else if (confirmPass.isEmpty())
        {
            passwordInput2.setErrorEnabled(true);
            passwordInput2.setError("Enter Your Password Again!");
        }
        else if (!pass.equals(confirmPass))
        {
            passwordInput2.setErrorEnabled(true);
            passwordInput2.setError("Password Doesn't Match!");
        }
        else
        {
            if (!progressDialog.isShowing())
            {
                progressDialog.show();
            }
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                    {
                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    try {
                                        FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                Log.d("Success", "Linked!");
                                            }
                                        });
                                    }
                                    catch (Exception e)
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(SignInActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
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
                                                intent.putExtra("email",emailInput.getEditText().getText().toString());
                                                startActivity(intent);
                                            }
                                            progressDialog.dismiss();
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            progressDialog.dismiss();
                                            Toast.makeText(SignInActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignInActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(SignInActivity.this, "Try With Different Email!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    progressDialog.dismiss();
                    Toast.makeText(SignInActivity.this, "Sign In Cancelled!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // [START resend_verification]
    // [END resend_verification]

    // [START sign_in_with_phone]
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//
//
//
//
//                            // [START_EXCLUDE]
//                            // [END_EXCLUDE]
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                                // [START_EXCLUDE silent]
//                                signInState.setError("Invalid code.");
//                                // [END_EXCLUDE]
//                            }
//                            // [START_EXCLUDE silent]
//                            // Update UI
//
//                            // [END_EXCLUDE]
//                        }
//                    }
//                });
//    }
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

