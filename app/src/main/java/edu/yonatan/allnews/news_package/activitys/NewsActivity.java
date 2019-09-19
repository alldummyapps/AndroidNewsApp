package edu.yonatan.allnews.news_package.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.yonatan.allnews.R;
import edu.yonatan.allnews.activitys.CategoriesActivity;
import edu.yonatan.allnews.recyclers.rvRss;
import edu.yonatan.allnews.register_login_activitys.LoginActivity;
import edu.yonatan.allnews.settings.SettingsActivity;

import static edu.yonatan.allnews.fragments.WebView.inWebview;


public class NewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //props:
    private static final String YNET = "https://www.ynet.co.il/Integration/StoryRss2.xml";
    private static final String WALLA = "http://rss.walla.co.il/feed/1?type=main";
    private static final String MAKO = "http://rcs.mako.co.il/rss/31750a2610f26110VgnVCM1000005201000aRCRD.xml";
    private static final String HAARETZ = "https://www.haaretz.co.il/cmlink/1.1617539";
    //https://www.haaretz.co.il/cmlink/1.1470869
    private TextView newsLabel;
    public static Button toCategoriesBtn;
    public static CircleImageView newsProfileImgCiv;


    private BottomNavigationView bnv;

    //fireBase Props:
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    //bnv - tab item selected:
    private int bnvTabNumber = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init the views:
        initViews();

        //nav setup -> listener
        bnv.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);


        //on app create(first init) -> load Ynet RSS
        getSupportFragmentManager().beginTransaction().
                replace(R.id.newsContent, rvRss.newInstance(YNET)).
                commit();


        //firebase init
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        //sets the current activity title to non:
        this.setTitle("");

        //sends the user to categories activity
        //back button arrow on the upper toolbar
        toCategoriesBtn.setOnClickListener(v -> {

            //changes the user database current state(current location) to 'categories'
            if (mAuth.getCurrentUser().getUid() != null) {
                mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("category_state").setValue("categories");
            }
            //sends the user to categories activity:
            sendUserToCategoriesActivity();


        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //sets the categories back btn to visible
        toCategoriesBtn.setVisibility(View.VISIBLE);


        //sets the profile img margins inside the webview:
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) newsProfileImgCiv.getLayoutParams();
        marginParams.setMargins(410, 0, 0, 0);

        //minimize the app -> home button click
        if (!inWebview) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    //init the views:
    private void initViews() {
        bnv = findViewById(R.id.navigation_menu);
        newsLabel = findViewById(R.id.newsLabel);
        toCategoriesBtn = findViewById(R.id.toCategoriesBtn);
        newsProfileImgCiv = findViewById(R.id.civProfilePicNews);
    }

    //changing layout listener:
    // when landscape -> make the bottom nav bar disappear and profile Icone
    // when portrait -> back to default -> bottom nav bar appear and profile Icone
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
           newsProfileImgCiv.setVisibility(View.GONE);

            bnv.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            newsProfileImgCiv.setVisibility(View.VISIBLE);
            bnv.setVisibility(View.VISIBLE);
        }
    }


    //onStart checks if the user exists in the database:
    //if not -> to Login screen
    //if appear -> to main app layout:
    @Override
    protected void onStart() {
        super.onStart();

        inWebview = false;

        //sets the visibility to visible of the to categories btn
        toCategoriesBtn.setVisibility(View.VISIBLE);


        if (mAuth.getCurrentUser() == null) {
            sendUserToLoginActivity();

        } else {
            //adds the user img pic if available to the toolbar:
            uploadProfileImageViewToCiv();

        }




    }
    //adds the user img pic if available to the toolbar:
    private void uploadProfileImageViewToCiv() {
        //adds the user img pic if available to the toolbar:
        mRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("image")) {

                    String profileImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(profileImage).into(newsProfileImgCiv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // //checks which tab is on -> refresh it if needed: onResume
    @Override
    protected void onResume() {
        super.onResume();


        if (bnv != null) {
            int selectedItem = bnv.getSelectedItemId();

            //Toast.makeText(this, selectedItem + "", Toast.LENGTH_SHORT).show();
            switch (bnvTabNumber) {

                case 0:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.newsContent, rvRss.newInstance(YNET)).
                            commit();


                    break;
                case 1:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.newsContent, rvRss.newInstance(MAKO)).
                            commit();

                    break;


                case 2:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.newsContent, rvRss.newInstance(WALLA)).
                            commit();

                    break;

                case 3:
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.newsContent, rvRss.newInstance(HAARETZ)).
                            commit();

                    break;


            }
        }

    }


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


    //bottom nav item selection -> loads rss -> set title;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.navigation_ynet:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.newsContent, rvRss.newInstance(YNET)).
                        commit();

                toCategoriesBtn.setVisibility(View.VISIBLE);
                newsLabel.setText("Ynet");
                bnvTabNumber = 0;
                return true;
            case R.id.navigation_mako:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.newsContent, rvRss.newInstance(MAKO)).
                        commit();

                toCategoriesBtn.setVisibility(View.VISIBLE);
                newsLabel.setText("Mako");
                bnvTabNumber = 1;
                return true;

            case R.id.navigation_walla:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.newsContent, rvRss.newInstance(WALLA)).
                        commit();

                toCategoriesBtn.setVisibility(View.VISIBLE);
                newsLabel.setText("Walla");
                bnvTabNumber = 2;
                return true;

            case R.id.navigation_haaretz:
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.newsContent, rvRss.newInstance(HAARETZ)).
                        commit();

                toCategoriesBtn.setVisibility(View.VISIBLE);
                newsLabel.setText("Haaretz");
                bnvTabNumber = 3;
                return true;


        }


        return true;
    }

    //sends the user to the Categories activity:
    private void sendUserToCategoriesActivity() {

        Intent categoriesActivity = new Intent(this, CategoriesActivity.class);
        startActivity(categoriesActivity);
        finish();

    }
    //sends the user to settings activity:
    private void sendUserToSettingsActivity() {

        Intent settingsActivity = new Intent(this, SettingsActivity.class);
        startActivity(settingsActivity);
        finish();

    }

    //sends the user to login activity:
    private void sendUserToLoginActivity() {

        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
        finish();

    }


}
