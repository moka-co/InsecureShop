package xyz.krsh.insecuresite.rest.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
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

@Service
public class BoardgameService {

    private static Validator validator = Validation.byDefaultProvider().configure()
            .messageInterpolator(new MyMessageInterpolator())
            .buildValidatorFactory()
            .getValidator();

    @Autowired
    BoardgameRepository boardgameRepository;

    @Autowired
    OrderedBoardgamesRepository orderedBoardgameRepository;

    public List<Boardgame> findByNameContaining(String queryTerm) throws ItemNotFoundException {
        List<Boardgame> queryResult = boardgameRepository.findByNameContaining(queryTerm);

        if (queryResult.isEmpty()) {
            throw new ItemNotFoundException();
        }

        return queryResult;

    }

    public Boardgame getById(String name) {
        Boardgame boardgame = boardgameRepository.findByNameContaining(name).get(0);
        return boardgame;
    }

    public void addBoardgame(BoardgameDto boardgameDto)
            throws IllegalAccessException, InvocationTargetException {

        // TODO: Why if you throw validate it isn't thrown ValidationException (in this
        // instance?)
        Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validate(boardgameDto);

        if (constraintViolations.size() > 0) {
            for (ConstraintViolation<BoardgameDto> cv : constraintViolations) {
                System.out.println(cv.getMessage());
            }
        } else {
            Boardgame newBoardgame = new Boardgame(boardgameDto.getName());
            BeanUtils.copyProperties(newBoardgame, boardgameDto);

            boardgameRepository.save(newBoardgame);
        }

    }

    // TODO: Refactoring needed
    public void editBoardgame(String boardgameName, BoardgameDto boardgameDto)
            throws ItemNotFoundException {
        Boardgame boardgame;
        boardgameDto.setName(boardgameName);
        boolean flag = false;

        List<Boardgame> queryResult = this.findByNameContaining(boardgameName);
        if (queryResult.size() == 0 || queryResult.isEmpty() == true) {
            throw new IndexOutOfBoundsException();
        } else {
            boardgame = queryResult.get(0);
        }

        if (boardgameDto.getPrice() > 0.0) {
            Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validateValue(BoardgameDto.class,
                    "price", boardgameDto.getPrice());
            if (constraintViolations.size() > 0) {
                for (ConstraintViolation<BoardgameDto> cv : constraintViolations) {
                    System.out.println(cv.getMessage());
                }
            } else {
                flag = true;
                boardgame.setPrice(boardgameDto.getPrice());
            }

        }
        if (boardgameDto.getQuantity() > 0) {
            Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validateValue(BoardgameDto.class,
                    "quantity", boardgameDto.getQuantity());
            if (constraintViolations.size() > 0) {
                for (ConstraintViolation<BoardgameDto> cv : constraintViolations) {
                    System.out.println(cv.getMessage());
                }
            } else {
                flag = true;
                boardgame.setQuantity(boardgameDto.getQuantity());
            }

        }
        if (boardgameDto.getDescription() != null && !boardgameDto.getDescription().equals("")) {
            Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validateValue(BoardgameDto.class,
                    "description", boardgameDto.getDescription());
            if (constraintViolations.size() > 0) {
                for (ConstraintViolation<BoardgameDto> cv : constraintViolations) {
                    System.out.println(cv.getMessage());
                }
            } else {
                flag = true;
                boardgame.setDescription(boardgameDto.getDescription());
            }
        }

        if (flag == true) {
            boardgameRepository.update(boardgame);
        }
    }

    public String deleteBoardgame(String name) {

        // Validate input
        Set<ConstraintViolation<BoardgameDto>> constraintViolations = validator.validateValue(BoardgameDto.class,
                "name", name);
        if (constraintViolations.size() > 0) {
            for (ConstraintViolation<BoardgameDto> cv : constraintViolations) { // Stampa l'errore
                System.out.println(cv.getMessage());
            }
            return "Invalid input";
        }

        Optional<List<OrderedBoardgames>> obQueryResult = orderedBoardgameRepository.findByBoardgameName(name);
        Optional<Boardgame> bQueryResult = boardgameRepository.findById(name);

        if (obQueryResult.isPresent() && bQueryResult.isPresent()) {
            List<OrderedBoardgames> list = obQueryResult.get();
            for (OrderedBoardgames ob : list) {
                orderedBoardgameRepository.delete(ob);
            }

            Boardgame boardgame = bQueryResult.get();
            boardgameRepository.delete(boardgame);
        }

        return "Successfully deleted " + name + " ";

    }

}
