package run.halo.movietask;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * @author ryanwang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "core.halo.run", version = "v1alpha1", kind = "MovieTaskGroup",
    plural = "movietaskgroups", singular = "movietaskgroup")
public class MovieTaskGroup extends AbstractExtension {

    @Schema(required = true)
    private MovieTaskGroupSpec spec;

    @Schema
    private PostGroupStatus status;

    @Data
    public static class MovieTaskGroupSpec {
        @Schema(required = true)
        private String displayName;

        private Integer priority;
    }

    @JsonIgnore
    public PostGroupStatus getStatusOrDefault() {
        if (this.status == null) {
            this.status = new PostGroupStatus();
        }
        return this.status;
    }

    @Data
    public static class PostGroupStatus {

        public Integer photoCount;
    }
}
