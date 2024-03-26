package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
private Button registerBtn;
private EditText usernameInput, phoneInput, passwordInput;
private ProgressDialog loadingBar;
FirebaseAuth uAuth;
private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = (Button) findViewById(R.id.register_button);
        usernameInput = (EditText) findViewById(R.id.register_username_input);
        phoneInput = (EditText) findViewById(R.id.register_phone_input);
        passwordInput = (EditText) findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);
        uAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = phoneInput.getText().toString();
                String password = passwordInput.getText().toString();
                String name = usernameInput.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this, "Введите почту", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(name))
                {
                    Toast.makeText(RegisterActivity.this, "Введите имя", Toast.LENGTH_SHORT).show();
                }
                else {

                    loadingBar.setCanceledOnTouchOutside(false);

                    RegisterUser(name, email, password);

                }


            }

            private void RegisterUser(String name, String email, String password) {
              loadingBar.setTitle("Регистрация пользователя");
              loadingBar.setMessage("Пожалуйста, подождите!");
              loadingBar.show();
              uAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = uAuth.getCurrentUser();
                            String userEmail = user.getEmail();
                            String userId = user.getUid();

                            // Сохранение адреса электронной почты в базе данных Firebase Realtime Database
                            usersRef.child(userId).child("email").setValue(userEmail);
                            usersRef.child(userId).child("name").setValue(name);


                            Toast.makeText(RegisterActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent homeIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(homeIntent);
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        });
    }


}