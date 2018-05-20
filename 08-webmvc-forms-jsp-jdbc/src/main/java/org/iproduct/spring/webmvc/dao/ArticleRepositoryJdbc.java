package org.iproduct.spring.webmvc.dao;

import org.iproduct.spring.webmvc.model.Article;
import org.iproduct.spring.webmvc.service.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ArticleRepositoryJdbc implements ArticleRepository {
    private static final Logger log = LoggerFactory.getLogger(ArticleRepositoryJdbc.class);
    private static final String INSERT_SQL =
        "INSERT INTO articles(title, content, created_date, picture_url) VALUES (?, ?, ?, ?)";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Collection<Article> findAll() {
        List<Article> articles = this.jdbcTemplate
                .query("select * from articles", new ArticleMapper());
        log.info("Articles loaded: {}", articles.size());
        return articles;
    }

    @Override
    public Article find(long  id) {
        Article article = this.jdbcTemplate.queryForObject(
                "select * from articles where id = ?",
                new Object[]{id}, new ArticleMapper());
        log.info("Article found: {}", article);
        return article;
    }

    @Override
    @Transactional
    public Article create(Article article) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[] {"id"});
                ps.setString(1, article.getTitle());
                ps.setString(2, article.getContent());
                ps.setTimestamp(3, new Timestamp(article.getCreatedDate().getTime()));
                ps.setString(4, article.getPictureUrl());
                return ps;
            }
        }, keyHolder);
        article.setId(keyHolder.getKey().longValue());
        log.info("Article created: {}", article);
        return article;
    }

    @Override
    public Article update(Article article) {
       int count = this.jdbcTemplate.update(
    "update articles set (title, content, created_date, picture_url) VALUES (?,?, ?, ?) where id = ?",
                article.getTitle(), article.getContent(), article.getCreatedDate(),
                article.getPictureUrl(), article.getId());
        log.info("Article updated: {}", article);
        return article;
    }

    @Override
    public boolean remove(long articleId) {
        int count = this.jdbcTemplate.update(
                "delete from articles where id = ?",
                Long.valueOf(articleId));
        return count > 0;
    }

    @PostConstruct
    public void init() {
        log.info("Start data initialization  ...");
//      jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS articles(" +
                "id SERIAL PRIMARY KEY, " +
                "title VARCHAR(80), " +
                "content VARCHAR(512), " +
                "created_date TIMESTAMP, " +
                "picture_url VARCHAR(80)" +
                ")");

        int articlesCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM articles", Integer.class);
        log.info("Articles count: {}", articlesCount);

        if (articlesCount == 0) {
            List<Article> articles = Arrays.asList(
                    new Article("Welcome to Spring 5", "Spring 5 is great beacuse ..."),
                    new Article("Dependency Injection", "Should I use DI or lookup ..."),
                    new Article("Spring Beans and Wireing", "There are several ways to configure Spring beans.")
            ).stream().collect(Collectors.toList());

            // Use a Java 8 stream to print out each tuple of the list
            articles.forEach(article -> {
                log.info(String.format("Inserting article record for %s %s",
                        article.getTitle(), article.getContent()));
                jdbcTemplate.update("INSERT INTO articles(title, content, created_date, picture_url) VALUES (?,?, ?, ?)",
                        article.getTitle(), article.getContent(), article.getCreatedDate(), article.getPictureUrl());
            });
        }

        log.info("Querying for article records where title contains = 'Spring':");
        jdbcTemplate.query(
                "SELECT id, title, content, created_date, picture_url FROM articles WHERE title LIKE ?", new Object[]{"%Spring%"},
                (rs, rowNum) -> new Article(
                        rs.getLong("id"), rs.getString("title"),
                        rs.getString("content"), rs.getDate("created_date"),
                        rs.getString("picture_url"))
        ).forEach(article -> log.info(article.toString()));
    }

    private static final class ArticleMapper implements RowMapper<Article> {

        public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Article(rs.getLong("id"), rs.getString("title"),
                    rs.getString("content"), rs.getDate("created_date"),
                    rs.getString("picture_url"));
        }
    }

}