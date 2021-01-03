package bg.chess.db;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

@ChangeLog(order = "001")
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "init_database", author = "mongock")
    public void initDatabase(MongoDatabase db) {
        MongoCollection<Document> roles = db.getCollection("authority");
        Document adminRole = new Document("name", "ROLE_ADMIN");
        Document userRole = new Document("name", "ROLE_USER");
        roles.insertOne(adminRole);
        roles.insertOne(userRole);

        MongoCollection<Document> users = db.getCollection("user");
        Document admin = new Document();
        admin.append("username", "admin@шах.ею");
        admin.append("password", "$2a$10$MYkP3aeSQy7DI.qgk4noreZ5uchb0i61OOeWu2tVHAO1yNSsGqCVG");
        admin.append("accountNonExpired", true);
        admin.append("accountNonLocked", true);
        admin.append("credentialsNonExpired", true);
        admin.append("enabled", true);
        admin.append("authorities", Arrays.asList(adminRole, userRole));
        users.insertOne(admin);
    }

}
