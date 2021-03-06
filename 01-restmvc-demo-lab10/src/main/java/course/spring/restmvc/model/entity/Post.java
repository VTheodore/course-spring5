package course.spring.restmvc.model.entity;

import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name ="POSTS")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(generator = "post_sequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(
            name = "post_sequence",
            sequenceName = "post_sequence",
            allocationSize = 3
    )
    private Long id;

    @NonNull
    @NotNull
    @Size(min=2, max = 50)
    private String title;

    @NonNull
    @NotNull
    @Size(min=20, max = 2048)
    private String content;

    @ManyToOne(targetEntity = User.class, optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name="AUTHOR_ID", nullable = false, updatable = false, referencedColumnName = "ID")
    @NonNull
    private User author;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<@Size(min=2, max=30) String> keywords;

    @URL
    private String imageUrl;

    @PastOrPresent
    private LocalDateTime created = LocalDateTime.now();

    @PastOrPresent
    private LocalDateTime modified = LocalDateTime.now();

    public Post(@NonNull @NotNull @Size(min = 2, max = 50) String title, @NonNull @NotNull @Size(min = 20, max = 2048) String content, @NonNull @NotNull User author, List<@Size(min = 2, max = 15) String> keywords, @URL String imageUrl) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.keywords = keywords;
        this.imageUrl = imageUrl;
    }

    public Post(long id, String title, String content, User user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = user;
    }
}
