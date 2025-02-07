package su.ezhidze.libapi.service;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.ezhidze.libapi.entity.Author;
import su.ezhidze.libapi.entity.Book;
import su.ezhidze.libapi.entity.Publisher;
import su.ezhidze.libapi.exception.BadArgumentException;
import su.ezhidze.libapi.exception.DuplicateEntryException;
import su.ezhidze.libapi.exception.RecordNotFoundException;
import su.ezhidze.libapi.repository.AuthorRepository;
import su.ezhidze.libapi.repository.BookRepository;
import su.ezhidze.libapi.repository.PublisherRepository;

import java.util.Set;

@Service
@Transactional
public class BookService implements IService<Book> {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final PublisherRepository publisherRepository;

    @Setter
    private PublisherService publisherService;

    @Setter
    private AuthorService authorService;

    @Autowired
    public BookService(BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    @Override
    public Book create(Book book) {
        if (book.getIsbn() != null && bookRepository.findByIsbn(book.getIsbn()) != null) {
            throw new DuplicateEntryException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        return bookRepository.save(book);
    }

    @Override
    public Book read(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + id + " not found"));
    }

    public Book getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            throw new RecordNotFoundException("Book with ISBN " + isbn + " not found");
        }
        return book;
    }

    public Set<Author> getBookAuthors(Long bookId) {
        Book book = read(bookId);
        return book.getAuthors();
    }

    public Book setPublisher(Long bookId, Long publisherId) {
        Book book = read(bookId);
        Publisher publisher = publisherRepository.findById(publisherId)
                .orElseThrow(() -> new RecordNotFoundException("Publisher with id " + publisherId + " not found"));
        if (book.getPublisher() != null) {
            throw new BadArgumentException("Book with id " + bookId + " already has a publisher");
        }
        book.setPublisher(publisher);
        publisher.getBooks().add(book);
        return bookRepository.save(book);
    }

    @Override
    public Book update(Long id, Book updatedBook) {
        Book existingBook = read(id);
        if (updatedBook.getTitle() != null && !updatedBook.getTitle().isBlank()) {
            existingBook.setTitle(updatedBook.getTitle());
        }
        if (updatedBook.getPublicationYear() != 0) {
            existingBook.setPublicationYear(updatedBook.getPublicationYear());
        }
        if (updatedBook.getIsbn() != null && !updatedBook.getIsbn().isBlank()) {
            existingBook.setIsbn(updatedBook.getIsbn());
        }
        if (updatedBook.getPageCount() != 0) {
            existingBook.setPageCount(updatedBook.getPageCount());
        }

        return bookRepository.save(existingBook);
    }

    @Override
    public void delete(Long id) {
        Book book = read(id);
        for (Author author : book.getAuthors()) authorService.removeBookFromAuthor(author.getId(), id);
        if (book.getPublisher() != null)
            publisherService.removeBookFromPublisher(book.getPublisher().getId(), book.getId());
        bookRepository.delete(read(id));
    }

    public Book addAuthorToBook(Long bookId, Long authorId) {
        Book book = read(bookId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RecordNotFoundException("Author with id " + authorId + " not found"));
        if (book.getAuthors().contains(author)) {
            throw new DuplicateEntryException("Author with id " + authorId + " already added to book with id " + bookId);
        }
        book.getAuthors().add(author);
        author.getBooks().add(book);
        return bookRepository.save(book);
    }

    public Book removeAuthorFromBook(Long bookId, Long authorId) {
        Book book = read(bookId);
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RecordNotFoundException("Author with id " + authorId + " not found"));
        if (!book.getAuthors().contains(author)) {
            throw new BadArgumentException("Book with id " + bookId + " does not contain author with id " + authorId);
        }
        book.getAuthors().remove(author);
        author.getBooks().remove(book);
        return bookRepository.save(book);
    }
}
