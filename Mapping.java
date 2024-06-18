package fonction;

public class Mapping {
    
    private String classe_name;
    private String methodName;

    // Constructeur
    public Mapping(String classe_name, String methodName) {
        this.classe_name = classe_name;
        this.methodName = methodName;
    }

    // Méthode getter pour classe_name
    public String getClasse_name() {
        return classe_name;
    }

    // Méthode setter pour classe_name
    public void setClasse_name(String classe_name) {
        this.classe_name = classe_name;
    }

    // Méthode getter pour methodName
    public String getMethodName() {
        return methodName;
    }
// 
    // Méthode setter pour methodName
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}

