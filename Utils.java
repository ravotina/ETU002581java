package fonction;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.lang.reflect.Method;



public class Utils {

  
    // Méthode pour obtenir la liste des classes dans un package
    public static List<Class<?>> getClassesInPackage(String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                // Si le package est un répertoire de fichiers
                File directory = new File(resource.getFile());
                classes.addAll(findClassesInDirectory(directory, packageName));
            } 
        }
        return classes;
    }

    // Méthode pour trouver les classes dans un répertoire de fichiers
    private static List<Class<?>> findClassesInDirectory(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                // Récursivement trouver les classes dans les sous-répertoires
                String subPackageName = packageName + "." + file.getName();
                classes.addAll(findClassesInDirectory(file, subPackageName));
            } else if (file.getName().endsWith(".class")) {
                // Charger la classe et l'ajouter à la liste
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                Class<?> myclass = Class.forName(className);
                classes.add(myclass);
            }
        }
        return classes;
    }

    public static List<Class<?>> getClassWithAnnotation(String packageName) throws IOException, ClassNotFoundException  {
        List<Class<?>> listClass = Utils.getClassesInPackage(packageName);
        List<Class<?>> annotatedClasses = new ArrayList<>();
        for (Class<?> myClass : listClass) {
            if (myClass.isAnnotationPresent(Controller.class)) {
                annotatedClasses.add(myClass);
            }
        }
        return annotatedClasses;
    }

    public static String execute_fontion(String nom_classe, String nom_Methode) {
        try {
            Class<?> classe = Class.forName(nom_classe);

            Object instance = classe.getDeclaredConstructor().newInstance();

            Method methode = classe.getMethod(nom_Methode);

            Object result = methode.invoke(instance);

            return result.toString();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return "Erreur : Classe non trouvée";
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return "Erreur : Méthode non trouvée";
        } catch (InstantiationException e) {
            e.printStackTrace();
            return "Erreur : Impossible de créer une instance de la classe";
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return "Erreur : Accès illégal";
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return "Erreur : Argument illégal";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'exécution de la méthode";
        }
    }
}    
   
