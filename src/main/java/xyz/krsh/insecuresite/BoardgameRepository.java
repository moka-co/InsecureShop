package xyz.krsh.insecuresite;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * Implements the Repository pattern for Boardgame by extending the CrudRepository
 */
@Repository
public interface BoardgameRepository extends CrudRepository<Boardgame, String> {
    List<Boardgame> findByNameContaining(String name);

    List<Boardgame> findAll();

}
