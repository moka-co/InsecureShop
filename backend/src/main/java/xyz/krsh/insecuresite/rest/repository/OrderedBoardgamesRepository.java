package xyz.krsh.insecuresite.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import xyz.krsh.insecuresite.rest.dao.OrderedBoardgames;
import xyz.krsh.insecuresite.rest.dao.OrderedBoardgamesId;

@Repository
public interface OrderedBoardgamesRepository extends CrudRepository<OrderedBoardgames, OrderedBoardgamesId> {

    @Query("Select ob from OrderedBoardgames ob WHERE ob.boardgame.name = :name")
    Optional<List<OrderedBoardgames>> findByBoardgameName(String name);

    @Query("Select ob FROM OrderedBoardgames ob WHERE ob.order.orderId = :id")
    Optional<List<OrderedBoardgames>> findById(Integer id);

    // Having email, the associated ordere boardgame must be find
    // orderedBoardgame is joined with orders based on orderId
    // and then the result is filtered with orderId.id (aka email)
    @Query("SELECT ob FROM OrderedBoardgames ob INNER JOIN ob.order o WHERE ob.order.user.id= :email")
    List<OrderedBoardgames> findByCustomerName(@Param("email") String email);

    List<OrderedBoardgames> findAll();

    @Modifying // Required by DELETE statement
    @Query("DELETE FROM OrderedBoardgames ob WHERE ob.order.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") Integer orderId);

}
