package fonction;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

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
                Class<?> myClass = Class.forName(className);
                classes.add(myClass);
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

    public static Object executeFontion(Map<String, String> paramMap, String nomClasse, String nomMethode) {
        try {
            Class<?> classe = Class.forName(nomClasse);
            Object instance = classe.getDeclaredConstructor().newInstance();
            Method[] methods = classe.getDeclaredMethods();
            Method targetMethod = null;

            for (Method method : methods) {
                if (method.getName().equals(nomMethode)) {
                    targetMethod = method;
                    break;
                }
            }

            if (targetMethod == null) {
                throw new NoSuchMethodException("Méthode non trouvée : " + nomMethode);
            }

            Parameter[] parameters = targetMethod.getParameters();
            Object[] parameterValues = new Object[parameters.length];

            // Utilisation de Paranamer pour obtenir les noms des paramètres si nécessaire
            Paranamer paranamer = new AdaptiveParanamer();

            System.out.println("Methode:");
            System.out.println(targetMethod);
            String[] parameterNames = paranamer.lookupParameterNames(targetMethod);

            for (int i = 0; i < parameters.length; i++) {
                Param paramAnnotation = parameters[i].getAnnotation(Param.class);
                if (paramAnnotation != null) {
                    String paramName = paramAnnotation.name();
                    String paramValue = paramMap.get(paramName);
                    parameterValues[i] = convertParameter(paramValue, parameters[i].getType());
                } else {
                    String paramValue = paramMap.get(parameterNames[i]);
                    parameterValues[i] = convertParameter(paramValue, parameters[i].getType());

                }
            }

            return targetMethod.invoke(instance, parameterValues);

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
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'exécution de la méthode";
        }
    }

    private static Object convertParameter(String value, Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else {
            return value;
        }
    }

    public static int testReturnType(Object obj) {
        if (obj instanceof ModelView) {
            return 1;
        } else if (obj instanceof String) {
            return 2;
        } else {
            return 3;
        }
    }
}
