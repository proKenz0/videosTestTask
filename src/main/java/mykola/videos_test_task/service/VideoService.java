package mykola.videos_test_task.service;


import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mykola.videos_test_task.dto.VideoRequestDto;
import mykola.videos_test_task.dto.VideoDataRequestDto;
import mykola.videos_test_task.dto.VideoStatsDto;
import mykola.videos_test_task.entity.Video;
import mykola.videos_test_task.entity.VideoData;
import mykola.videos_test_task.exception.VideoDataNotFoundException;
import mykola.videos_test_task.exception.VideoNotFoundException;
import mykola.videos_test_task.repository.VideoDataRepository;
import mykola.videos_test_task.repository.VideoRepository;
import mykola.videos_test_task.util.DtoConverter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoDataRepository videoDataRepository;

    @Transactional
    public VideoData createVideo(VideoRequestDto videoDto) {

        VideoData videoData = DtoConverter.getVideoDataFromVideoDto(videoDto);
        videoData = videoDataRepository.save(videoData);

        Video video = new Video();
        video.setContent(videoDto.getContent());
        video.setVideoData(videoData);
        videoRepository.save(video);

        log.info("Video with VideoData created.");

        return videoData;
    }

    @Transactional
    public VideoData updateVideoData(Long id, VideoDataRequestDto dto) {
        VideoData videoData = findVideoDataByVideoId(id);

        VideoData updatedVideoData = DtoConverter.getVideoDataFromVideoDataDto(dto);
        updatedVideoData.setId(videoData.getId());

        return videoDataRepository.save(updatedVideoData);
    }

    public VideoData findVideoDataByVideoId(Long id) {
        return videoDataRepository.findVideoDataByVideoId(id)
                .orElseThrow(() -> new VideoDataNotFoundException("Video data no found for video with id " + id));
    }

    public Video findVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new VideoNotFoundException("Video with id " + id + " not found"));
    }

    public void delistVideo(Long id) {
        Video video = findVideoById(id);
        video.setListed(false);
        videoRepository.save(video);
    }

    @Transactional
    public Video loadVideo(Long id) {
        Video video = findVideoById(id);
        video.setLoadedTimes(video.getLoadedTimes() + 1);
        return videoRepository.save(video);
    }

    @Transactional
    public String playVideo(Long id) {
        Video video = findVideoById(id);
        video.setPlayedTimes(video.getPlayedTimes() + 1);
        videoRepository.save(video);

        return video.getContent();
    }

    public List<VideoData> findAllVideosData() {
        return videoDataRepository.findAll();
    }

    public List<VideoData> findAllWithFilters(Map<String, Object> filters) {
        Specification<VideoData> specification = buildSpecification(filters);
        return videoDataRepository.findAll(specification);
    }

    private Specification<VideoData> buildSpecification(Map<String, Object> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filters.forEach((key, value) -> {
                if (key.equals("genres") && value instanceof Set<?>) {
                    ((Set<?>) value).forEach(genre ->
                            predicates.add(criteriaBuilder.isMember(genre, root.get("genres")))
                    );
                } else {
                    // Інші поля
                    predicates.add(criteriaBuilder.equal(root.get(key), value));
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public VideoStatsDto getStatsForVideo(Long id) {
        return videoRepository.getStatsForVideo(id)
                .orElseThrow(() -> new RuntimeException("Error while retrieve stats for video with id " + id));
    }
}

