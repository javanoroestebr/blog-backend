package br.usp.lucas.blogbackend.comment;

import br.usp.lucas.blogbackend.comment.dto.CommentReadDto;
import br.usp.lucas.blogbackend.comment.dto.CommentWriteDto;
import br.usp.lucas.blogbackend.post.Post;
import br.usp.lucas.blogbackend.post.PostRepository;
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

/*
Nesta classe, estamos gerenciando os comentários como nos outros controladores, como entidades próprias; uma alternativa
a isso seria controlá-los dentro do endpoint de postagens, de forma a mostrar que todos os comentários estão associados
obrigatoriamente a uma postagem. Neste caso, a classe CommentWriteDto não precisaria do atributo postId, sendo este
inferido pela URL.
 */
@CrossOrigin
@RestController
@RequestMapping("comments")
public class CommentRestController {
    private final CommentRepository repository;
    private final PostRepository postRepository;

    public CommentRestController(CommentRepository repository, PostRepository postRepository) {
        this.repository = repository;
        this.postRepository = postRepository;
    }

    /*
    Alternativa: método GET com URL "/posts/{postId}/comments"

    Nota: como o postId neste formato deve ser obrigatório, ele foi deixado de fora do filtro, pois assim a anotação
    @RequestParam garante que ele será obrigatório.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CommentReadDto> getAllByPostId(CommentFilter filter, @RequestParam Integer postId, Sort sort) {
        final Comment exampleComment = new Comment();
        exampleComment.setTitle(filter.getTitle());
        exampleComment.setBody(filter.getBody());
        exampleComment.setEmail(filter.getEmail());

        final Post examplePost = new Post();
        examplePost.setId(postId);

        exampleComment.setPost(examplePost);

        final ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase();

        final Example<Comment> example = Example.of(exampleComment, matcher);
        final List<Comment> comments = repository.findAll(example, sort);

        final List<CommentReadDto> dtos = new ArrayList<>(comments.size());
        for (Comment comment : comments) {
            dtos.add(convertToReadDto(comment));
        }

        return dtos;
    }

    private CommentReadDto convertToReadDto(Comment entity) {
        final CommentReadDto dto = new CommentReadDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setBody(entity.getBody());
        dto.setEmail(entity.getEmail());

        return dto;
    }

    /*
    Alternativa: método GET com URL "/posts/{postId}/comments/{id}". Esta URL pode ser redundante, pois seria necessário
    checar os IDs da postagem e do comentário, mas pode ser útil caso seja necessário garantir que o comentário pedido
    esteja realmente dentro da postagem, evitando uma requisição possivelmente maliciosa.
     */
    @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentReadDto> getById(@PathVariable Integer id) {
        Optional<Comment> optional = repository.findById(id);

        Optional<CommentReadDto> dtoOptional = optional.map(this::convertToReadDto);
        return ResponseEntity.of(dtoOptional);
    }

    /*
    Alternativa: método POST com URL "/posts/{postId}/comments"
   */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> create(@RequestBody CommentWriteDto dto) {
        final Comment comment = new Comment();
        comment.setTitle(dto.getTitle());
        comment.setBody(dto.getBody());
        comment.setEmail(dto.getEmail());

        final Optional<Post> postOptional = postRepository.findById(dto.getPostId());
        if (postOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Postagem com ID " + dto.getPostId() + " não encontrada");
        }

        comment.setPost(postOptional.get());

        repository.save(comment);
        return ResponseEntity.created(URI.create("/comments/" + comment.getId())).build();
    }

    /*
   Alternativa: método PUT com URL "/posts/{postId}/comments/{id}"
    */
    @PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> update(@PathVariable Integer id, @RequestBody CommentWriteDto dto) {
        final Optional<Comment> optional = repository.findById(id);
        if (optional.isPresent()) {
            final Comment existingComment = optional.get();
            existingComment.setTitle(dto.getTitle());
            existingComment.setBody(dto.getBody());
            existingComment.setEmail(dto.getEmail());

            final Optional<Post> postOptional = postRepository.findById(dto.getPostId());
            if (postOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Postagem com ID " + dto.getPostId() + " não encontrada");
            }

            existingComment.setPost(postOptional.get());

            repository.save(existingComment);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /*
    Alternativa: método DELETE com URL "/posts/{postId}/comments/{id}"
     */
    @DeleteMapping(path = "{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        final Optional<Comment> optional = repository.findById(id);
        if (optional.isPresent()) {
            final Comment comment = optional.get();
            repository.delete(comment);

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
