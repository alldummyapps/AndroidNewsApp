package edu.yonatan.allnews.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.yonatan.allnews.R;
import edu.yonatan.allnews.news_package.activitys.NewsActivity;
import edu.yonatan.allnews.register_login_activitys.LoginActivity;
import edu.yonatan.allnews.settings.SettingsActivity;
import edu.yonatan.allnews.sports_package.activities.SportsActivity;
import edu.yonatan.allnews.tech_package.TechActivity;



public class CategoriesActivity extends AppCompatActivity {


    //props:
    private Button toNewsBtn, toSportsBtn, toTechBtn;
    private CircleImageView categoriesProfileImgCiv;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        //hamburger:
        Toolbar toolbar = findViewById(R.id.toolbarTwo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //init fields:
        findviews();

        //firebase init:
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();


        //sends the user to newsActivity:
        toNewsBtn.setOnClickListener((v) -> {

            mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("category_state").setValue("news");


            //sends the user to newsActivity:
            sendUserToNewsActivity();


        });


        toSportsBtn.setOnClickListener(v -> {

            mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("category_state").setValue("sports");

            sendUserToSportsActivity();


        });

        toTechBtn.setOnClickListener(v -> {

            mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("category_state").setValue("tech");


            sendUserToTechActivity();
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    //onStart checks if the user exists in the database:
    //if not -> to Login screen
    //if appear -> to main app layout:
    @Override
    protected void onStart() {
        super.onStart();




        //checks if the user is in the database(is logged in) -> if not sends the user to login activity;
        //if he is int he database - > continue to categories activity:
        if (mAuth.getCurrentUser() == null) {
            sendUserToLoginActivity();

        } else {
            mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("category_state").setValue("categories");
            //checks what is the current state of the user - > where he is located inside the app;
            //when resumes the app - > send the user to his recent location;
            //writes the current user state to the user database entry;
            if (mRef != null) {
                mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("category_state").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String currentUserState = dataSnapshot.getValue().toString();

                            if (currentUserState.equals("news")) {

                                sendUserToNewsActivity();

                            } else if (currentUserState.equals("tech")) {

                                sendUserToTechActivity();


                            } else if (currentUserState.equals("sports")) {
                                sendUserToSportsActivity();

                            } else {

                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            uploadProfileImageViewToCiv();
        }
    }

    //changing layout listener:
    // when landscape -> make the bottom nav bar disappear and profile Icone
    // when portrait -> back to default -> bottom nav bar appear and profile Icone
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            categoriesProfileImgCiv.setVisibility(View.GONE);


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            categoriesProfileImgCiv.setVisibility(View.VISIBLE);

        }
    }


    //init fields:
    private void findviews() {

        toNewsBtn = findViewById(R.id.toNewsBtn);
        toSportsBtn = findViewById(R.id.toSportsBtn);
        toTechBtn = findViewById(R.id.toTechBtn);
        categoriesProfileImgCiv = findViewById(R.id.civProfilePicCategories);


    }

    //inflate top options menu:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /// HAMBURGER  upper menu bar hamburger
    //misc and settings:
    //not yet operational
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.menuAbout) {
            Toast.makeText(this, "show About", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menuSetting) {

             //sends the user to settings activity:
            sendUserToSettingsActivity();

            return true;

        } else if (id == R.id.menuLogout) {
            //singout + send user to login activity
            mAuth.signOut();
            sendUserToLoginActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //sends the user to settings activity:
    private void sendUserToSettingsActivity() {

        Intent settingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivity);

    }


    //sends the user to login activity:
    private void sendUserToLoginActivity() {

        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
        finish();

    }


    //sends the user to newsActivity:
    private void sendUserToNewsActivity() {
        String userID = mAuth.getCurrentUser().getUid();

        mRef.child("Users").child(userID).child("category_state").setValue("news");


        Intent newsActivity = new Intent(this, NewsActivity.class);
        startActivity(newsActivity);
        finish();

    }


    private void sendUserToSportsActivity() {
        String userID = mAuth.getCurrentUser().getUid();

        mRef.child("Users").child(userID).child("category_state").setValue("sports");

        Intent sportsActivity = new Intent(this, SportsActivity.class);
        startActivity(sportsActivity);
        finish();
    }

    //sends the user to tech activity:
    private void sendUserToTechActivity() {

        String userID = mAuth.getCurrentUser().getUid();

        mRef.child("Users").child(userID).child("category_state").setValue("tech");

        Intent techActivity = new Intent(this, TechActivity.class);
        startActivity(techActivity);
        finish();


    }

    //adds the user img pic if available to the toolbar:
    private void uploadProfileImageViewToCiv() {
        //adds the user img pic if available to the toolbar:
        mRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("image")) {

                    String profileImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_img).into(categoriesProfileImgCiv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
