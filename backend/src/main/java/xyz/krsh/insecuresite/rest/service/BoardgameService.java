package xyz.krsh.insecuresite.rest.service;

import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.entities.OrderedBoardgames;
import xyz.krsh.insecuresite.rest.repository.BoardgameRepository;
import xyz.krsh.insecuresite.rest.repository.OrderedBoardgamesRepository;
import xyz.krsh.insecuresite.security.LoggerWrapper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Service
public class BoardgameService {

    protected static final Logger logger = LogManager.getLogger();
    protected static final LoggerWrapper loggerSplunk = new LoggerWrapper();

    @Autowired
    private static ESAPIValidatorService validator;

    @Autowired
    BoardgameRepository boardgameRepository;

    @Autowired
    OrderedBoardgamesRepository orderedBoardgameRepository;

    // This method is used in front end query and API calls
    public List<Boardgame> findByNameContaining(String queryTerm) throws ItemNotFoundException {
        List<Boardgame> queryResult = boardgameRepository.findByNameContaining(queryTerm);

        if (queryResult.isEmpty()) {
            throw new ItemNotFoundException();
        }

        return queryResult;

    }

    public void addBoardgame(BoardgameDto boardgameDto, Principal principal, HttpServletRequest request)
            throws IllegalAccessException, InvocationTargetException, org.owasp.esapi.errors.ValidationException {

        logger.info("Beginning validation for " + boardgameDto.toString());
        if (validator.validateBean(boardgameDto, "boardgame_v2") == false) {
            loggerSplunk.log(request, principal, "Validation failed for " + boardgameDto.toString());
            return;
        }
        Boardgame newBoardgame = new Boardgame(boardgameDto.getName());
        BeanUtils.copyProperties(newBoardgame, boardgameDto);
        boardgameRepository.save(newBoardgame);

    }

    public void editBoardgame(String boardgameName, BoardgameDto boardgameDto, Principal principal,
            HttpServletRequest request)
            throws ItemNotFoundException, IndexOutOfBoundsException, IllegalAccessException, InvocationTargetException,
            org.owasp.esapi.errors.ValidationException {

        Boardgame boardgame = findByNameContaining(boardgameName).get(0);
        BoardgameDto newBoardgameDto = new BoardgameDto();
        BeanUtils.copyProperties(newBoardgameDto, boardgame);

        if (boardgameDto.getPrice() > 0.0) {
            newBoardgameDto.setPrice(boardgameDto.getPrice());
        }

        if (boardgameDto.getQuantity() != 0) {
            newBoardgameDto.setQuantity(boardgameDto.getQuantity());
        }
        if (boardgameDto.getDescription() != null) {
            newBoardgameDto.setDescription(boardgameDto.getDescription());
        }

        // Validation
        loggerSplunk.log(request, principal, "Beginning validation for " + newBoardgameDto.toString());
        if (validator.validateBean(newBoardgameDto, "boardgame_v2") == false) {
            loggerSplunk.log(request, principal, "Validation failed for " + newBoardgameDto.toString());
            return;
        }

        BeanUtils.copyProperties(boardgame, newBoardgameDto);
        boardgameRepository.update(boardgame);
    }

    public String deleteBoardgame(String name, Principal principal, HttpServletRequest request)
            throws org.owasp.esapi.errors.ValidationException {

        loggerSplunk.log(request, principal, "Requested deletion for boardgame with name " + name);
        BoardgameDto boardgameDto = new BoardgameDto(name, (float) 1.0, 1, name);
        if (validator.validateBean(boardgameDto, "boardgame_v2") == false) {
            loggerSplunk.log(request, principal, "Validation failed for Boardgame name " + name);
            return "failed";
        }

        logger.info("Ended Validation in BoardgameService.deleteBoardgame() with success");

        Optional<List<OrderedBoardgames>> obQueryResult = orderedBoardgameRepository.findByBoardgameName(name);
        Optional<Boardgame> bQueryResult = boardgameRepository.findById(name);

        if (obQueryResult.isEmpty() || bQueryResult.isEmpty()) {
            logger.fatal("Illegal state, cannot find OrderedBoardgame or Boardgame Entity associated to Order");
            loggerSplunk.log(request, principal, "Illegal Access, cannot find boardgame with " + name);
            throw new RuntimeException("Internal server error");
        }

        for (OrderedBoardgames ob : obQueryResult.get()) {
            orderedBoardgameRepository.delete(ob);
        }

        boardgameRepository.delete(bQueryResult.get());
        return "Successfully deleted " + name + " ";

    }

}
