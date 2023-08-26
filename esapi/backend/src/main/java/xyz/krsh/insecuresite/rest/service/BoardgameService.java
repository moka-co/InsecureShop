package xyz.krsh.insecuresite.rest.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.codecs.MySQLCodec.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.entities.OrderedBoardgames;
import xyz.krsh.insecuresite.rest.repository.BoardgameRepository;
import xyz.krsh.insecuresite.rest.repository.OrderedBoardgamesRepository;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.commons.beanutils.BeanUtils;

@Service
public class BoardgameService {
    protected static final Logger logger = LogManager.getLogger();

    MySQLCodec codec = new MySQLCodec(Mode.STANDARD); // Codec for mysql encoding

    @Autowired
    BoardgameRepository boardgameRepository;

    @Autowired
    OrderedBoardgamesRepository orderedBoardgameRepository;

    public List<Boardgame> findByNameContaining(String queryTerm) throws ItemNotFoundException {
        List<Boardgame> queryResult = boardgameRepository.findByNameContaining(queryTerm);

        if (queryResult.isEmpty()) {
            throw new ItemNotFoundException();
        }

        /*
         * Example of canonicalization + encoding then encoding for HTML
         */
        for (Boardgame b : queryResult) {
            String descr = b.getDescription();
            String canonForm = ESAPI.encoder().canonicalize(descr, false, false);
            descr = ESAPI.encoder().encodeForHTML(canonForm);
            b.setDescription(descr);
        }

        return queryResult;

    }

    public Boardgame getById(String name) {
        Boardgame boardgame = boardgameRepository.findByNameContaining(name).get(0);
        return boardgame;
    }

    // Old
    public Boardgame addBoardgame(String name, float price, int quantity, String description) {

        // Example sql encoding
        String encodedDescr = ESAPI.encoder().canonicalize(description, false, false);
        encodedDescr = ESAPI.encoder().encodeForSQL(codec, encodedDescr);

        Boardgame newBoardgame = new Boardgame(name, price, quantity, encodedDescr);

        boardgameRepository.save(newBoardgame);
        return newBoardgame;

    }

    /*
     * New
     */
    public void addBoardgame(BoardgameDto boardgameDto)
            throws IllegalAccessException, InvocationTargetException {

        // Example sql encoding
        String encodedDescr = ESAPI.encoder().canonicalize(boardgameDto.getDescription(), false, false);
        encodedDescr = ESAPI.encoder().encodeForSQL(codec, encodedDescr);
        boardgameDto.setDescription(encodedDescr);

        Boardgame newBoardgame = new Boardgame(boardgameDto.getName());
        BeanUtils.copyProperties(newBoardgame, boardgameDto);
        boardgameRepository.save(newBoardgame);
        logger.info("Saved new boardgame: " + newBoardgame.getName());

    }

    public Boardgame editBoardgame(String name, Float price, Integer quantity, String description,
            HttpServletRequest request)
            throws ItemNotFoundException {
        Boardgame boardgame;

        List<Boardgame> queryResult = this.findByNameContaining(name);
        if (queryResult.size() == 0 || queryResult.isEmpty() == true) {
            throw new IndexOutOfBoundsException();
        } else {
            boardgame = queryResult.get(0);
        }

        // Check existance of params
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

            // Encode for SQL
            description = ESAPI.encoder().canonicalize(description);
            description = ESAPI.encoder().encodeForSQL(codec, description);
            boardgame.setDescription(description);
        }

        // update the boardgames with new values
        boardgameRepository.update(boardgame);
        return boardgame;
    }

    public void editBoardgame(String boardgameName, BoardgameDto boardgameDto)
            throws ItemNotFoundException, IndexOutOfBoundsException, IllegalAccessException, InvocationTargetException {

        Boardgame boardgame = findByNameContaining(boardgameName).get(0);
        BoardgameDto newBoardgameDto = new BoardgameDto();
        BeanUtils.copyProperties(newBoardgameDto, boardgame);

        logger.info("Begin validation in BoardgameService.editBoardgame()");
        if (boardgameDto.getPrice() > 0.0) {
            newBoardgameDto.setPrice(boardgameDto.getPrice());
        }

        if (boardgameDto.getQuantity() != 0) {
            newBoardgameDto.setQuantity(boardgameDto.getQuantity());
        }
        if (boardgameDto.getDescription() != null) {
            String description = ESAPI.encoder().canonicalize(boardgameDto.getDescription());
            description = ESAPI.encoder().encodeForSQL(codec, description);
            newBoardgameDto.setDescription(boardgameDto.getDescription());
        }

        logger.info("Validation in BoardgameService.addBoardgame() ended with success");
        BeanUtils.copyProperties(boardgame, newBoardgameDto);
        boardgameRepository.update(boardgame);
    }

    public String deleteBoardgame(String name) {
        Optional<List<OrderedBoardgames>> obQueryResult = orderedBoardgameRepository.findByBoardgameName(name);
        Optional<Boardgame> bQueryResult = boardgameRepository.findById(name);

        if (obQueryResult.isPresent() && bQueryResult.isPresent()) {
            List<OrderedBoardgames> list = obQueryResult.get();
            for (OrderedBoardgames ob : list) {
                orderedBoardgameRepository.delete(ob);
            }

            Boardgame boardgame = bQueryResult.get();
            boardgameRepository.delete(boardgame);
            logger.info("Deleted boardgame " + boardgame.getName());
        }

        return "Successfully deleted " + name + " ";

    }

}
