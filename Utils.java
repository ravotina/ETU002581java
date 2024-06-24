package fonction;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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




    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
        List<Object> parameterValues = new ArrayList<>();

        for (Parameter parameter : parameters) {
            Class<?> paramType = parameter.getType();
            Object paramInstance = paramType.getDeclaredConstructor().newInstance();
            Field[] fields = paramType.getDeclaredFields();
            for (Field field : fields) {
                String paramName = field.getName();
                if (paramMap.containsKey(paramName)) {
                    Method setMethod = findSetterMethod(paramType, field);

                    if (setMethod != null) {
                        setMethod.invoke(paramInstance, convertParameterValue(paramMap.get(paramName), field.getType()));

                        System.out.println("==========================");
                        System.out.println(setMethod.getName());
                        System.out.println("==========================");
                    }
                }
            }
            parameterValues.add(paramInstance);
        }

        // Affichage des valeurs des paramètres pour le débogage
        System.out.println("Paramètres pour la méthode : " + targetMethod.getName());
        for (Object paramValue : parameterValues) {
            System.out.println(paramValue);
        }

        Object result = targetMethod.invoke(instance, parameterValues.toArray());
        return result;

    } catch (Exception e) {
        e.printStackTrace();
        return "Erreur lors de l'exécution de la méthode : " + e.getMessage();
    }
}

private static Method findSetterMethod(Class<?> clazz, Field field) {
    String setterName = "set" + capitalize(field.getName());
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
        if (method.getName().equals(setterName) && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(field.getType())) {
            return method;
        }
    }
    return null;
}

private static String capitalize(String str) {
    if (str == null || str.isEmpty()) {
        return str;
    }
    return Character.toUpperCase(str.charAt(0)) + str.substring(1);
}

private static Object convertParameterValue(String value, Class<?> type) {
    if (type == String.class) {
        return value;
    } else if (type == Integer.class || type == int.class) {
        return Integer.parseInt(value);
    } else if (type == Double.class || type == double.class) {
        return Double.parseDouble(value);
    } else if (type == Boolean.class || type == boolean.class) {
        return Boolean.parseBoolean(value);
    } else {
        throw new IllegalArgumentException("Type de paramètre non supporté : " + type.getName());
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
