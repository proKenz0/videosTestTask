package mykola.videos_test_task.dto;

import lombok.Data;
import mykola.videos_test_task.enums.Genre;

import java.util.Set;

@Data
public class VideoDataRequestDto {
    private String title;
    private String synopsis;
    private String director;
    private String castMembers;
    private Integer yearOfRelease;
    private Set<Genre> genres;
    private Integer runningTime;
}
