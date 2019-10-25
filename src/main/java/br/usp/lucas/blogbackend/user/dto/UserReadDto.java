package br.usp.lucas.blogbackend.user.dto;

public class UserReadDto {
    private int id; //Como este é o modelo de leitura, podemos usar o int primitivo, pois ele sempre existirá
    private String name;
    private String username;
    private String email;

    public UserReadDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
