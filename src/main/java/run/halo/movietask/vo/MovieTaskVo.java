package run.halo.movietask.vo;

import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import run.halo.movietask.MovieTask;

/**
 * @author LIlGG
 */
@Value
@Builder
public class MovieTaskVo implements ExtensionVoOperator {
    
    MetadataOperator metadata;
    
    MovieTask.MovieSpec spec;
    
    public static MovieTaskVo from(MovieTask photo) {
        return MovieTaskVo.builder()
            .metadata(photo.getMetadata())
            .spec(photo.getSpec())
            .build();
    }
}
