package xyz.krsh.insecuresite.rest.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import xyz.krsh.insecuresite.exceptions.ApiError;
import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.service.BoardgameService;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/boardgames")
@Tag(name = "Boardgame Controller", description = "This controller is responsable for handling HTTP request related to boardgame management")
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
     * Retrieve a boardgame by his name
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
    @PostMapping(value = "/add")
    public ResponseEntity<String> addBoardgameReq(@RequestBody BoardgameDto boardgameDto) {
        try {
            boardgameService.addBoardgame(boardgameDto);
            return new ResponseEntity<String>("Successfully added " + boardgameDto.getName(), HttpStatus.ACCEPTED);

        } catch (Exception e) {
            return new ResponseEntity<String>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /*
     * Edit an existing boardgame by specifying his name and optional parameters
     * that will replace the older ones
     * Return the Boardgame with newest values
     */

    @PostMapping(value = "/{name}/edit")
    public ResponseEntity<String> ediBoardgame(@PathVariable String name, @RequestBody BoardgameDto boardgameDto,
            HttpServletRequest request) {

        try {
            boardgameService.editBoardgame(name, boardgameDto);
            return new ResponseEntity<String>("Successfully edited boardgame " + name, HttpStatus.OK);
        } catch (ItemNotFoundException | IndexOutOfBoundsException e2) {
            return new ResponseEntity<String>("Boardgame " + name + " not found ", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<String>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * Delete a Boardgame by specifing his name (id)
     * Return a success message
     * 
     */
    @PostMapping(value = "/{name}/delete")
    public ResponseEntity<String> deleteBoardgame(@PathVariable String name) throws EmptyResultDataAccessException {
        try {
            return new ResponseEntity<String>(boardgameService.deleteBoardgame(name), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<String>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

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

}
