package com.road.roaddrive.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.road.roaddrive.R;
import com.road.roaddrive.model.AgentProfile;
import com.road.roaddrive.model.DriverProfile;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout nameText, agentUsernameText;
    private Spinner driverTypeSpinner;
    private ImageView carLicense1, carLicense2, drivingLicense1, drivingLicense2;
    private Button finishButton;
    private final int CAR_LICENSE_IMAGE_1 = 1;
    private final int CAR_LICENSE_IMAGE_2 = 2;
    private final int DRIVING_LICENSE_IMAGE_1 = 3;
    private final int DRIVING_LICENSE_IMAGE_2 = 4;
    private Uri carLicense1path, carLicense2path, drivingLicense1path, drivingLicense2path;
    private String driverType;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    private boolean agentExist=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();

    }

    private void initView() {
        storageRef = storage.getReference().child("DriverPapers").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        nameText = findViewById(R.id.nameText);
        agentUsernameText = findViewById(R.id.agentUsernameText);
        driverTypeSpinner = findViewById(R.id.driverTypeSpinner);
        carLicense1 = findViewById(R.id.carLicense1);
        carLicense2 = findViewById(R.id.carLicense2);
        drivingLicense1 = findViewById(R.id.drivingLicense1);
        drivingLicense2 = findViewById(R.id.drivingLicense2);
        finishButton = findViewById(R.id.finishButton);
        driverTypeSpinner = findViewById(R.id.driverTypeSpinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.typeDriver, R.layout.spinner_item);
        driverTypeSpinner.setAdapter(adapter);
        driverTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    driverType = null;
                } else {
                    driverType = Objects.requireNonNull(adapter.getItem(position)).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                driverType = null;
            }
        });


        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInfo();
            }
        });
        carLicense1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareImagePicker(CAR_LICENSE_IMAGE_1);
            }
        });

        carLicense2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareImagePicker(CAR_LICENSE_IMAGE_2);
            }
        });
        drivingLicense1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareImagePicker(DRIVING_LICENSE_IMAGE_1);
            }
        });
        drivingLicense2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareImagePicker(DRIVING_LICENSE_IMAGE_2);
            }
        });

    }

    private void validateInfo() {
        final String name, agentUsername, mobile;
        mobile = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber();
        name = Objects.requireNonNull(nameText.getEditText()).getText().toString().trim();
        agentUsername = Objects.requireNonNull(agentUsernameText.getEditText()).getText().toString().trim();
        if (mobile == null) {
            Toast.makeText(this, "Please Sign In Again!", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        } else if (name.isEmpty()) {
            nameText.setErrorEnabled(true);
            nameText.setError("Please Provide Your Name");
        } else if (agentUsername.isEmpty()) {
            agentUsernameText.setErrorEnabled(true);
            agentUsernameText.setError("Please Provide Your Agent Username");
        } else if (driverType == null) {
            Toast.makeText(this, "Please Select Your Vehicle Type!", Toast.LENGTH_SHORT).show();
        } else if (carLicense1path == null || carLicense2path == null) {
            Toast.makeText(this, "Please Upload Car License Photo", Toast.LENGTH_SHORT).show();
        } else if (drivingLicense1path == null || drivingLicense2path == null) {
            Toast.makeText(this, "Please Upload Driving License Photo", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please Be Patient! Uploading Your Data! (0%)");
            progressDialog.show();
            final DriverProfile driverProfile = new DriverProfile(FirebaseAuth.getInstance().getUid(), name, mobile, driverType,agentUsername);
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DriverProfile").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
            Bitmap car1 = ((BitmapDrawable) carLicense1.getDrawable()).getBitmap();
            Bitmap car2 = ((BitmapDrawable) carLicense2.getDrawable()).getBitmap();
            Bitmap driving1 = ((BitmapDrawable) drivingLicense1.getDrawable()).getBitmap();
            Bitmap driving2 = ((BitmapDrawable) drivingLicense2.getDrawable()).getBitmap();
            ByteArrayOutputStream baosCar1 = new ByteArrayOutputStream();
            car1.compress(Bitmap.CompressFormat.JPEG, 100, baosCar1);
            final byte[] car1Data = baosCar1.toByteArray();
            ByteArrayOutputStream baosCar2 = new ByteArrayOutputStream();
            car2.compress(Bitmap.CompressFormat.JPEG, 100, baosCar2);
            final byte[] car2Data = baosCar2.toByteArray();
            ByteArrayOutputStream baosDriving1 = new ByteArrayOutputStream();
            driving1.compress(Bitmap.CompressFormat.JPEG, 100, baosDriving1);
            final byte[] driving1Data = baosDriving1.toByteArray();
            ByteArrayOutputStream baosDriving2 = new ByteArrayOutputStream();
            driving2.compress(Bitmap.CompressFormat.JPEG, 100, baosDriving2);
            final byte[] driving2Data = baosDriving1.toByteArray();
            DatabaseReference agentRef=FirebaseDatabase.getInstance().getReference().child("AgentProfile");
            agentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        AgentProfile agentProfile=dataSnapshot1.getValue(AgentProfile.class);
                        if(agentProfile!=null)
                        {
                            String agent=null;
                            try {

                                agent = agentProfile.getUsername();

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            if(agent!=null) {
                                if (agentProfile.getUsername().equals(agentUsername)) {
                                    agentExist = true;
                                    dataSnapshot1.child("DriversRegistered").child(FirebaseAuth.getInstance().getUid()).child("a").getRef().setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                storageRef.child("CarLicense1.jpg").putBytes(car1Data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            progressDialog.setMessage("Please Be Patient! Uploading Your Data! (25%)");
                                                            storageRef.child("CarLicense2.jpg").putBytes(car2Data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        progressDialog.setMessage("Please Be Patient! Uploading Your Data! (50%)");
                                                                        storageRef.child("DrivingLicense1.jpg").putBytes(driving1Data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    progressDialog.setMessage("Please Be Patient! Uploading Your Data! (75%)");
                                                                                    storageRef.child("DrivingLicense2.jpg").putBytes(driving2Data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                progressDialog.setMessage("Please Be Patient! Uploading Your Data! (100%)");
                                                                                                databaseReference.setValue(driverProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            progressDialog.dismiss();
                                                                                                            startActivity(new Intent(SignUpActivity.this, PendingActivity.class));
                                                                                                            finish();
                                                                                                        } else {
                                                                                                            progressDialog.dismiss();
                                                                                                            Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    }
                                                                                                });

                                                                                            } else {
                                                                                                progressDialog.dismiss();
                                                                                                Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    progressDialog.dismiss();
                                                                                    Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    } else {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                }
                            }
                            else 
                            {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Server Error! Restart App!", Toast.LENGTH_SHORT).show();
                                DatabaseReference ref=dataSnapshot1.getRef();
                                ref.removeValue();
                            }
                            }


                    }
                    if(!agentExist)
                    {
                        progressDialog.dismiss();
                        agentUsernameText.setErrorEnabled(true);
                        agentUsernameText.setError("Invalid Agent Username!!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

    }

    private void prepareImagePicker(int code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, code);
            } else {
                chooseImage(code);
            }

        } else {
            chooseImage(code);
        }
    }

    private void chooseImage(int code) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAR_LICENSE_IMAGE_1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            carLicense1path = data.getData();
            carLicense1.setImageURI(carLicense1path);
            carLicense1.setDrawingCacheEnabled(true);
            carLicense1.buildDrawingCache();
        } else if (requestCode == CAR_LICENSE_IMAGE_2 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            carLicense2path = data.getData();
            carLicense2.setImageURI(carLicense2path);
            carLicense2.setDrawingCacheEnabled(true);
            carLicense2.buildDrawingCache();
        } else if (requestCode == DRIVING_LICENSE_IMAGE_1 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            drivingLicense1path = data.getData();
            drivingLicense1.setImageURI(drivingLicense1path);
            drivingLicense1.setDrawingCacheEnabled(true);
            drivingLicense1.buildDrawingCache();
        } else if (requestCode == DRIVING_LICENSE_IMAGE_2 && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            drivingLicense2path = data.getData();
            drivingLicense2.setImageURI(drivingLicense2path);
            drivingLicense2.setDrawingCacheEnabled(true);
            drivingLicense2.buildDrawingCache();
        }

    }
}
