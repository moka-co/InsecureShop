package xyz.krsh.insecuresite.rest.controller;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.exceptions.ApiError;
import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.exceptions.UnauthorizedException;
import xyz.krsh.insecuresite.rest.dto.OrderDto;
import xyz.krsh.insecuresite.rest.dto.OrderedBoardgameDto;
import xyz.krsh.insecuresite.rest.entities.Order;
import xyz.krsh.insecuresite.rest.entities.OrderedBoardgames;
import xyz.krsh.insecuresite.rest.service.OrdersService;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @Autowired
    OrdersService ordersService;

    @RequestMapping("/admin")
    public List<OrderedBoardgames> findAllOrders() throws UnauthorizedException {
        return ordersService.findAllOrders();
    }

    /*
     * Returns every order from the current logged User
     */
    @RequestMapping("/")
    public List<OrderedBoardgames> findUserOrders() throws UnauthorizedException {
        return ordersService.findUserOrders();
    }

    /*
     * Returns a specific order by id;
     */
    @RequestMapping("/{orderId}/")
    public Order findOrder(@PathVariable("orderId") int orderId) throws ItemNotFoundException, UnauthorizedException {
        return ordersService.findOrder(orderId);
    }

    /*
     * Return every ordered games with quantity
     */
    @RequestMapping("/{orderId}/ob")
    public List<OrderedBoardgames> getAllOrderedBoardgamesFromUser(@PathVariable("orderId") int orderId)
            throws UnauthorizedException {
        return ordersService.getAllOrderedBoardgamesFromUser(orderId);

    }

    /*
     * Add boardgame to an order
     * Requires orderId, boardgame name and the quantity of boardgames to order
     * 
     */
    @RequestMapping(value = "/{orderId}/addBoardgame", method = RequestMethod.POST)
    public ResponseEntity<String> addBoardgameToOrder(
            @PathVariable("orderId") int orderId, @RequestBody OrderedBoardgameDto obd,
            @RequestParam(value = "boardgameName") String boardgameName,
            @RequestParam(value = "quantity") Integer quantity)
            throws ItemNotFoundException, UnauthorizedException {

        return new ResponseEntity<String>(ordersService.addBoardgameToOrder(orderId, obd), HttpStatus.ACCEPTED);

    }

    @RequestMapping(value = "/{orderId}/delete", method = RequestMethod.POST)
    public String deleteOrder(@PathVariable("orderId") int orderId)
            throws UnauthorizedException, ItemNotFoundException {
        return ordersService.deleteOrder(orderId);
    }

    /*
     * Add a new order to the database
     * Request parameters are: date
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addBoardgameReq(@RequestBody OrderDto orderDto) throws Exception {

        return ordersService.addOrder(orderDto.getOrderDate());
    }

    /*
     * Exceptions Handlers
     */

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
