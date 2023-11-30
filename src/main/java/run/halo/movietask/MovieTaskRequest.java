package run.halo.movietask;

import lombok.Data;

/**
 * @author cody
 */
@Data
public class MovieTaskRequest {
    private String group;
    
    private MovieTask movieTask;
}
