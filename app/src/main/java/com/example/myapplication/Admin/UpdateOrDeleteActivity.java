package com.example.myapplication.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateOrDeleteActivity extends AppCompatActivity {
    private String pid, downloadImageUri;

    private boolean imageBool = false;

    private TextView name, price, description;
    private ImageView image;
    private ImageButton deleteBtn;

    private Button updateBtn;

    private Uri ImageUri;

    private static DatabaseReference deleteProduct;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog loadingBar;
    private DatabaseReference ProductsRef;
    private StorageReference productImageRef, productImageRefNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_or_delete);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = findViewById(R.id.update_product_name);
            price = findViewById(R.id.update_product_price);
            description = findViewById(R.id.update_product_description);
            image = findViewById(R.id.update_product_image);
            ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
            ImageUri = Uri.parse(extras.getString("image"));
            productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images").child(ImageUri.getLastPathSegment());
            productImageRefNew = FirebaseStorage.getInstance().getReference().child("Product Images");
            loadingBar = new ProgressDialog(UpdateOrDeleteActivity.this);

            updateBtn = findViewById(R.id.update_product_button);
            deleteBtn = (ImageButton) findViewById(R.id.delete_button);
            pid = extras.getString("pid");
            description.setText(extras.getString("description"));
            price.setText(extras.getString("price"));
            Picasso.get().load(extras.getString("image")).into(image);
            name.setText(extras.getString("pname"));

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OpenGallery();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadingBar.setTitle("");
                    loadingBar.setMessage("");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    deleteProduct = FirebaseDatabase.getInstance().getReference().child("Products").child(pid);
                    deleteProduct.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Успешное удаление
                                Toast.makeText(UpdateOrDeleteActivity.this, "Товар успешно удален", Toast.LENGTH_SHORT).show();
                                productImageRef.delete();
                            } else {
                                // Ошибка при удалении
                                Toast.makeText(UpdateOrDeleteActivity.this, "Ошибка при удалении товара", Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                            Intent AdminServesesFragment = new Intent(UpdateOrDeleteActivity.this, AdminServesesFragment.class);
                            startActivity(AdminServesesFragment);
                        }
                    });
                }
            });


                    updateBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadingBar = new ProgressDialog(UpdateOrDeleteActivity.this);
                            loadingBar.setTitle("Изменение товара");
                            loadingBar.setMessage("Пожалуйста, подождите");
                            loadingBar.setCanceledOnTouchOutside(false);
                            loadingBar.show();

                            ProductsRef.child(pid).child("description").setValue(description.getText().toString());
                            ProductsRef.child(pid).child("price").setValue(price.getText().toString());
                            ProductsRef.child(pid).child("pname").setValue(name.getText().toString());

                            if (imageBool) {
                                productImageRef.delete();


                                StorageReference filePath = productImageRefNew.child(ImageUri.getLastPathSegment() + pid + ".jpg");
                                final UploadTask uploadTask = filePath.putFile(ImageUri);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle the failure case
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                downloadImageUri = uri.toString();

                                                downloadImageUri = filePath.getDownloadUrl().toString();

                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    downloadImageUri = task.getResult().toString();
                                                    ProductsRef.child(pid).child("image").setValue(downloadImageUri);
                                                }

                                            }
                                        });
                                    }
                                });
                            }


                                loadingBar.dismiss();
                                Intent intent = new Intent(UpdateOrDeleteActivity.this, AdminServesesFragment.class);
                                startActivity(intent);
                                finish();
                            }

                    });
                }
    }



    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            Picasso.get().load(ImageUri).into(image);
            imageBool = true;
        }
    }
}
