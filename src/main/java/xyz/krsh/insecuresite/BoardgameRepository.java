package xyz.krsh.insecuresite;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardgameRepository extends CrudRepository<Boardgame, String> {
    List<Boardgame> findByNameContaining(String name);

}
