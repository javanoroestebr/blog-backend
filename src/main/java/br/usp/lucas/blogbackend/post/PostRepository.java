package br.usp.lucas.blogbackend.post;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    /*
    Nós definimos que o usuário não deve ser buscado do banco a não ser que seja necessário; neste caso, ele será, mas
    o método que o Spring gera por padrão não o buscará, o que pode causar erros ou tornar a consulta ineficiente. Para
    resolver isso, utilizamos a anotação @EntityGraph, que pede ao banco para fazer um join SQL com a tabela de usuários.
     */
    @Override
    @EntityGraph(attributePaths = "user")
    <S extends Post> List<S> findAll(Example<S> example, Sort sort);

    /*
    Este método não faz parte da interface JpaRepository; ele foi definido por nós mesmos. Normalmente, o Spring consegue
    identificar que tipo de consulta queremos fazer pelo próprio nome do método; quando a consulta é um pouco mais complexa,
    podemos ajudá-lo com a anotação @Query utilizando uma linguagem de consulta parecida com SQL, mas com nomes e atributos
    mais parecidos com Java.

    A expressão "join fetch" é semelhante ao EntityGraph: ela pede que o usuário associado à postagem seja buscado do banco
    através de um join SQL.
     */
    @Query("select p from Post p join fetch p.user where p.id = :id")
    Optional<Post> findByIdWithUser(Integer id);
}
