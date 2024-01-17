package jku.win.se.assignmentManager.backend.config;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import dev.morphia.Datastore;
import dev.morphia.Morphia;

public class MongoDBConfig {
	
	private static MongoDBConfig instance = new MongoDBConfig();
	
	private static final String MONGO_URI = "mongodb://mongodb:27017";
	private static final String DATABASE_NAME = "pacman";
	
	private MongoClient mongoClient;
	private Datastore datastore;
	
	public MongoDBConfig() {
		CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
	            MongoClientSettings.getDefaultCodecRegistry(),
	            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
	    );
		
		MongoClientSettings settings = MongoClientSettings.builder()
		        .codecRegistry(codecRegistry)
		        .applyConnectionString(new ConnectionString(MONGO_URI))
		        .build();
		
		this.mongoClient = MongoClients.create(settings);
		datastore = Morphia.createDatastore(this.mongoClient, DATABASE_NAME);
	}
	
	public static MongoDBConfig getInstance() {
		return instance;
	}

	public Datastore getDatastore() {
		return this.datastore;
	}
}
