package controlleur;
import fonction.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

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
            out.println(Utils.execute_fontion(mappinge.get(urlAnoter).getClasse_name() , mappinge.get(urlAnoter).getMethodName()));
            out.print("</br>");
        } else {
            out.println("URL :" + contextPath + " est introuvable");
        }
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
