package mykola.videos_test_task.util;

import mykola.videos_test_task.dto.VideoRequestDto;
import mykola.videos_test_task.dto.VideoDataRequestDto;
import mykola.videos_test_task.entity.VideoData;
import org.springframework.stereotype.Component;

@Component
public class DtoConverter {

    public static VideoData getVideoDataFromVideoDto(VideoRequestDto dto) {
        VideoData videoData = new VideoData();

        videoData.setTitle(dto.getTitle());
        videoData.setSynopsis(dto.getSynopsis());
        videoData.setDirector(dto.getDirector());
        videoData.setCastMembers(dto.getCastMembers());
        videoData.setYearOfRelease(dto.getYearOfRelease());
        videoData.setGenres(dto.getGenres());
        videoData.setRunningTime(dto.getRunningTime());

        return videoData;
    }

    public static VideoData getVideoDataFromVideoDataDto(VideoDataRequestDto dto) {
        VideoData videoData = new VideoData();

        videoData.setTitle(dto.getTitle());
        videoData.setSynopsis(dto.getSynopsis());
        videoData.setDirector(dto.getDirector());
        videoData.setCastMembers(dto.getCastMembers());
        videoData.setYearOfRelease(dto.getYearOfRelease());
        videoData.setGenres(dto.getGenres());
        videoData.setRunningTime(dto.getRunningTime());

        return videoData;
    }
}
