package br.usp.lucas.blogbackend.user;

import javax.persistence.*;
import java.util.Objects;

//Para fazer o mapeamento entre o banco de dados e nossas classes, utilizaremos as anotações do pacote javax.persistence

@Entity //Define que esta classe é uma entidade (isto é, um tipo de dados com equivalente no banco)
public class User {
    @Id //Este atributo é o identificador da entidade (a chave primária do banco)
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Caso não especificado, o ID deve ser gerado automaticamente
    private Integer id; //O tipo deve ser mudado para Integer, pois ele pode ser nulo caso o usuário ainda não exista no banco

    @Column(nullable = false, length = 100) //Sobrescreve as configurações padrão da coluna para que ela seja obrigatória e tenha tamanho máximo de 100 caracteres
    private String name;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(length = 100) //A coluna é opcional ("nullable" tem valor false por padrão) e tem tamanho máximo de 100 caracteres
    private String email;

    //Um construtor vazio é obrigatório, mesmo que não o utilizemos
    public User() {
    }

    public User(Integer id, String name, String username, String email) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    //Para classes de entidade, é essencial que os métodos equals e hashCode sejam definidos e baseados no ID; desta forma,
    //o gerenciador do banco consegue saber quando uma entidade deve ser atualizada, mesmo quando seus campos foram alterados.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;

        //É importante que o ID só seja checado caso não seja nulo; isso evita que dois objetos diferentes que acabaram
        //de ser criados sejam considerados iguais.
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
