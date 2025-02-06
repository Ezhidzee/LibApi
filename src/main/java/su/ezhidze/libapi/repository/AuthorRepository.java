package su.ezhidze.libapi.repository;

import org.springframework.data.repository.CrudRepository;
import su.ezhidze.libapi.entity.Author;

public interface AuthorRepository extends CrudRepository<Author, Long> {
    Author findByName(String name);
}
