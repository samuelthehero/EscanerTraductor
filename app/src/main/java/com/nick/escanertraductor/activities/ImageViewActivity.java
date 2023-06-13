package com.nick.escanertraductor.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.nick.escanertraductor.R;

import java.io.IOException;

public class ImageViewActivity extends AppCompatActivity  {

    private ImageView imageIv;

    private Button ReconocerTexto, traducirBtn, btnCrop, btnVolver, btnSiguiente;

    private EditText TextoReconocidoET;

    private ProgressDialog progressDialog;

    ActivityResultLauncher<String> mGetContent;

    // Reconocedor de texto
    private TextRecognizer textRecognizer;
    private String image;
    private Context context;

    private static final String TAG = "IMAGE_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        getSupportActionBar().setTitle("Editar y traducir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageIv = findViewById(R.id.imageIv);
        btnCrop = findViewById(R.id.btnCrop);
        btnSiguiente = findViewById(R.id.btnSiguiente);

        image = getIntent().getStringExtra("imageUri");
        Log.d(TAG, "onCreate: Image:"+image);

        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.icon_image)
                .into(imageIv);

        imageIv = findViewById(R.id.imageIv);

        image = getIntent().getStringExtra("imageUri");
        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.icon_image)
                .into(imageIv);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reconocerTextoImagen();
            }
        });

        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageViewActivity.this, CropperActivity.class);
                intent.putExtra("DATA", image);
                startActivityForResult(intent, 101);
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        Intent intent = new Intent(ImageViewActivity.this, CropperActivity.class);
                        intent.putExtra("DATA", result.toString());
                        startActivityForResult(intent, 101);
                    }
                });


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false); // Esto evita que el progress dialog se cierre cuando el usuario presione fuera del progressdialog

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == -1 && requestCode == 101){

            String result = data.getStringExtra("RESULT");
            Uri resultUri;
            if (result != null) {

                resultUri = Uri.parse(result);

                imageIv.setImageURI(resultUri);
            }
        }
    }

    private void reconocerTextoImagen() {
        progressDialog.setMessage("Preparando imagen");
        progressDialog.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(this, Uri.parse(image));
            progressDialog.setMessage("Reconociendo texto");
            Task<Text> textTask = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            // En caso de que la acción se ejecute correctamente entra en este método
                            progressDialog.dismiss(); // Descartamos el progressdialog
                            // Obtenemos el texto de la imagen
                            Intent intent = new Intent(ImageViewActivity.this, TraductorActivity.class);
                            String texto = text.getText().toString();
//                            String textoReconocido = TextoReconocidoET.getText().toString();
                            intent.putExtra("textoReconocido", texto);
//                            TextoReconocidoET.setText(texto);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // En caso contrario entraría en este otro
                            progressDialog.dismiss();
                            Toast.makeText(ImageViewActivity.this, "No se pudo reconocer el texto. Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error al preparar la imagen "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}