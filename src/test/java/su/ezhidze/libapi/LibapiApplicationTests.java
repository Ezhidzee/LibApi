package su.ezhidze.libapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import su.ezhidze.libapi.entity.Author;
import su.ezhidze.libapi.entity.Book;
import su.ezhidze.libapi.entity.Publisher;
import su.ezhidze.libapi.exception.DuplicateEntryException;
import su.ezhidze.libapi.exception.RecordNotFoundException;
import su.ezhidze.libapi.service.AuthorService;
import su.ezhidze.libapi.service.BookService;
import su.ezhidze.libapi.service.PublisherService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class LibapiApplicationTests {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private PublisherService publisherService;

    @Test
    void testAddBook_Success() {
        Book book = new Book();
        book.setTitle("Test title");
        book.setPublicationYear(2025);
        book.setIsbn("test-isbn");
        book.setPageCount(727);
        Book saved = bookService.create(book);
        assertNotNull(saved);
        assertEquals("Test title", saved.getTitle());
    }

    @Test
    public void testAddBook_DuplicateISBN() {
        Book book1 = new Book();
        book1.setIsbn("test-isbn");
        Book book2 = new Book();
        book2.setIsbn("test-isbn");
        bookService.create(book1);
        DuplicateEntryException ex = assertThrows(DuplicateEntryException.class, () -> bookService.create(book2));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    public void testGetBookByIsbn_Found() {
        Book book = new Book();
        book.setIsbn("727");
        bookService.create(book);
        Book found = bookService.getBookByIsbn("727");
        assertNotNull(found);
        assertEquals("727", found.getIsbn());
    }

    @Test
    public void testGetBookByIsbn_NotFound() {
        Book book = new Book();
        book.setIsbn("727");
        bookService.create(book);
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> bookService.getBookByIsbn("1800"));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    public void testGetBookById_Found() {
        Book book = new Book();
        Book saved = bookService.create(book);
        Book found = bookService.read(saved.getId());
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    public void testGetBookById_NotFound() {
        Book book = new Book();
        Book saved = bookService.create(book);
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> bookService.read(saved.getId() + 1));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    public void testUpdateBook() {
        Book book = new Book();
        book.setTitle("Test title");
        book.setPublicationYear(2025);
        book.setIsbn("test-isbn");
        book.setPageCount(727);
        Book saved = bookService.create(book);
        assertNotNull(saved);
        Book update = new Book();
        update.setTitle("Updated title");
        update.setPublicationYear(1984);
        update.setIsbn("123");
        update.setPageCount(999);
        Book updatedBook = bookService.update(saved.getId(), update);
        assertNotNull(updatedBook);
        assertEquals("Updated title", updatedBook.getTitle());
        assertEquals(1984, updatedBook.getPublicationYear());
        assertEquals("123", updatedBook.getIsbn());
        assertEquals(999, updatedBook.getPageCount());
    }

    @Test
    public void testDeleteBook() {
        Book book = new Book();
        Book saved = bookService.create(book);
        assertNotNull(saved);
        Long id = saved.getId();
        bookService.delete(id);
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> bookService.read(id));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    public void testAddAuthor_Success() {
        Author author = new Author();
        author.setName("Test name");
        author.setBiography("Test bio");
        Author saved = authorService.create(author);
        assertNotNull(saved);
        assertEquals("Test name", saved.getName());
    }

    @Test
    public void testAddAuthor_DuplicateName() {
        Author author = new Author();
        author.setName("Test name");
        authorService.create(author);
        Author author2 = new Author();
        author2.setName("Test name");
        DuplicateEntryException ex = assertThrows(DuplicateEntryException.class, () -> authorService.create(author));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    public void testGetAuthorById_Found() {
        Author author = new Author();
        Author saved = authorService.create(author);
        Author found = authorService.read(saved.getId());
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    public void testGetAuthorById_NotFound() {
        Author author = new Author();
        Author saved = authorService.create(author);
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> authorService.read(saved.getId() + 1));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    public void testUpdateAuthor() {
        Author author = new Author();
        author.setName("Test name");
        author.setBiography("Test bio");
        Author saved = authorService.create(author);
        assertNotNull(saved);
        Author update = new Author();
        update.setName("Updated name");
        update.setBiography("Updated bio");
        Author updated = authorService.update(saved.getId(), update);
        assertNotNull(updated);
        assertEquals("Updated name", updated.getName());
        assertEquals("Updated bio", updated.getBiography());
    }

    @Test
    public void testDeleteAuthor() {
        Author author = new Author();
        Author saved = authorService.create(author);
        assertNotNull(saved);
        Long id = saved.getId();
        authorService.delete(id);
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> authorService.read(id));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    public void testAddAuthorToBook() {
        Book book = new Book();
        Author author = new Author();
        Author savedAuthor = authorService.create(author);
        Book savedBook = bookService.create(book);
        Book result = bookService.addAuthorToBook(savedBook.getId(), savedAuthor.getId());
        assertNotNull(result);
        assertTrue(result.getAuthors().contains(savedAuthor));
        assertTrue(savedAuthor.getBooks().contains(result));
    }

    @Test
    public void testRemoveAuthorFromBook() {
        Book book = new Book();
        Author author = new Author();
        Author savedAuthor = authorService.create(author);
        Book savedBook = bookService.create(book);
        bookService.addAuthorToBook(savedBook.getId(), savedAuthor.getId());
        assertTrue(savedBook.getAuthors().contains(savedAuthor));
        Book result = bookService.removeAuthorFromBook(savedBook.getId(), savedAuthor.getId());
        assertFalse(result.getAuthors().contains(savedAuthor));
        assertFalse(savedAuthor.getBooks().contains(result));
    }

    @Test
    public void testAddBookToAuthor() {
        Book book = new Book();
        Author author = new Author();
        Author savedAuthor = authorService.create(author);
        Book savedBook = bookService.create(book);
        Author result = authorService.addBookToAuthor(savedAuthor.getId(), savedBook.getId());
        assertNotNull(result);
        assertTrue(result.getBooks().contains(savedBook));
        assertTrue(savedBook.getAuthors().contains(result));
    }

    @Test
    public void testRemoveBookFromAuthor() {
        Book book = new Book();
        Author author = new Author();
        Author savedAuthor = authorService.create(author);
        Book savedBook = bookService.create(book);
        authorService.addBookToAuthor(savedAuthor.getId(), savedBook.getId());
        assertTrue(savedAuthor.getBooks().contains(savedBook));
        Author result = authorService.removeBookFromAuthor(savedAuthor.getId(), savedBook.getId());
        assertFalse(result.getBooks().contains(savedBook));
        assertFalse(savedBook.getAuthors().contains(result));
    }

    @Test
    public void testRemoveAuthorFromBookByDeletingTheAuthor() {
        Book book = new Book();
        Author author = new Author();
        Author savedAuthor = authorService.create(author);
        Book savedBook = bookService.create(book);
        bookService.addAuthorToBook(savedBook.getId(), savedAuthor.getId());
        authorService.delete(savedAuthor.getId());
        assertTrue(savedBook.getAuthors().isEmpty());
    }

    @Test
    public void testRemoveBookFromAuthorByDeletingTheBook() {
        Book book = new Book();
        Author author = new Author();
        Author savedAuthor = authorService.create(author);
        Book savedBook = bookService.create(book);
        authorService.addBookToAuthor(savedAuthor.getId(), savedBook.getId());
        bookService.delete(savedBook.getId());
        assertTrue(savedAuthor.getBooks().isEmpty());
    }

    @Test
    public void testGetBooksByAuthor() {
        Book book = new Book();
        Author author = new Author();
        Author savedAuthor = authorService.create(author);
        Book savedBook = bookService.create(book);
        Author result = authorService.addBookToAuthor(savedAuthor.getId(), savedBook.getId());
        assertTrue(authorService.getBooksByAuthor(result.getId()).contains(savedBook));
    }

    @Test
    public void testGetBookAuthors() {
        Book book = new Book();
        Author author = new Author();
        Author savedAuthor = authorService.create(author);
        Book savedBook = bookService.create(book);
        Author result = authorService.addBookToAuthor(savedAuthor.getId(), savedBook.getId());
        assertTrue(bookService.getBookAuthors(savedBook.getId()).contains(result));
    }

    @Test
    public void testAddPublisher() {
        Publisher publisher = new Publisher();
        publisher.setName("Test name");
        publisher.setAddress("Test address");
        Publisher saved = publisherService.create(publisher);
        assertNotNull(saved);
        assertEquals("Test name", saved.getName());
    }

    @Test
    public void testGetPublisherById_Found() {
        Publisher publisher = new Publisher();
        Publisher saved = publisherService.create(publisher);
        Publisher found = publisherService.read(saved.getId());
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    public void testGetPublisherById_NotFound() {
        Publisher publisher = new Publisher();
        Publisher saved = publisherService.create(publisher);
        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () -> publisherService.read(saved.getId() + 1));
        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    public void testUpdatePublisher() {
        Publisher publisher = new Publisher();
        publisher.setName("Test name");
        publisher.setAddress("Test address");
        Publisher saved = publisherService.create(publisher);
        Publisher update = new Publisher();
        update.setName("Update name");
        update.setAddress("Update address");
        Publisher result = publisherService.update(saved.getId(), update);
        assertEquals("Update name", result.getName());
        assertEquals("Update address", result.getAddress());
    }

    @Test
    public void testDeletePublisher() {
        Publisher publisher = new Publisher();
        Publisher saved = publisherService.create(publisher);
        Long id = saved.getId();
        publisherService.delete(id);
        assertThrows(RecordNotFoundException.class, () -> publisherService.read(id));
    }

    @Test
    public void testAddBookToPublisher() {
        Publisher publisher = new Publisher();
        Book book = new Book();
        Book savedBook = bookService.create(book);
        Publisher savedPublisher = publisherService.create(publisher);
        Publisher result = publisherService.addBookToPublisher(savedPublisher.getId(), savedBook.getId());
        assertNotNull(result);
        assertTrue(result.getBooks().contains(savedBook));
        assertEquals(savedBook.getPublisher(), result);
    }

    @Test
    public void testRemoveBookFromPublisher() {
        Publisher publisher = new Publisher();
        Book book = new Book();
        Book savedBook = bookService.create(book);
        Publisher savedPublisher = publisherService.create(publisher);
        publisherService.addBookToPublisher(savedPublisher.getId(), savedBook.getId());
        assertTrue(savedPublisher.getBooks().contains(savedBook));
        Publisher result = publisherService.removeBookFromPublisher(savedPublisher.getId(), savedBook.getId());
        assertFalse(result.getBooks().contains(savedBook));
        assertNull(savedBook.getPublisher());
    }

    @Test
    public void testRemoveBookFromPublisherByDeletingTheBook() {
        Publisher publisher = new Publisher();
        Book book = new Book();
        Book savedBook = bookService.create(book);
        Publisher savedPublisher = publisherService.create(publisher);
        publisherService.addBookToPublisher(savedPublisher.getId(), savedBook.getId());
        assertTrue(savedPublisher.getBooks().contains(savedBook));
        bookService.delete(savedBook.getId());
        assertFalse(savedPublisher.getBooks().contains(savedBook));
    }

}
