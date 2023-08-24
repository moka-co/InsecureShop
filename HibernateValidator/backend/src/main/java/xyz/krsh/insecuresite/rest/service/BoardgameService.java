package xyz.krsh.insecuresite.rest.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.rest.dto.BoardgameDto;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.entities.OrderedBoardgames;
import xyz.krsh.insecuresite.rest.repository.BoardgameRepository;
import xyz.krsh.insecuresite.rest.repository.OrderedBoardgamesRepository;
import xyz.krsh.insecuresite.security.hibernateValidatorBootstrapping.MyMessageInterpolator;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Service
public class BoardgameService {

    protected static final Logger logger = LogManager.getLogger();

    private static Validator validator = Validation.byDefaultProvider().configure()
            .messageInterpolator(new MyMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

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

    public void addBoardgame(BoardgameDto boardgameDto)
            throws IllegalAccessException, InvocationTargetException {

        Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validate(boardgameDto);

        if (constraintViolations.size() > 0) {
            for (ConstraintViolation<BoardgameDto> cv : constraintViolations) {
                logger.warn(cv.getMessage());
            }
            throw new ValidationException("Invalid input received when adding a boardgame");
        }

        Boardgame newBoardgame = new Boardgame(boardgameDto.getName());
        BeanUtils.copyProperties(newBoardgame, boardgameDto);
        boardgameRepository.save(newBoardgame);

    }

    // TODO: first version of edit Boardgame()
    public void editBoardgame2(String boardgameName, BoardgameDto boardgameDto)
            throws ItemNotFoundException, IndexOutOfBoundsException {

        // List<Boardgame> queryResult = findByNameContaining(boardgameName);
        Boardgame boardgame = findByNameContaining(boardgameName).get(0);

        if (boardgameDto.getPrice() > 0.0) {
            Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validateValue(BoardgameDto.class,
                    "price", boardgameDto.getPrice());
            if (constraintViolations.size() > 0) {
                for (ConstraintViolation<BoardgameDto> cv : constraintViolations) {
                    logger.warn(cv.getMessage());
                }
                throw new IllegalArgumentException(
                        "Invalid price value when trying to edit boardgame " + boardgameName);
            }
            boardgame.setPrice(boardgameDto.getPrice());
        }

        if (boardgameDto.getQuantity() != 0) {
            Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validateValue(BoardgameDto.class,
                    "quantity", boardgameDto.getQuantity());
            if (constraintViolations.size() > 0) {
                for (ConstraintViolation<BoardgameDto> cv : constraintViolations) {
                    logger.warn(cv.getMessage());
                }
                throw new IllegalArgumentException(
                        "Invalid quantity when trying to edit boardgame " + boardgameName);
            }
            boardgame.setQuantity(boardgameDto.getQuantity());

        }
        if (boardgameDto.getDescription() != null) {
            Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validateValue(BoardgameDto.class,
                    "description", boardgameDto.getDescription());
            if (constraintViolations.size() > 0) {
                for (ConstraintViolation<BoardgameDto> cv : constraintViolations) {
                    logger.warn(cv.getMessage());
                }
                throw new IllegalArgumentException(
                        "Invalid description when trying to edit boardgame " + boardgameName);
            }
            boardgame.setDescription(boardgameDto.getDescription());
        }

        boardgameRepository.update(boardgame);
    }

    //TODO: another version of editBoardgame()
    public void editBoardgame(String boardgameName, BoardgameDto boardgameDto)
            throws ItemNotFoundException, IndexOutOfBoundsException, IllegalAccessException, InvocationTargetException {

        // List<Boardgame> queryResult = findByNameContaining(boardgameName);
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

        Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validate(newBoardgameDto);
        if (constraintViolations.size() > 0) {
            for (ConstraintViolation<BoardgameDto> cv : constraintViolations) {
                logger.warn(cv.getMessage());
                throw new IllegalArgumentException(
                        cv.getMessage() + " - when trying to edit boardgame " + boardgameName);
            }
        }

        BeanUtils.copyProperties(boardgame, newBoardgameDto);
        boardgameRepository.update(boardgame);
    }

    public String deleteBoardgame(String name) {

        // Validate input
        Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validateValue(BoardgameDto.class,
                "name", name);
        if (constraintViolations.size() > 0) {
            for (ConstraintViolation<BoardgameDto> cv : constraintViolations) {
                logger.warn(cv.getMessage());
            }
            throw new IllegalArgumentException("Invalid name " + name);
        }

        Optional<List<OrderedBoardgames>> obQueryResult = orderedBoardgameRepository.findByBoardgameName(name);
        Optional<Boardgame> bQueryResult = boardgameRepository.findById(name);

        if (obQueryResult.isEmpty() || bQueryResult.isEmpty()) {
            logger.fatal("Illegal state, cannot find OrderedBoardgame or Boardgame Entity associated to Order");
            throw new RuntimeException("Internal server error");
        }

        for (OrderedBoardgames ob : obQueryResult.get()) {
            orderedBoardgameRepository.delete(ob);
        }
        boardgameRepository.delete(bQueryResult.get());
        return "Successfully deleted " + name + " ";

    }

}
