package run.halo.movietask.service;

import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.router.IListRequest.QueryListRequest;
import run.halo.movietask.MovieTaskGroup;

/**
 * A service for {@link MovieTaskGroup}.
 *
 * @author cody
 * @since 2.0.0
 */
public interface MovieTaskGroupService {
    
    /**
     * List photo groups.
     *
     * @param request request
     * @return a mono of list result
     */
    Mono<ListResult<MovieTaskGroup>> listMovieTaskGroup(QueryListRequest request);
    
    /**
     * Create a photo group.
     *
     * @param name name
     * @return a mono of photo group
     */
    Mono<MovieTaskGroup> deleteMovieTaskGroup(String name);
    
}
