package su.ezhidze.libapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.ezhidze.libapi.dto.AuthorDto;
import su.ezhidze.libapi.entity.Author;
import su.ezhidze.libapi.entity.Book;
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
    public ResponseEntity<AuthorDto> createAuthor(@Valid @RequestBody AuthorDto authorDto) {
        Author author = convertDtoToAuthor(authorDto);
        Author saved = authorService.addAuthor(author);
        return new ResponseEntity<>(convertAuthorToDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable Long id) {
        Author author = authorService.getAuthorById(id);
        return ResponseEntity.ok(convertAuthorToDto(author));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<AuthorDto> getAuthorByName(@PathVariable String name) {
        Author author = authorService.getAuthorByName(name);
        return ResponseEntity.ok(convertAuthorToDto(author));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable Long id,
                                                  @Valid @RequestBody AuthorDto authorDto) {
        Author updated = authorService.updateAuthor(id, convertDtoToAuthor(authorDto));
        return ResponseEntity.ok(convertAuthorToDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<Set<Long>> getBooksByAuthor(@PathVariable Long id) {
        Set<Book> books = authorService.getBooksByAuthor(id);
        Set<Long> bookIds = books.stream()
                .map(Book::getId)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(bookIds);
    }

    @PostMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<AuthorDto> addBookToAuthor(@PathVariable Long authorId,
                                                     @PathVariable Long bookId) {
        Author author = authorService.addBookToAuthor(authorId, bookId);
        return ResponseEntity.ok(convertAuthorToDto(author));
    }

    @DeleteMapping("/{authorId}/books/{bookId}")
    public ResponseEntity<AuthorDto> removeBookFromAuthor(@PathVariable Long authorId,
                                                          @PathVariable Long bookId) {
        Author author = authorService.removeBookFromAuthor(authorId, bookId);
        return ResponseEntity.ok(convertAuthorToDto(author));
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
