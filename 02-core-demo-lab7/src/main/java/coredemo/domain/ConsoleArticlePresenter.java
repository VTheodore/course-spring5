package coredemo.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.util.List;

@Service("presenter")
public class ConsoleArticlePresenter implements ArticlePresenter {
    @Autowired
    private List<ArticleProvider> providers;

    public ConsoleArticlePresenter() {
    }

//    public ConsoleArticlePresenter(ArticleProvider provider) {
//        this.provider = provider;
//    }

    @Override
    public void present() {
        if(providers.size() == 0) {
            System.out.println("No providers!!!");
            return;
        }
        providers.stream()
                .flatMap(prov -> prov.getArticles().stream())
                .forEach(System.out::println);
    }

//    public static ArticlePresenter createPresenter(ArticleProvider prov) {
//        return new ConsoleArticlePresenter(prov);
//    }

//    public ArticleProvider getProvider() {
//        return provider;
//    }

//    @Autowired
//    @Inject
//    @Resource(name = "provider")
//    public void setProvider(ArticleProvider pr) {
//        this.provider = pr;
//    }

//    public  void configure(ArticleProvider pr) {
//        this.provider = pr;
//    }
}
