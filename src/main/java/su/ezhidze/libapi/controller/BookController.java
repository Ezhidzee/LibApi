package su.ezhidze.libapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.ezhidze.libapi.dto.BookDto;
import su.ezhidze.libapi.entity.Author;
import su.ezhidze.libapi.entity.Book;
import su.ezhidze.libapi.entity.Publisher;
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
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody BookDto bookDto) {
        Book book = convertDtoToBook(bookDto);
        Book saved = bookService.addBook(book);
        return new ResponseEntity<>(convertBookToDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(convertBookToDto(book));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDto> getBookByIsbn(@PathVariable String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(convertBookToDto(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id,
                                              @Valid @RequestBody BookDto bookDto) {
        Book book = convertDtoToBook(bookDto);
        Book updated = bookService.updateBook(id, book);
        return ResponseEntity.ok(convertBookToDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/authors")
    public ResponseEntity<Set<Long>> getBookAuthors(@PathVariable Long id) {
        Set<Author> authors = bookService.getBookAuthors(id);
        Set<Long> authorIds = authors.stream()
                .map(Author::getId)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(authorIds);
    }

    @PostMapping("/{bookId}/authors/{authorId}")
    public ResponseEntity<BookDto> addAuthorToBook(@PathVariable Long bookId,
                                                   @PathVariable Long authorId) {
        Book book = bookService.addAuthorToBook(bookId, authorId);
        return ResponseEntity.ok(convertBookToDto(book));
    }

    @DeleteMapping("/{bookId}/authors/{authorId}")
    public ResponseEntity<BookDto> removeAuthorFromBook(@PathVariable Long bookId,
                                                        @PathVariable Long authorId) {
        Book book = bookService.removeAuthorFromBook(bookId, authorId);
        return ResponseEntity.ok(convertBookToDto(book));
    }

    @PutMapping("/{bookId}/publisher/{publisherId}")
    public ResponseEntity<BookDto> setPublisherForBook(@PathVariable Long bookId,
                                                       @PathVariable Long publisherId) {
        Book book = bookService.setPublisher(bookId, publisherId);
        return ResponseEntity.ok(convertBookToDto(book));
    }

    private BookDto convertBookToDto(Book book) {
        Long publisherId = (book.getPublisher() != null) ? book.getPublisher().getId() : null;
        Set<Long> authorIds = book.getAuthors().stream()
                .map(Author::getId)
                .collect(Collectors.toSet());
        return new BookDto(book.getId(), book.getTitle(), book.getPublicationYear(),
                book.getIsbn(), book.getPageCount(), publisherId, authorIds);
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
