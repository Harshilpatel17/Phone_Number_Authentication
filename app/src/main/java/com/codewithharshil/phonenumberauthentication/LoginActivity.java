package com.codewithharshil.phonenumberauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity
{

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private EditText mCountryCode;
    private EditText mPhoneNumber;

    private Button mGenerateBtn;
    private ProgressBar mLoginProgress;

    private TextView mLoginFeedbackText;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mCountryCode = findViewById(R.id.country_code_Text);
        mPhoneNumber = findViewById(R.id.OTP_Text);
        mGenerateBtn =  findViewById(R.id.verify_btn);
        mLoginProgress = findViewById(R.id.login_progress_bar);
        mLoginFeedbackText = findViewById(R.id.login_form_feedback);
        mGenerateBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String country_code = mCountryCode.getText().toString();
                String phone_number = mPhoneNumber.getText().toString();

                String complete_phone_number = "+" + country_code + phone_number;

                Toast.makeText(LoginActivity.this,"Phone Number :" + complete_phone_number,Toast.LENGTH_LONG).show();

                if (country_code.isEmpty() || phone_number.isEmpty())
                {
                    mLoginFeedbackText.setText("Please fill in the form to continue.");
                    mLoginFeedbackText.setVisibility(View.VISIBLE);
                }
                else
                {
                    mLoginProgress.setVisibility(View.VISIBLE);
                    mGenerateBtn.setEnabled(false);


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            complete_phone_number,
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            mCallbacks
                    );


                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d("PHONE VERIF","Phone Number verification failed");

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                Intent otpIntent = new Intent(LoginActivity.this, OtpActivity.class);
                otpIntent.putExtra("AuthCredential",s);
                startActivity(otpIntent);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null)
        {
            Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
            finish();
        }
    }
}