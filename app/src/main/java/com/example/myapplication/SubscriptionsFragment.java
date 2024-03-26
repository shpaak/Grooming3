package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubscriptionsFragment extends Fragment {
    private ImageButton editButton;
    private TextView emailTextView, nameTextView, userPets, featuresPets, namePets;
    private CircleImageView userImageView; // Added ImageView for user image
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        editButton = view.findViewById(R.id.edit_button);
        emailTextView = view.findViewById(R.id.user_email);
        nameTextView = view.findViewById(R.id.user_name);
        userPets = view.findViewById(R.id.user_pets);
        featuresPets = view.findViewById(R.id.features_pets);
        namePets = view.findViewById(R.id.name_pets);
        userImageView = view.findViewById(R.id.user_image_view); // Initialize user image view
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String pets = snapshot.child("pets").getValue(String.class);
                        String petName = snapshot.child("petInfo").child("petName").getValue(String.class);
                        String petFeatures = snapshot.child("petInfo").child("petFeatures").getValue(String.class);
                        String userImage = snapshot.child("uimage").getValue(String.class); // Get user image URL

                        nameTextView.setText(name);
                        emailTextView.setText(email);
                        userPets.setText(pets);
                        namePets.setText(petName);
                        featuresPets.setText(petFeatures);

                        // Load user image using Picasso
                        Picasso.get().load(userImage).into(userImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error
                }
            });
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserSettingActivity.class);
                intent.putExtra("pid", "Pname");
                startActivity(intent);
            }
        });

        ImageButton exitButton = view.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences loginPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return view;
    }
}
