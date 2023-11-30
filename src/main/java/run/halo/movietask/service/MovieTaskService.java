package run.halo.movietask.service;

import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.movietask.MovieTask;
import run.halo.movietask.MovieTaskQuery;

/**
 * A service for {@link MovieTask}.
 *
 * @author LIlGG
 * @since 1.0.0
 */
public interface MovieTaskService {
    
    /**
     * List photos.
     *
     * @param query query
     * @return a mono of list result
     */
    Mono<ListResult<MovieTask>> listPhoto(MovieTaskQuery query);
}
