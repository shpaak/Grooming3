package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class GalletyFragment extends Fragment {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

    public GalletyFragment() {
        // Пустой конструктор обязателен для фрагментов
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallety, container, false);

        recyclerView = view.findViewById(R.id.photo_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Создайте список фотографий
        List<Photo> photoList = new ArrayList<>();
        // Добавьте фотографии в список
        photoList.add(new Photo(R.drawable.im1));
        photoList.add(new Photo(R.drawable.im2));
        photoList.add(new Photo(R.drawable.im3));
        photoList.add(new Photo(R.drawable.im4));
        photoList.add(new Photo(R.drawable.im5));
        photoList.add(new Photo(R.drawable.im6));
        photoList.add(new Photo(R.drawable.im7));
        photoList.add(new Photo(R.drawable.im8));
        photoList.add(new Photo(R.drawable.im9));
        photoList.add(new Photo(R.drawable.im10));
        photoList.add(new Photo(R.drawable.im11));
        photoList.add(new Photo(R.drawable.im12));
        // Добавьте остальные фотографии в список

        // Создайте и установите адаптер для отображения фотографий
        photoAdapter = new PhotoAdapter(photoList);
        recyclerView.setAdapter(photoAdapter);

        return view;
    }

    // Внутренний класс Photo для представления данных о фотографии
    private static class Photo {
        private int imageResId;

        public Photo(int imageResId) {
            this.imageResId = imageResId;
        }

        public int getImageResId() {
            return imageResId;
        }
    }

    // Внутренний класс PhotoAdapter для управления отображением фотографий в RecyclerView
    private static class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

        private List<Photo> photoList;

        public PhotoAdapter(List<Photo> photoList) {
            this.photoList = photoList;
        }

        public static class PhotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public PhotoViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.photo_image_view);
            }
        }

        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
            return new PhotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoViewHolder holder, int position) {
            Photo photo = photoList.get(position);
            holder.imageView.setImageResource(photo.getImageResId());
        }

        @Override
        public int getItemCount() {
            return photoList.size();
        }
    }
}
