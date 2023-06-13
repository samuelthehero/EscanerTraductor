package com.nick.escanertraductor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.nick.escanertraductor.adapter.AdapterPdfView;
import com.nick.escanertraductor.models.ModelPdfView;
import com.nick.escanertraductor.R;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PdfViewActivity extends AppCompatActivity {

    private RecyclerView pdfViewRv;

    private String pdfUri;

    private static final String TAG = "PDF_VIEW_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

//        getSupportActionBar().setTitle("PDF");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pdfViewRv = findViewById(R.id.pdfViewRv);

        pdfUri = getIntent().getStringExtra("pdfUri");

        Log.d(TAG, "onCreate: pdfUri "+pdfUri);

        cargarPaginasPdf();
    }

    private PdfRenderer.Page mCurrentPage = null;

    private void cargarPaginasPdf() {

        Log.d(TAG, "cargarPaginasPdf: ");

        ArrayList<ModelPdfView> pdfViewArrayList = new ArrayList<>();

        AdapterPdfView adapterPdfView = new AdapterPdfView(this, pdfViewArrayList);
        pdfViewRv.setAdapter(adapterPdfView);

        File file = new File(Uri.parse(pdfUri).getPath());
        try{

            getSupportActionBar().setSubtitle(file.getName());

        }catch(Exception e){

            Log.d(TAG, "cargarPaginasPdf: cargarPaginasPdf: ", e);

        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                try{

                    ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

                    PdfRenderer mPdfRenderer = new PdfRenderer(parcelFileDescriptor);

                    int pageCount = mPdfRenderer.getPageCount();

                    if(pageCount <= 0){

                        Log.d(TAG, "run: Sin páginas en el archivo PDF");

                    }
                    else{

                        Log.d(TAG, "run: El archivo PDF tiene páginas");

                        for (int i = 0; i < pageCount; i++){

                            if(mCurrentPage != null){

                                mCurrentPage.close();

                            }

                            mCurrentPage = mPdfRenderer.openPage(i);

                            Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(), Bitmap.Config.ARGB_8888);

                            mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                            pdfViewArrayList.add(new ModelPdfView(Uri.parse(pdfUri), (i+1), pageCount, bitmap));

                        }

                    }

                }catch(Exception e){

                    Log.d(TAG, "run: 01");

                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: UI thread...");
                        adapterPdfView.notifyDataSetChanged();
                    }
                });

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}