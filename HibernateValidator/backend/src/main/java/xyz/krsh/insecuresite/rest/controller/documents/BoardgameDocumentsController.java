package xyz.krsh.insecuresite.rest.controller.documents;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.mongodb.BoardgameDocument;
import xyz.krsh.insecuresite.rest.service.documents.BoardgameDocumentService;

@RestController
@RequestMapping("/api/document/boardgame")
@Tag(name = "Boardgame Documents Controller", description = "This controller is responsible for managing validation documents for boardgame")
public class BoardgameDocumentsController {

    @Autowired
    BoardgameDocumentService boardgameDocumentService;

    @PostMapping(value = "/add")
    public ResponseEntity<String> createBoardgameDocument(@RequestBody BoardgameDto boardgameDto) {
        try {
            System.out.println(boardgameDto.toString());
            String message = boardgameDocumentService.createBoardgameDocument(boardgameDto);
            return new ResponseEntity<String>(message, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            String message = "Boardgame with name " + boardgameDto.getName() + " not found!";
            return new ResponseEntity<String>(message, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e + " = " + e.getMessage());
            return new ResponseEntity<String>("Error" + e, HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/{name}")
    public BoardgameDocument readOneBoardgameDocument(@PathVariable String name) {
        return boardgameDocumentService.readOneBoardgameDocument(name);
    }

    @GetMapping
    public List<BoardgameDocument> readAllBoardgameDocument(
            @RequestParam(name = "q", defaultValue = "") String queryTerm) {
        return boardgameDocumentService.readAllBoardgameDocument(queryTerm);
    }

    @PostMapping("/{name}/delete")
    public ResponseEntity<String> deleteOneBoardgameDocument(@PathVariable String name) {
        try {
            String message = boardgameDocumentService.deleteBoardgameDocument(name);
            return new ResponseEntity<String>(message + name,
                    HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<String>("Error " + e, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{name}/edit")
    public ResponseEntity<String> updateOneBoardDocument(@PathVariable String name,
            @RequestBody BoardgameDto boardgameDto) {
        try {
            String message = boardgameDocumentService.updateBoardgameDocument(name, boardgameDto);
            return new ResponseEntity<String>(message, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error " + e, HttpStatus.BAD_REQUEST);
        }
    }

}
