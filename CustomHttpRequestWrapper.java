package fonction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CustomHttpRequestWrapper extends HttpServletRequestWrapper {

    private String urlToSet;
    private String methodToSet = "GET"; // Par défaut, méthode GET

    /**
     * Constructeur pour le wrapper de requête HTTP personnalisé.
     *
     * @param request L'objet HttpServletRequest original.
     */
    public CustomHttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * Définir une nouvelle URL.
     *
     * @param newUrl La nouvelle URL à définir.
     */
    public void setUrl(String newUrl) {
        this.urlToSet = newUrl;
    }

    /**
     * Définir une nouvelle méthode HTTP.
     *
     * @param newMethod La nouvelle méthode HTTP à définir.
     */
    public void setMethod(String newMethod) {
        this.methodToSet = newMethod;
    }

    @Override
    public String getRequestURI() {
        return urlToSet != null ? urlToSet : super.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(urlToSet != null ? urlToSet : super.getRequestURL());
    }

    @Override
    public String getMethod() {
        return methodToSet != null ? methodToSet : super.getMethod();
    }
}
