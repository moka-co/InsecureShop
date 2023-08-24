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

import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.service.BoardgameService;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

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
    public Boardgame getById(@PathVariable String name) throws ItemNotFoundException {
        return boardgameService.findByNameContaining(name).get(0);
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

        } catch (ValidationException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);

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
        } catch (ValidationException | IllegalArgumentException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * Delete a Boardgame by specifing his name (id)
     * Return a success message
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

}
