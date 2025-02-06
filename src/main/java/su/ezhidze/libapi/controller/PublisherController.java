package su.ezhidze.libapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.ezhidze.libapi.dto.PublisherDto;
import su.ezhidze.libapi.entity.Book;
import su.ezhidze.libapi.entity.Publisher;
import su.ezhidze.libapi.exception.ExceptionBodyBuilder;
import su.ezhidze.libapi.service.PublisherService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @PostMapping
    public ResponseEntity createPublisher(@Valid @RequestBody PublisherDto publisherDto) {
        try {
            Publisher publisher = convertDtoToPublisher(publisherDto);
            Publisher saved = publisherService.addPublisher(publisher);
            return new ResponseEntity<>(convertPublisherToDto(saved), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity getPublisherById(@PathVariable Long id) {
        try {
            Publisher publisher = publisherService.getPublisherById(id);
            return ResponseEntity.ok(convertPublisherToDto(publisher));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity getPublisherByName(@PathVariable String name) {
        try {
            Publisher publisher = publisherService.getPublisherByName(name);
            return ResponseEntity.ok(convertPublisherToDto(publisher));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePublisher(@PathVariable Long id, @Valid @RequestBody PublisherDto publisherDto) {
        try {
            Publisher publisher = convertDtoToPublisher(publisherDto);
            Publisher updated = publisherService.updatePublisher(id, publisher);
            return ResponseEntity.ok(convertPublisherToDto(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePublisher(@PathVariable Long id) {
        try {
            publisherService.deletePublisher(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/{id}/books")
    public ResponseEntity getBooksByPublisher(@PathVariable Long id) {
        try {
            Set<Book> books = publisherService.getBooksByPublisher(id);
            Set<Long> bookIds = books.stream().map(Book::getId).collect(Collectors.toSet());
            return ResponseEntity.ok(bookIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @PostMapping("/{publisherId}/books/{bookId}")
    public ResponseEntity addBookToPublisher(@PathVariable Long publisherId, @PathVariable Long bookId) {
        try {
            Publisher publisher = publisherService.addBookToPublisher(publisherId, bookId);
            return ResponseEntity.ok(convertPublisherToDto(publisher));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{publisherId}/books/{bookId}")
    public ResponseEntity removeBookFromPublisher(@PathVariable Long publisherId, @PathVariable Long bookId) {
        try {
            Publisher publisher = publisherService.removeBookFromPublisher(publisherId, bookId);
            return ResponseEntity.ok(convertPublisherToDto(publisher));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ExceptionBodyBuilder.build(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    private PublisherDto convertPublisherToDto(Publisher publisher) {
        Set<Long> bookIds = publisher.getBooks().stream().map(Book::getId).collect(Collectors.toSet());
        return new PublisherDto(publisher.getId(), publisher.getName(), publisher.getAddress(), bookIds);
    }

    private Publisher convertDtoToPublisher(PublisherDto dto) {
        Publisher publisher = new Publisher();
        publisher.setName(dto.getName());
        publisher.setAddress(dto.getAddress());
        return publisher;
    }
}
