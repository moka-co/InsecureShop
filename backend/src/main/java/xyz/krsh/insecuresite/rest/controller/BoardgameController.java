package xyz.krsh.insecuresite.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
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
import xyz.krsh.insecuresite.rest.dao.Boardgame;
import xyz.krsh.insecuresite.rest.repository.BoardgameRepository;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/boardgames")
public class BoardgameController {

    @Autowired
    BoardgameRepository repo;

    /*
     * Returns every boardgame or the ones that match the query value
     */
    @GetMapping
    @ResponseBody
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    public List<Boardgame> find(@RequestParam(name = "q", defaultValue = "") String queryTerm)
            throws ItemNotFoundException {

        List<Boardgame> query = repo.findByNameContaining(queryTerm);
        if (query.isEmpty()) {
            throw new ItemNotFoundException();
        } else {
            return query;
        }
    }

    /*
     * Get an existing boardgame querying using his name (primary key)
     * Returns only the first result
     */

    @GetMapping("/{name}")
    @ResponseBody
    Boardgame getById(@PathVariable String name) throws IndexOutOfBoundsException {
        return repo.findByNameContaining(name).get(0);
    }

    /*
     * Add a new boardgame to the database by REST call
     * Request parameters are: name, price, quantity and description
     */
    @GetMapping("/add")
    @ResponseBody
    public Boardgame addBoardgameReq(@RequestParam("name") String name,
            @RequestParam("price") float price,
            @RequestParam("quantity") int quantity,
            @RequestParam("description") String description,
            HttpServletRequest request) throws MissingServletRequestParameterException {

        Boardgame result = repo.save(new Boardgame(name, price, quantity, description));
        return result;
    }

    /*
     * Edit an existing boardgame by specifying his name and optional parameters
     * that will replace the older ones
     * Return the Boardgame with newest values
     */

    @GetMapping(value = "/{name}/edit")
    @ResponseBody
    public Boardgame ediBoardgame(@PathVariable String name,
            @RequestParam(value = "price", required = false) Float price,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam(value = "description", required = false) String description,
            HttpServletRequest request) {

        List<Boardgame> queryResult = repo.findByNameContaining(name);
        Boardgame boardgame;

        if (queryResult.size() == 0 || queryResult.isEmpty() == true) {
            throw new IndexOutOfBoundsException();
        } else {
            boardgame = queryResult.get(0);
        }

        repo.delete(boardgame); // Delete the old boardgame

        /* Check existance of params */
        boolean priceParamExists = request.getParameterMap().containsKey("price");
        boolean quantityParamExists = request.getParameterMap().containsKey("quantity");
        boolean descriptionParamExists = request.getParameterMap().containsKey("description");

        // if price exists as parameter in the HTTP request, change the price
        if (priceParamExists) {
            boardgame.setPrice(price);
        }
        if (quantityParamExists) {
            boardgame.setQuantity(quantity);
        }
        if (descriptionParamExists) {
            boardgame.setDescription(description);
        }

        repo.save(boardgame); // Save the new boardgame
        return boardgame;
    }

    /*
     * Delete a Boardgame by specifing his name (id)
     * Return a success message
     * 
     */
    @GetMapping("/{name}/delete")
    public String deleteBoardgame(@PathVariable String name) throws EmptyResultDataAccessException {
        repo.deleteById(name);

        return "Successfully deleted " + name + " ";
    }

    /*
     * Exception Handlers
     */

    // Occurrs when you can't find any boardgames
    @ExceptionHandler({ ItemNotFoundException.class, IndexOutOfBoundsException.class,
            EmptyResultDataAccessException.class })

    public ApiError handleIndexOutOfBoundsException() {
        return new ApiError("Bordgame not found, retry", HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler({ MissingServletRequestParameterException.class })
    public ApiError handleMissingParametersException() {
        return new ApiError("Bad Paremeters: required name, price, quantity and description", HttpStatus.BAD_REQUEST);

    }

}
