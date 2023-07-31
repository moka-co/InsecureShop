package xyz.krsh.insecuresite.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import xyz.krsh.insecuresite.rest.dao.Order;

/*
 * Implements the Repository pattern for Boardgame by extending the CrudRepository
 */
@Repository
public interface OrdersRepository extends CrudRepository<Order, Integer> {

    Optional<Order> findById(Integer id);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    List<Order> findByCustomerName(@Param("email") String email);

    List<Order> findAll();

}
