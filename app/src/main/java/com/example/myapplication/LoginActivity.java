package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Admin.AdminActivity;
import com.example.myapplication.Admin.AdminAddNewProductActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
private Button loginButton;
private EditText loginPhoneInput, loginPasswordInput;
private ProgressDialog loadingBar;

private String Admin = "Admin";
FirebaseAuth uAuth;
FirebaseUser FifebaseUser;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean rememberMe;
private CheckBox chekBoxRememberMe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uAuth = FirebaseAuth.getInstance();
        loginButton = (Button) findViewById(R.id.login_button);

        loginPhoneInput = (EditText) findViewById(R.id.login_phone_input);
        loginPasswordInput = (EditText) findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();



        chekBoxRememberMe = findViewById(R.id.login_chekBox);
        rememberMe = loginPreferences.getBoolean("rememberMe", false);
        chekBoxRememberMe.setChecked(rememberMe);



        rememberMe = loginPreferences.getBoolean("rememberMe", false);
        if (rememberMe) {
            String email = loginPreferences.getString("email", "");
            String password = loginPreferences.getString("password", "");
            SignUser(email, password);
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = loginPhoneInput.getText().toString();
                String password = loginPasswordInput.getText().toString();

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    ValidateAdmin(user);
                                } else {
                                    loadingBar.dismiss();
                                    Toast.makeText(LoginActivity.this, "Ошибка входа", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Введите почту", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setCanceledOnTouchOutside(false);


                    SignUser(email, password);
                }
            }
        });





    }

    private void ValidateAdmin(FirebaseUser user) {
        // Проверить, является ли пользователь администратором
        if (user != null && user.getUid().equals("YcCVjlFu1Nda5LYph6i6lzn3WPB2")) {
            loadingBar.dismiss();
            // Пользователь является администратором
            // Выполнить необходимые действия для администратора
            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
            startActivity(intent);
        } else {
            loadingBar.dismiss();
            Toast.makeText(this, "Вход для администратора", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = uAuth.getCurrentUser();
        if (currentUser != null) {
            // Пользователь уже аутентифицирован, перенаправить его на главный экран
            Intent HomeFragment = new Intent(LoginActivity.this, UserHomeActivity.class);
            startActivity(HomeFragment);
        }
    }

    private void SignUser(String email, String password){
        loadingBar.setTitle("Вход в приложение");
        loadingBar.setMessage("Пожалуйста, подождите");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        uAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (rememberMe) {
                        loginPrefsEditor.putBoolean("rememberMe", true);
                        loginPrefsEditor.putString("email", email);
                        loginPrefsEditor.putString("password", password);
                        loginPrefsEditor.apply();
                    }
                    Toast.makeText(LoginActivity.this, "Вход прошел успешно", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Intent HomeFragment = new Intent(LoginActivity.this, UserHomeActivity.class);
                    startActivity(HomeFragment);
                } else {
                    Toast.makeText(LoginActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }
}