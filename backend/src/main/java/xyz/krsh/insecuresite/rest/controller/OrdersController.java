package xyz.krsh.insecuresite.rest.controller;

import java.text.ParseException;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Order Controller", description = "This controller is responsible for handling specific HTTP Requests related to orders and ordered boardgames")
public class OrdersController {

    @Autowired
    OrdersService ordersService;

    /*
     * Admin only command that returns every order from every user
     * 
     * @GetMapping("/admin")
     * 
     * @ApiResponse(description =
     * "Admin only request that returns every order from every user")
     * public List<OrderedBoardgames> findAllOrders() throws UnauthorizedException {
     * return ordersService.findAllOrders();
     * }
     */

    /*
     * Returns every order from the current logged User
     */
    @ApiResponse(description = "Returns every order from the current logged user")
    @GetMapping("/")
    public List<OrderedBoardgames> findUserOrders() throws UnauthorizedException {
        return ordersService.findUserOrders();
    }

    /*
     * Returns a specific order by id;
     */
    @ApiResponse(description = "Return a specific order by id")
    @GetMapping("/{orderId}/")
    public Order findOrder(@PathVariable("orderId") int orderId) throws UnauthorizedException {
        return ordersService.findOrder(orderId);
    }

    /*
     * Return every ordered games with quantity
     */
    @ApiResponse(description = "Return every ordered boardgame and quantity from an order")
    @GetMapping("/{orderId}/ob")
    public List<OrderedBoardgames> getAllOrderedBoardgamesFromUser(@PathVariable("orderId") int orderId)
            throws UnauthorizedException {
        return ordersService.getAllOrderedBoardgamesFromUser(orderId);

    }

    /*
     * Add boardgame to an order
     * Requires orderId, boardgame name and the quantity of boardgames to order
     * 
     */
    @ApiResponse(description = "Add boardgame to an order")
    @PostMapping("/{orderId}/addBoardgame")
    public ResponseEntity<String> addBoardgameToOrder(
            @PathVariable("orderId") int orderId, @RequestBody OrderedBoardgameDto obd)
            throws UnauthorizedException {
        try {
            return new ResponseEntity<String>(ordersService.addBoardgameToOrder(orderId, obd), HttpStatus.ACCEPTED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/{orderId}/delete")
    @ApiResponse(description = "Delete specified order")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId") int orderId)
            throws UnauthorizedException, ItemNotFoundException {
        try {
            return new ResponseEntity<String>(ordersService.deleteOrder(orderId), HttpStatus.OK);

        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>("Order with id " + orderId + " not found ", HttpStatus.NOT_FOUND);
        }

    }

    /*
     * Add a new order to the database
     * Request parameters are: date
     */
    @PostMapping(value = "/add")
    @ApiResponse(description = "Add a new order to the database")
    public ResponseEntity<String> addBoardgameReq(@RequestBody OrderDto orderDto) throws UnauthorizedException {

        try {
            return new ResponseEntity<String>(ordersService.addOrder(orderDto.getOrderDate()), HttpStatus.OK);

        } catch (IllegalArgumentException | NullPointerException | ParseException e) {
            return new ResponseEntity<String>("Inserted date " + orderDto.getOrderDate() + " isn't correct ",
                    HttpStatus.OK);
        }

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
        return new ApiError("Unauthorized", HttpStatus.UNAUTHORIZED);

    }

}
