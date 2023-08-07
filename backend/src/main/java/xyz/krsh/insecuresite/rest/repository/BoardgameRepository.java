package xyz.krsh.insecuresite.rest.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import xyz.krsh.insecuresite.rest.dao.Boardgame;

import java.util.List;

import javax.transaction.Transactional;

/*
 * Implements the Repository pattern for Boardgame by extending the CrudRepository
 */
@Repository
public interface BoardgameRepository extends CrudRepository<Boardgame, String> {
    List<Boardgame> findByNameContaining(String name);

    List<Boardgame> findAll();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Boardgame b SET b.quantity = :#{#boardgame.quantity}, b.price = :#{#boardgame.price}, b.description = :#{#boardgame.description} WHERE b.name = :#{#boardgame.name}")
    // @Query("UPDATE Boardgame b SET b.quantity= :boardgame.quantity, b.price=
    // :boardgame.price, b.description= :boardgame.description WHERE b.name=
    // :boardgame.name")
    void update(@Param("boardgame") Boardgame boardgame);

}
