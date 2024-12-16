package mykola.videos_test_task.controller;

import mykola.videos_test_task.dto.VideoDataRequestDto;
import mykola.videos_test_task.dto.VideoRequestDto;
import mykola.videos_test_task.dto.VideoStatsDto;
import mykola.videos_test_task.entity.Video;
import mykola.videos_test_task.entity.VideoData;
import mykola.videos_test_task.enums.Genre;
import mykola.videos_test_task.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping
    public ResponseEntity<VideoData> publishVideo(@RequestBody VideoRequestDto dto) {
        VideoData videoData = videoService.createVideo(dto);
        return ResponseEntity.ok(videoData);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoData> updateVideo(
            @PathVariable Long id,
            @RequestBody VideoDataRequestDto videoDataRequestDto) {

        VideoData videoData = videoService.updateVideoData(id, videoDataRequestDto);
        return ResponseEntity.ok(videoData);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> delistVideo(@PathVariable Long id) {
        videoService.delistVideo(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/load")
    public ResponseEntity<Video> loadVideo(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.loadVideo(id));
    }

    @GetMapping("/{id}/play")
    public ResponseEntity<String> playVideo(@PathVariable Long id) {
        String content = videoService.playVideo(id);
        return ResponseEntity.ok(content);
    }

    @GetMapping
    public ResponseEntity<List<VideoData>> getVideosData() {
        return ResponseEntity.ok(videoService.findAllVideosData());
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<VideoData>> getFilteredVideoData(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String synopsis,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) String castMembers,
            @RequestParam(required = false) Integer yearOfRelease,
            @RequestParam(required = false) Set<Genre> genres,
            @RequestParam(required = false) Integer runningTime
    ) {
        Map<String, Object> filters = new HashMap<>();
        if (title != null) filters.put("title", title);
        if (synopsis != null) filters.put("synopsis", synopsis);
        if (director != null) filters.put("director", director);
        if (castMembers != null) filters.put("castMembers", castMembers);
        if (yearOfRelease != null) filters.put("yearOfRelease", yearOfRelease);
        if (genres != null) filters.put("genres", genres);
        if (runningTime != null) filters.put("runningTime", runningTime);

        List<VideoData> results = videoService.findAllWithFilters(filters);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<VideoStatsDto> getVideoStats(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getStatsForVideo(id));
    }
}

