package su.ezhidze.libapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.ezhidze.libapi.dto.AuthorDto;
import su.ezhidze.libapi.entity.Author;
import su.ezhidze.libapi.entity.Book;
import su.ezhidze.libapi.exception.ExceptionBodyBuilder;
import su.ezhidze.libapi.service.AuthorService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity createAuthor(@Valid @RequestBody AuthorDto authorDto) {
        try {
            Author author = convertDtoToAuthor(authorDto);
            Author saved = authorService.create(author);
            return new ResponseEntity<>(convertAuthorToDto(saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getAuthorById(@PathVariable Long id) {
        try {
            Author author = authorService.read(id);
            return ResponseEntity.ok(convertAuthorToDto(author));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity getAuthorByName(@PathVariable String name) {
        try {
            Author author = authorService.getAuthorByName(name);
            return ResponseEntity.ok(convertAuthorToDto(author));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDto authorDto) {
        try {
            Author updated = authorService.update(id, convertDtoToAuthor(authorDto));
            return ResponseEntity.ok(convertAuthorToDto(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteAuthor(@PathVariable Long id) {
        try {
            authorService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/{id}/books")
    public ResponseEntity getBooksByAuthor(@PathVariable Long id) {
        try {
            Set<Book> books = authorService.getBooksByAuthor(id);
            Set<Long> bookIds = books.stream().map(Book::getId).collect(Collectors.toSet());
            return ResponseEntity.ok(bookIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PostMapping("/{authorId}/books/{bookId}")
    public ResponseEntity addBookToAuthor(@PathVariable Long authorId, @PathVariable Long bookId) {
        try {
            Author author = authorService.addBookToAuthor(authorId, bookId);
            return ResponseEntity.ok(convertAuthorToDto(author));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{authorId}/books/{bookId}")
    public ResponseEntity removeBookFromAuthor(@PathVariable Long authorId, @PathVariable Long bookId) {
        try {
            Author author = authorService.removeBookFromAuthor(authorId, bookId);
            return ResponseEntity.ok(convertAuthorToDto(author));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    private AuthorDto convertAuthorToDto(Author author) {
        Set<Long> bookIds = author.getBooks().stream()
                .map(Book::getId)
                .collect(Collectors.toSet());
        return new AuthorDto(author.getId(), author.getName(), author.getBiography(), bookIds);
    }

    private Author convertDtoToAuthor(AuthorDto dto) {
        Author author = new Author();
        author.setName(dto.getName());
        author.setBiography(dto.getBiography());
        return author;
    }
}
