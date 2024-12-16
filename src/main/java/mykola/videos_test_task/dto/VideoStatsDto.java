package mykola.videos_test_task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoStatsDto {
    private int loadedTimes;
    private int playedTimes;
}

