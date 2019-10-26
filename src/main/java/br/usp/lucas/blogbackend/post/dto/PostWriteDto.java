package br.usp.lucas.blogbackend.post.dto;

public class PostWriteDto {
    private String title;
    private String body;
    private int userId; //Note que não usaremos o usuário inteiro neste caso; apenas uma referência ao ID dele basta (e deve ser usada)

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
