package xyz.krsh.insecuresite.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import xyz.krsh.insecuresite.rest.dao.OrderedBoardgames;
import xyz.krsh.insecuresite.rest.dao.OrderedBoardgamesId;

@Repository
public interface OrderedBoardgamesRepository extends CrudRepository<OrderedBoardgames, OrderedBoardgamesId> {

    @Query("Select ob FROM OrderedBoardgames ob WHERE ob.order.orderId = :id")
    Optional<OrderedBoardgames> findById(Integer id);

    @Query("SELECT ob FROM OrderedBoardgames ob WHERE ob.order.orderId = (SELECT o.orderId FROM Order o WHERE o.user.email = :email)")
    List<OrderedBoardgames> findByCustomerName(@Param("email") String email);

    List<OrderedBoardgames> findAll();

}
