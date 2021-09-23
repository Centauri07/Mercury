package me.centauri07.mercury.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.centauri07.mercury.util.PrivateUtil;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

public class MongoConnection {
    private final ConnectionString connectionString = new ConnectionString(
            String.format("mongodb+srv://Mercury:%s@school.ie3m7.mongodb.net/myFirstDatabase?retryWrites=true&w=majority",
            PrivateUtil.DATABASE_PASSWORD)
    );
    CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
    CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
    private final MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .codecRegistry(codecRegistry)
            .build();
    @Getter
    private final MongoClient mongoClient = MongoClients.create(settings);

    public MongoDatabase getSchoolDatabase() {
        return mongoClient.getDatabase("school");
    }

    public MongoDatabase getDatabase(String name) {
        return mongoClient.getDatabase(name);
    }

    public boolean collectionExists(MongoDatabase db, String col) {
        for (String collection : db.listCollectionNames()) {
            if (collection.equals(col)) {
                return true;
            }
        }
        return false;
    }

}
