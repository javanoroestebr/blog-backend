package br.usp.lucas.blogbackend.user;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin //Esta anotação define que estes serviços podem ser chamados por um serviço externo (ex. outra aplicação)
@RestController //Esta, define que esta classe é um controlador REST e seus métodos devem ser associados a URLs HTTP
@RequestMapping("users") //Define que todos os métodos devem estar dentro do endpoint "users" (na URL http://localhost:8080/users) e derivados
public class UserRestController {
    //Este método será chamado quando o cliente mandar uma requisição HTTP GET apontando para a mesma URL do controlador
    //(ex. http://localhost:8080/users), e irá produzir a resposta no formato JSON. Experimente abrir a URL no seu navegador!
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAll() {
        List<User> users = new ArrayList<>();

        //Não estamos conectados com o banco de dados ainda, então vamos adicionar alguns objetos criados na hora para demonstração
        users.add(new User(1, "Lucas Souza", "lucas", "lucas_jose_92@hotmail.com"));
        users.add(new User(2, "Fernando Silva", "fernando", null));
        users.add(new User(3, "Carlos Gonçalves", "carlos", "carlosfgo@gmail.com"));

        //Note que estamos apenas retornando o objeto Java diretamente, mas não o estamos convertendo para JSON; é o Spring
        //que fará essa conversão, baseado nos atributos e métodos da classe User.
        return users;
    }
}
