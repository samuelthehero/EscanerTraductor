package com.nick.escanertraductor.models;

public class ModelLanguage {

    private String CodIdioma;
    private String tituloIdioma;

    /**
     *
     * @param codIdioma
     * @param tituloIdioma
     */
    public ModelLanguage(String codIdioma, String tituloIdioma) {
        CodIdioma = codIdioma;
        this.tituloIdioma = tituloIdioma;
    }

    /**
     * Getter
     * @return
     */
    public String getCodIdioma() {
        return CodIdioma;
    }

    /**
     * Setter
     * @param codIdioma
     */
    public void setCodIdioma(String codIdioma) {
        CodIdioma = codIdioma;
    }

    /**
     * Getter
     * @return
     */
    public String getTituloIdioma() {
        return tituloIdioma;
    }

    /**
     * Setter
     * @param tituloIdioma
     */
    public void setTituloIdioma(String tituloIdioma) {
        this.tituloIdioma = tituloIdioma;
    }
}
