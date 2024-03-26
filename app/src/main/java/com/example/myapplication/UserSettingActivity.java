package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSettingActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private EditText nameU, emailU, petU, petN, petF;
    private ImageView closeBtn, saveBtn;
    private TextView imageChangeBtn;
    private String getType;
    String checker = "";

    private static final int GALLERY_REQUEST_CODE = 1;
    private StorageReference storageReference;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        getType = getIntent().getStringExtra("pid");

        circleImageView = findViewById(R.id.profile_image);
        nameU = findViewById(R.id.user_name);
        emailU = findViewById(R.id.user_email);
        petU = findViewById(R.id.user_pets);
        petN = findViewById(R.id.name_pets);
        petF = findViewById(R.id.features_pets);
        saveBtn = findViewById(R.id.save_button);
        closeBtn = findViewById(R.id.close_button);
        imageChangeBtn = findViewById(R.id.change_foto);

        storageReference = FirebaseStorage.getInstance().getReference().child("User Foto");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        // Load name and email from the database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        nameU.setText(name);
                        emailU.setText(email);
                    }
                    // Handle the error
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error
                }
            });
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getType.equals("Pname")) {
                    startActivity(new Intent(UserSettingActivity.this, SubscriptionsFragment.class));
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle save button click
                String userName = nameU.getText().toString();
                String userEmail = emailU.getText().toString();
                String userPets = petU.getText().toString();
                String petName = petN.getText().toString();
                String petFeatures = petF.getText().toString();

                // Save the data to the database
                String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference currentUserRef = usersRef.child(currentUserID);
                currentUserRef.child("name").setValue(userName);
                currentUserRef.child("email").setValue(userEmail);
                currentUserRef.child("pets").setValue(userPets);
                currentUserRef.child("petInfo").child("petName").setValue(petName);
                currentUserRef.child("petInfo").child("petFeatures").setValue(petFeatures);

                // Display the data in SubscriptionsFragment
                Intent intent = new Intent(UserSettingActivity.this, SubscriptionsFragment.class);
                intent.putExtra("userName", userName);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("userPets", userPets);
                intent.putExtra("petName", petName);
                intent.putExtra("petFeatures", petFeatures);
                startActivity(intent);
            }
        });

        imageChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";
                // Open the gallery
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            Uri imageUri = data.getData();

            // Save the image to "User Foto" storage
            final StorageReference filePath = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        // Get the download URL of the uploaded image
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                // Save the image URL to the "uimage" field in the "Users" table
                                usersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uimage").setValue(imageUrl);
                                Picasso.get().load(imageUrl).into(circleImageView);
                            }
                        });
                    }
                }
            });
        }
    }
}
