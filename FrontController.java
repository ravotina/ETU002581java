
package controlleur;

import fonction.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.google.gson.*;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

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
            super(message);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {

            String chemin_url = request.getRequestURL().toString();
            String contextPath = request.getContextPath(); // Ex: /YourAppName
            String queryString = request.getQueryString(); // Ex: ville=105
            String[] result_fin = chemin_url.split(contextPath);
            String urlAnoter = result_fin[1].replaceFirst("^/", "");

            // Enlever le texte après "?" s'il existe
            String baseUrl = urlAnoter.split("\\?")[0];

            // Déclarer une Map pour les paramètres
            Map<String, String> paramMap = new HashMap<>();

            if ("GET".equalsIgnoreCase(request.getMethod())) {
                // Extraire les paramètres de l'URL en tant que Map pour GET
                if (queryString != null) {
                    String[] params = queryString.split("&");
                    for (String param : params) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2) {
                            paramMap.put(keyValue[0], keyValue[1]);
                        }
                    }
                }
            } else if ("POST".equalsIgnoreCase(request.getMethod())) {
                // Extraire les paramètres de la requête POST en tant que Map
                request.getParameterMap().forEach((key, values) -> paramMap.put(key, values[0]));
            }

            try {
                out.print("</br>");
                Object resultat = Utils.executeFontion2(paramMap, mappinge.get(urlAnoter).getClasse_name(), mappinge.get(urlAnoter).getMethodName() , request);
                if (Utils.testReturnType(resultat) == 1) {
                    try {
                        ModelView result_model_view = (ModelView) resultat;
                        for (HashMap.Entry<String, Object> entry : result_model_view.getData().entrySet()) {
                            request.setAttribute(entry.getKey(), entry.getValue());
                            if(Utils.convertirEnJson(mappinge.get(urlAnoter).getClasse_name() , mappinge.get(urlAnoter).getMethodName() , entry)!="tsia"){
                                out.print(Utils.convertirEnJson(mappinge.get(urlAnoter).getClasse_name() , mappinge.get(urlAnoter).getMethodName() , entry));
                                break;
                            }
                        }
                        //String url = result_model_view.getUrl();
                        //out.print(url);
                        //out.print("==========resulat model view==========");
                        RequestDispatcher dispatcher = request.getRequestDispatcher("/web/" + url);
                        dispatcher.forward(request, response);
                    } catch (Exception e) {
                        log("Error processing ModelView", e);
                        out.println(e.getMessage());
                    }
                } else if (Utils.testReturnType(resultat) == 2) {
                    //out.println(resultat.toString());
                    out.print(Utils.convertirEnJson(mappinge.get(urlAnoter).getClasse_name() , mappinge.get(urlAnoter).getMethodName() , resultat));
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
                System.out.println("UrlAlreadyExistsException erreur");
                e.printStackTrace(System.err);
            } catch (Exception e) {
                log("Error executing function", e);
                out.println(e.getMessage());
                System.err.println(e.getMessage());
                out.println("URL non reconnue: " + chemin_url);
                System.out.println("Exception erreur");
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
                            // Afficher en terminal
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


