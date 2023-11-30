package run.halo.movietask.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.router.IListRequest.QueryListRequest;
import run.halo.movietask.MovieTask;
import run.halo.movietask.MovieTaskGroup;
import run.halo.movietask.service.MovieTaskGroupService;

import java.util.function.Function;
import java.util.function.Predicate;

import static run.halo.app.extension.router.selector.SelectorUtil.labelAndFieldSelectorToPredicate;

/**
 * Service implementation for {@link MovieTask}.
 *
 * @author LIlGG
 * @since 1.0.0
 */
@Component
public class MovieTaskGroupServiceImpl implements MovieTaskGroupService {

    private final ReactiveExtensionClient client;

    public MovieTaskGroupServiceImpl(ReactiveExtensionClient client) {
        this.client = client;
    }
    
    @SuppressWarnings("checkstyle:Indentation")
    @Override
    public Mono<ListResult<MovieTaskGroup>> listMovieTaskGroup(QueryListRequest query) {
        return this.client.list(MovieTaskGroup.class, photoListPredicate(query),
            null, query.getPage(), query.getSize()
        ).flatMap(listResult -> Flux.fromStream(
                listResult.get().map(this::populateMovies))
            .concatMap(Function.identity())
            .collectList()
            .map(groups -> new ListResult<>(listResult.getPage(),
                listResult.getSize(), listResult.getTotal(), groups
            )));
    }
    
    @Override
    public Mono<MovieTaskGroup> deleteMovieTaskGroup(String name) {
        return this.client.fetch(MovieTaskGroup.class, name).flatMap(
            photoGroup -> this.client.delete(photoGroup)
                .flatMap(deleted -> this.client.list(MovieTask.class,
                    (photo) -> StringUtils.equals(name,
                        photo.getSpec().getGroupName()
                    ), null
                ).flatMap(this.client::delete).then(Mono.just(deleted))));
    }
    
    private Mono<MovieTaskGroup> populateMovies(MovieTaskGroup movieTaskGroup) {
        return Mono.just(movieTaskGroup).flatMap(fg -> fetchPhotoCount(fg).doOnNext(
                count -> fg.getStatusOrDefault().setPhotoCount(count))
            .thenReturn(fg));
    }
    
    @SuppressWarnings("checkstyle:OperatorWrap")
    Mono<Integer> fetchPhotoCount(MovieTaskGroup movieTaskGroup) {
        Assert.notNull(movieTaskGroup, "The photoGroup must not be null.");
        String name = movieTaskGroup.getMetadata().getName();
        return client.list(MovieTask.class, photo -> !photo.isDeleted()
                && photo.getSpec().getGroupName().equals(name), null)
            .count()
            .defaultIfEmpty(0L)
            .map(Long::intValue);
    }
    
    Predicate<MovieTaskGroup> photoListPredicate(QueryListRequest query) {
        return labelAndFieldSelectorToPredicate(query.getLabelSelector(),
            query.getFieldSelector()
        );
    }
}
