package com.nick.escanertraductor.models;

import android.graphics.Bitmap;
import android.net.Uri;

public class ModelPdfView {

    Uri pdfUri;
    int pageNumber;
    int pageCount;
    Bitmap bitmap;

    /**
     * Constructor
     * @param pdfUri
     * @param pageNumber
     * @param pageCount
     * @param bitmap
     */
    public ModelPdfView(Uri pdfUri, int pageNumber, int pageCount, Bitmap bitmap) {
        this.pdfUri = pdfUri;
        this.pageNumber = pageNumber;
        this.pageCount = pageCount;
        this.bitmap = bitmap;
    }

    /**
     * Getter
     * @return
     */
    public Uri getPdfUri() {
        return pdfUri;
    }

    /**
     * Setter
     * @param pdfUri
     */
    public void setPdfUri(Uri pdfUri) {
        this.pdfUri = pdfUri;
    }

    /**
     * Getter
     * @return
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Setter
     * @param pageNumber
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Getter
     * @return
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * Setter
     * @param pageCount
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    /**
     * Getter
     * @return
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Setter
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
