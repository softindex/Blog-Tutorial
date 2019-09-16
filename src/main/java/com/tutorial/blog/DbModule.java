package com.tutorial.blog;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.datakernel.config.Config;
import io.datakernel.di.annotation.Export;
import io.datakernel.di.annotation.Named;
import io.datakernel.di.annotation.Provides;
import io.datakernel.di.module.AbstractModule;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

//[START EXAMPLE]
public class DbModule extends AbstractModule {

	@Provides
	MongoClient mongoClient(MongoClientSettings settings) {
		return MongoClients.create(settings);
	}

	@Provides
	MongoClientSettings settings(@Named("app") Config config, CodecRegistry codecRegistry) {
		return MongoClientSettings.builder()
				.applyConnectionString(new ConnectionString(config.get("mongo.url")))
				.codecRegistry(codecRegistry)
				.build();
	}

	@Provides
	CodecRegistry codecRegistry() {
		return fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	}

	@Provides
	MongoDatabase database(MongoClient mongoClient, @Named("app") Config config) {
		return mongoClient.getDatabase(config.get("mongo.database"));
	}

	@Provides
	MongoCollection<ArticleSchemaDao.ArticleSchema> articlesCollection(MongoDatabase database) {
		return database.getCollection("articles", ArticleSchemaDao.ArticleSchema.class);
	}

	@Provides
	@Export
	ArticleSchemaDao schemaDaoDb(MongoCollection<ArticleSchemaDao.ArticleSchema> collection) {
		return new ArticleSchemaDaoDb(collection);
	}
}
//[END EXAMPLE]
