package fonction;

public class Mapping {
    
    private String classe_name;
    private String methodName;
    private String verbe;

    // Constructeur
    public Mapping(String classe_name, String methodName , String verbe) {
        this.classe_name = classe_name;
        this.methodName = methodName;
        this.verbe = verbe;
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

    public void setVerbe(String verbe){
        this.verbe = verbe;
    }

    public String getVerbe(){
        return this.verbe;
    }
}

