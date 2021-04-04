package com.codewithharshil.phonenumberauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private String mAuthVerificationId;

    private EditText mOtpText;
    private Button mVerifyBtn;

    private ProgressBar mOtpProgress;
    private TextView mOtpFeedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mAuthVerificationId = getIntent().getStringExtra("AuthCredentials");

        mOtpFeedback = findViewById(R.id.otp_form_feedback);
        mOtpProgress = findViewById(R.id.otp_progress_bar);
        mOtpText = findViewById(R.id.OTP_Text);
        mVerifyBtn = findViewById(R.id.verify_btn);

        mVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = mOtpText.getText().toString();
                if(otp.isEmpty())
                {
                    mOtpFeedback.setVisibility(view.VISIBLE);
                    mOtpFeedback.setText("Please fill in the form and try again.");
                }
                else
                {
                    mOtpProgress.setVisibility(View.VISIBLE);
                    mVerifyBtn.setEnabled(false);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mAuthVerificationId,otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            senduserToHome();
                            // ...
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mOtpFeedback.setVisibility(View.VISIBLE);
                                mOtpFeedback.setText("There was error verifying OTP.");
                            }
                        }
                        mOtpProgress.setVisibility(View.INVISIBLE);
                        mVerifyBtn.setEnabled(true);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null)
        {
            senduserToHome();
        }
    }
    public void senduserToHome()
    {
        Intent homeIntent = new Intent(OtpActivity.this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }
}