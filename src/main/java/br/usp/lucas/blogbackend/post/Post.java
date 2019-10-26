package br.usp.lucas.blogbackend.post;

import br.usp.lucas.blogbackend.user.User;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    //No banco, definimos que a postagem terá uma chave estrangeira para a tabela de usuários; no código, isto é correspondente
    //a definir um atributo do tipo User e um tipo de associação (no nosso caso, Many-to-one, indicando que N postagens
    //podem pertencer a 1 usuário de cada vez).
    @JoinColumn(nullable = false)
    //Esta anotação define que este atributo corresponde a uma coluna de chave estrangeira, e é obrigatório
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    //O fetch Lazy indica que, ao buscar a entidade do banco, o usuário deve ser ignorado; ele será buscado apenas se necessário.
    private User user;

    public Post() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id != null && id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", user=" + user +
                '}';
    }
}
