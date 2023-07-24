package xyz.krsh.insecuresite;


import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface ProductRepository extends CrudRepository<Boardgame, String> {
    List<Boardgame> findByNameContaining(String name);

}
