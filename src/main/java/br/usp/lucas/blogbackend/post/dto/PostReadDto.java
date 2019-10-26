package br.usp.lucas.blogbackend.post.dto;

import br.usp.lucas.blogbackend.user.dto.UserReadDto;

public class PostReadDto {
    private int id;
    private String title;
    private String body;
    private UserReadDto user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UserReadDto getUser() {
        return user;
    }

    public void setUser(UserReadDto user) {
        this.user = user;
    }
}
