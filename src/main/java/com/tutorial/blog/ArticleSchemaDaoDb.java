package com.tutorial.blog;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

//[START EXAMPLE]
public class ArticleSchemaDaoDb implements ArticleSchemaDao {
	private final MongoCollection<ArticleSchema> collection;

	public ArticleSchemaDaoDb(MongoCollection<ArticleSchema> collection) {
		this.collection = collection;
	}

	@Override
	public ArticleSchema save(ArticleSchema articleSchema) {
		collection.insertOne(articleSchema);
		return articleSchema;
	}

	@Override
	public List<ArticleSchema> findAll() {
		ArrayList<ArticleSchema> articles = new ArrayList<>();
		collection.find().forEach((Consumer<? super ArticleSchema>) articles::add);
		return articles;
	}

	@Nullable
	@Override
	public ArticleSchema findById(String id) {
		return collection.find(new BasicDBObject("_id", new ObjectId(id))).first();
	}

	@Override
	public void delete(String id) {
		collection.deleteOne(new BasicDBObject("_id", new ObjectId(id)));
	}

	@Nullable
	@Override
	public ArticleSchema update(String id, ArticleSchema articleSchema) {
		collection.updateOne(new BasicDBObject("_id", new ObjectId(id)),
				new BasicDBObject("$set", new BasicDBObject()
						.append("title", articleSchema.getTitle())
						.append("body", articleSchema.getBody())
						.append("author", articleSchema.getAuthor())
						.append("updatedAt", new Date())));
		return findById(id);
	}
}
//[END EXAMPLE]
