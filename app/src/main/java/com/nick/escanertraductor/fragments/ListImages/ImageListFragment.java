package com.nick.escanertraductor.fragments.ListImages;

import static android.graphics.ImageDecoder.createSource;
import static android.graphics.ImageDecoder.decodeBitmap;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nick.escanertraductor.Constantes;
import com.nick.escanertraductor.R;
import com.nick.escanertraductor.adapter.AdapterImagen;
import com.nick.escanertraductor.models.ModelImage;
import com.nick.escanertraductor.activities.CropperActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// TODO: 28/05/2023 ImageListFragment
public class ImageListFragment extends Fragment {

    private static final String TAG = "IMAGE_LIST_TAG";
    private Uri imageUri = null;
    private FloatingActionButton addImageFab;
    private RecyclerView imagenList;
    private ArrayList<ModelImage> imagenesArrayList;
    private AdapterImagen adapterImagen;
    private ProgressDialog progressDialog;
    private Context mContext;

    public ImageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addImageFab = view.findViewById(R.id.addImageFab);
        imagenList = view.findViewById(R.id.imagesRv);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Por favor, espere...");
        progressDialog.setCanceledOnTouchOutside(false);

        cargarImagenes();

        addImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputImageDialog();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_imagenes, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if(itemId == R.id.images_item_delete){

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Eliminar imágenes")
                    .setMessage("¿Quieres eliminar las imágenes?")
                    .setPositiveButton("Eliminar todas las imágenes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            eliminarImagenes(true);
                        }
                    })
                    .setNeutralButton("Eliminar las seleccionadas", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            eliminarImagenes(false);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();

        } else if (itemId == R.id.lista_imagenes_pdf) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Covertir a PDF")
                    .setMessage("Convertir imagenes a PDF")
                    .setPositiveButton("Convertir todas las imágenes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            convertirImagenesAPDF(true);
                        }
                    })
                    .setNeutralButton("Convertir las seleccionadas", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            convertirImagenesAPDF(false);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void    convertirImagenesAPDF(boolean convertirTodo){

        Log.d(TAG, "convertirImagenesAPDF: comvertirTodo: "+convertirTodo);

        progressDialog.setMessage("Convirtiendo a PDF...");
        progressDialog.show();

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: BG work start");

                ArrayList<ModelImage> listaImagenesPdf = new ArrayList<>();
                if(convertirTodo){

                    listaImagenesPdf = imagenesArrayList;

                }
                else{

                    for(int i = 0; i < imagenesArrayList.size(); i++){

                        if(imagenesArrayList.get(i).isChecked()){

                            listaImagenesPdf.add(imagenesArrayList.get(i));

                        }

                    }

                }

                Log.d(TAG, "run: tamaño de la lista de imagenes convertidas a PDF: "+listaImagenesPdf.size());

                try {

                    File root = new File(mContext.getExternalFilesDir(null), Constantes.DIRECTORIO_PDF);
                    root.mkdirs();

                    long timestamp = System.currentTimeMillis();
                    String fileName = "PDF_" + timestamp + ".pdf";

                    Log.d(TAG, "run: fileName: "+fileName);

                    File file = new File(root, fileName);

                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    PdfDocument pdfDocument = new PdfDocument();

                    for(int i = 0; i < listaImagenesPdf.size(); i++){

                        Uri imageToAdInPdfUri = listaImagenesPdf.get(i).getImageUri();

                        try {

                            Bitmap bitmap;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                bitmap = decodeBitmap(createSource(mContext.getContentResolver(), imageToAdInPdfUri));
                            }
                            else{

                                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imageToAdInPdfUri);

                            }

                            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);

                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), i+1).create();

                            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                            Canvas canvas = page.getCanvas();

                            Paint paint = new Paint();
                            paint.setColor(Color.WHITE);

                            canvas.drawPaint(paint);
                            canvas.drawBitmap(bitmap, 0f, 0f, null);

                            pdfDocument.finishPage(page);

                            bitmap.recycle();

                        }catch(Exception e){

                            Log.d(TAG, "run: ", e);

                        }

                    }

                    pdfDocument.writeTo(fileOutputStream);
                    pdfDocument.close();

                }catch(Exception e){

                    progressDialog.dismiss();
                    Log.d(TAG, "run: ", e);

                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: Imagen convertida");
                        progressDialog.dismiss();
                        Toast.makeText(mContext, "Imagen convertida", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void eliminarImagenes(boolean eliminarTodo){

        ArrayList<ModelImage> eliminarImagenesLista = new ArrayList<>();

        if(eliminarTodo){

            eliminarImagenesLista = imagenesArrayList;

        }
        else{

            for(int i = 0; i < imagenesArrayList.size(); i++){

                if(imagenesArrayList.get(i).isChecked()){

                    eliminarImagenesLista.add(imagenesArrayList.get(i));

                }
            }
        }

        for(int i = 0; i < eliminarImagenesLista.size(); i++){

            try {
                String rutaImagenEliminada = eliminarImagenesLista.get(i).getImageUri().getPath();

                File file = new File(rutaImagenEliminada);

                if(file.exists()){

                    boolean esEliminada = file.delete();

                    Log.d(TAG, "eliminarImagenes: esEliminada: "+esEliminada);

                }
            }catch(Exception e){

                Log.d(TAG, "eliminarImagenes: ", e);

            }
        }

        Toast.makeText(mContext, "Eliminada", Toast.LENGTH_SHORT).show();

        cargarImagenes();

    }

    private void cargarImagenes() {
        Log.d(TAG, "cargarImagenes: ");

        imagenesArrayList = new ArrayList<>();
        adapterImagen = new AdapterImagen(mContext, imagenesArrayList);

        imagenList.setAdapter(adapterImagen);

        File carpeta = new File(mContext.getExternalFilesDir(null), Constantes.DIRECTORIO_IMAGENES);

        if(carpeta.exists()){
            Log.d(TAG, "cargarImagenes: La carpeta existe");
            File[] files = carpeta.listFiles();

            if(carpeta != null){

                Log.d(TAG, "cargarImagenes: La carpeta existe y tiene imágenes");

                for(File file: files){

                    Log.d(TAG, "cargarImagenes: Nombre del archivo "+file.getName());

                    Uri imageUri = Uri.fromFile(file);
                    ModelImage modelImage = new ModelImage(imageUri, false);

                    imagenesArrayList.add(modelImage);
                    adapterImagen.notifyItemInserted(imagenesArrayList.size());
                }


            }
            else{
                Log.d(TAG, "cargarImagenes: La carpeta existe pero está vacía");
            }

        }
        else{
            Log.d(TAG, "cargarImagenes: La carpeta no existe");
        }
    }

    private void guardarImagenEnDirectorio(Uri imagenAGuardar){

        Log.d(TAG, "guardarImagenEnDirectorio: ");

        try {

            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = decodeBitmap(createSource(mContext.getContentResolver(), imagenAGuardar));
            }
            else{
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), imagenAGuardar);
            }

            File directorio = new File(mContext.getExternalFilesDir(null), Constantes.DIRECTORIO_IMAGENES);
            directorio.mkdirs();

            long timestamp = System.currentTimeMillis();
            String nombreArchivo = timestamp+ ".jpeg";

            File file = new File(mContext.getExternalFilesDir(null), ""+Constantes.DIRECTORIO_IMAGENES+ "/"+nombreArchivo);

            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                fos.flush();
                fos.close();

                Log.d(TAG, "guardarImagenEnDirectorio: Imagen guardada");
                Toast.makeText(mContext, "Imagen guardada", Toast.LENGTH_SHORT).show();

            }catch(Exception e){
                Log.d(TAG, "guardarImagenEnDirectorio: ", e);
                Log.d(TAG, "guardarImagenEnDirectorio: Fallo al guardar imagen debido a "+e.getMessage());
                Toast.makeText(mContext, "Fallo al guardar imagen debido a "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }catch(Exception e){
            Log.d(TAG, "guardarImagenEnDirectorio: ", e);
            Log.d(TAG, "guardarImagenEnDirectorio: Fallo al preparar la imagen debido a "+e.getMessage());
            Toast.makeText(mContext, "Fallo al preparar la imagen debido a "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void showInputImageDialog(){

        Log.d(TAG, "showInputImageDialog: ");

        PopupMenu popupMenu = new PopupMenu(mContext, addImageFab);

        popupMenu.getMenu().add(Menu.NONE, 1, 1, "CÁMARA");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "GALERÍA");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int itemId = item.getItemId();
                if(itemId == 1){
                    Log.d(TAG, "onMenuItemClick: Se pulsó en la cámara, se comprueba si los permisos fueron concedidos");
                    if(comprobarPermisosCamara()){

                        tomarFotoCamara();

                    }
                    else{

                        pedirPermisosCamara.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});

                    }

                } else if (itemId == 2) {
                    Log.d(TAG, "onMenuItemClick: Se pulsó en la galería, se comprueba si los permisos fueron concedidos");
                    if(comprobarPermisosTarjeta()){
                        tomarImagenGaleria();
                    }
                    else{

                        pedirPermisosTarjeta.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    }

                }

                return true;
            }
        });
    }

    private void tomarImagenGaleria(){

        Log.d(TAG, "tomarImagenGaleria: ");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();

                        imageUri = data.getData();

                        Log.d(TAG, "onActivityResult: Elegir imagen de la galería "+imageUri);

                        guardarImagenEnDirectorio(imageUri);

                        ModelImage modelImage = new ModelImage(imageUri, false);
                        imagenesArrayList.add(modelImage);
                        adapterImagen.notifyItemInserted(imagenesArrayList.size());
                    }
                    else{

                        Toast.makeText(mContext, "Cancelado...", Toast.LENGTH_SHORT).show();
                    }


                }
            }
    );

    private void tomarFotoCamara(){

        Log.d(TAG, "tomarFotoCamara: ");

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP IMAGE TITLE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP IMAGE DESCRIPTION");

        imageUri = mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        camaraActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent>  camaraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){

                        Log.d(TAG, "onActivityResult: Tomar foto de la cámara "+imageUri);
                        guardarImagenEnDirectorio(imageUri);

                        ModelImage modelImage = new ModelImage(imageUri, false);
                        imagenesArrayList.add(modelImage);
                        adapterImagen.notifyItemInserted(imagenesArrayList.size());
                    }
                    else{
                        Toast.makeText(mContext, "Cancelado...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private boolean comprobarPermisosTarjeta(){

        Log.d(TAG, "comprobarPermisosTarjeta: ");

        boolean result = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;

        return result;
    }

    private ActivityResultLauncher<String> pedirPermisosTarjeta = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean esConcedido) {

                    Log.d(TAG, "onActivityResult: esConcedido"+esConcedido);

                    if(esConcedido){

                        tomarImagenGaleria();

                    }
                    else{

                        Toast.makeText(mContext, "Permiso denegado", Toast.LENGTH_SHORT).show();

                    }

                }
            }
    );

    private boolean comprobarPermisosCamara(){

        Log.d(TAG, "comprobarPermisosCamara: ");

        boolean camaraResult = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;
        boolean tarjetaResult = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;

        return camaraResult && tarjetaResult;
    }

    private ActivityResultLauncher<String[]> pedirPermisosCamara = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.d(TAG, "onActivityResult: ");
                    Log.d(TAG, "onActivityResult: "+result.toString());

                    boolean todosConcedidos = true;
                    for(Boolean esConcedido: result.values()){
                        Log.d(TAG, "onActivityResult: esConcedido: "+esConcedido);
                        todosConcedidos = todosConcedidos && esConcedido;
                    }

                    // Si ambos permisos están concedidos se procede a arrancar la cámara para tomar fotos
                    if(todosConcedidos){
                        Log.d(TAG, "onActivityResult: Todos los permisos concedidos e.g. Cámara y almacenamiento");
                        tomarFotoCamara();
                    }
                    else{
                        Log.d(TAG, "onActivityResult: Cámara o almacenamiento o ambos denegados...");
                        Toast.makeText(mContext, "Cámara o almacenamiento o ambos denegados...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

}