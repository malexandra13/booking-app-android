package com.example.licenta.client.login_register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.licenta.R;
import com.example.licenta.owner.others.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterClient extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextLastName, editTextFirstName, editTextPhoneNumber;
    Button buttonRegister;
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBar;
    TextView textViewLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_client);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonRegister = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
        textViewLogin = findViewById(R.id.loginNow);
        editTextLastName = findViewById(R.id.lastName);
        editTextFirstName = findViewById(R.id.firstName);
        editTextPhoneNumber = findViewById(R.id.phoneNumber);



        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginClient.class);
                startActivity(intent);
                finish();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String userType = "client";
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                String lastName = String.valueOf(editTextLastName.getText());
                String firstName = String.valueOf(editTextFirstName.getText());
                String phoneNumber = String.valueOf(editTextPhoneNumber.getText());

                if (TextUtils.isEmpty(firstName)) {
                    editTextFirstName.setError("Prenumele este obligatoriu!");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(lastName)) {
                    editTextLastName.setError("Numele este obligatoriu!");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(phoneNumber)) {
                    editTextPhoneNumber.setError("Numarul de telefon este obligatoriu!");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("E-mail este obligatoriu!");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Parola este obligatorie!");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (password.length() < 6) {
                    editTextPassword.setError("Parola trebuie sa aiba minim 6 caractere!");
                    progressBar.setVisibility(View.GONE);
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterClient.this, "Cont creat. Vă rugăm să vă verificați e-mailul pentru verificare.",
                                                        Toast.LENGTH_SHORT).show();
                                                firebaseFirestore.collection("clients").
                                                        document(mAuth.getCurrentUser().getUid()).
                                                        set(new User(userType, firstName, lastName, phoneNumber, email));
                                            } else {
                                                Toast.makeText(RegisterClient.this, "Contul există deja.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
//
                                } else {
                                    Toast.makeText(RegisterClient.this, "Contul există deja.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

    }

}