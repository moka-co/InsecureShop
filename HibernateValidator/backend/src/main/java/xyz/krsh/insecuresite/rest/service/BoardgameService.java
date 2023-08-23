package xyz.krsh.insecuresite.rest.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.ValidationException;

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

    //TODO: make this method with BoardgameDto
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
            boardgame.setDescription(description);
        }

        Set<ConstraintViolation<Boardgame>> constraintViolations = validator.validate(boardgame);
        // if there are constraint violations

        if (constraintViolations.size() > 0) {
            for (ConstraintViolation<Boardgame> cv : constraintViolations) {
                System.out.println(cv.getMessage());
            }
        }

        boardgameRepository.update(boardgame);
        return boardgame;
    }

    //Todo: Make this method with BoardgameDto
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
        }

        return "Successfully deleted " + name + " ";

    }

}
