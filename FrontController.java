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

    HashMap<String, Mapping> mappinge = new HashMap<>();

    // Définition de l'exception personnalisée
    public class UrlAlreadyExistsException extends Exception {
        public UrlAlreadyExistsException(String message) {
            //super(message);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {
            String chemin_url = request.getRequestURL().toString();
            String contextPath = request.getContextPath(); // Ex: /YourAppName
            String[] result_fin = chemin_url.split(contextPath);
            String urlAnoter = result_fin[1].replaceFirst("^/", "");

            try {
                out.print("</br>");
                Object resultat = Utils.execute_fontion(mappinge.get(urlAnoter).getClasse_name(), mappinge.get(urlAnoter).getMethodName());
                if (Utils.testReturnType(resultat) == 1) {
                    try {
                        ModelView result_model_view = (ModelView) resultat;
                        for (HashMap.Entry<String, Object> entry : result_model_view.getData().entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                        }
                        String url = result_model_view.getUrl();
                        out.print(url);
                        RequestDispatcher dispatcher = request.getRequestDispatcher("/web/" + url);
                        dispatcher.forward(request, response);
                    } catch (Exception e) {
                        log("Error processing ModelView", e);
                        out.println(e.getMessage());
                    }
                } else if (Utils.testReturnType(resultat) == 2) {
                    out.println(resultat.toString());
                } else {
                    out.println("</br>");
                    out.println("type de return non reconnu");
                    out.println("</br>");
                    throw new UrlAlreadyExistsException("type de return non reconnu");
                }
                out.print("</br>");
            } catch (UrlAlreadyExistsException e) {
                out.println("<p style='color:red;'>Error: " + e.getMessage() + "</p>");
                System.err.println(e.getMessage());
                System.out.println("UrlAlreadyExistsException eurrer");
                e.printStackTrace(System.err);
            } catch (Exception e) {
                log("Error executing function", e);
                out.println(e.getMessage());
                System.err.println(e.getMessage());
                out.println("URL non reconnue: " + chemin_url);
                System.out.println("Exception eurrer");
                e.printStackTrace(System.err);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public void init() throws ServletException {
        String dossier_controleur = this.getInitParameter("dossier_controleur");
        try {
            List<Class<?>> liste_classe = Utils.getClassWithAnnotation(dossier_controleur);

            for (Class<?> classe_name : liste_classe) {
                Method[] liste_methode = classe_name.getDeclaredMethods();
                for (Method methode : liste_methode) {
                    if (methode.isAnnotationPresent(Get.class)) {
                        String url = methode.getAnnotation(Get.class).value();

                        // Vérifier si l'URL existe déjà dans la map
                        System.out.println("Liste key: " + url);
                        if (mappinge.containsKey(url)) {
                            // Afficher en terminale
                            System.err.println("Duplicate URL detected: " + url);
                            // Lancer une exception pour la gestion interne
                            throw new UrlAlreadyExistsException("Duplicate URL detected: " + url);
                        }

                        // Ajouter l'URL à la map
                        mappinge.put(url, new Mapping(classe_name.getName(), methode.getName()));
                    }
                }
            }

        } catch (UrlAlreadyExistsException e) {
            // Gérer l'exception de doublon d'URL sans la relancer
            log("Duplicate URL detected: " + e.getMessage());
            System.err.println("Duplicate URL detected: " + e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            // Gérer d'autres exceptions
            log("Error during initialization: " + e.getMessage(), e);
            System.err.println("Error during initialization: " + e.getMessage());
            e.printStackTrace(System.err);
            throw new ServletException(e);
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





 // public void init() throws ServletException {
    //     String dossier_controleur = this.getInitParameter("dossier_controleur");
    //     try{
    //         List<Class<?>> liste_classe = Utils.getClassWithAnnotation(dossier_controleur);

    //         for(Class<?> classe_name : liste_classe) {
    //             Method [] liste_methode = classe_name.getDeclaredMethods();
    //             for(Method methode : liste_methode){
    //                 if(methode.isAnnotationPresent(Get.class)){
    //                     String url = methode.getAnnotation(Get.class).value();
    //                     mappinge.put(url , new Mapping(classe_name.getName() , methode.getName()));
    //                 }
    //             }
    //         }

    //     } catch (Exception e){
    //             System.out.println(e.getMessage());
    //     }
    // }



     // if(mappinge.containsKey(urlAnoter)){
        //     out.print("</br>");
        //     Object resultat =  Utils.execute_fontion(mappinge.get(urlAnoter).getClasse_name() , mappinge.get(urlAnoter).getMethodName());
        //     if (Utils.testReturnType(resultat) == 1) {
        //         try {
        //             ModelView result_model_view = (ModelView) resultat;
        //             for (HashMap.Entry<String, Object> entry : result_model_view.getData().entrySet()) {
        //                 request.setAttribute(entry.getKey(), entry.getValue());
        //             }
        //             String url = result_model_view.getUrl();
        //             out.print(url);
        //             RequestDispatcher dispatcher = request.getRequestDispatcher("/web/"+url);
        //             dispatcher.forward(request, response);
        //         } catch (Exception e) {
        //             out.println(e.getMessage());
        //         }
        //     } else if(Utils.testReturnType(resultat)==2){
        //         out.print(resultat.toString());
        //     } else {
        //         out.println("type de return nom reconnu");
        //     }
        //     out.print("</br>");
        // } else {
        //     out.println("URL :" + contextPath + " est introuvable");
        // }