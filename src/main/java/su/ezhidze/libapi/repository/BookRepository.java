package su.ezhidze.libapi.repository;

import org.springframework.data.repository.CrudRepository;
import su.ezhidze.libapi.entity.Book;

public interface BookRepository extends CrudRepository<Book, Long> {
    Book findByIsbn(String isbn);
}
