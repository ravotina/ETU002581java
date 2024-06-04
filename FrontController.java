package controlleur;
import fonction.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.RequestDispatcher; 
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {

    HashMap <String , Mapping>  mappinge = new HashMap<>();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        String chemin_url = request.getRequestURL().toString();

        String contextPath = request.getContextPath(); // Ex: /YourAppName

        String[] result_fin =  chemin_url.split(contextPath);

        String urlAnoter = result_fin[1].replaceFirst("^/", "");

        if(mappinge.containsKey(urlAnoter)){
            out.print("</br>");
            Object resultat =  Utils.execute_fontion(mappinge.get(urlAnoter).getClasse_name() , mappinge.get(urlAnoter).getMethodName());
            if (Utils.testReturnType(resultat) == 1) {
                try {
                    ModelView result_model_view = (ModelView) resultat;
                    for (HashMap.Entry<String, Object> entry : result_model_view.getData().entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                    String url = result_model_view.getUrl();
                    out.print(url);
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/web/"+url);
                    dispatcher.forward(request, response);
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            } else if(Utils.testReturnType(resultat)==2){
                out.print(resultat.toString());
            } else {
                out.println("type de return nom reconnu");
            }
            out.print("</br>");
        } else {
            out.println("URL :" + contextPath + " est introuvable");
        }
        // RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
        // dispatcher.forward(request, response);
    }

    public void init() throws ServletException {
        String dossier_controleur = this.getInitParameter("dossier_controleur");
        try{
            List<Class<?>> liste_classe = Utils.getClassWithAnnotation(dossier_controleur);

            for(Class<?> classe_name : liste_classe) {
                Method [] liste_methode = classe_name.getDeclaredMethods();
                for(Method methode : liste_methode){
                    if(methode.isAnnotationPresent(Get.class)){
                        String url = methode.getAnnotation(Get.class).value();
                        mappinge.put(url , new Mapping(classe_name.getName() , methode.getName()));
                    }
                }
            }

        } catch (Exception e){

        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
