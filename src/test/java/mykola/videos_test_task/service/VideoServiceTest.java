package mykola.videos_test_task.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import mykola.videos_test_task.dto.VideoDataRequestDto;
import mykola.videos_test_task.dto.VideoRequestDto;
import mykola.videos_test_task.dto.VideoStatsDto;
import mykola.videos_test_task.entity.Video;
import mykola.videos_test_task.entity.VideoData;
import mykola.videos_test_task.enums.Genre;
import mykola.videos_test_task.exception.VideoDataNotFoundException;
import mykola.videos_test_task.exception.VideoNotFoundException;
import mykola.videos_test_task.repository.VideoDataRepository;
import mykola.videos_test_task.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoDataRepository videoDataRepository;

    @InjectMocks
    private VideoService videoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createVideo_ShouldSaveVideoAndVideoData() {
        VideoRequestDto videoDto = new VideoRequestDto();
        videoDto.setTitle("Inception");
        videoDto.setSynopsis("A mind-bending thriller about dreams within dreams.");
        videoDto.setDirector("Christopher Nolan");
        videoDto.setCastMembers("Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page");
        videoDto.setYearOfRelease(2010);
        videoDto.setGenres(Set.of(Genre.SCI_FI, Genre.THRILLER));
        videoDto.setRunningTime(148);
        videoDto.setContent("Dream within a dream.");

        VideoData savedVideoData = new VideoData();
        savedVideoData.setId(1L);

        when(videoDataRepository.save(any(VideoData.class))).thenReturn(savedVideoData);

        Video savedVideo = new Video();
        savedVideo.setId(1L);

        when(videoRepository.save(any(Video.class))).thenReturn(savedVideo);

        VideoData result = videoService.createVideo(videoDto);

        ArgumentCaptor<VideoData> videoDataCaptor = ArgumentCaptor.forClass(VideoData.class);
        verify(videoDataRepository, times(1)).save(videoDataCaptor.capture());

        VideoData capturedVideoData = videoDataCaptor.getValue();
        assertThat(capturedVideoData.getTitle(), is("Inception"));
        assertThat(capturedVideoData.getDirector(), is("Christopher Nolan"));
        assertThat(capturedVideoData.getGenres(), containsInAnyOrder(Genre.SCI_FI, Genre.THRILLER));

        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository, times(1)).save(videoCaptor.capture());

        Video capturedVideo = videoCaptor.getValue();
        assertThat(capturedVideo.getContent(), is("Dream within a dream."));
        assertThat(capturedVideo.getVideoData(), is(savedVideoData));

        assertThat(result.getId(), is(1L));
    }

    @Test
    void updateVideoData_ShouldUpdateAndSaveVideoData() {
        Long videoId = 1L;

        VideoData existingVideoData = new VideoData();
        existingVideoData.setId(videoId);
        existingVideoData.setTitle("Old Title");
        existingVideoData.setDirector("Old Director");

        VideoDataRequestDto requestDto = new VideoDataRequestDto();
        requestDto.setTitle("New Title");
        requestDto.setDirector("New Director");
        requestDto.setGenres(Set.of());
        requestDto.setSynopsis("Updated Synopsis");
        requestDto.setYearOfRelease(2022);
        requestDto.setRunningTime(120);
        requestDto.setCastMembers("Updated Cast");

        when(videoDataRepository.findVideoDataByVideoId(videoId)).thenReturn(Optional.of(existingVideoData));

        VideoData updatedVideoData = new VideoData();
        updatedVideoData.setId(videoId);
        updatedVideoData.setTitle("New Title");
        updatedVideoData.setDirector("New Director");

        when(videoDataRepository.save(any(VideoData.class))).thenReturn(updatedVideoData);

        VideoData result = videoService.updateVideoData(videoId, requestDto);

        ArgumentCaptor<VideoData> captor = ArgumentCaptor.forClass(VideoData.class);
        verify(videoDataRepository, times(1)).save(captor.capture());

        VideoData capturedVideoData = captor.getValue();
        assertThat(capturedVideoData.getId(), is(videoId));
        assertThat(capturedVideoData.getTitle(), is("New Title"));
        assertThat(capturedVideoData.getDirector(), is("New Director"));
        assertThat(capturedVideoData.getSynopsis(), is("Updated Synopsis"));
        assertThat(capturedVideoData.getYearOfRelease(), is(2022));
        assertThat(capturedVideoData.getRunningTime(), is(120));
        assertThat(capturedVideoData.getCastMembers(), is("Updated Cast"));

        assertThat(result.getTitle(), is("New Title"));
        assertThat(result.getDirector(), is("New Director"));
    }

    @Test
    void updateVideoData_ShouldThrowException_WhenVideoDataNotFound() {
        Long videoId = 1L;
        VideoDataRequestDto requestDto = new VideoDataRequestDto();

        when(videoDataRepository.findVideoDataByVideoId(videoId)).thenReturn(Optional.empty());

        try {
            videoService.updateVideoData(videoId, requestDto);
        } catch (VideoDataNotFoundException e) {
            assertThat(e.getMessage(), is("Video data no found for video with id " + videoId));
        }

        verify(videoDataRepository, never()).save(any(VideoData.class));
    }

    @Test
    void findVideoById_ShouldReturnVideo_WhenVideoExists() {
        Long videoId = 1L;
        Video expectedVideo = new Video();
        expectedVideo.setId(videoId);
        expectedVideo.setContent("Test Content");

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(expectedVideo));

        Video result = videoService.findVideoById(videoId);

        verify(videoRepository, times(1)).findById(videoId);
        assertThat(result.getId(), is(videoId));
        assertThat(result.getContent(), is("Test Content"));
    }

    @Test
    void findVideoById_ShouldThrowException_WhenVideoDoesNotExist() {
        Long videoId = 1L;
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        VideoNotFoundException exception = assertThrows(VideoNotFoundException.class, () -> {
            videoService.findVideoById(videoId);
        });

        assertThat(exception.getMessage(), is("Video with id " + videoId + " not found"));
        verify(videoRepository, times(1)).findById(videoId);
    }

    @Test
    void delistVideo_ShouldSetListedToFalse_WhenVideoExists() {
        Long videoId = 1L;
        Video existingVideo = new Video();
        existingVideo.setId(videoId);
        existingVideo.setListed(true);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(existingVideo));

        videoService.delistVideo(videoId);

        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository, times(1)).save(videoCaptor.capture());

        Video updatedVideo = videoCaptor.getValue();
        assertThat(updatedVideo.getId(), is(videoId));
        assertThat(updatedVideo.isListed(), is(false));
    }

    @Test
    void delistVideo_ShouldThrowException_WhenVideoDoesNotExist() {
        Long videoId = 1L;
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        VideoNotFoundException exception = assertThrows(VideoNotFoundException.class, () -> {
            videoService.delistVideo(videoId);
        });

        assertThat(exception.getMessage(), is("Video with id " + videoId + " not found"));
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    void loadVideo_ShouldIncrementLoadedTimes_WhenVideoExists() {
        Long videoId = 1L;
        Video existingVideo = new Video();
        existingVideo.setId(videoId);
        existingVideo.setLoadedTimes(5);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(existingVideo));
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Video result = videoService.loadVideo(videoId);

        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository, times(1)).save(videoCaptor.capture());

        Video updatedVideo = videoCaptor.getValue();
        assertThat(updatedVideo.getId(), is(videoId));
        assertThat(updatedVideo.getLoadedTimes(), is(6));

        assertThat(result.getId(), is(videoId));
        assertThat(result.getLoadedTimes(), is(6));
    }

    @Test
    void loadVideo_ShouldThrowException_WhenVideoDoesNotExist() {
        Long videoId = 1L;
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        VideoNotFoundException exception = assertThrows(VideoNotFoundException.class, () -> {
            videoService.loadVideo(videoId);
        });

        assertThat(exception.getMessage(), is("Video with id " + videoId + " not found"));
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    void playVideo_ShouldIncrementPlayedTimesAndReturnContent_WhenVideoExists() {
        Long videoId = 1L;
        Video existingVideo = new Video();
        existingVideo.setId(videoId);
        existingVideo.setContent("Sample Content");
        existingVideo.setPlayedTimes(3);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(existingVideo));
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String content = videoService.playVideo(videoId);

        ArgumentCaptor<Video> videoCaptor = ArgumentCaptor.forClass(Video.class);
        verify(videoRepository, times(1)).save(videoCaptor.capture());

        Video updatedVideo = videoCaptor.getValue();
        assertThat(updatedVideo.getId(), is(videoId));
        assertThat(updatedVideo.getPlayedTimes(), is(4));
        assertThat(content, is("Sample Content"));
    }

    @Test
    void playVideo_ShouldThrowException_WhenVideoDoesNotExist() {
        Long videoId = 1L;
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        VideoNotFoundException exception = assertThrows(VideoNotFoundException.class, () -> {
            videoService.playVideo(videoId);
        });

        assertThat(exception.getMessage(), is("Video with id " + videoId + " not found"));
        verify(videoRepository, never()).save(ArgumentMatchers.any(Video.class));
    }

    @Test
    void findAllVideosData_ShouldReturnAllVideoData() {
        VideoData videoData1 = new VideoData();
        videoData1.setId(1L);
        videoData1.setTitle("Video 1");

        VideoData videoData2 = new VideoData();
        videoData2.setId(2L);
        videoData2.setTitle("Video 2");

        when(videoDataRepository.findAll()).thenReturn(Arrays.asList(videoData1, videoData2));

        List<VideoData> result = videoService.findAllVideosData();

        verify(videoDataRepository, times(1)).findAll();
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(videoData1, videoData2));
    }

    @Test
    void findAllWithFilters_ShouldReturnFilteredVideoData() {
        VideoData videoData1 = new VideoData();
        videoData1.setId(1L);
        videoData1.setTitle("Filtered Video 1");

        VideoData videoData2 = new VideoData();
        videoData2.setId(2L);
        videoData2.setTitle("Filtered Video 2");

        when(videoDataRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(videoData1, videoData2));

        Map<String, Object> filters = new HashMap<>();
        filters.put("genres", Set.of(Genre.SCI_FI, Genre.THRILLER));
        filters.put("yearOfRelease", 2021);

        List<VideoData> result = videoService.findAllWithFilters(filters);

        verify(videoDataRepository, times(1)).findAll(any(Specification.class));
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(videoData1, videoData2));
    }

    @Test
    void findAllWithFilters_ShouldFilterByGenres() {
        VideoData videoData1 = new VideoData();
        videoData1.setId(1L);
        videoData1.setGenres(Set.of(Genre.SCI_FI));

        VideoData videoData2 = new VideoData();
        videoData2.setId(2L);
        videoData2.setGenres(Set.of(Genre.THRILLER));

        when(videoDataRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(videoData1, videoData2));

        Map<String, Object> filters = new HashMap<>();
        filters.put("genres", Set.of(Genre.SCI_FI, Genre.THRILLER));

        List<VideoData> result = videoService.findAllWithFilters(filters);

        verify(videoDataRepository, times(1)).findAll(any(Specification.class));
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(videoData1, videoData2));
    }

    @Test
    void findAllWithFilters_ShouldFilterBySingleField() {
        VideoData videoData1 = new VideoData();
        videoData1.setId(1L);
        videoData1.setTitle("Filtered Video");

        when(videoDataRepository.findAll(any(Specification.class))).thenReturn(List.of(videoData1));

        Map<String, Object> filters = new HashMap<>();
        filters.put("title", "Filtered Video");

        List<VideoData> result = videoService.findAllWithFilters(filters);

        verify(videoDataRepository, times(1)).findAll(any(Specification.class));
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getTitle(), is("Filtered Video"));
    }

    @Test
    void findAllWithFilters_ShouldCombineMultipleFilters() {
        VideoData videoData1 = new VideoData();
        videoData1.setId(1L);
        videoData1.setTitle("Video 1");
        videoData1.setYearOfRelease(2022);

        when(videoDataRepository.findAll(any(Specification.class))).thenReturn(List.of(videoData1));

        Map<String, Object> filters = new HashMap<>();
        filters.put("title", "Video 1");
        filters.put("yearOfRelease", 2022);

        List<VideoData> result = videoService.findAllWithFilters(filters);

        verify(videoDataRepository, times(1)).findAll(any(Specification.class));
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getTitle(), is("Video 1"));
        assertThat(result.get(0).getYearOfRelease(), is(2022));
    }

    @Test
    void findAllWithFilters_ShouldReturnAllData_WhenNoFiltersApplied() {
        VideoData videoData1 = new VideoData();
        videoData1.setId(1L);
        videoData1.setTitle("Video 1");

        VideoData videoData2 = new VideoData();
        videoData2.setId(2L);
        videoData2.setTitle("Video 2");

        when(videoDataRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(videoData1, videoData2));

        Map<String, Object> filters = new HashMap<>();

        List<VideoData> result = videoService.findAllWithFilters(filters);

        verify(videoDataRepository, times(1)).findAll(any(Specification.class));
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(videoData1, videoData2));
    }

    @Test
    void findAllWithFilters_ShouldHandleEmptyGenresFilter() {
        VideoData videoData1 = new VideoData();
        videoData1.setId(1L);
        videoData1.setGenres(Set.of());

        when(videoDataRepository.findAll(any(Specification.class))).thenReturn(List.of(videoData1));

        Map<String, Object> filters = new HashMap<>();
        filters.put("genres", Set.of());

        List<VideoData> result = videoService.findAllWithFilters(filters);

        verify(videoDataRepository, times(1)).findAll(any(Specification.class));
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getGenres(), is(empty()));
    }

    @Test
    void getStatsForVideo_ShouldReturnStats_WhenVideoExists() {
        Long videoId = 1L;
        VideoStatsDto expectedStats = new VideoStatsDto(10, 5);

        when(videoRepository.getStatsForVideo(videoId)).thenReturn(Optional.of(expectedStats));

        VideoStatsDto result = videoService.getStatsForVideo(videoId);

        verify(videoRepository, times(1)).getStatsForVideo(videoId);
        assertThat(result.getLoadedTimes(), is(10));
        assertThat(result.getPlayedTimes(), is(5));
    }

    @Test
    void getStatsForVideo_ShouldThrowException_WhenVideoDoesNotExist() {
        Long videoId = 1L;

        when(videoRepository.getStatsForVideo(videoId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            videoService.getStatsForVideo(videoId);
        });

        assertThat(exception.getMessage(), is("Error while retrieve stats for video with id " + videoId));
        verify(videoRepository, times(1)).getStatsForVideo(videoId);
    }
}
