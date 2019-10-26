package br.usp.lucas.blogbackend.user;

import br.usp.lucas.blogbackend.user.dto.UserReadDto;
import br.usp.lucas.blogbackend.user.dto.UserWriteDto;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin //Esta anotação define que estes serviços podem ser chamados por um serviço externo (ex. outra aplicação)
@RestController //Esta, define que esta classe é um controlador REST e seus métodos devem ser associados a URLs HTTP
@RequestMapping("users")
//Define que todos os métodos devem estar dentro do endpoint "users" (na URL http://localhost:8080/users) e derivados
public class UserRestController {
    private final UserRepository repository;

    //Note que, apesar de estarmos definindo um construtor com parâmetros, nós não criaremos esta classe manualmente;
    //o Spring a detectará e verá que ela depende do repositório, e então fará os passos necessários para criar este objeto
    //corretamente. Essa detecção é chamada de Injeção de Dependência, muito comum em projetos web.
    public UserRestController(UserRepository repository) {
        this.repository = repository;
    }

    /*
    Este método será chamado quando o cliente mandar uma requisição HTTP GET apontando para a mesma URL do controlador
    (ex. http://localhost:8080/users), e irá produzir a resposta no formato JSON. Experimente abrir a URL no seu navegador!

    Se desejarmos, podemos passar alguns parâmetros na URL, como no exemplo: "http://localhost:8080/users?name=lu&email=hotmail".
    Isto permitirá que busquemos apenas os usuários em que esses atributos contenham os valores que passarmos.

    Ainda, podemos definir a ordenação dos usuários usando a classe Sort do Spring; para usá-la, passe na URL os campos
    que deseja usar para a ordenação e, opcionalmente, a direção dela (por padrão, ascendente). Por exemplo:
    "...?sort=name&sort=username,desc" usará o nome na ordem ascendente e, se dois ou mais forem iguais, usará o username
    descendente.
    */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserReadDto> getAll(UserFilter filter, Sort sort) {
        //Primeiramente, vamos definir uma "entidade de exemplo", sem nenhum atributo previamente definido
        User exampleUser = new User();

        //Agora, vamos definir os campos passados no filtro. Se nenhum campo foi passado, a busca retornará todos os usuários;
        //senão, apenas os usuários correspondentes ao exemplo serão retornados. Note que definir o ID não é necessário,
        //pois já existe o web service getById; fica a seu critério definir se o utilizará no filtro ou não.
        exampleUser.setId(filter.getId());
        exampleUser.setName(filter.getName());
        exampleUser.setUsername(filter.getUsername());
        exampleUser.setEmail(filter.getEmail());

        //Agora, vamos definir um matcher. Esse objeto definirá como os atributos devem ser usados no filtro; no exemplo,
        //ele define que TODOS os filtros devem ser obedecidos na busca e basta que os campos de texto contenham apenas
        //parte do valor passado no filtro (e não seja uma busca por EXATAMENTE aquele texto), ignorando letras maiúsculas ou minúsculas.
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        //Agora, crie um objeto Example para ser usado no repositório
        Example<User> example = Example.of(exampleUser, matcher);
        List<User> users = repository.findAll(example, sort);

        List<UserReadDto> dtos = new ArrayList<>(users.size());
        for (User user : users) {
            dtos.add(convertToReadDto(user));
        }

        return dtos;
    }

    //Quando vamos retornar dados para uma API, é importante criar classes específicas para tal e não expor nossas entidades
    //diretamente, já que qualquer alteração nelas acabaria afetando também os web services e isso também poderia expor
    //dados sensitivos. Essas classes são chamadas de DTO (Data Transfer Objects), e são úteis para expor apenas os dados
    //desejados e nos formatos corretos, além de algumas outras vantagens.
    private UserReadDto convertToReadDto(User entity) {
        UserReadDto dto = new UserReadDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());

        return dto;
    }

    //Este método será chamado quando o cliente mandar uma requisição HTTP GET apontando para a URL do controlador seguida
    //por uma barra e o ID do usuário (ex. http://localhost:8080/users/10), e irá produzir a resposta no formato JSON.
    //Caso o usuário exista, os dados dele serão retornados normalmente; caso contrário, devemos retornar uma status HTTP
    //404 (Não Encontrado); a classe ResponseEntity permite que construamos esses dois tipos de resposta diferentes.
    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserReadDto> getById(@PathVariable Integer id) {
        //O repositório retorna uma instância de Optional, indicando que o usuário pode ou não ter sido encontrado.
        Optional<User> optional = repository.findById(id);

        //O método map permite que convertamos nossa entidade no DTO sem que seja necessário sabermos se ela foi encontrada;
        //Se nosso optional estiver vazio, o objeto abaixo continuará vazio; caso contrário, ele conterá o DTO convertido.
        Optional<UserReadDto> dtoOptional = optional.map(this::convertToReadDto);
        //Passando um optional para o método abaixo, ele checará corretamente se ele possui um valor (retornando nosso
        //usuário juntamente com o status HTTP 200 OK) ou não (retornando HTTP 404 Não Encontrado).
        return ResponseEntity.of(dtoOptional);
    }

    //Para chamar o método abaixo, o cliente deve enviar uma requisição HTTP POST para a mesma URL do controlador
    //(http://localhost:8080/users) - a mesma do "getAll" - mandando os dados no formato de um objeto JSON. Retornaremos
    //uma ResponseEntity com tipo Void, pois é necessário definirmos um status HTTP de sucesso, mas não é necessário
    //mandar um conteúdo de resposta.
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(@RequestBody UserWriteDto dto) {
        //Vamos criar um usuário novo e definir todos os atributos, exceto o ID; desta forma, ele será criado no banco e
        //terá um ID gerado automaticamente.
        User user = new User();
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        repository.save(user); //Este método ficará responsável por inserir o registro no banco e definir o ID no próprio objeto user

        //Quando uma entidade é criada, é uma boa prática retornar o status HTTP 201 Criado, passando a URL do endpoint
        //com o ID dela; desta forma, o cliente pode entender algo como "para ver as informações salvas, acesse esta URL".
        //Essa URL fica em um cabeçalho de resposta chamado "Location".
        return ResponseEntity.created(URI.create("/users/" + user.getId())).build();
    }

    //Para o método abaixo, o cliente deve enviar uma requisição HTTP PUT para a URL do controlador seguida por uma barra
    //e o ID do usuário (ex. http://localhost:8080/users/10) - a mesma do getById - mandando os dados no formato de
    //um objeto JSON. A resposta pode ser tanto HTTP 204 (Sem Conteúdo - isto é, o usuário foi atualizado, mas uma resposta
    //não é necessária) caso o usuário exista, ou HTTP 404 (Não Encontrado) caso tenha sido passado um ID que não exista.
    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody UserWriteDto dto) {
        //Primeiramente, devemos garantir que o usuário já exista no banco
        Optional<User> optional = repository.findById(id);
        if (optional.isPresent()) {
            //Se existir, vamos atualizar as informações dele e atualizá-lo no banco
            User existingUser = optional.get();
            existingUser.setName(dto.getName());
            existingUser.setUsername(dto.getUsername());
            existingUser.setEmail(dto.getEmail());

            repository.save(existingUser); //Como o ID do usuário já está definido neste caso, este método fará um Update no banco

            return ResponseEntity.noContent().build(); //Retorne uma mensagem vazia de sucesso
        } else {
            return ResponseEntity.notFound().build(); //Se não existir, retorne um erro 404 Não Encontrado
        }
    }

    //Para este método, o cliente deve enviar uma requisição HTTP DELETE para a URL do controlador + ID - a mesma do
    //getById e do update. Não é necessário enviar dados neste caso. A resposta pode ser HTTP 204 Sem Conteúdo caso o
    //usuário exista e tenha sido corretamente removido, ou HTTP 404 Não Encontrado caso não exista.
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Optional<User> optional = repository.findById(id); //Cheque se o usuário existe
        if (optional.isPresent()) {
            //Se existir, vamos removê-lo do banco
            User user = optional.get();
            repository.delete(user);

            //Retorne uma mensagem vazia de sucesso
            return ResponseEntity.noContent().build();
        } else {
            //Se não existir, retorne um erro 404 Não Encontrado
            return ResponseEntity.notFound().build();
        }
    }
}
