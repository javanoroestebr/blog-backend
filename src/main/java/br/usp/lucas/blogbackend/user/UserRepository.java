package br.usp.lucas.blogbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;

//Para nos conectarmos com o banco e manipularmos a tabela de usuários, basta que declaremos esta interface estendendo
//a JpaRepository do Spring; não precisamos nem ao menos implementá-la! Nos parâmetros de tipo, o primeiro é a classe
//da nossa entidade (User) e o segundo é a classe usada no ID (Integer).
public interface UserRepository extends JpaRepository<User, Integer> {
}
