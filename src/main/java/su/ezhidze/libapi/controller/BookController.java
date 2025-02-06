package su.ezhidze.libapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.ezhidze.libapi.dto.BookDto;
import su.ezhidze.libapi.entity.Author;
import su.ezhidze.libapi.entity.Book;
import su.ezhidze.libapi.exception.ExceptionBodyBuilder;
import su.ezhidze.libapi.service.BookService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity createBook(@Valid @RequestBody BookDto bookDto) {
        try {
            Book book = convertDtoToBook(bookDto);
            Book saved = bookService.addBook(book);
            return new ResponseEntity<>(convertBookToDto(saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getBookById(@PathVariable Long id) {
        try {
            Book book = bookService.getBookById(id);
            return ResponseEntity.ok(convertBookToDto(book));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity getBookByIsbn(@PathVariable String isbn) {
        try {
            Book book = bookService.getBookByIsbn(isbn);
            return ResponseEntity.ok(convertBookToDto(book));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {
        try {
            Book book = convertDtoToBook(bookDto);
            Book updated = bookService.updateBook(id, book);
            return ResponseEntity.ok(convertBookToDto(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/{id}/authors")
    public ResponseEntity getBookAuthors(@PathVariable Long id) {
        try {
            Set<Author> authors = bookService.getBookAuthors(id);
            Set<Long> authorIds = authors.stream().map(Author::getId).collect(Collectors.toSet());
            return ResponseEntity.ok(authorIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PostMapping("/{bookId}/authors/{authorId}")
    public ResponseEntity addAuthorToBook(@PathVariable Long bookId, @PathVariable Long authorId) {
        try {
            Book book = bookService.addAuthorToBook(bookId, authorId);
            return ResponseEntity.ok(convertBookToDto(book));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{bookId}/authors/{authorId}")
    public ResponseEntity removeAuthorFromBook(@PathVariable Long bookId, @PathVariable Long authorId) {
        try {
            Book book = bookService.removeAuthorFromBook(bookId, authorId);
            return ResponseEntity.ok(convertBookToDto(book));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PutMapping("/{bookId}/publisher/{publisherId}")
    public ResponseEntity setPublisherForBook(@PathVariable Long bookId, @PathVariable Long publisherId) {
        try {
            Book book = bookService.setPublisher(bookId, publisherId);
            return ResponseEntity.ok(convertBookToDto(book));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    private BookDto convertBookToDto(Book book) {
        Long publisherId = (book.getPublisher() != null) ? book.getPublisher().getId() : null;
        Set<Long> authorIds = book.getAuthors().stream().map(Author::getId).collect(Collectors.toSet());
        return new BookDto(book.getId(), book.getTitle(), book.getPublicationYear(), book.getIsbn(), book.getPageCount(), publisherId, authorIds);
    }

    private Book convertDtoToBook(BookDto dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setPublicationYear(dto.getPublicationYear());
        book.setIsbn(dto.getIsbn());
        book.setPageCount(dto.getPageCount());
        return book;
    }
}
