package fonction;

public class MyFile {
    byte[] fileContent; // Using 'byte' array to store file content
    String fileName; // Use String for file names


    // Constructeur par défaut
    public MyFile() {
        // Initialisation des attributs si nécessaire
    }
    
    // Constructor
    public MyFile(byte[] fileContent, String fileName) {
        this.fileContent = fileContent;
        this.fileName = fileName;
    }

    // Getters
    public byte[] getFileContent() {
        return fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    // Setters
    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
