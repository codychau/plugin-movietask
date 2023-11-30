package run.halo.movietask.vo;

import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import run.halo.movietask.MovieTask;
import run.halo.movietask.MovieTaskGroup;
import java.util.List;

/**
 * @author LIlGG
 */
@Value
@Builder
public class MovieTaskGroupVo implements ExtensionVoOperator {
    MetadataOperator metadata;

    MovieTaskGroup.MovieTaskGroupSpec spec;
    
    MovieTaskGroup.PostGroupStatus status;
    
    List<MovieTaskVo> photos;
    
    public static MovieTaskGroupVoBuilder from(MovieTaskGroup photoGroup) {
        return MovieTaskGroupVo.builder()
            .metadata(photoGroup.getMetadata())
            .spec(photoGroup.getSpec())
            .status(photoGroup.getStatusOrDefault())
            .photos(List.of());
    }
}
