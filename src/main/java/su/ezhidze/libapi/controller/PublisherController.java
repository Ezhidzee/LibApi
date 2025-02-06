package su.ezhidze.libapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.ezhidze.libapi.dto.PublisherDto;
import su.ezhidze.libapi.entity.Book;
import su.ezhidze.libapi.entity.Publisher;
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
    public ResponseEntity<PublisherDto> createPublisher(@Valid @RequestBody PublisherDto publisherDto) {
        Publisher publisher = convertDtoToPublisher(publisherDto);
        Publisher saved = publisherService.addPublisher(publisher);
        return new ResponseEntity<>(convertPublisherToDto(saved), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherDto> getPublisherById(@PathVariable Long id) {
        Publisher publisher = publisherService.getPublisherById(id);
        return ResponseEntity.ok(convertPublisherToDto(publisher));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PublisherDto> getPublisherByName(@PathVariable String name) {
        Publisher publisher = publisherService.getPublisherByName(name);
        return ResponseEntity.ok(convertPublisherToDto(publisher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherDto> updatePublisher(@PathVariable Long id,
                                                        @Valid @RequestBody PublisherDto publisherDto) {
        Publisher publisher = convertDtoToPublisher(publisherDto);
        Publisher updated = publisherService.updatePublisher(id, publisher);
        return ResponseEntity.ok(convertPublisherToDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<Set<Long>> getBooksByPublisher(@PathVariable Long id) {
        Set<Book> books = publisherService.getBooksByPublisher(id);
        Set<Long> bookIds = books.stream()
                .map(Book::getId)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(bookIds);
    }

    @PostMapping("/{publisherId}/books/{bookId}")
    public ResponseEntity<PublisherDto> addBookToPublisher(@PathVariable Long publisherId,
                                                           @PathVariable Long bookId) {
        Publisher publisher = publisherService.addBookToPublisher(publisherId, bookId);
        return ResponseEntity.ok(convertPublisherToDto(publisher));
    }

    @DeleteMapping("/{publisherId}/books/{bookId}")
    public ResponseEntity<PublisherDto> removeBookFromPublisher(@PathVariable Long publisherId,
                                                                @PathVariable Long bookId) {
        Publisher publisher = publisherService.removeBookFromPublisher(publisherId, bookId);
        return ResponseEntity.ok(convertPublisherToDto(publisher));
    }

    private PublisherDto convertPublisherToDto(Publisher publisher) {
        Set<Long> bookIds = publisher.getBooks().stream()
                .map(Book::getId)
                .collect(Collectors.toSet());
        return new PublisherDto(publisher.getId(), publisher.getName(), publisher.getAddress(), bookIds);
    }

    private Publisher convertDtoToPublisher(PublisherDto dto) {
        Publisher publisher = new Publisher();
        publisher.setName(dto.getName());
        publisher.setAddress(dto.getAddress());
        return publisher;
    }
}
