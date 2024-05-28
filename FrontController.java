package controlleur;
import fonction.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {

    boolean test = true;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        try {
            if(test){
                String dossier_cotroleur = this.getInitParameter("dossier_controleur");
                out.println("tongasoa");
                out.println(dossier_cotroleur);
                List<Class<?>> liste_classe = Utils.getClassWithAnnotation(dossier_cotroleur);

                for (Class<?> classe_name : liste_classe) {
                    out.println("</br>");
                    out.println(classe_name.getName());
                    out.println("</br>");
                }

                test = false;
            }
        } catch(Exception e){
            out.println(e.getMessage());
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
