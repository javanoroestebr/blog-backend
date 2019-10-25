package br.usp.lucas.blogbackend.user.dto;

//Note que esta classe não possui o ID; fizemos desta forma pois esta classe será usada ou para criação (quando o ID
//realmente não existe) ou para atualização (quando o ID já será passado na URL).
public class UserWriteDto {
    private String name;
    private String username;
    private String email;

    public UserWriteDto() {
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
