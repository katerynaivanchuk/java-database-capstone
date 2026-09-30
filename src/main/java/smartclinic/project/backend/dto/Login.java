package smartclinic.project.backend.dto; // Змініть пакет відповідно до вашої структури проєкту

public class Login {

    private String identifier;
    private String password;

    // Порожній конструктор (необхідний для десеріалізації JSON у Spring)
    public Login() {
    }

    // Конструктор з параметрами
    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // Getters and Setters
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}