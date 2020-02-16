package com.example.psy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
   

    Button buttonkod, buttonkodcd, buttonzaloguj;

    EditText text, textkod;

    String codeSent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonkod =  (Button) findViewById(R.id.buttonkod);
        buttonkodcd =  (Button) findViewById(R.id.buttonkodcd);
        buttonzaloguj = (Button) findViewById(R.id.buttonzaloguj);

        text = (EditText) findViewById(R.id.phone);
        textkod = (EditText) findViewById(R.id.editkod);

        mAuth = FirebaseAuth.getInstance();

        buttonkod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                sendcode();

            }
        });

        buttonzaloguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                veryfycode();
            }
        });

        buttonkodcd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendcode();
            }
        });
    }

    private void veryfycode() {

        String code = textkod.getText().toString();

        if(code.isEmpty()){
            textkod.setError(getResources().getString(R.string.telkod));
            textkod.requestFocus();
            return;

        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "zalogowany", Toast.LENGTH_LONG).show();


                        } else {

                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                                Toast.makeText(getApplicationContext(), "niepoprawny kod", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }
    private void sendcode() {

        String phone = text.getText().toString();

        if (phone.isEmpty()) {

            text.setError(getResources().getString(R.string.tel));
            text.requestFocus();
            return;
        }

        if (phone.length() != 12) {
            text.setError(getResources().getString(R.string.tel));
            text.requestFocus();
            return;
        }

        buttonkodcd.setVisibility(View.VISIBLE);
        buttonkodcd.setEnabled(true);
        buttonzaloguj.setEnabled(true);
        buttonzaloguj.setVisibility(View.VISIBLE);

        buttonkod.setEnabled(false);


        textkod.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone, 60, TimeUnit.SECONDS, this, mCallbacks
        );

    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            //super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };
}


