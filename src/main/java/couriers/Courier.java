package couriers;

public class Courier {
    private String login;
    private String password;
    private String firstName;

    // Конструктор
    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    public Courier (String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Геттер для поля login
    public String getLogin() {
        return login;
    }

    // Сеттер для поля login
    public void setLogin(String login) {
        this.login = login;
    }

    // Геттер для поля password
    public String getPassword() {
        return password;
    }

    // Сеттер для поля password
    public void setPassword(String password) {
        this.password = password;
    }

    // Геттер для поля firstName
    public String getFirstName() {
        return firstName;
    }

    // Сеттер для поля firstName
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}