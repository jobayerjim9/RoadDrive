package com.road.roaddrive.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.road.roaddrive.R;
import com.road.roaddrive.model.AppData;
import com.road.roaddrive.model.DriverProfile;

import java.util.Objects;

public class SignInActivityEmail extends AppCompatActivity {
    TextInputLayout emailInput;
    TextInputLayout passwordInput;
    Button signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_email);
        TextView signInState=findViewById(R.id.signInState);
        emailInput=findViewById(R.id.emailInput);
        passwordInput=findViewById(R.id.passwordInput);
        signInButton=findViewById(R.id.signInButton);
        signInState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivityEmail.this,SignInActivity.class));
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailInput.getEditText().getText().toString();
                String pass=passwordInput.getEditText().getText().toString();
                if (email.isEmpty())
                {
                    emailInput.setErrorEnabled(true);
                    emailInput.setError("Email Cannot Be Blank");
                }
                else if (pass.isEmpty())
                {
                    passwordInput.setErrorEnabled(true);
                    passwordInput.setError("Password Cannot Be Blank");
                }
                else
                {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
//                                startActivity(new Intent(SignInActivityEmail.this,MainActivity.class));
//                                finish();
                                checkSignUp();
                            }
                            else
                            {
                                Toast.makeText(SignInActivityEmail.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void checkSignUp() {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("DriverProfile").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    DriverProfile driverProfile=dataSnapshot.getValue(DriverProfile.class);
                    if(driverProfile!=null)
                    {
                        AppData.setDriverProfile(driverProfile);
                        if(driverProfile.isVerified())
                        {
                            startActivity(new Intent(SignInActivityEmail.this,MainActivity.class));
                        }
                        else
                        {
                            startActivity(new Intent(SignInActivityEmail.this,PendingActivity.class));
                        }
                        finish();
                    }

                }
                else
                {
                    Intent intent=new Intent(SignInActivityEmail.this,SignUpActivity.class);
                    intent.putExtra("mobile",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    intent.putExtra("email",FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
