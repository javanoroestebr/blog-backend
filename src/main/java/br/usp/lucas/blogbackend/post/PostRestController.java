package br.usp.lucas.blogbackend.post;

import br.usp.lucas.blogbackend.post.dto.PostReadDto;
import br.usp.lucas.blogbackend.post.dto.PostWriteDto;
import br.usp.lucas.blogbackend.user.User;
import br.usp.lucas.blogbackend.user.UserFilter;
import br.usp.lucas.blogbackend.user.UserRepository;
import br.usp.lucas.blogbackend.user.dto.UserReadDto;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("posts")
public class PostRestController {
    private final PostRepository repository;
    private final UserRepository userRepository;

    public PostRestController(PostRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    /*
    Podemos usar um filtro aqui de forma semelhante ao que fizemos na classe UserRestController, e podemos inclusive usar
    a mesma classe para o filtro de usuário. Para referenciar os atributos do usuário no filtro, utilize algo como "user.id"
    ou "user.name". Um exemplo para o filtro de postagens: "http://localhost:8080/posts?title=tut&user.name=lu" buscará
    postagens que contenham "tut" no título e cujo autor contenha "lu" no nome.

    É possível ainda passar os atributos do usuário para ordenação: basta passá-los como "?sort=user.name", por exemplo!
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PostReadDto> getAll(PostFilter filter, Sort sort) {
        Post examplePost = new Post();
        examplePost.setTitle(filter.getTitle());
        examplePost.setBody(filter.getBody());

        //Se nenhum atributo for passado no filtro de usuário, este filtro estará nulo, portanto, devemos checar isto antes.
        UserFilter user = filter.getUser();
        if (user != null) {
            User exampleUser = new User();
            exampleUser.setId(user.getId());
            exampleUser.setName(user.getName());
            exampleUser.setUsername(user.getUsername());
            exampleUser.setEmail(user.getEmail());

            examplePost.setUser(exampleUser);
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        Example<Post> example = Example.of(examplePost, matcher);
        List<Post> posts = repository.findAll(example, sort);

        List<PostReadDto> dtos = new ArrayList<>(posts.size());
        for (Post post : posts) {
            dtos.add(convertToReadDto(post));
        }

        return dtos;
    }

    private PostReadDto convertToReadDto(Post entity) {
        PostReadDto dto = new PostReadDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setBody(entity.getBody());

        User user = entity.getUser();

        UserReadDto userDto = new UserReadDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());

        dto.setUser(userDto);

        return dto;
    }

    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostReadDto> getById(@PathVariable Integer id) {
        Optional<Post> optional = repository.findByIdWithUser(id);

        Optional<PostReadDto> dtoOptional = optional.map(this::convertToReadDto);
        return ResponseEntity.of(dtoOptional);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody PostWriteDto dto) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setBody(dto.getBody());

        //Se não encontrarmos o usuário pedido para a postagem, podemos retornar um HTTP 404 (Não Encontrado) junto com
        //uma mensagem na resposta para que o cliente saiba o que aconteceu de errado.
        Optional<User> userOptional = userRepository.findById(dto.getUserId());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário com ID " + dto.getUserId() + " não encontrado");
        }

        post.setUser(userOptional.get());

        repository.save(post);
        return ResponseEntity.created(URI.create("/posts/" + post.getId())).build();
    }

    //Note que, neste método, há 2 casos que podem resultar em HTTP 404 (Não Encontrado): quando o usuário não é encontrado
    //(em que retornamos junto uma mensagem na resposta) e quando a postagem em si não é encontrada (quando a resposta está vazia).
    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody PostWriteDto dto) {
        //Note que o método que usamos aqui não foi o "findByIdWithUser"; para checar se a postagem existia, apenas as
        //informações dela eram necessárias, já que o usuário será substituído.
        Optional<Post> optional = repository.findById(id);
        if (optional.isPresent()) {
            Post existingPost = optional.get();
            existingPost.setTitle(dto.getTitle());
            existingPost.setBody(dto.getBody());

            Optional<User> userOptional = userRepository.findById(dto.getUserId());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário com ID " + dto.getUserId() + " não encontrado");
            }

            existingPost.setUser(userOptional.get());

            repository.save(existingPost);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Optional<Post> optional = repository.findById(id);
        if (optional.isPresent()) {
            Post post = optional.get();
            repository.delete(post);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
