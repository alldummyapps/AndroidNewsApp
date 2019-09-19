package edu.yonatan.allnews.register_login_activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import edu.yonatan.allnews.R;
import edu.yonatan.allnews.activitys.CategoriesActivity;
import edu.yonatan.allnews.news_package.activitys.NewsActivity;

public class PhoneRegisterActivity extends AppCompatActivity {

    //props:
    private EditText etPhoneNumber;
    private Button btnSendVerifyCode;
    private EditText etVerificationCode;
    private Button btnSubmitVerifyCode;
    private TextView tvVerifyInfoOne;
    private TextView tvVerifyInfoTwo;
    private TextView tvPhoneInput;
    private Spinner spCodeOptions;



    private FirebaseAuth fbAuth;
    private FirebaseDatabase aRef;

    //init the phone callback:
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private String mVerificationId;


    private PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_register);


        fbAuth = FirebaseAuth.getInstance();
        aRef = FirebaseDatabase.getInstance();


        //init fields:
        initFields();

        //spinner adapter init:
        //spinner string values ->list:
        spinnerAdapterInit();


        //firebase phone auth:
        //onClicksubmit phone number:
        // phoneAuthProvider -> set the properies of the phone callback:
        //accepts the phone number via var -> duration between messages and so on.
        btnSendVerifyCode.setOnClickListener(v -> {

            //spinner item:
            String phoneCode = spCodeOptions.getSelectedItem().toString();

            //completed user phone number:
            String phoneNumber = phoneCode + etPhoneNumber.getText().toString();

            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(this, "Please Enter a valid Phone number.", Toast.LENGTH_SHORT).show();
            } else {

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        this,               // Activity (for callback binding)
                        callbacks);        // OnVerificationStateChangedCallbacks

            }
            //after onclick, change layouts:
            etPhoneNumber.setVisibility(View.INVISIBLE);
            btnSendVerifyCode.setVisibility(View.INVISIBLE);
            tvPhoneInput.setVisibility(View.INVISIBLE);
            spCodeOptions.setVisibility(View.INVISIBLE);

            btnSubmitVerifyCode.setVisibility(View.VISIBLE);
            tvVerifyInfoOne.setVisibility(View.VISIBLE);
            tvVerifyInfoTwo.setVisibility(View.VISIBLE);
            etVerificationCode.setVisibility(View.VISIBLE);

        });


        //submit verification code:
        //on some devices (samsung) will do this automaticly and log the user in:
        btnSubmitVerifyCode.setOnClickListener(v -> {
            String verificationCode = etVerificationCode.getText().toString();

            if (TextUtils.isEmpty(verificationCode)) {

                Toast.makeText(this, "Please Enter Valid Verification Code.", Toast.LENGTH_SHORT).show();
            } else {

                //create credential -> accepts mVerificationID(the sent code from firebase) and the code the user
                //inputed -> verification code and checks if they are the same:
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                //method that login the user with his new credential if they are correct:
                signInWithPhoneAuthCredential(credential);
            }


        });


        //callbacks method listener:
        //checks if the verification complete,failed,code sent:
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            //if verification completed -> sign the user to the app:
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                //signs the user to the app if the credentials are correct:
                signInWithPhoneAuthCredential(phoneAuthCredential);


            }

            //on failed listener:
            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(PhoneRegisterActivity.this, "Please Enter a Valid Number and Try Again.", Toast.LENGTH_SHORT).show();

            }

            //on codeSent:
            //sends the user a verification code:
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                etPhoneNumber.setVisibility(View.INVISIBLE);
                btnSendVerifyCode.setVisibility(View.INVISIBLE);
                tvPhoneInput.setVisibility(View.INVISIBLE);
                spCodeOptions.setVisibility(View.INVISIBLE);

                btnSubmitVerifyCode.setVisibility(View.VISIBLE);
                tvVerifyInfoOne.setVisibility(View.VISIBLE);
                tvVerifyInfoTwo.setVisibility(View.VISIBLE);


            }
        };
    }

    private void spinnerAdapterInit() {
        //spinner adapter init:
        //spinner string values:
        String[] items = new String[]{"+972", "xxx", "yyy"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spCodeOptions.setAdapter(adapter);
    }


    //checks the phone credentials object:
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

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

    //sends the user to main activity:
    private void sendUserToMainActivity() {
        Intent mainActivity = new Intent(this, NewsActivity.class);
        startActivity(mainActivity);
        finish();
    }

    //init fields:
    private void initFields() {
        //layout one:
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSendVerifyCode = findViewById(R.id.btnSendVerifyCode);
        etVerificationCode = findViewById(R.id.etVerificationCode);
        spCodeOptions = findViewById(R.id.spCodeOptions);


        //layout two:
        btnSubmitVerifyCode = findViewById(R.id.btnSubmitVerifyCode);
        tvVerifyInfoOne = findViewById(R.id.tvVerifyInfoOne);
        tvVerifyInfoTwo = findViewById(R.id.tvVerifyInfoTwo);
        tvPhoneInput = findViewById(R.id.tvPhoneInfo);
    }
}
