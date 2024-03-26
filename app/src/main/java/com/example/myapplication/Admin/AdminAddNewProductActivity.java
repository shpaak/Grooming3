package com.example.myapplication.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.SimpleTimeZone;


public class AdminAddNewProductActivity extends AppCompatActivity {
    private String Description, Price, pname, saveCurrentDate, saveCurrentTime, productRandomKey;
    private String downloadImageUrl;
private ImageView productImage;
private EditText productName, productPrice, productDescription;
private Button addNewProguctButton;
private static final int GALLERYYPICK = 1;
private Uri ImageUri;
private StorageReference ProductImageRef;
private DatabaseReference ProductsRef;
private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        init();
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
        addNewProguctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });

    }

    private void ValidateProductData() {
        Description = productDescription.getText().toString();
        Price = productPrice.getText().toString();
        pname= productName.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this, "Добавьте изображение", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Добавьте описание", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Добавьте цену", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pname)){
            Toast.makeText(this, "Добавьте название", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreProductInformatoin();
        }
    }

    private void StoreProductInformatoin() {

        loadingBar.setTitle("Загрузка данных");
        loadingBar.setMessage("Пожалуйста, подождите!");
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Ошибка " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Изображение усшешно загружено", Toast.LENGTH_SHORT).show();
                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadImageUrl = uri.toString();

                        // Сохраняем информацию о продукте в базу данных
                        SaveProductInfoToDatabase();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminAddNewProductActivity.this, "Ошибка при загрузке изображения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void  SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("price", Price);
        productMap.put("pname", pname);

        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Товар добавлен", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminServesesFragment.class);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Ошибка: "+ message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }
                    }
                });

    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERYYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERYYPICK && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
productImage.setImageURI(ImageUri);
        }
    }

    private void init(){
        productImage=findViewById(R.id.product_image);
        productName=findViewById(R.id.product_name);
        productPrice=findViewById(R.id.product_price);
        productDescription=findViewById(R.id.product_description);
        addNewProguctButton=findViewById(R.id.add_product_button);
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar = new ProgressDialog(this);
    }
}