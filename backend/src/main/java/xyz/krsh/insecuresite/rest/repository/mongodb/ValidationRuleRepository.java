package xyz.krsh.insecuresite.rest.repository.mongodb;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import xyz.krsh.insecuresite.rest.entities.mongodb.ValidationRuleDocument;

public interface ValidationRuleRepository extends MongoRepository<ValidationRuleDocument, String> {

    @Query("{'_id':  ?0}")
    Optional<ValidationRuleDocument> findDocumentById(String name);

}
