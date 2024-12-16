package mykola.videos_test_task.exception;

public class VideoDataNotFoundException extends RuntimeException {
    public VideoDataNotFoundException(String message) {
        super(message);
    }
}
