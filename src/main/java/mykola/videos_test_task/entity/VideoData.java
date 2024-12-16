package mykola.videos_test_task.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import mykola.videos_test_task.enums.Genre;

import java.util.Set;

@Entity
@Table(name = "video_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "synopsis")
    private String synopsis;

    @Column(name = "director")
    private String director;

    @Column(name = "cast_members")
    private String castMembers;

    @Column(name = "year_of_release")
    private Integer yearOfRelease;

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "video_genres", joinColumns = @JoinColumn(name = "video_data_id"))
    @Column(name = "genre")
    private Set<Genre> genres;

    @Column(name = "running_time")
    private Integer runningTime;

    @OneToOne(mappedBy = "videoData")
    @JsonIgnore
    @ToString.Exclude
    private Video video;
}
