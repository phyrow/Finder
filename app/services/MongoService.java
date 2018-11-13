package services;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

@Singleton
public class MongoService {

  private static final String MONGODB_URI = System.getenv("MONGODB_URI");

  private static final String REGEX_QUERY = "^{0}";

  private static final String COLLECTION_NAME = "finder";

  private static final String SUB_THEME_FIELD = "subtema";

  private static final String TITLE_FIELD = "titulo";

  private static final String AUTHOR_NAME_FIELD = "authorname";

  private static final String VIEWS_FIELD = "vistas";

  private static final String LINK_FIELD = "link";

  private static final String IMAGE_FIELD = "imagen";

  private static final String AUTH_LINK = "aut_link";

  private static final String LIKES_FIELD = "likes";

  private final MongoDatabase mongoDatabase;

  private final MongoCollection<Document> collection;

  private final Logger logger = LoggerFactory.getLogger(MongoService.class);

  public MongoService() throws MongoException {
    try {
      final MongoClientURI mongoClientURI = new MongoClientURI(MONGODB_URI);
      final MongoClient mongoClient = new MongoClient(mongoClientURI);
      mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
      collection = mongoDatabase.getCollection(COLLECTION_NAME);

      final List<String> indexFields = new ArrayList<>();
      indexFields.add(LIKES_FIELD);
      indexFields.add(VIEWS_FIELD);
      collection.createIndex(Indexes.text(SUB_THEME_FIELD));
      collection.createIndex(Indexes.descending(indexFields));
      collection.createIndex(Indexes.ascending(TITLE_FIELD));
    } catch (Exception exception) {
      logger.error(exception.getMessage(), exception);
      throw new MongoException(exception.getMessage());
    }
  }

  public String getMessage(final String query, final String sort) {
    final MongoCursor<Document> iterator = collection
            .find(or(regex(SUB_THEME_FIELD, MessageFormat.format(REGEX_QUERY, query)),
                    regex(TITLE_FIELD, MessageFormat.format(REGEX_QUERY, query)),
                    regex(AUTHOR_NAME_FIELD, MessageFormat.format(REGEX_QUERY, query)))).projection(
                    fields(include(SUB_THEME_FIELD, TITLE_FIELD, AUTHOR_NAME_FIELD, AUTH_LINK, LINK_FIELD, IMAGE_FIELD,
                            VIEWS_FIELD, LIKES_FIELD), excludeId()))
            .sort(TITLE_FIELD.equals(sort) ? Sorts.ascending(sort) : Sorts.descending(sort)).iterator();
    final BasicDBList list = new BasicDBList();
    while (iterator.hasNext()) {
      list.add(iterator.next());
    }
    System.out.println(list.size());
    return JSON.serialize(list);
  }
}