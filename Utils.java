package fonction;

import java.io.File;
import java.io.IOException;
//import java.lang.ModuleLayer.Controller;
import java.lang.reflect.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

import fonction.MyFile;
import fonction.MySession;

import fonction.DetailedValidationException;
import fonction.Controller;

import java.nio.file.*;
import jakarta.servlet.http.Part;

import java.util.Base64;


import jakarta.servlet.http.HttpServlet;




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

            //Field[] fields = classe.getDeclaredFields();



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
                //if()
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




    public static Object executeFontion2(Map<String, String> paramMap, String nomClasse, String nomMethode , HttpServletRequest req) throws Exception {

        try {
            // Utilisation de Part pour gérer le téléchargement
           

            Class<?> classe = Class.forName(nomClasse);

            Object instance = classe.getDeclaredConstructor().newInstance();

            Field[] fields_controleur = classe.getDeclaredFields();

                for (Field field : fields_controleur) {
                    if (field.getType().equals(MySession.class)) {
                        field.setAccessible(true); // Permet d'accéder aux champs privés
                        field.set(instance, new MySession(req.getSession(true)));
                    }
                }


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

            // Utilisation de Paranamer pour obtenir les noms des paramètres si nécessaire
            Paranamer paranamer = new AdaptiveParanamer();

            System.out.println("Methode:");
            System.out.println(targetMethod);
            String[] parameterNames = paranamer.lookupParameterNames(targetMethod);


            Parameter[] parameters = targetMethod.getParameters();
            List<Object> parameterValues = new ArrayList<>();

            for (int i = 0; i < parameters.length; i++) {
                Class<?> paramType = parameters[i].getType();
                if(test_type(paramType)==0){

                    Param paramAnnotation = parameters[i].getAnnotation(Param.class);

                    if (paramAnnotation != null) {
                        String paramNameanotation = paramAnnotation.name();
                        
                        Object paramInstance = paramType.getDeclaredConstructor().newInstance();
                        Field[] fields = paramType.getDeclaredFields();
                        for (Field field : fields) {

                            if(field.getType()==MyFile.class){
                                //System.out.println("MyFile.class = "+paramMap.get(paramNameanotation+"."+paramName));
                                String paramName = field.getName();
                                Part filePart = req.getPart(paramNameanotation+"."+paramName);

                                if(filePart!=null){
                                    System.out.println(filePart);
                                    MyFile myFile = new MyFile();
                                    myFile.setFileName(filePart.getSubmittedFileName());
                                    myFile.setFileContent(filePart.getInputStream().readAllBytes());
                                    Method setMethod = findSetterMethod(paramType, field);
                                    if (setMethod != null) {
                                        System.out.println("ato zao atao zao ato zao atao zao  ato zao atao zao ato zao atao zao ");
                                        setMethod.invoke(paramInstance, myFile);
                                    }
                                }
                                
                            }
                            else
                            {
                                String paramName = field.getName();
                                if (paramMap.containsKey(paramNameanotation+"."+paramName)) {
                                    System.out.println();
                                    Method setMethod = findSetterMethod(paramType, field);
                                    
                                    if (setMethod != null) {
    
                                        setMethod.invoke(paramInstance, convertParameterValue(paramMap.get(paramNameanotation+"."+paramName), field.getType()));
                                        
                                        System.out.println("==========================");
                                        System.out.println(setMethod.getName());
                                        System.out.println("==========================");
                                    }
                                }
                            }
                        }
                        parameterValues.add(paramInstance);
                        
                    } else {
                        //System.out.println("====================================");
                        if(paramType.equals(MySession.class)){
                            parameterValues.add(new MySession(req.getSession(true)));
                        } else {
                            throw new Exception("ETU002581 none anotation");
                        }

                        //System.out.println("====================================");

                        // String paramValue = parameterNames[i];
                        // //System.out.println("parama name value 1:"+paramValue+".");
                        // Object paramInstance = paramType.getDeclaredConstructor().newInstance();
                        // Field[] fields = paramType.getDeclaredFields();
                        // for (Field field : fields) {
                        //     String paramName = field.getName();
                        //     //System.out.println("parama name :"+paramValue+"."+paramName);
                        //     if (paramMap.containsKey(paramValue+"."+paramName)) {
                        //         Method setMethod = findSetterMethod(paramType, field);

                        //         if (setMethod != null) {
                        //             setMethod.invoke(paramInstance, convertParameterValue(paramMap.get(paramValue+"."+paramName), field.getType()));

                        //             System.out.println("==========================");
                        //             System.out.println(setMethod.getName());
                        //             System.out.println("==========================");
                        //         }
                        //     }
                        // }
                        //parameterValues.add(paramInstance);
                        //throw new Exception("ETU002581 none anotation");
                    }

                } else {
                    Param paramAnnotation = parameters[i].getAnnotation(Param.class);
                    if (paramAnnotation != null) {
                        String paramName = paramAnnotation.name();
                        String paramValue = paramMap.get(paramName);
                        System.out.println("nom param :"+paramName);
                        System.out.println("nom param :"+paramValue);
                        if(parameters[i].getType() == MyFile.class){

                            Part filePart = req.getPart(paramName); // "sary" doit correspondre au nom du champ dans le formulaire
                            System.out.println("=================filePart=====================");
                            System.out.println(filePart);
                            System.out.println("======================================");

                            if(filePart != null){
                                System.out.println("ato m execution ilay file ilay fila ilay file 2222222222221111111111111111112222222222222");

                                MyFile myFile = new MyFile();
                                myFile.setFileName(filePart.getSubmittedFileName());
                                myFile.setFileContent(filePart.getInputStream().readAllBytes());

                                parameterValues.add(myFile);
                            }
                        } else {
                            parameterValues.add(convertParameterValue(paramValue, parameters[i].getType()));
                        }
                        
                        
                    } else {
                        //String paramValue = paramMap.get(parameterNames[i]);
                        //parameterValues.add(convertParameterValue(paramValue, parameters[i].getType()));
                        throw new Exception("ETU002581 none anotation");

                    }
                }
            } 

            // Affichage des valeurs des paramètres pour le débogage
            System.out.println("Paramètres pour la méthode : " + targetMethod.getName());
            for (Object paramValue : parameterValues) {
                System.out.println(paramValue);
                //validation_donner_object(paramValue , req);
                validation_donner_object_recuperation_eurreur(paramValue , req);
            }

            
            Object result = targetMethod.invoke(instance, parameterValues.toArray());
           
            return result;

        }catch(DetailedValidationException e){
            throw new DetailedValidationException("signe", "eurror", "eurror Formulaire");
        }
        
        catch (Exception e) {
            // e.printStackTrace();
            throw new Exception("Erreur lors de l'exécution de la méthode : " + e.getMessage());
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
    } else if(type == MyFile.class){
        try {
             // Assuming the input string is a file path
             File file = new File(value);
             byte[] fileContent = Files.readAllBytes(file.toPath());
             return new MyFile(fileContent, file.getName());
         } catch (IOException e) {
             throw new IllegalArgumentException("Impossible de convertir en MyFile. Fichier introuvable ou non lisible : " + value, e);
         }
    
    } else {
        throw new IllegalArgumentException("Type de paramètre non supporté : " + type.getName());
        //return null;
    }
}

private static int test_type( Class<?> type) {
    if (type == String.class) {
        return 1;
    } else if (type == Integer.class || type == int.class) {
        return 1;
    } else if (type == Double.class || type == double.class) {
        return 1;
    } else if (type == Boolean.class || type == boolean.class) {
        return 1;  
    } else if (type == MyFile.class) {
        return 1;
    }else {
        return 0;
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


    public static String convertirEnJson(String nomClasse, String nomMethode ,  Object objet ) {

        String valeur = null;
        try {

            // Création d'un objet Gson
            Class<?> classe = Class.forName(nomClasse);

            Method[] methods = classe.getDeclaredMethods();
                Method targetMethod = null;

                for (Method method : methods) {
                    if (method.getName().equals(nomMethode)) {
                        if(method.isAnnotationPresent(Api.class)){
                            Gson gson = new Gson();
            
                            // Transformation de l'objet en JSON
                            valeur = gson.toJson(objet);
                        } else {
                            valeur = "tsia";
                        }
                    }
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return valeur;
    }




    public static String pageeurreur(String nomClasse, String nomMethode ) {

        String valeur = null;
        try {

            // Création d'un objet Gson
            Class<?> classe = Class.forName(nomClasse);

            Method[] methods = classe.getDeclaredMethods();
                Method targetMethod = null;

                for (Method method : methods) {
                    if (method.getName().equals(nomMethode)) {
                        if(method.isAnnotationPresent(PageEurrer.class)){
                            PageEurrer pageurteurrer =  method.getAnnotation(PageEurrer.class);
                            valeur = pageurteurrer.value();
                        } else {
                            valeur =  null;
                        }
                    }
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return valeur;
    }


    public static void validation_donner_object(Object obj , HttpServletRequest req) throws DetailedValidationException {
        Class<?> objClass = obj.getClass();
        for (Field field : objClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                // Validation pour @Required
                if (field.isAnnotationPresent(Required.class)) {
                    if (value == null || value.toString().isEmpty()) {
                        Required required = field.getAnnotation(Required.class);
                        throw new DetailedValidationException(field.getName(), value, required.message());
                    }
                }
                // Validation pour @Numerique
                if (field.isAnnotationPresent(Numerique.class)) {
                    if (value instanceof Number) {
                        Numerique numerique = field.getAnnotation(Numerique.class);
                        double numericValue = ((Number) value).doubleValue();
                        if (numericValue < numerique.min() || numericValue > numerique.max()) {
                            throw new DetailedValidationException(
                                field.getName(),
                                value,
                                numerique.message().replace("{min}", String.valueOf(numerique.min()))
                                                 .replace("{max}", String.valueOf(numerique.max())));
                        }
                    } else {
                        throw new DetailedValidationException(field.getName(), value, "Field is not numeric.");
                    }
                }
            } catch (IllegalAccessException e) {
                throw new DetailedValidationException(field.getName(), "N/A", "Could not access field.");
            }
        }
    }


    public static void validation_donner_object_recuperation_eurreur(Object obj, HttpServletRequest req) throws DetailedValidationException {
        Class<?> objClass = obj.getClass();
        boolean hasErrors = false;
    
        for (Field field : objClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
    
                // Validation pour @Required
                if (field.isAnnotationPresent(Required.class)) {
                    if (value == null || value.toString().isEmpty()) {
                        Required required = field.getAnnotation(Required.class);
                        String errorMessage = required.message();
                        req.setAttribute("error_" + field.getName(), errorMessage);
                        req.setAttribute("valiny_" + field.getName(), "null");
                        hasErrors = true;
                    }
                }
    
                // Validation pour @Numerique
                if (field.isAnnotationPresent(Numerique.class)) {
                    if (value instanceof Number) {
                        Numerique numerique = field.getAnnotation(Numerique.class);
                        double numericValue = ((Number) value).doubleValue();
                        if (numericValue < numerique.min() || numericValue > numerique.max()) {
                            String errorMessage = numerique.message()
                                    .replace("{min}", String.valueOf(numerique.min()))
                                    .replace("{max}", String.valueOf(numerique.max()));
                            req.setAttribute("error_" + field.getName(), errorMessage);
                            req.setAttribute("valiny_" + field.getName(), value.toString());
                            hasErrors = true;
                        }
                    } else {
                        req.setAttribute("error_" + field.getName(), "Field is not numeric.");
                        req.setAttribute("valiny_" + field.getName(),  value.toString());
                        hasErrors = true;
                    }
                }
            } catch (IllegalAccessException e) {
                req.setAttribute("error_" + field.getName(), "Could not access field.");
                hasErrors = true;
            }
        }
    
        // Si des erreurs sont détectées, lever une exception avec un message général
        if (hasErrors) {
            throw new DetailedValidationException("signe", "eurror", "eurror Formulaire");
        }
    }
    
}
