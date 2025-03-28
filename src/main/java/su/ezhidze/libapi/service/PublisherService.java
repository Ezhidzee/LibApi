package su.ezhidze.libapi.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.ezhidze.libapi.entity.Author;
import su.ezhidze.libapi.entity.Book;
import su.ezhidze.libapi.entity.Publisher;
import su.ezhidze.libapi.exception.BadArgumentException;
import su.ezhidze.libapi.exception.DuplicateEntryException;
import su.ezhidze.libapi.exception.RecordNotFoundException;
import su.ezhidze.libapi.repository.BookRepository;
import su.ezhidze.libapi.repository.PublisherRepository;

import java.util.Set;

@Service
@Transactional
public class PublisherService implements IService<Publisher> {

    private final PublisherRepository publisherRepository;

    private final BookRepository bookRepository;

    private final BookService bookService;

    private final AuthorService authorService;

    @Autowired
    public PublisherService(PublisherRepository publisherRepository,
                            BookRepository bookRepository, BookService bookService, AuthorService authorService) {
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @PostConstruct
    public void init() {
        bookService.setPublisherService(this);
    }

    @Override
    public Publisher create(Publisher publisher) {
        if (publisher.getName() != null && publisherRepository.findByName(publisher.getName()) != null) {
            throw new DuplicateEntryException("Publisher with name " + publisher.getName() + " already exists");
        }
        return publisherRepository.save(publisher);
    }

    @Override
    public Publisher read(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Publisher with id " + id + " not found"));
    }

    public Publisher getPublisherByName(String name) {
        Publisher publisher = publisherRepository.findByName(name);
        if (publisher == null) {
            throw new RecordNotFoundException("Publisher with name " + name + " not found");
        }
        return publisher;
    }

    public Set<Book> getBooksByPublisher(Long publisherId) {
        Publisher publisher = read(publisherId);
        return publisher.getBooks();
    }

    @Override
    public Publisher update(Long id, Publisher updatedPublisher) {
        Publisher publisher = read(id);
        if (updatedPublisher.getName() != null && !updatedPublisher.getName().isBlank()) {
            publisher.setName(updatedPublisher.getName());
        }
        if (updatedPublisher.getAddress() != null) {
            publisher.setAddress(updatedPublisher.getAddress());
        }
        return publisherRepository.save(publisher);
    }

    @Override
    public void delete(Long id) {
        Publisher publisher = read(id);

        for (Book book : publisher.getBooks()) {
//            for (Author author : book.getAuthors()) {
//                authorService.removeBookFromAuthor(author.getId(), book.getId());
//            }
            book.setPublisher(null);
        }
        publisherRepository.delete(publisher);
    }

    public Publisher addBookToPublisher(Long publisherId, Long bookId) {
        Publisher publisher = read(publisherId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " not found"));
        if (book.getPublisher() != null) {
            throw new BadArgumentException("Book with id " + bookId + " already has a publisher");
        }
        book.setPublisher(publisher);
        publisher.getBooks().add(book);
        bookRepository.save(book);
        return publisher;
    }

    public Publisher removeBookFromPublisher(Long publisherId, Long bookId) {
        Publisher publisher = read(publisherId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " not found"));
        if (book.getPublisher() == null || !book.getPublisher().getId().equals(publisherId)) {
            throw new BadArgumentException("Book with id " + bookId + " does not belong to publisher with id " + publisherId);
        }
        book.setPublisher(null);
        publisher.getBooks().remove(book);
        bookRepository.save(book);
        return publisher;
    }
}
