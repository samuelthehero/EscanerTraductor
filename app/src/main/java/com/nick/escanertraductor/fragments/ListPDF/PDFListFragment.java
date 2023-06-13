package com.nick.escanertraductor.fragments.ListPDF;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.nick.escanertraductor.adapter.AdapterPdf;
import com.nick.escanertraductor.Constantes;
import com.nick.escanertraductor.models.ModelPdf;
import com.nick.escanertraductor.activities.PdfViewActivity;
import com.nick.escanertraductor.R;
import com.nick.escanertraductor.RvListenerPdf;

import java.io.File;
import java.util.ArrayList;

public class PDFListFragment extends Fragment implements SearchView.OnQueryTextListener{

    SearchView txtBuscar;
    private RecyclerView pdfRv;
    private Context mContext;

    private ArrayList<ModelPdf> pdfArrayList;

    private AdapterPdf adapterPdf;

    private static final String TAG = "PDF_LIST_TAG";

    public PDFListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pdfRv = view.findViewById(R.id.pdfRv);
//        txtBuscar = view.findViewById(R.id.txtBuscar);

        cargarDocumentosPdf();

//        txtBuscar.setOnQueryTextListener(this);
    }

    private void cargarDocumentosPdf() {

        pdfArrayList = new ArrayList<>();
        adapterPdf = new AdapterPdf(mContext, pdfArrayList, new RvListenerPdf() {
            @Override
            public void onPdfClick(ModelPdf modelPdf, int position) {

                Intent intent = new Intent(mContext, PdfViewActivity.class);
                intent.putExtra("pdfUri", ""+modelPdf.getUri());
                startActivity(intent);

            }

            @Override
            public void onPdfMoreClick(ModelPdf modelPdf, int position, AdapterPdf.HolderPdf holder) {

                mostrarMasOpciones(modelPdf, holder);

            }
        });

        pdfRv.setAdapter(adapterPdf);

        File carpeta = new File(mContext.getExternalFilesDir(null), Constantes.DIRECTORIO_PDF);

        if(carpeta.exists()){

            File[] files = carpeta.listFiles();
            Log.d(TAG, "cargarDocumentosPdf: Total de archivos: "+files.length);

            for(File fileEntry: files){

                Log.d(TAG, "cargarDocumentosPdf: Nombre del archivo: "+fileEntry.getName());

                Uri uri = Uri.fromFile(fileEntry);


                ModelPdf modelPdf = new ModelPdf(fileEntry, uri);

                pdfArrayList.add(modelPdf);

                adapterPdf.notifyItemInserted(pdfArrayList.size());

            }

        }

    }

    private void mostrarMasOpciones(ModelPdf modelPdf, AdapterPdf.HolderPdf holder) {
        Log.d(TAG, "mostrarMasOpciones: ");


        PopupMenu popupMenu = new PopupMenu(mContext, holder.moreBtn);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Renombrar");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Eliminar");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Compartir");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if(itemId == 0){

                    renombrarPdf(modelPdf);

                } else if (itemId == 1) {

                    eliminarPdf(modelPdf);

                } else if (itemId == 2) {

                    compartirPdf(modelPdf);

                }

                return true;
            }
        });

    }

    private void renombrarPdf(ModelPdf modelPdf){
        Log.d(TAG, "renombrarPdf: ");

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_rename, null);

        EditText pdfNewNameEt = view.findViewById(R.id.pdfNewNameEt);
        Button btnRenombrar = view.findViewById(R.id.btnRenombrar);

        String nombrePrevio = ""+modelPdf.getFile().getName();
        Log.d(TAG, "renombrarPdf: nombrePrecio: "+nombrePrevio);

        pdfNewNameEt.setText(nombrePrevio);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnRenombrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nuevoNombre = pdfNewNameEt.getText().toString().trim();
                Log.d(TAG, "onClick: nuevoNombre: "+nuevoNombre);

                if(nuevoNombre.isEmpty()){

                    Toast.makeText(mContext, "Introduce un nombre...", Toast.LENGTH_SHORT).show();

                }
                else{

                    try{

                        File nuevoArchivo = new File(mContext.getExternalFilesDir(null), Constantes.DIRECTORIO_PDF + "/" + nuevoNombre + ".pdf");

                        modelPdf.getFile().renameTo(nuevoArchivo);

                        Toast.makeText(mContext, "Nombre cambiado correctamente", Toast.LENGTH_SHORT).show();

                        cargarDocumentosPdf();

                    }catch (Exception e){
                        Log.d(TAG, "onClick: ", e);
                        Toast.makeText(mContext, "Fallo al cambiar nombre debido a "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    alertDialog.dismiss();

                }
            }
        });
    }

    private void eliminarPdf(ModelPdf modelPdf) {

        Log.d(TAG, "eliminarPdf: ");

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Eliminar archivo")
                .setMessage("¿Estás seguro de querer eliminar el "+modelPdf.getFile().getName()+"?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {

                            modelPdf.getFile().delete();
                            Toast.makeText(mContext, "Archivo eliminado con éxito", Toast.LENGTH_SHORT).show();

                            cargarDocumentosPdf();

                        }
                        catch(Exception e){

                            Log.d(TAG, "eliminarPdf onClick: ", e);
                            Toast.makeText(mContext, "Fallo al eliminar el archivo debido a "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

    }

    private void compartirPdf(ModelPdf modelPdf) {

        Log.d(TAG, "compartirPdf: ");

        File file = modelPdf.getFile();

        // Genera el Uri. Defino la autoridad como el ID de la aplicación en el Manifest. El último parámetro de ese archivo es que el quiero abrir
        Uri uri = FileProvider.getUriForFile(mContext, "com.nick.escanertraductor.fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Compartir PDF"));

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapterPdf.filtrado(s);
        return false;
    }
}