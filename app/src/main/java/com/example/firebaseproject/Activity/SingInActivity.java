package com.example.firebaseproject.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseproject.Model.User;
import com.example.firebaseproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SingInActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private static final String TAG = "SingInActivity";
    private boolean loginModeActive;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private EditText nameEditText;
    private TextView toggleLoginSingUpTextView;
    private Button loginSingUpButton;

    FirebaseDatabase database;
    DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //Создаем узел для данных
        usersDatabaseReference = database.getReference().child("users");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        toggleLoginSingUpTextView = findViewById(R.id.toggleLoginSingUpTextView);
        loginSingUpButton = findViewById(R.id.loginSingUpButton);

        loginSingUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Trim убирает пробелы в начале и конце
                loginSingUser(emailEditText.getText().toString().trim(),
                passwordEditText.getText().toString().trim());
            }
        });
        //Если пользователь залогинился, он будет сразу направлен в чат
        if(auth.getCurrentUser() !=null){
            startActivity(new Intent(SingInActivity.this, MainActivity.class));
        }
    }

    private void loginSingUser(String email, String password) {

        if(loginModeActive){
            if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Password must be 6 characters", Toast.LENGTH_SHORT).show();
            }

            else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please, input your email", Toast.LENGTH_SHORT).show();
            }
            else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    Intent intent = new Intent(SingInActivity.this, MainActivity.class);
                                    intent.putExtra("userName", nameEditText.getText().toString().trim());
                                    startActivity(intent);
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SingInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                    // ...
                                }

                                // ...
                            }
                        });
            }

        } else {

            if(!passwordEditText.getText().toString().trim().equals(
                    repeatPasswordEditText.getText().toString().trim())) {
                Toast.makeText(this, "Password dont match", Toast.LENGTH_SHORT).show();
            }
            else if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Password must be 6 characters", Toast.LENGTH_SHORT).show();
            }

            else if (emailEditText.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please, input your email", Toast.LENGTH_SHORT).show();
            }

            else {

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    createUser(user);
                                    Intent intent = new Intent(SingInActivity.this, MainActivity.class);
                                    intent.putExtra("userName", nameEditText.getText().toString().trim());
                                    startActivity(intent);
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SingInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }

                                // ...
                            }
                        });
            }
        }
    }

    private void createUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(nameEditText.getText().toString().trim());

        usersDatabaseReference.push().setValue(user);
    }

    public void toggleLoginMode(View view) {

        if(loginModeActive){
            loginModeActive = false;
            loginSingUpButton.setText("Sing Up");
            toggleLoginSingUpTextView.setText("Or, log In");
            repeatPasswordEditText.setVisibility(View.VISIBLE);
        } else {
            loginModeActive = true;
            loginSingUpButton.setText("Log In");
            toggleLoginSingUpTextView.setText("Or, sing Up ");
            repeatPasswordEditText.setVisibility(View.GONE);
        }
    }
}