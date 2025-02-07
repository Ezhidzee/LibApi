package su.ezhidze.libapi.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.ezhidze.libapi.entity.Author;
import su.ezhidze.libapi.entity.Book;
import su.ezhidze.libapi.exception.BadArgumentException;
import su.ezhidze.libapi.exception.DuplicateEntryException;
import su.ezhidze.libapi.exception.RecordNotFoundException;
import su.ezhidze.libapi.repository.AuthorRepository;
import su.ezhidze.libapi.repository.BookRepository;

import java.util.Set;

@Service
@Transactional
public class AuthorService implements IService<Author> {

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final BookService bookService;

    @Autowired
    public AuthorService(AuthorRepository authorRepository,
                         BookRepository bookRepository, BookService bookService) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    @PostConstruct
    public void init() {
        bookService.setAuthorService(this);
    }

    @Override
    public Author create(Author author) {
        if (author.getName() != null && authorRepository.findByName(author.getName()) != null) {
            throw new DuplicateEntryException("Author with name " + author.getName() + " already exists");
        }
        return authorRepository.save(author);
    }

    @Override
    public Author read(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Author with id " + id + " not found"));
    }

    public Author getAuthorByName(String name) {
        Author author = authorRepository.findByName(name);
        if (author == null) {
            throw new RecordNotFoundException("Author with name " + name + " not found");
        }
        return author;
    }

    public Set<Book> getBooksByAuthor(Long authorId) {
        Author author = read(authorId);
        return author.getBooks();
    }

    @Override
    public Author update(Long id, Author updatedAuthor) {
        Author existingAuthor = read(id);
        if (updatedAuthor.getName() != null && !updatedAuthor.getName().isBlank()) {
            existingAuthor.setName(updatedAuthor.getName());
        }
        if (updatedAuthor.getBiography() != null) {
            existingAuthor.setBiography(updatedAuthor.getBiography());
        }
        return authorRepository.save(existingAuthor);
    }

    @Override
    public void delete(Long id) {
        Author author = read(id);
        for (Book book : author.getBooks()) bookService.removeAuthorFromBook(book.getId(), id);
        authorRepository.delete(author);
    }

    public Author addBookToAuthor(Long authorId, Long bookId) {
        Author author = read(authorId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " not found"));
        if (author.getBooks().contains(book)) {
            throw new DuplicateEntryException("Author with id " + authorId + " already has book with id " + bookId);
        }
        author.getBooks().add(book);
        book.getAuthors().add(author);
        bookRepository.save(book);
        return author;
    }

    public Author removeBookFromAuthor(Long authorId, Long bookId) {
        Author author = read(authorId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RecordNotFoundException("Book with id " + bookId + " not found"));
        if (!author.getBooks().contains(book)) {
            throw new BadArgumentException("Author with id " + authorId + " does not have book with id " + bookId);
        }
        author.getBooks().remove(book);
        book.getAuthors().remove(author);
        bookRepository.save(book);
        return authorRepository.save(author);
    }
}
