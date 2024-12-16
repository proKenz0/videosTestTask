package mykola.videos_test_task.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "videos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "played_times")
    private int playedTimes;

    @Column(name = "loaded_times")
    private int loadedTimes;

    @Column(name = "listed", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean listed = true;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "video_data_id", referencedColumnName = "id")
    @ToString.Exclude
    private VideoData videoData;
}
