package mykola.videos_test_task.repository;

import mykola.videos_test_task.dto.VideoStatsDto;
import mykola.videos_test_task.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT new mykola.videos_test_task.dto.VideoStatsDto(v.loadedTimes, v.playedTimes) " +
            "FROM Video v WHERE v.id = :id")
    Optional<VideoStatsDto> getStatsForVideo(@Param("id") Long id);

}
