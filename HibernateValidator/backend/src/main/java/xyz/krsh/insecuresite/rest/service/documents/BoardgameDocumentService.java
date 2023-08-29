package xyz.krsh.insecuresite.rest.service.documents;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.entities.mongodb.BoardgameDocument;
import xyz.krsh.insecuresite.rest.repository.BoardgameRepository;
import xyz.krsh.insecuresite.rest.repository.mongodb.BoardgameDocumentRepository;

@Service
public class BoardgameDocumentService {

    @Autowired
    BoardgameRepository boardgameRepository;

    @Autowired
    BoardgameDocumentRepository boardgameDocumentRepository;

    protected static final Logger logger = LogManager.getLogger();

    public String createBoardgameDocument(BoardgameDto boardgameDto)
            throws IllegalAccessException, InvocationTargetException, NoSuchElementException {

        logger.info("Received boardgameDto with id: " + boardgameDto.toString());
        String name = boardgameDto.getName();
        Boardgame boardgame = boardgameRepository.findById(name).get();
        if (boardgame.getName() == null) {
            logger.warn("Boardgame with name " + name + " not found");
            throw new NoSuchElementException("Boardgame with name " + name + " not found");
        }

        logger.info("Looking for boardgame with specified id...");

        BoardgameDocument boardgameDocument = new BoardgameDocument();
        BeanUtils.copyProperties(boardgameDocument, boardgameDto);
        boardgameDocumentRepository.save(boardgameDocument);

        logger.info("Boardgame found, saved boardgame document with name " + boardgameDocument.getId());

        return "Saved boardgame document with name " + boardgameDocument.getId();

    }

    public BoardgameDocument readOneBoardgameDocument(String name) {
        logger.info("Querying Boardgame Document Repository by name: " + name);
        Optional<BoardgameDocument> result = boardgameDocumentRepository.findDocumentById(name);
        if (result.isEmpty()) {
            logger.warn("Query failed or BoardgameDocument with name" + name + " not found");
            return null;
        }
        return result.get();
    }

    public List<BoardgameDocument> readAllBoardgameDocument(String queryTerm) {
        logger.info("Querying Boardgame Document Repository with string: " + queryTerm);
        Optional<List<BoardgameDocument>> result = boardgameDocumentRepository.findAllDocumentsById(queryTerm);
        return result.isPresent() == true ? result.get() : null;

    }

    public String updateBoardgameDocument(String name, BoardgameDto boardgameDto)
            throws IllegalAccessException, InvocationTargetException {
        Optional<BoardgameDocument> result = boardgameDocumentRepository.findDocumentById(name);

        if (result.isPresent() == false) {
            logger.warn("Boardgame Document with name " + name + " not found");
            throw new NoSuchElementException("Boardgame Document with name " + name + " not found");
        }

        BoardgameDocument old = result.get();

        BoardgameDto newBoardgameDto = new BoardgameDto(name);
        BeanUtils.copyProperties(newBoardgameDto, old);

        System.out.println(newBoardgameDto.toString());

        if (boardgameDto.getPrice() > 0.0) {
            newBoardgameDto.setPrice(boardgameDto.getPrice());
        }

        if (boardgameDto.getQuantity() != 0) {
            newBoardgameDto.setQuantity(boardgameDto.getQuantity());
        }
        if (boardgameDto.getDescription() != null) {
            newBoardgameDto.setDescription(boardgameDto.getDescription());
        }

        BoardgameDocument newBoardgameDocument = old;
        BeanUtils.copyProperties(newBoardgameDocument, newBoardgameDto);
        boardgameDocumentRepository.save(newBoardgameDocument);

        logger.info("Successfully updated Boardgame Document with name " + name);
        return "Successfully updated boardgame document with name " + name;

    }

    public String deleteBoardgameDocument(String name) {
        logger.info("Trying to delete boardgame document with name " + name);
        Optional<BoardgameDocument> result = boardgameDocumentRepository.findById(name);
        BoardgameDocument toDelete = result.get();

        boardgameDocumentRepository.delete(toDelete);
        logger.info("Boardgame Document with name" + toDelete.getId() + " found and successfully deleted");
        return "Successfully deleted BoardgameDocument with name " + name;

    }

}
