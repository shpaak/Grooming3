package com.example.myapplication.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.GalletyFragment;
import com.example.myapplication.HomeFragment;
import com.example.myapplication.R;
import com.example.myapplication.ServesFragment;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    BottomNavigationView bottomNavigationView2;
    FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_admin);
        bottomNavigationView2 = findViewById(R.id.bottomNavigationView2);
        fab = findViewById(R.id.fab);

        bottomNavigationView2.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.shorts) {
                replaceFragment(new AdminServesesFragment());
            } else if (item.getItemId() == R.id.subscriptions) {
                replaceFragment(new GalletyFragment());
            }
            return true;
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminAddNewProductActivity.class);
                startActivity(intent);
            }
        });
    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout2, fragment);
        fragmentTransaction.commit();
    }




}

