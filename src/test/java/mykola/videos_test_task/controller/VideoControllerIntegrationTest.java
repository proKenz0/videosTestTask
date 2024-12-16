package mykola.videos_test_task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mykola.videos_test_task.dto.VideoDataRequestDto;
import mykola.videos_test_task.dto.VideoRequestDto;
import mykola.videos_test_task.dto.VideoStatsDto;
import mykola.videos_test_task.entity.Video;
import mykola.videos_test_task.entity.VideoData;
import mykola.videos_test_task.enums.Genre;
import mykola.videos_test_task.service.VideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
class VideoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VideoService videoService;

    private Video video;
    private VideoData videoData;
    private VideoRequestDto videoRequestDto;

    @BeforeEach
    void setUp() {
        video = new Video();
        video.setId(1L);
        video.setContent("Sample Video Content");
        video.setPlayedTimes(5);
        video.setLoadedTimes(10);
        video.setListed(true);

        videoData = new VideoData();
        videoData.setId(1L);
        videoData.setTitle("Inception");
        videoData.setDirector("Christopher Nolan");
        videoData.setGenres(Set.of(Genre.SCI_FI, Genre.THRILLER));
        videoData.setYearOfRelease(2010);

        videoRequestDto = new VideoRequestDto();
        videoRequestDto.setTitle("Inception");
        videoRequestDto.setDirector("Christopher Nolan");
        videoRequestDto.setGenres(Set.of(Genre.SCI_FI, Genre.THRILLER));
        videoRequestDto.setYearOfRelease(2010);
        videoRequestDto.setContent("Dream within a dream");
    }

    @Test
    void publishVideo_ShouldCreateVideoData() throws Exception {
        Mockito.when(videoService.createVideo(any(VideoRequestDto.class))).thenReturn(videoData);

        mockMvc.perform(post("/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Inception")))
                .andExpect(jsonPath("$.director", is("Christopher Nolan")))
                .andExpect(jsonPath("$.genres", containsInAnyOrder("SCI_FI", "THRILLER")))
                .andExpect(jsonPath("$.yearOfRelease", is(2010)));
    }

    @Test
    void updateVideo_ShouldUpdateVideoData() throws Exception {
        VideoDataRequestDto videoDataRequestDto = new VideoDataRequestDto();
        videoDataRequestDto.setTitle("Updated Title");
        videoDataRequestDto.setYearOfRelease(2021);

        VideoData updatedVideoData = new VideoData();
        updatedVideoData.setId(1L);
        updatedVideoData.setTitle("Updated Title");
        updatedVideoData.setYearOfRelease(2021);

        Mockito.when(videoService.updateVideoData(eq(1L), any(VideoDataRequestDto.class))).thenReturn(updatedVideoData);

        mockMvc.perform(put("/videos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoDataRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.yearOfRelease", is(2021)));
    }

    @Test
    void delistVideo_ShouldReturnOk() throws Exception {
        Mockito.doNothing().when(videoService).delistVideo(1L);

        mockMvc.perform(patch("/videos/1"))
                .andExpect(status().isOk());

        Mockito.verify(videoService, Mockito.times(1)).delistVideo(1L);
    }

    @Test
    void loadVideo_ShouldReturnVideo() throws Exception {
        Mockito.when(videoService.loadVideo(1L)).thenReturn(video);

        mockMvc.perform(get("/videos/1/load"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Sample Video Content")))
                .andExpect(jsonPath("$.playedTimes", is(5)))
                .andExpect(jsonPath("$.loadedTimes", is(10)))
                .andExpect(jsonPath("$.listed", is(true)));

        Mockito.verify(videoService, Mockito.times(1)).loadVideo(1L);
    }

    @Test
    void playVideo_ShouldReturnContent() throws Exception {
        String videoContent = "Sample Video Content";
        Mockito.when(videoService.playVideo(1L)).thenReturn(videoContent);

        mockMvc.perform(get("/videos/1/play"))
                .andExpect(status().isOk())
                .andExpect(content().string(videoContent));

        Mockito.verify(videoService, Mockito.times(1)).playVideo(1L);
    }

    @Test
    void getVideosData_ShouldReturnAllVideos() throws Exception {
        List<VideoData> videoDataList = Arrays.asList(videoData);

        Mockito.when(videoService.findAllVideosData()).thenReturn(videoDataList);

        mockMvc.perform(get("/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Inception")));
    }

    @Test
    void getFilteredVideoData_ShouldReturnFilteredVideos() throws Exception {
        Mockito.when(videoService.findAllWithFilters(any())).thenReturn(List.of(videoData));

        mockMvc.perform(get("/videos/filtered")
                        .param("title", "Inception")
                        .param("yearOfRelease", "2010"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Inception")))
                .andExpect(jsonPath("$[0].yearOfRelease", is(2010)));
    }

    @Test
    void getVideoStats_ShouldReturnVideoStats() throws Exception {
        VideoStatsDto statsDto = new VideoStatsDto(5, 10);
        Mockito.when(videoService.getStatsForVideo(1L)).thenReturn(statsDto);

        mockMvc.perform(get("/videos/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loadedTimes", is(5)))
                .andExpect(jsonPath("$.playedTimes", is(10)));
    }
}
