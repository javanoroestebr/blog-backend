package br.usp.lucas.blogbackend.post;

import br.usp.lucas.blogbackend.user.UserFilter;

public class PostFilter {
    private String title;
    private String body;
    private UserFilter user;

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

    public UserFilter getUser() {
        return user;
    }

    public void setUser(UserFilter user) {
        this.user = user;
    }
}
