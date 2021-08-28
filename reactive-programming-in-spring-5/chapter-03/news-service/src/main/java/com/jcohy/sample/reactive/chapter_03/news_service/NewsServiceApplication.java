package com.jcohy.sample.reactive.chapter_03.news_service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcohy.sample.reactive.chapter_03.news_service.dto.News;
import com.jcohy.sample.reactive.chapter_03.news_service.dto.News.NewsBuilder;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.connection.netty.NettyStreamFactory;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import org.bson.codecs.pojo.PojoCodecProvider;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.jackson.Jackson;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;
import ratpack.spring.config.EnableRatpack;
import ratpack.stream.Streams;
import ratpack.stream.TransformablePublisher;
import rx.Observable;
import rx.RxReactiveStreams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static ratpack.jackson.Jackson.json;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/28:10:57
 * @since 1.0.0
 */
@SpringBootApplication
@EnableRatpack
public class NewsServiceApplication {
    public static final int NEWS_SERVER_PORT = 8070;

    @Autowired
    MongoClient client;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(NewsServiceApplication.class, args);

        RatpackServer.start(spec ->
                spec.serverConfig(ServerConfig.embedded().port(NEWS_SERVER_PORT))
                        .handlers(chain -> chain.get(ctx -> ctx.render(json(Arrays.asList(
                                NewsBuilder.builder()
                                        .author("test")
                                        .category("test")
                                        .content("test")
                                        .publishedOn(new Date())
                                        .title("test")
                                        .build(),
                                NewsBuilder.builder()
                                        .author("test")
                                        .category("test")
                                        .content("test")
                                        .publishedOn(new Date())
                                        .title("1test1")
                                        .build(),
                                NewsBuilder.builder()
                                        .author("test")
                                        .category("test")
                                        .content("test")
                                        .publishedOn(new Date())
                                        .title("2test2")
                                        .build(),
                                NewsBuilder.builder()
                                        .author("test")
                                        .category("test")
                                        .content("test")
                                        .publishedOn(new Date())
                                        .title("3test3")
                                        .build(),
                                NewsBuilder.builder()
                                        .author("test")
                                        .category("test")
                                        .content("test")
                                        .publishedOn(new Date())
                                        .title("4test4")
                                        .build(),
                                NewsBuilder.builder()
                                        .author("test")
                                        .category("test")
                                        .content("test")
                                        .publishedOn(new Date())
                                        .title("5test5")
                                        .build(),
                                NewsBuilder.builder()
                                        .author("test")
                                        .category("test")
                                        .content("test")
                                        .publishedOn(new Date())
                                        .title("6test6")
                                        .build()
                        )))))
        );
    }

    @Bean
    MongoClient mongoClient(MongoProperties properties) {
        ConnectionString connectionString = new ConnectionString(properties.determineUri());
        MongoClientSettings.Builder builder = MongoClientSettings
                .builder()
                .streamFactoryFactory(NettyStreamFactory::new)
                .applyToClusterSettings(b -> b.applyConnectionString(connectionString))
                .applyToConnectionPoolSettings(b -> b.applyConnectionString(connectionString))
                .applyToServerSettings(b -> b.applyConnectionString(connectionString))
                .applyToSslSettings(b -> b.applyConnectionString(connectionString))
                .applyToSocketSettings(b -> b.applyConnectionString(connectionString))
                .codecRegistry(fromRegistries(
                        MongoClients.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder()
                                .automatic(true)
                                .register(News.class)
                                .build())
                ));

        if (connectionString.getReadPreference() != null) {
            builder.readPreference(connectionString.getReadPreference());
        }
        if (connectionString.getReadConcern() != null) {
            builder.readConcern(connectionString.getReadConcern());
        }
        if (connectionString.getWriteConcern() != null) {
            builder.writeConcern(connectionString.getWriteConcern());
        }
        if (connectionString.getApplicationName() != null) {
            builder.applicationName(connectionString.getApplicationName());
        }
        return MongoClients.create(builder.build());
    }

    @Bean
    DatabaseNewsService databaseNews() {
        return () -> client.getDatabase("news")
                .getCollection("news")
                .find(News.class)
                .sort(Sorts.descending("publishedOn"))
                .filter(Filters.eq("category", "tech"));
    }

    @Bean
    HttpNewsService externalNews() {
        return () -> HttpClient.newClient(new InetSocketAddress(NEWS_SERVER_PORT))
                .createGet("")
                .flatMap(HttpClientResponse::getContent)
                .flatMapIterable(bb -> {
                    try {
                        return new ObjectMapper().readValue(
                                bb.toString(Charset.defaultCharset()),
                                new TypeReference<ArrayList<News>>() {}
                        );
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * 请求处理程序声明 。在这里， 要创建一个 Ratpack 请求处理程序，就必须使用 Action<Chain> 类型声明一个 bean
     *
     * @return /
     */
    @Bean
    public Action<Chain> home() {
        return chain -> chain.get(ctx -> {

            // 服务调用和结果聚合.在这里，我们执行服务的方法， 并使用 Ratpack Streams API 合并返回的流。
            FindPublisher<News> databasePublisher =
                    databaseNews().lookupNews();
            Observable<News> httpNewsObservable =
                    externalNews().retrieveNews();
            TransformablePublisher<News> stream = Streams.merge(
                    databasePublisher,
                    RxReactiveStreams.toPublisher(httpNewsObservable)
            );

            // 这是合并流阶段的呈现 在这里， 我们将所有元素异步裁剪到一个列表中，然后将该列
            // 表转换为 JSON 等特定的呈现视图。
            ctx.render(
                    stream.toList()
                            .map(Jackson::json)
            );
        });
    }

    private interface DatabaseNewsService {
        FindPublisher<News> lookupNews();
    }

    private interface HttpNewsService {

        Observable<News> retrieveNews();
    }
}
