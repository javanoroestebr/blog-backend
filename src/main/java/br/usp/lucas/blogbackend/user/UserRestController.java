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
    private final UserRepository repository;

    //Note que, apesar de estarmos definindo um construtor com parâmetros, nós não criaremos esta classe manualmente;
    //o Spring a detectará e verá que ela depende do repositório, e então fará os passos necessários para criar este objeto
    //corretamente. Essa detecção é chamada de Injeção de Dependência, muito comum em projetos web.
    public UserRestController(UserRepository repository) {
        this.repository = repository;
    }

    //Este método será chamado quando o cliente mandar uma requisição HTTP GET apontando para a mesma URL do controlador
    //(ex. http://localhost:8080/users), e irá produzir a resposta no formato JSON. Experimente abrir a URL no seu navegador!
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> getAll() {
        //Note que, para nos conectarmos ao banco e buscarmos a lista de usuários, basta chamarmos o método findAll.
        //A implementação do repositório (que será feita automaticamente pelo Spring) se encarregará dos detalhes e do SQL.
        return repository.findAll();
    }
}
