package xyz.krsh.insecuresite.rest.repository.mongodb;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import xyz.krsh.insecuresite.rest.entities.mongodb.BoardgameDocument;

public interface BoardgameDocumentRepository extends MongoRepository<BoardgameDocument, String> {

    @Query("{'_id':  ?0}")
    Optional<BoardgameDocument> findDocumentById(String name);

    @Query("{'_id': ?0}")
    Optional<List<BoardgameDocument>> findAllDocumentsById(String queryTerm);

}
