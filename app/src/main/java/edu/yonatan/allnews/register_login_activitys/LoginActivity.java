package edu.yonatan.allnews.register_login_activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import edu.yonatan.allnews.R;
import edu.yonatan.allnews.activitys.CategoriesActivity;
import edu.yonatan.allnews.news_package.activitys.NewsActivity;

public class LoginActivity extends AppCompatActivity {


    //props:
    private Button btnGmail, btnAnon, btnPhone;


    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private GoogleSignInClient mGoogleSignInClient;


    private static final int RC_SIGN_IN = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();


        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

       //init views:
        initViews();



        googleSignIn();

        //sign in the user via connected Gmail:
        btnGmail.setOnClickListener(v -> {


            signIn();



        });


//sign in the user anonymously:
        btnAnon.setOnClickListener(v->{

            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            sendUserToCategoriesActivity();

                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "sth went wrong...", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    });

        });

      //sends the user to phone register page: register via sms;
        btnPhone.setOnClickListener(v->{

            //sends the user to RegisterActivity:
            sendToPhoneRegisterActivity();



        });


    }
//sends the user to phone register activity:
    private void sendToPhoneRegisterActivity() {


Intent phoneRegisterActivity = new Intent(this, PhoneRegisterActivity.class);
startActivity(phoneRegisterActivity);
finish();


    }


    private void googleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();

            }
        }
    }






    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        String email = acct.getEmail();
                        String displayName = acct.getGivenName();
                        String name = acct.getGivenName() + " " + acct.getFamilyName();
                        String userID = mAuth.getCurrentUser().getUid();

                        Map<String,String> userDetails = new HashMap<>();

                        userDetails.put("display_name",displayName);
                        userDetails.put("name",name);
                        userDetails.put("email",email);
                        userDetails.put("id",userID);


                  mRef.child("Users").child(userID).child("userDetails").setValue(userDetails);


           sendUserToCategoriesActivity();



                    } else {


                    }


                });
    }


    //sends the user to the Categories activity:
    private void sendUserToCategoriesActivity(){

        Intent categoriesActivity = new Intent(this, CategoriesActivity.class);
        startActivity(categoriesActivity);
        finish();

    }

    private void sendUserToNewsActivity() {
        Intent newsActivity = new Intent(this, NewsActivity.class);
        startActivity(newsActivity);
        finish();
    }


    //init views:
    private void initViews() {

        btnAnon = findViewById(R.id.btnAnonymousLogin);
        btnGmail = findViewById(R.id.btnGmailLogin);
        btnPhone = findViewById(R.id.btnPhoneLogin);


    }
}
