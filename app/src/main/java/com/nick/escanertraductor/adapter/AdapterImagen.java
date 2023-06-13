package com.nick.escanertraductor.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nick.escanertraductor.activities.ImageViewActivity;
import com.nick.escanertraductor.models.ModelImage;
import com.nick.escanertraductor.R;

import java.util.ArrayList;

public class AdapterImagen extends RecyclerView.Adapter<AdapterImagen.HolderImage>{

    private Context context;
    private ArrayList<ModelImage> imageArrayList;

    /**
     *
     * @param context
     * @param imageArrayList
     */

    public AdapterImagen(Context context, ArrayList<ModelImage> imageArrayList) {
        this.context = context;
        this.imageArrayList = imageArrayList;
    }

    /**
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public HolderImage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_image, parent, false);
        return new HolderImage(view);
    }

    /**
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HolderImage holder, @SuppressLint("RecyclerView") int position) {

        ModelImage modelImage = imageArrayList.get(position);

        Uri imageUri = modelImage.getImageUri();

        Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.icon_image)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra("imageUri", ""+imageUri);
                context.startActivity(intent);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                modelImage.setChecked(isChecked);
            }
        });
    }

    /**
     * @return
     */
    @Override
    public int getItemCount() {
        return imageArrayList.size();
    }

    class HolderImage extends RecyclerView.ViewHolder{

        ImageView imageView;
        CheckBox checkBox;

        public HolderImage(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imagen);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
