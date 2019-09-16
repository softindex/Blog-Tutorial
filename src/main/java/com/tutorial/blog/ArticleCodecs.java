package com.tutorial.blog;

import com.tutorial.blog.ArticleSchemaDao.ArticleSchema;
import io.datakernel.codec.StructuredCodec;
import org.bson.types.ObjectId;

import java.util.Date;

import static io.datakernel.codec.StructuredCodecs.*;

//[START EXAMPLE]
@SuppressWarnings("WeakerAccess")
public final class ArticleCodecs {
	public static final StructuredCodec<ArticleSchema> ARTICLE_CODEC = object(ArticleSchema::new,
			"title", ArticleSchema::getTitle, STRING_CODEC,
			"body", ArticleSchema::getBody, STRING_CODEC,
			"author", ArticleSchema::getAuthor, STRING_CODEC);
	private static final StructuredCodec<Date> DATE_CODEC = LONG_CODEC.transform(Date::new, Date::getTime);
	private static final StructuredCodec<ObjectId> OBJECT_ID_STRUCTURED_CODEC = STRING_CODEC.transform(ObjectId::new, ObjectId::toHexString);
	public static final StructuredCodec<ArticleSchema> ARTICLE_CODEC_FULL = object(ArticleSchema::new,
			"_id", ArticleSchema::getId, OBJECT_ID_STRUCTURED_CODEC,
			"title", ArticleSchema::getTitle, STRING_CODEC,
			"body", ArticleSchema::getBody, STRING_CODEC,
			"author", ArticleSchema::getAuthor, STRING_CODEC,
			"createdAt", ArticleSchema::getCreatedAt, DATE_CODEC,
			"updatedAt", ArticleSchema::getUpdatedAt, DATE_CODEC)
			.transform(value -> value.setTitle("article"), articleSchema -> articleSchema);
}
//[END EXAMPLE]
