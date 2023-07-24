package xyz.krsh.insecuresite;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/boardgames")
public class BoardgameController {

    @Autowired
    BoardgameRepository repo;

    /*
     * Returns every boardgame in the database
     */
    @GetMapping
    public List<Boardgame> findAll() {

        // Unfortunately, repo.finAll() returns an Iterator,
        // and i cannot cast it into a LinkedList
        List<Boardgame> listbg = new LinkedList<Boardgame>();
        for (Boardgame e : repo.findAll()) {
            if (listbg.add(e) == false) {
                System.out.println("Cannot add element " + e.toString());
            }
        }
        return listbg;
    }

    /*
     * Get an existing boardgame querying using his name (primary key)
     * Returns only the first result
     */

    @GetMapping("/{name}")
    Boardgame getById(@PathVariable String name) {
        return repo.findByNameContaining(name).get(0);
    }

    /*
     * Add a new boardgame to the database by REST call
     * Request parameters are: name, price, quantity and description
     */
    @GetMapping("/add")
    public Boardgame addBoardgame(@RequestParam("name") String name,
            @RequestParam("price") float price,
            @RequestParam("quantity") int quantity,
            @RequestParam("description") String description) {
        Boardgame result = repo.save(new Boardgame(name, price, quantity, description));
        return result;
    }

    /*
     * Delete a Boardgame by specifing his name (id)
     * Return a success message
     * 
     */
    @GetMapping("/{name}/delete")
    public String deleteBoardgame(@PathVariable String name) {
        repo.deleteById(name);

        return "Successfully deleted " + name + " ";
    }

}
