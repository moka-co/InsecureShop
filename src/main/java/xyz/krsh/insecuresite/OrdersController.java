package xyz.krsh.insecuresite;

import java.security.Principal;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.rest.ApiError;
import xyz.krsh.insecuresite.security.MyUserDetails;
import xyz.krsh.insecuresite.security.UserRepository;

//TODO: Abstract Get Authentication Info into a method
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    UserRepository userRepo;

    /*
     * Returns every order
     */

    @RequestMapping
    public List<Order> findUserOrders(Principal principal) {
        System.out.println(principal.getClass().getName());
        List<Order> resultQuery = ordersRepository.findAll();

        return resultQuery;
    }

    /*
     * Returns a specific order by id;
     */
    @RequestMapping("/{orderId}/")
    public Order findOrder(@PathVariable("orderId") int orderId) throws Exception {
        Optional<Order> resultQuery = ordersRepository.findById(orderId);

        return resultQuery.get();

    }

    // TODO: Fix security issue: you can delete to others users orders
    // TODO: Make exceptions for every return "error" in the code
    // TODO: Make Custom Exception for every
    // TODO: Put Orders in another folder called "orders"
    @RequestMapping("/{orderId}/delete")
    public String deleteOrder(@PathVariable("orderId") int orderId) {
        Optional<Order> resultQuery = ordersRepository.findById(orderId);

        // Get Authentication Info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        if (resultQuery.isEmpty()) {
            return "error";
        }

        boolean isAdmin = authentication.isAuthenticated() && authentication.getAuthorities().stream()
                .anyMatch(authority -> "admin".equals(authority.getAuthority()));

        // Admin can do whatever they want, users cannot delete other user's orders
        if (isAdmin == false
                && userDetails.getUsername() != resultQuery.get().getUser().getId()) {
            return "error";

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

        if (date == null) {
            throw new Exception();
        }

        // Get Authentication Info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        Order toSave = new Order();
        toSave.setUser(userRepo.findById(userDetails.getUsername()).get());
        toSave.setOrderDate(date);

        Order result = ordersRepository.save(toSave);

        return result;
    }

    // Occurrs when you can't find any orders
    @ExceptionHandler({ NoSuchElementException.class, IndexOutOfBoundsException.class,
            EmptyResultDataAccessException.class })
    public ApiError handleIndexOutOfBoundsException() {
        return new ApiError("Order not found, retry", HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler({ MissingServletRequestParameterException.class })
    public ApiError handleMissingParametersException() {
        return new ApiError("Bad Paremeters: required date in format dd-MM-yyyy", HttpStatus.BAD_REQUEST);

    }

}
