package su.ezhidze.libapi.service;

public interface IService<T> {

    T create(T t);

    T read(Long id);

    T update(Long id, T t);

    void delete(Long id);
}
