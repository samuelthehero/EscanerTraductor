package com.nick.escanertraductor;

import com.nick.escanertraductor.adapter.AdapterPdf;
import com.nick.escanertraductor.models.ModelPdf;

public interface RvListenerPdf {

    void onPdfClick(ModelPdf modelPdf, int position);
    void onPdfMoreClick(ModelPdf modelPdf, int position, AdapterPdf.HolderPdf holder);

}
