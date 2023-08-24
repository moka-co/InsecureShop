package xyz.krsh.insecuresite.rest.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.exceptions.UnauthorizedException;
import xyz.krsh.insecuresite.rest.dto.OrderedBoardgameDto;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.entities.Order;
import xyz.krsh.insecuresite.rest.entities.OrderedBoardgames;
import xyz.krsh.insecuresite.rest.entities.OrderedBoardgamesId;
import xyz.krsh.insecuresite.rest.repository.BoardgameRepository;
import xyz.krsh.insecuresite.rest.repository.OrderedBoardgamesRepository;
import xyz.krsh.insecuresite.rest.repository.OrdersRepository;
import xyz.krsh.insecuresite.rest.repository.UserRepository;
import xyz.krsh.insecuresite.security.MyUserDetails;
import xyz.krsh.insecuresite.security.hibernateValidatorBootstrapping.MyMessageInterpolator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Service
public class OrdersService {

    protected static final Logger logger = LogManager.getLogger();

    private static Validator validator = Validation.byDefaultProvider().configure()
            .messageInterpolator(new MyMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    BoardgameRepository boardgameRepository;

    @Autowired
    OrderedBoardgamesRepository orderedBoardgamesRepository;

    @Autowired
    UserRepository userRepo;

    /*
     * Get logged user email (identificator)
     */
    public String getLoggedId() throws UnauthorizedException {
        // Get Authentication info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        if ((userDetails.getUsername() instanceof String) == false) {
            throw new UnauthorizedException();
        }

        return userDetails.getUsername();
    }

    /*
     * Check if current user has authority of admin
     */

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.isAuthenticated() && authentication.getAuthorities().stream()
                .anyMatch(authority -> "admin".equals(authority.getAuthority()));

        return isAdmin;
    }

    /*
     * Admin only request, return every ordered boardgame from every order
     */
    public List<OrderedBoardgames> findAllOrders() throws UnauthorizedException {
        if (this.isAdmin() == false) {
            throw new UnauthorizedException();

        }
        List<OrderedBoardgames> resultQuery = orderedBoardgamesRepository.findAll();
        return resultQuery;

    }

    /*
     * Returns every order from the current logged User
     */
    public List<OrderedBoardgames> findUserOrders() throws UnauthorizedException {
        String id = this.getLoggedId();
        List<OrderedBoardgames> resultQuery = orderedBoardgamesRepository.findByCustomerName(id);
        return resultQuery;

    }

    /*
     * Returns a specific order find by using his id
     */
    public Order findOrder(int orderId) throws ItemNotFoundException, UnauthorizedException {
        Optional<Order> resultQuery = ordersRepository.findById(orderId);
        if (resultQuery.isEmpty() == true) {
            throw new ItemNotFoundException();
        }

        String loggedId = this.getLoggedId();
        String resultQueryUserId = resultQuery.get().getUser().getId();
        // Check if loggedId isn't equal to the id of the query
        if (!loggedId.equals(resultQueryUserId)) {
            throw new UnauthorizedException();
        }

        return resultQuery.get();

    }

    /*
     * Return every ordered games with quantity
     */
    public List<OrderedBoardgames> getAllOrderedBoardgamesFromUser(int orderId) throws UnauthorizedException {
        String email = this.getLoggedId();
        List<OrderedBoardgames> resultQuery = orderedBoardgamesRepository.findByCustomerName(email);
        return resultQuery;

    }

    /*
     * Add boardgame to an order
     */
    public String addBoardgameToOrder(int orderId, OrderedBoardgameDto obd)
            throws ItemNotFoundException, UnauthorizedException {

        Set<ConstraintViolation<OrderedBoardgameDto>> constraintViolations = validator.validate(obd);

        if (constraintViolations.size() > 0) {
            for (ConstraintViolation<OrderedBoardgameDto> cv : constraintViolations) {
                logger.warn(cv.getMessage());
            }
            return "Error: invalid input";
        }

        Optional<Order> queryOrder = ordersRepository.findById(orderId);
        Optional<Boardgame> queryBoardgame = boardgameRepository.findById(obd.getName());

        OrderedBoardgames ob = null;

        if (queryOrder.isPresent() && queryBoardgame.isPresent()) {

            String email = this.getLoggedId();
            String compareEmail = queryOrder.get().getUser().getId();

            // Check if user email is the same from the order
            if (!compareEmail.equals(email)) {
                throw new UnauthorizedException();
            }

            OrderedBoardgamesId id = new OrderedBoardgamesId(orderId, obd.getName());

            ob = new OrderedBoardgames(id, queryOrder.get(), queryBoardgame.get(), obd.getQuantity());
            orderedBoardgamesRepository.save(ob);
        } else {
            throw new ItemNotFoundException();
        }

        return "Added successfully " + obd.getQuantity() + " of " + obd.getName() + " to Order id " + orderId;

    }

    public String deleteOrder(int orderId) throws ItemNotFoundException, UnauthorizedException {
        Optional<Order> resultQuery = ordersRepository.findById(orderId);

        if (resultQuery.isEmpty()) {
            throw new ItemNotFoundException();
        }

        String email = this.getLoggedId();
        String resultQueryId = resultQuery.get().getUser().getId();

        // Admin can do whatever they want, users cannot delete other user's orders
        if ((isAdmin() == false)
                && (email.equals(resultQueryId) == false)) {
            throw new UnauthorizedException();

        }

        // https://stackoverflow.com/a/13252120
        Optional<List<OrderedBoardgames>> orderedBoardgamesOptional = orderedBoardgamesRepository.findById(orderId);
        if (orderedBoardgamesOptional.isPresent()) {
            List<OrderedBoardgames> list = orderedBoardgamesOptional.get();
            for (OrderedBoardgames ob : list) {
                orderedBoardgamesRepository.delete(ob);
            }

            Optional<Order> order = ordersRepository.findById(orderId);
            if (order.isPresent()) {
                ordersRepository.delete(order.get());
            }

        }

        return "Order deleted successfully";
    }

    public String addOrder(String dateString)
            throws UnauthorizedException {
        // Get Date info
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            logger.warn("Exception + e");
        }

        String email = this.getLoggedId(); // Get email from current session
        Order toSave = new Order(); // make new order
        toSave.setUser(userRepo.findById(email).get()); // Set User got from user repository
        toSave.setOrderDate(date); // set date
        Order result = ordersRepository.save(toSave);

        return "Added: " + result.getId();

    }

}
