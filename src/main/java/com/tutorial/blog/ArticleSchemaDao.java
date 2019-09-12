package com.tutorial.blog;

import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

public interface ArticleSchemaDao {
	ArticleSchema save(ArticleSchema articleSchema);

	List<ArticleSchema> findAll();

	@Nullable
	ArticleSchema findById(String id);

	void delete(String id);

	@Nullable
	ArticleSchema update(String id, ArticleSchema articleSchema);

	@SuppressWarnings({"unused"})
	final class ArticleSchema implements Comparable<ArticleSchema> {
		private ObjectId id;
		private String title;
		private String body;
		private String author;
		private Date createdAt;
		private Date updatedAt;

		public ArticleSchema() {
		}

		public ArticleSchema(String title, String body, String author) {
			long now = System.currentTimeMillis();
			this.id = new ObjectId();
			this.title = title;
			this.body = body;
			this.author = author;
			this.createdAt = new Date(now);
			this.updatedAt = new Date(now);
		}

		public ArticleSchema(ObjectId id, String title, String body, String author, Date createdAt, Date updatedAt) {
			this.id = id;
			this.title = title;
			this.body = body;
			this.author = author;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
		}

		public ObjectId getId() {
			return id;
		}

		public ArticleSchema setId(ObjectId id) {
			this.id = id;
			return this;
		}

		public String getTitle() {
			return title;
		}

		public ArticleSchema setTitle(String title) {
			this.title = title;
			return this;
		}

		public String getBody() {
			return body;
		}

		public ArticleSchema setBody(String body) {
			this.body = body;
			return this;
		}

		public String getAuthor() {
			return author;
		}

		public ArticleSchema setAuthor(String author) {
			this.author = author;
			return this;
		}

		public Date getCreatedAt() {
			return createdAt;
		}

		public ArticleSchema setCreatedAt(Date createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public Date getUpdatedAt() {
			return updatedAt;
		}

		public ArticleSchema setUpdatedAt(Date updatedAt) {
			this.updatedAt = updatedAt;
			return this;
		}

		public void update() {
			updatedAt.setTime(System.currentTimeMillis());
		}

		@Override
		public int compareTo(@NotNull ArticleSchema articleSchema) {
			return createdAt.getTime() > articleSchema.createdAt.getTime() ? 1 : -1;
		}
	}
}
