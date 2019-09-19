package edu.yonatan.allnews.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.yonatan.allnews.R;
import edu.yonatan.allnews.activitys.CategoriesActivity;

public class SettingsActivity extends AppCompatActivity {


    //properties:
    private CircleImageView civSetProfileImage;
    private EditText etSetUserName;
    private EditText etSetStatus;
    private Button btnUpdateUserSettings;


    private String currentUserID;
    private FirebaseAuth fbAuth;
    private DatabaseReference fbRootRef;
    private StorageReference userProfileImgRef;


    public static final int galleryImage = 1;
    public static String downloadUrl;
    public String currentImgURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        fbAuth = FirebaseAuth.getInstance();
        currentUserID = fbAuth.getCurrentUser().getUid();
        fbRootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImgRef = FirebaseStorage.getInstance().getReference().child("MyProfileImages");

        //findViews initializer:
        initializeFields();


        //update settings btn
        btnUpdateUserSettings.setOnClickListener(v -> {


            updateAccountSettings();
        });

        //retrieve the user data from the firebase database:
        userInfoRetrieval();


        //upload img from phone button -> when the user press on the empty img:
        civSetProfileImage.setOnClickListener(v -> {

            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, galleryImage);

        });

    }




    @Override
    protected void onStart() {
        super.onStart();
        //saves the current user img to the civ while updated:
        saveUpdatedImage();
    }

    //saves the current user img to the civ while updated:
    private void saveUpdatedImage() {
        fbRootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("image")) {
                    currentImgURL = dataSnapshot.child("image").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //initialize fields:
    private void initializeFields() {
        civSetProfileImage = findViewById(R.id.civSetProfileImage);
        etSetStatus = findViewById(R.id.etSetStatus);
        etSetUserName = findViewById(R.id.etSetUserName);
        btnUpdateUserSettings = findViewById(R.id.btnUpdateUserSettings);
    }

    //method that let the user crop the picture using external library > arthur crop library:
    /*requestCode -> the imaged picked by the user
     * result code -> process completed
     * data ->user must select the img first:*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == galleryImage && resultCode == RESULT_OK && data != null) {

            Uri imageUri = data.getData();

            //crop library:
            CropImage.activity().
                    setGuidelines(CropImageView.Guidelines.ON).
                    setAspectRatio(1, 1).
                    start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImgRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Profile Image uploaded successfully.", Toast.LENGTH_SHORT).show();

                            task.getResult().getMetadata().getReference().getDownloadUrl().addOnSuccessListener(imgUrl -> {


                                //downloads the img link from the RTS(real time storage) to a string prop:
                                downloadUrl = imgUrl.toString();

                                //adds the img url into the current user database:
                                fbRootRef.child("Users").child(currentUserID).child("image").
                                        setValue(downloadUrl).
                                        addOnCompleteListener(v -> {
                                            if (task.isSuccessful()) {

                                            } else {
                                                Toast.makeText(SettingsActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }

                                        });
                            });


                        } else {
                            Toast.makeText(SettingsActivity.this, "Something went wrong.. Please try again later.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        }


    }

    //sends the user back to home screen when back is pressed:
    @Override
    public void onBackPressed() {
        sendUserToMainActivity();
    }


    //retrieve the user data from the firebase database:
    //checks if he has some of the info fields(name,status and img) if so show them.
    //if the user doesnt have the basic fields name and status, he will be compelled to make them:
    private void userInfoRetrieval() {

        fbRootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) &&
                        (dataSnapshot.hasChild("image") && (dataSnapshot.hasChild("status")))) {

                    String userName = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();


                    Picasso.get().load(image).into(civSetProfileImage);

                    etSetStatus.setText(status);
                    etSetUserName.setText(userName);

                } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("status"))) {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    etSetStatus.setText(status);
                    etSetUserName.setText(userName);

                } else {

                    Toast.makeText(SettingsActivity.this, "Profile Update Required.", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //update the user settings into the database/app:
    private void updateAccountSettings() {

        String userName = etSetUserName.getText().toString();
        String userStatus = etSetStatus.getText().toString();


        if (TextUtils.isEmpty(userName)) {

            Toast.makeText(this, "Please Add a Valid User Name.", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(userStatus)) {

            Toast.makeText(this, "Please Add a Status Message.", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("image", currentImgURL);
            profileMap.put("name", userName);
            profileMap.put("status", userStatus);
            fbRootRef.child("Users").child(currentUserID).setValue(profileMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    Toast.makeText(this, "Profile Saved.", Toast.LENGTH_SHORT).show();
                    sendUserToMainActivity();
                } else {
                    Toast.makeText(this, "Something Went Wrong...", Toast.LENGTH_SHORT).show();
                }

            });
        }


    }


    //sends the user to main activity:
    private void sendUserToMainActivity() {
        Intent categoriesActivity = new Intent(this, CategoriesActivity.class);
        categoriesActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(categoriesActivity);
        finish();
    }
}
