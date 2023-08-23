package xyz.krsh.insecuresite.rest.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import xyz.krsh.insecuresite.exceptions.ItemNotFoundException;
import xyz.krsh.insecuresite.rest.entities.Boardgame;
import xyz.krsh.insecuresite.rest.entities.OrderedBoardgames;
import xyz.krsh.insecuresite.rest.repository.BoardgameRepository;
import xyz.krsh.insecuresite.rest.repository.OrderedBoardgamesRepository;
import xyz.krsh.insecuresite.security.hibernateValidatorBootstrapping.MyMessageInterpolator;

@Service
public class BoardgameService {

    // private static Validator validator =
    // Validation.buildDefaultValidatorFactory().getValidator();

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

    public Boardgame addBoardgame(String name, float price, int quantity, String description) {
        Boardgame newBoardgame = new Boardgame(name, price, quantity, description);

        Set<ConstraintViolation<Boardgame>> constraintViolations = validator.validate(newBoardgame);

        /*
         * if (constraintViolations.size() > 0) {
         * for (ConstraintViolation<Boardgame> cv : constraintViolations) {
         * System.out.println(
         * "Invalid input for class: " + cv.getRootBeanClass());
         * System.out.println(
         * "Invalid value: " + cv.getInvalidValue() + " triggered message error: " +
         * cv.getMessage());
         * }
         * }
         */

        boardgameRepository.save(newBoardgame);
        return newBoardgame;

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
