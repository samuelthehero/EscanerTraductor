package com.nick.escanertraductor.models;

import android.net.Uri;

public class ModelImage {

    /**
     * Variables para cargar datos
     */
    Uri imageUri;
    boolean checked;

    /**
     * Constructor
     * @param imageUri
     */
    public ModelImage(Uri imageUri, boolean checked){
        this.imageUri = imageUri;
        this.checked = checked;
    }

    /**
     * Getter
     * @return
     */
    public Uri getImageUri() {
        return imageUri;
    }

    /**
     * Setter
     * @param imageUri
     */
    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    /**
     * Getter
     * @return
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Setter
     * @param checked
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
