package fonction;

import java.util.HashMap;

public class ModelView {

    private String url;
    private HashMap<String, Object> data;

    public ModelView() {
        this.data = new HashMap<>();
    }

    // Getter pour l'attribut url
    public String getUrl() {
        return url;
    }

    // Setter pour l'attribut url
    public void setUrl(String url) {
        this.url = url;
    }

    // Getter pour l'attribut data
    public HashMap<String, Object> getData() {
        return data;
    }

    // Setter pour l'attribut data
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    // Méthode pour ajouter un objet à la HashMap
    public void addObject(String nom_variable, Object valeur) {
        data.put(nom_variable, valeur);
    }

}
