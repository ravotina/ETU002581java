package fonction;
import java.util.Objects;

public class VerbAction {
    String method;
    String verb;

    public VerbAction() {}

    public VerbAction(String method, String verb) {
        this.method = method;
        this.verb = verb;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getVerb() {
        return this.verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

  @Override
    public boolean equals(Object o) {
        if (!(o instanceof VerbAction)) {
            return false;
        }
        boolean verificateur = false;
        VerbAction that = (VerbAction) o;

        // Si l'objet actuel n'a pas de méthode définie, retourne false immédiatement
        if (this == null) {
            System.out.println("Méthode non définie, retour false.");
            return false;
        } 
        if (Objects.equals(verb, that.verb)) {
            verificateur = true;
            System.out.println("verbe:" + verificateur);
        }        
        if (Objects.equals(method, that.method)){
            verificateur = true;
            System.out.println("method:" + verificateur);
        } 
        return verificateur;        
    } 

    @Override
    public int hashCode() {
        return Objects.hash(method, verb);
    }



}
