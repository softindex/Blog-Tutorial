package com.tutorial.blog;

import com.tutorial.blog.ArticleSchemaDao.ArticleSchema;
import io.datakernel.common.parse.ParseException;
import io.datakernel.config.Config;
import io.datakernel.di.annotation.Named;
import io.datakernel.di.annotation.Provides;
import io.datakernel.di.module.Module;
import io.datakernel.http.AsyncServlet;
import io.datakernel.http.RoutingServlet;
import io.datakernel.http.StaticServlet;
import io.datakernel.launchers.http.HttpServerLauncher;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static com.tutorial.blog.ArticleCodecs.ARTICLE_CODEC;
import static com.tutorial.blog.ArticleCodecs.ARTICLE_CODEC_FULL;
import static io.datakernel.codec.StructuredCodecs.ofList;
import static io.datakernel.codec.json.JsonUtils.fromJson;
import static io.datakernel.config.Config.ofClassPathProperties;
import static io.datakernel.http.AsyncServletDecorator.loadBody;
import static io.datakernel.http.AsyncServletDecorator.mapException;
import static io.datakernel.http.HttpMethod.*;
import static io.datakernel.http.HttpResponse.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Comparator.reverseOrder;
import static java.util.concurrent.Executors.newCachedThreadPool;

//[START EXAMPLE]
public class BlogLauncher extends HttpServerLauncher {
	@Provides
	@Named("app")
	Config config() {
		return ofClassPathProperties("blog.properties");
	}

	@Override
	protected Module getBusinessLogicModule() {
		return new DbModule();
	}

	@Provides
	Executor executor() {
		return newCachedThreadPool();
	}

	@Provides
	@Named("static")
	AsyncServlet staticServlet(Executor executor) {
		return StaticServlet.ofClassPath(executor, "dist")
				.withIndexHtml();
	}

	@Provides
	@Named("failed")
	AsyncServlet failServlet() {
		return $ -> notFound404();
	}

	@Provides
	public AsyncServlet rootServlet(@Named("failed") AsyncServlet failServlet,
									@Named("static") AsyncServlet staticServlet,
									ArticleSchemaDao articleDao) {
		return mapException(Objects::nonNull, failServlet)
				.serve(RoutingServlet.create()
						.map("/*", staticServlet)
						.map("/api/articles/*", RoutingServlet.create()
								.map(GET, "/", $ -> ok200()
										.withJson(ofList(ARTICLE_CODEC_FULL),
												articleDao.findAll()
														.stream()
														.sorted(reverseOrder())
														.collect(Collectors.toList())))
								.map(POST, "/", loadBody().serve(request -> {
									try {
										return ok200()
												.withJson(ARTICLE_CODEC_FULL,
														articleDao.save(fromJson(ARTICLE_CODEC, request.getBody().getString(UTF_8))));
									} catch (ParseException e) {
										return ofCode(400);
									}
								}))
								.map(GET, "/:id", request -> {
									ArticleSchema foundArticle = articleDao.findById(request.getPathParameter("id"));
									return foundArticle == null ?
											ofCode(404) :
											ok200().withJson(ARTICLE_CODEC_FULL, foundArticle);
								})
								.map(DELETE, "/:id", request -> {
									articleDao.delete(request.getPathParameter("id"));
									return ok200();
								})
								.map(PATCH, "/:id", loadBody().serve(request -> {
									try {
										ArticleSchema updatedArticle = articleDao.update(request.getPathParameter("id"),
												fromJson(ARTICLE_CODEC, request.getBody().getString(UTF_8)));
										return updatedArticle == null ?
												ofCode(400) :
												ok200().withJson(ARTICLE_CODEC_FULL, updatedArticle);
									} catch (ParseException e) {
										return ofCode(400);
									}
								}))));
	}

	public static void main(String[] args) throws Exception {
		HttpServerLauncher launcher = new BlogLauncher();
		launcher.launch(args);
	}
}
//[END EXAMPLE]
