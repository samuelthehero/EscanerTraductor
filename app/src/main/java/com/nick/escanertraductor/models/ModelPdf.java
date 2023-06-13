package com.nick.escanertraductor.models;

import android.net.Uri;

import java.io.File;

public class ModelPdf {

    File file;
    Uri uri;

    /**
     * Constructor
     * @param file
     * @param uri
     */
    public ModelPdf(File file, Uri uri) {
        this.file = file;
        this.uri = uri;
    }

    /**
     * Getter
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * Setter
     * @param file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Getter
     * @return
     */
    public Uri getUri() {
        return uri;
    }

    /**
     * Setter
     * @param uri
     */
    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
