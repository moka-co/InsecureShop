package xyz.krsh.insecuresite.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.service.BoardgameService;
import xyz.krsh.insecuresite.rest.service.ESAPIValidatorService;
import xyz.krsh.insecuresite.security.LoggerWrapper;

import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

@RestController
@RequestMapping("/api/boardgames")
@Tag(name = "Boardgame Controller", description = "This controller is responsable for handling HTTP request related to boardgame management")
public class BoardgameController {

    private static final LoggerWrapper logger = new LoggerWrapper();

    @Autowired
    BoardgameService boardgameService;

    @Autowired
    ESAPIValidatorService validator;

    /*
     * Returns every boardgame or the ones that match the query value
     */
    @GetMapping
    @ResponseBody
    @ApiResponse(description = "Returns every boardgame that matches the query value")
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
    @ApiResponse(description = "Return a boardgame from the database querying by his primary key")
    public Boardgame getById(@PathVariable String name, HttpServletRequest request) throws ItemNotFoundException {
        try {
            return boardgameService.findByNameContaining(name).get(0);
        } catch (ItemNotFoundException e) {
            logger.log(
                    "Cannot find Boardgame with name + '" + name + "'", request, HttpStatus.BAD_REQUEST.value());
            return null;
        }
    }

    /*
     * Add a new boardgame to the database by REST call
     * Request parameters are: name, price, quantity and description
     */
    @PostMapping(value = "/add")
    @ApiResponse(description = "Add a new boardgame to the database")
    public ResponseEntity<String> addBoardgameReq(@RequestBody BoardgameDto boardgameDto, HttpServletRequest request) {
        try {
            boardgameService.addBoardgame(boardgameDto, request);
            return new ResponseEntity<String>("Successfully added " + boardgameDto.getName(), HttpStatus.ACCEPTED);

        } catch (ValidationException e) {
            logger.log(
                    "Invalid bean: " + boardgameDto.toString(), request, HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            logger.log(
                    "Catched exception" + e + " with input: " + boardgameDto.toString(), request,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<String>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /*
     * Edit an existing boardgame by specifying his name and optional parameters
     * that will replace the older ones
     * Return the Boardgame with newest values
     */

    @PostMapping(value = "/{name}/edit")
    @ApiResponse(description = "Edit an existing boardgame by specifying his name and optional parameters")
    public ResponseEntity<String> ediBoardgame(@PathVariable String name, @RequestBody BoardgameDto boardgameDto,
            HttpServletRequest request) {

        try {
            boardgameService.editBoardgame(name, boardgameDto, request);
            return new ResponseEntity<String>("Successfully edited boardgame " + name, HttpStatus.OK);
        } catch (ItemNotFoundException | IndexOutOfBoundsException e2) {
            return new ResponseEntity<String>("Boardgame " + name + " not found ", HttpStatus.NOT_FOUND);
        } catch (ValidationException | IllegalArgumentException e) {
            logger.log(
                    "Invalid bean: " + boardgameDto.toString(), request, HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.log(
                    "Catched exception" + e + " with input: " + boardgameDto.toString(), request,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<String>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * Delete a Boardgame by specifing his name (id)
     * Return a success message
     */
    @PostMapping(value = "/{name}/delete")
    @ApiResponse(description = "Delete a boardgame by his name")
    public ResponseEntity<String> deleteBoardgame(@PathVariable String name, Principal principal,
            HttpServletRequest request) throws EmptyResultDataAccessException {
        try {
            return new ResponseEntity<String>(boardgameService.deleteBoardgame(name, request),
                    HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.log(
                    "Invalid bean: cannot find boardgame with name '" + name + "'", request,
                    HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            System.out.println(e);
            logger.log(
                    "Caught exception " + e + " with Boardgame name: '" + name + "'", request,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<String>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
