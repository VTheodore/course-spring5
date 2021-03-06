package course.spring.restmvc.service.impl;

import course.spring.restmvc.dao.PostsJpaRepository;
import course.spring.restmvc.dao.PostsRepository;
import course.spring.restmvc.exception.InvalidEntityDataException;
import course.spring.restmvc.exception.NonexistingEntityException;
import course.spring.restmvc.exception.ValidationErrorsException;
import course.spring.restmvc.model.Post;
import course.spring.restmvc.service.PostsService;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class PostsServiceImpl implements PostsService {
    private PostsJpaRepository postsRepo;

    @Autowired
    public PostsServiceImpl(PostsJpaRepository postsRepo) {
        this.postsRepo = postsRepo;
    }

    @Autowired
    private Validator validator;

    @Override
    public List<Post> getAllPosts() {
        return postsRepo.findAll();
    }

    @Override
    public Post getPostById(Long id) throws NonexistingEntityException {
        Post result = postsRepo.findById(id).get();
        if (result == null) {
            throw new NonexistingEntityException(String.format("Post with ID:%d does not exist", id));
        }
        return result;
    }

    @Override
    public Post createPost(Post post) {
//        Errors errors = new BindException(post, "Post");
//        Set<ConstraintViolation<Post>> violations = validator.validate(post);
//        if(!violations.isEmpty()) {
//            throw new ValidationErrorsException(violations);
//        }
        post.setCreated(LocalDateTime.now());
        post.setModified(LocalDateTime.now());
        Post result = null;
        try {
            result = postsRepo.save(post);
        } catch(Throwable e) {
            Throwable ex = e;
            while(ex.getCause() != null && !(ex instanceof ConstraintViolationException) ) {
                ex = ex.getCause();
            }
            if(ex instanceof ConstraintViolationException) {
                ConstraintViolationException cvex = (ConstraintViolationException) ex;
                throw new ValidationErrorsException(cvex.getConstraintViolations());
            } else {
                throw e;
            }
//            log.error("!!!!!!  Exception catched:", e);
        }

        return result;
    }

    @Override

    public Post updatePost(Post post) throws InvalidEntityDataException, NonexistingEntityException {
        Post result = postsRepo.findById(post.getId()).get();
        if(!result.getCreated().equals(post.getCreated())) {
            throw new InvalidEntityDataException(String.format("Post creation date: %s can not be modified.",
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(result.getCreated())));
        }
        result = postsRepo.save(post);
        if (result == null) {
            throw new NonexistingEntityException(String.format("Post with ID:%d does not exist", post.getId()));
        }
        post.setModified(LocalDateTime.now());
        return result;
    }

    @Override
    public Post deletePost(Long id) throws NonexistingEntityException{
        Post result = null;
        postsRepo.deleteById(id);
        if (result == null) {
            throw new NonexistingEntityException(String.format("Post with ID:%d does not exist", id));
        }
        return result;
    }

    @Override
    public long count() {
        return postsRepo.count();
    }
}
