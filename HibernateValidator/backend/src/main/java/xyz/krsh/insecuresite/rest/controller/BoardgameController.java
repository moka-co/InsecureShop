package xyz.krsh.insecuresite.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import xyz.krsh.insecuresite.exceptions.ApiError;
import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.service.BoardgameService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@RestController
@RequestMapping("/api/boardgames")
public class BoardgameController {

    @Autowired
    BoardgameService boardgameService;

    /*
     * Returns every boardgame or the ones that match the query value
     */
    @GetMapping
    @ResponseBody
    public List<Boardgame> find(@RequestParam(name = "q", defaultValue = "") String queryTerm)
            throws ItemNotFoundException {
        return boardgameService.findByNameContaining(queryTerm);
    }

    /*
     * Get an existing boardgame querying using his name (primary key)
     * Returns only the first result
     */
    @GetMapping("/{name}")
    @ResponseBody
    Boardgame getById(@PathVariable String name) throws IndexOutOfBoundsException {
        return boardgameService.getById(name);
    }

    /*
     * Add a new boardgame to the database by REST call
     * Request parameters are: name, price, quantity and description
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<String> addBoardgameReq(@RequestBody BoardgameDto boardgameDto)
            throws IllegalAccessException, InvocationTargetException {

        boardgameService.addBoardgame(boardgameDto);

        String responseData = "Received data: " + boardgameDto.toString();
        return new ResponseEntity<String>(responseData, HttpStatus.ACCEPTED);

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
            HttpServletRequest request) throws ItemNotFoundException, ConstraintViolationException {

        return boardgameService.editBoardgame(name, price, quantity, description, request);
    }

    /*
     * Delete a Boardgame by specifing his name (id)
     * Return a success message
     * 
     */
    @GetMapping("/{name}/delete")
    public String deleteBoardgame(@PathVariable String name) throws EmptyResultDataAccessException {
        return boardgameService.deleteBoardgame(name);
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
        return new ApiError("Bad Parameters: required name, price, quantity and description", HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ApiError handleConstraintViolationException() {
        return new ApiError("Invalid input", HttpStatus.FORBIDDEN);

    }

}
