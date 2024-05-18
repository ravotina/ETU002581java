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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String dossier_cotroleur = this.getInitParameter("dossier_controleur");

            out.println("tongasoa");
            out.println(dossier_cotroleur);
            List<Class<?>> liste_classe = Utils.getClassesInPackage(dossier_cotroleur);

            out.println(liste_classe);
            for (Class<?> classe_name : liste_classe) {
                out.println(classe_name.getName());
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
