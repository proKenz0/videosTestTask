package mykola.videos_test_task.repository;

import mykola.videos_test_task.entity.VideoData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoDataRepository extends JpaRepository<VideoData, Long>, JpaSpecificationExecutor<VideoData> {

    @Query("SELECT vd FROM VideoData vd JOIN Video v ON v.videoData = vd WHERE v.id = :videoId")
    Optional<VideoData> findVideoDataByVideoId(@Param("videoId") Long videoId);
}
