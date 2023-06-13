package com.nick.escanertraductor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;

import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.nick.escanertraductor.R;
import com.nick.escanertraductor.models.ModelLanguage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TraductorActivity extends AppCompatActivity {

    private EditText sourceLanguageEt;
    private TextView DestinationLanguageTv;

    private MaterialButton sourceLanguageChooseBtn, destinationLanguageChooseBtn, translateBtn;

    private TranslatorOptions translatorOptions;
    private Translator translator;

    private ProgressDialog progressDialog;

    private ArrayList<ModelLanguage> languageArrayList;

    private static final String TAG = "MAIN_TAG";

    private String sourceLanguageCode = "en";
    private String sourceLanguageTitle = "English";
    private String destinationLanguageCode = "es";
    private String destinationLanguageTitle = "Spanish";

    private LanguageIdentifier languageIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traductor);

        findViewById(R.id.translateBtn);

        // Obtenemos el texto reconocido para procesarlo y traducirlo
        Intent intent = getIntent();
        String textoReconocido = intent.getStringExtra("textoReconocido");

        sourceLanguageEt = findViewById(R.id.sourceLanguageEt);

        sourceLanguageEt.setText(textoReconocido);

        sourceLanguageChooseBtn = findViewById(R.id.sourceLanguageChooseBtn);
        DestinationLanguageTv = findViewById(R.id.DestinationLanguageTv);
        destinationLanguageChooseBtn = findViewById(R.id.destinationLanguageChooseBtn);
        translateBtn = findViewById(R.id.translateBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);

        loadAvailableLanguage();

        // Al hacer click se despliega la lista de idomas que desas traducir
        sourceLanguageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceLanguageChoose();
            }
        });
        // Al hacer click se despliega la lista de idomas que desas traducir
        destinationLanguageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destinationLanguageChoose();
            }
        });
        // Al hacer click se comienza a traducir al idoma seleccionado
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });

    }

    private String sourceLanguageText = "";
    private void validarDatos() {
        sourceLanguageText = sourceLanguageEt.getText().toString().trim();

        Log.d(TAG, "validarDatos: sourceLanguageText: "+sourceLanguageText);

        // Valida los datos introducidos, si el campoe está vacío se muestra un mensaje de error
        // en caso contrario se procederá a traducir
        if(sourceLanguageText.isEmpty()){
            Toast.makeText(this, "No hay texto a traducir, escriba un texto", Toast.LENGTH_SHORT).show();
        }else{
            startTranslation();
        }
    }

    private void startTranslation() {
        progressDialog.setMessage("Procesando modelo de idioma");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(destinationLanguageCode)
                .build();

        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Modelo de traducción listo para traducir
                        Log.d(TAG, "onSuccess: modelo listo, comenzando a traducir...");
                        progressDialog.setMessage("Traduciendo...");

                        translator.translate(sourceLanguageText)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String textoTraducido) {
                                        // Traducción exitosa

                                        Log.d(TAG, "onSuccess: textoTraducido"+textoTraducido);
                                        progressDialog.dismiss();

                                        DestinationLanguageTv.setText(textoTraducido);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Fallo al traducir
                                        progressDialog.dismiss();
                                        Log.d(TAG, "onFailure: ",e);
                                        Toast.makeText(TraductorActivity.this, "Fallo al traducir debido a "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Fallo al leer el modelo de traducción, no se puede procesar la traducción
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: ",e);
                        Toast.makeText(TraductorActivity.this, "Fallo al leer modelo debido a "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sourceLanguageChoose(){

        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(String.valueOf(sourceLanguageEt))
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode.equals("und")) {
                                    Log.i(TAG, "No se pudo identificar el idioma.");
                                } else {
                                    Log.i(TAG, "idioma: " + languageCode);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TraductorActivity.this, "No se pudo reconocer el idioma debido a " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

        PopupMenu popupMenu = new PopupMenu(this, sourceLanguageChooseBtn);

        for(int i=0; i<languageArrayList.size(); i++){

            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).getTituloIdioma());
        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int position = menuItem.getItemId();

                sourceLanguageCode = languageArrayList.get(position).getCodIdioma();
                sourceLanguageTitle = languageArrayList.get(position).getTituloIdioma();

                sourceLanguageChooseBtn.setText(sourceLanguageTitle);
                sourceLanguageEt.setHint("Intro "+sourceLanguageTitle);

                Log.d(TAG, "onMenuItemClick: CodIdioma: "+sourceLanguageCode);
                Log.d(TAG, "onMenuItemClick: CodIdiomaTitulo: "+sourceLanguageTitle);

                return false;
            }
        });
    }

    private void destinationLanguageChoose(){

        PopupMenu popupMenu = new PopupMenu(this, destinationLanguageChooseBtn);

        for(int i=0; i<languageArrayList.size(); i++){
            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).getTituloIdioma());
        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Obtiene el item de la lista al que se ha clicado
                int position = menuItem.getItemId();

                // Obtiene el código y el título de los idiomas seleccionados
                destinationLanguageCode = languageArrayList.get(position).getCodIdioma();
                destinationLanguageTitle = languageArrayList.get(position).getTituloIdioma();

                // Setea el idioma seleccionado al botón de eleccion de idioma de destino como texto
                destinationLanguageChooseBtn.setText(destinationLanguageTitle);

                Log.d(TAG, "onMenuItemClick: idiomaDestino"+destinationLanguageCode);
                Log.d(TAG, "onMenuItemClick: tituloIdiomaDestino"+destinationLanguageTitle);
                return false;
            }
        });
    }

    private void loadAvailableLanguage() {

        languageArrayList = new ArrayList<>();

        List<String> languageCodeList = TranslateLanguage.getAllLanguages();

        for(String codIdioma: languageCodeList){

            String tituloIdioma = new Locale(codIdioma).getDisplayLanguage(); // Esto hace el cambio de idioma
            Log.d(TAG, "loadAvailableLanguage: languageCode" + codIdioma);
            Log.d(TAG, "loadAvailableLanguage: languageTitle" + codIdioma);

            ModelLanguage modelLanguage = new ModelLanguage(codIdioma, tituloIdioma);
            languageArrayList.add(modelLanguage);
        }
    }
}