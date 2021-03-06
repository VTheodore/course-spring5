package org.iproduct.spring.webmvc.web;

import lombok.extern.slf4j.Slf4j;
import org.iproduct.spring.webmvc.exception.EntityNotFoundException;
import org.iproduct.spring.webmvc.exception.InvalidEntityIdException;
import org.iproduct.spring.webmvc.model.Article;
import org.iproduct.spring.webmvc.service.ArticleService;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/articles")
@Slf4j
public class ArticleController {

    @Autowired
    ArticleService service;

    @GetMapping
    public List<Article> getArticles() {
        return service.getArticles();
    }

    @GetMapping("{id}")
    public Article getArticles(@PathVariable String id) {
        return service.getArticleById(id);
    }

    @DeleteMapping("{id}")
    public Article deleteArticles(@PathVariable String id) {
        return service.deleteArticle(id);
    }

    @PostMapping
    public ResponseEntity<Article> addArticle(@RequestBody Article article, UriComponentsBuilder uriComponentsBuilder,
                                              HttpServletRequest req) {
        Article created = service.addArticle(article);
        log.info(">>> Request URI {}", req.getServletPath());
        URI location = uriComponentsBuilder.path(req.getServletPath())
                .pathSegment("{id}").buildAndExpand(created.getId()).toUri() ;
        return ResponseEntity.created(location).body(created);
//        return ResponseEntity.status(303).location(location).body(created);
    }

    @PutMapping("{id}")
    public ResponseEntity<Article> addArticle(@PathVariable String id, @RequestBody Article article) {
        if(!article.getId().equals(id)) throw new InvalidEntityIdException(
                String.format("Article ID=%s from path is different from Entity ID=%s", id, article.getId()));
        Article updated = service.updateArticle(article);
        log.info("Article updated: {}", updated);
        return ResponseEntity.ok(updated);
    }
}
