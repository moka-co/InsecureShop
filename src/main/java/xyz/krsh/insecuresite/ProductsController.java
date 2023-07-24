package xyz.krsh.insecuresite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/boardgames")
public class ProductsController {

    @Autowired
    ProductRepository repo;

    @GetMapping
    public List<Boardgame> findAll() {
        for (Boardgame bg : repo.findAll()) {
            System.out.println(bg.toString());

        }

        List<Boardgame> listbg = new LinkedList<Boardgame>();
        for (Boardgame e : repo.findAll()) {
            if (listbg.add(e) == false) {
                System.out.println("Cannot add element " + e.toString());
            }
        }
        return listbg;
    }

    @GetMapping("/items/{id}")
    Boardgame getById(@PathVariable String id) {
        return repo.findById(id).get();
    }

}
