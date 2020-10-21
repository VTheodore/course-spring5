package course.spring.coredemo.consumer;

import course.spring.coredemo.formatter.ArticleFormatter;
import course.spring.coredemo.model.Article;
import course.spring.coredemo.provider.ArticleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

public class ConsoleArticleConsumer implements  ArticleConsumer {
    private ArticleProvider provider;
    private String message;
    private ArticleFormatter formatter;

    public ConsoleArticleConsumer() {
    }

    public ConsoleArticleConsumer(ArticleProvider provider) {
        this.provider = provider;
    }

    public ArticleFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(ArticleFormatter formatter) {
        this.formatter = formatter;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArticleProvider getProvider() {
        return provider;
    }

    public void setProvider(ArticleProvider provider) {
        provider.addArticle(new Article("Added by property DI", "Added by property DI"));
        this.provider = provider;
    }

    public void updateProviderAndMessage(
            ArticleProvider provider,
            ArticleFormatter formatter,
            @Value("${message}") String message) {
        provider.addArticle(new Article("Added method DI", "Added method DI"));
        this.provider = provider;
        this.message = message;
        this.formatter = formatter;
    }

    @Override
    public void consume() {
        System.out.println(message);
        provider.getAticles().forEach(article -> {
            System.out.println(formatter.formatArticle(article));
        });
    }
}
