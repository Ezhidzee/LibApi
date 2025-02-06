package su.ezhidze.libapi.repository;

import org.springframework.data.repository.CrudRepository;
import su.ezhidze.libapi.entity.Publisher;

public interface PublisherRepository extends CrudRepository<Publisher, Long> {
    Publisher findByName(String name);
}
