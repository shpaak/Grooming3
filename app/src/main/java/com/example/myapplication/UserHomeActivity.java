package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Model.Products;
import com.example.myapplication.ViewHolder.ServicerHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class UserHomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    DatabaseReference ProductsRef;
    FirebaseRecyclerAdapter<Products, ServicerHolder> adapter;
    private ImageView vkImageView,telegImageView, instaImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.shorts) {
                replaceFragment(new ServesFragment());
            } else if (item.getItemId() == R.id.subscriptions) {
                replaceFragment(new GalletyFragment());
            } else if (item.getItemId() == R.id.library) {
                replaceFragment(new SubscriptionsFragment());
            }
            return true;
        });



        vkImageView = findViewById(R.id.VK);
        telegImageView = findViewById(R.id.Teleg);
        instaImageView = findViewById(R.id.Insta);
        vkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открываем ссылку на VK
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com")));
            }
        });

        telegImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открываем ссылку на Telegram
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me")));
            }
        });

        instaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открываем ссылку на Instagram
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com")));
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
