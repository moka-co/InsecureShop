package xyz.krsh.insecuresite.rest.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.exceptions.ApiError;
import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.exceptions.UnauthorizedException;
import xyz.krsh.insecuresite.rest.dao.Boardgame;
import xyz.krsh.insecuresite.rest.dao.Order;
import xyz.krsh.insecuresite.rest.dao.OrderedBoardgames;
import xyz.krsh.insecuresite.rest.dao.OrderedBoardgamesId;
import xyz.krsh.insecuresite.rest.repository.BoardgameRepository;
import xyz.krsh.insecuresite.rest.repository.OrderedBoardgamesRepository;
import xyz.krsh.insecuresite.rest.repository.OrdersRepository;
import xyz.krsh.insecuresite.security.MyUserDetails;
import xyz.krsh.insecuresite.security.UserRepository;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    BoardgameRepository boardgameRepository;

    @Autowired
    OrderedBoardgamesRepository orderedBoardgamesRepository;

    @Autowired
    UserRepository userRepo;

    public String getLoggedUsername() throws UnauthorizedException {
        // Get Authentication info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        if ((userDetails.getUsername() instanceof String) == false) {
            throw new UnauthorizedException();
        }
        return userDetails.getUsername();
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.isAuthenticated() && authentication.getAuthorities().stream()
                .anyMatch(authority -> "admin".equals(authority.getAuthority()));

        return isAdmin;
    }

    @RequestMapping("/admin")
    public List<OrderedBoardgames> findAllOrders() throws UnauthorizedException {
        if (isAdmin() != true) {
            throw new UnauthorizedException();
        }
        List<OrderedBoardgames> resultQuery = orderedBoardgamesRepository.findAll(); // ordersRepository.findAll();
        return resultQuery;
    }

    /*
     * Returns every order from an User
     */
    @RequestMapping
    public List<Order> findUserOrders() throws UnauthorizedException {
        List<Order> resultQuery = ordersRepository.findByCustomerName(this.getLoggedUsername());

        return resultQuery;
    }

    /*
     * Returns a specific order by id;
     */
    @RequestMapping("/{orderId}/")
    public Order findOrder(@PathVariable("orderId") int orderId) throws ItemNotFoundException, UnauthorizedException {
        Optional<Order> resultQuery = ordersRepository.findById(orderId);
        if (resultQuery.isEmpty() == true) {
            throw new ItemNotFoundException();

        }
        if (this.getLoggedUsername() != resultQuery.get().getUser().getName()) {
            throw new UnauthorizedException();
        }

        return resultQuery.get();

    }

    /*
     * Return every ordered games with quantity
     */
    @RequestMapping("/{orderId}/ob")
    public List<OrderedBoardgames> getAllOrderedBoardgamesFromUser(@PathVariable("orderId") int orderId)
            throws UnauthorizedException {
        String email = this.getLoggedUsername();

        List<OrderedBoardgames> query = orderedBoardgamesRepository.findByCustomerName(email);

        return query;

    }

    @RequestMapping("/{orderId}/addBoardgame")
    public OrderedBoardgames addBoardgameToOrder(
            @PathVariable("orderId") int orderId,
            @RequestParam("boardgameName") String boardgameName, @RequestParam("quantity") int quantity)
            throws ItemNotFoundException, UnauthorizedException {

        Optional<Order> queryOrder = ordersRepository.findById(orderId);
        Optional<Boardgame> queryBoardgame = boardgameRepository.findById(boardgameName);

        OrderedBoardgames ob = null;
        if (queryOrder.isPresent() && queryBoardgame.isPresent()) {

            String email = this.getLoggedUsername();
            if (queryOrder.get().getUser().getName() != email) {
                throw new UnauthorizedException();
            }

            OrderedBoardgamesId id = new OrderedBoardgamesId(orderId, boardgameName);

            ob = new OrderedBoardgames(id, queryOrder.get(), queryBoardgame.get(), quantity);
            orderedBoardgamesRepository.save(ob);
        } else {
            throw new ItemNotFoundException();
        }

        return ob;

    }

    @RequestMapping("/{orderId}/delete")
    public String deleteOrder(@PathVariable("orderId") int orderId)
            throws UnauthorizedException, ItemNotFoundException {
        Optional<Order> resultQuery = ordersRepository.findById(orderId);

        if (resultQuery.isEmpty()) {
            throw new ItemNotFoundException();
        }

        String email = this.getLoggedUsername();

        // Admin can do whatever they want, users cannot delete other user's orders
        if (isAdmin() == false
                && email != resultQuery.get().getUser().getId()) {
            throw new UnauthorizedException();

        }

        ordersRepository.deleteById(orderId);
        return "Deleted order";
    }

    /*
     * Add a new boardgame to the database by REST call
     * Request parameters are: name, price, quantity and description
     */
    @GetMapping("/add")
    @ResponseBody
    public Order addBoardgameReq(
            @RequestParam("date") String dateString,
            HttpServletRequest request) throws Exception {

        // Get Date info
        Date date = null;
        try {
            date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

        String email = this.getLoggedUsername();

        Order toSave = new Order();
        toSave.setUser(userRepo.findById(email).get());
        toSave.setOrderDate(date);

        Order result = ordersRepository.save(toSave);

        return result;
    }

    // Occurrs when you can't find any orders
    @ExceptionHandler({ ItemNotFoundException.class, NoSuchElementException.class, IndexOutOfBoundsException.class,
            EmptyResultDataAccessException.class })

    public ApiError handleIndexOutOfBoundsException() {
        return new ApiError("Order not found, retry", HttpStatus.NOT_FOUND);

    }

    // Occurrs when you can't parse date
    @ExceptionHandler({ MissingServletRequestParameterException.class })
    public ApiError handleMissingParametersException() {
        return new ApiError("Bad Paremeters: required date in format dd-MM-yyyy", HttpStatus.BAD_REQUEST);

    }

    // Not authorized
    @ExceptionHandler({ UnauthorizedException.class })
    public ApiError HandleUnauthorizedException() {
        return new ApiError("Unauthorized", HttpStatus.BAD_REQUEST);

    }

}
