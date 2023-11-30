package run.halo.movietask.finders.impl;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.comparator.Comparators;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.finders.Finder;
import run.halo.movietask.MovieTask;
import run.halo.movietask.MovieTaskGroup;
import run.halo.movietask.finders.MovieTaskFinder;
import run.halo.movietask.vo.MovieTaskGroupVo;
import run.halo.movietask.vo.MovieTaskVo;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author LIlGG
 */
@Finder("photoFinder")
public class MovieTaskFInderImpl implements MovieTaskFinder {
    private final ReactiveExtensionClient client;
    
    public MovieTaskFInderImpl(ReactiveExtensionClient client) {
        this.client = client;
    }
    
    @Override
    public Flux<MovieTaskVo> listAll() {
        return this.client.list(MovieTask.class, null, defaultPhotoComparator())
            .flatMap(photo -> Mono.just(MovieTaskVo.from(photo)));
    }
    
    @Override
    public Mono<ListResult<MovieTaskVo>> list(Integer page, Integer size) {
        return list(page, size, null);
    }
    
    @Override
    public Mono<ListResult<MovieTaskVo>> list(Integer page, Integer size,
        String group) {
        return pagePhoto(page, size, group, null, defaultPhotoComparator());
    }
    
    private Mono<ListResult<MovieTaskVo>> pagePhoto(Integer page, Integer size,
        String group, Predicate<MovieTask> photoPredicate,
        Comparator<MovieTask> comparator) {
        Predicate<MovieTask> predicate = photoPredicate == null ? photo -> true
            : photoPredicate;
        if (StringUtils.isNotEmpty(group)) {
            predicate = predicate.and(photo -> {
                String groupName = photo.getSpec().getGroupName();
                return StringUtils.equals(groupName, group);
            });
        }
        return client.list(MovieTask.class, predicate, comparator,
            pageNullSafe(page), sizeNullSafe(size)
        ).flatMap(list -> Flux.fromStream(list.get())
            .concatMap(photo -> Mono.just(MovieTaskVo.from(photo)))
            .collectList()
            .map(momentVos -> new ListResult<>(list.getPage(), list.getSize(),
                list.getTotal(), momentVos
            ))).defaultIfEmpty(new ListResult<>(page, size, 0L, List.of()));
    }
    
    @Override
    public Flux<MovieTaskVo> listBy(String groupName) {
        return client.list(MovieTask.class, photo -> {
            String group = photo.getSpec().getGroupName();
            return StringUtils.equals(group, groupName);
        }, defaultPhotoComparator()).flatMap(
            photo -> Mono.just(MovieTaskVo.from(photo)));
    }
    
    @Override
    public Flux<MovieTaskGroupVo> groupBy() {
        return this.client.list(MovieTaskGroup.class, null,
            defaultGroupComparator()
        ).concatMap(group -> {
            MovieTaskGroupVo.MovieTaskGroupVoBuilder builder = MovieTaskGroupVo.from(group);
            return this.listBy(group.getMetadata().getName()).collectList().map(
                photos -> {
                    MovieTaskGroup.PostGroupStatus status = group.getStatus();
                    status.setPhotoCount(photos.size());
                    builder.status(status);
                    builder.photos(photos);
                    return builder.build();
                });
        });
    }
    
    static Comparator<MovieTaskGroup> defaultGroupComparator() {
        Function<MovieTaskGroup, Integer> priority = group -> group.getSpec()
            .getPriority();
        Function<MovieTaskGroup, Instant> createTime = group -> group.getMetadata()
            .getCreationTimestamp();
        Function<MovieTaskGroup, String> name = group -> group.getMetadata()
            .getName();
        return Comparator.comparing(priority, Comparators.nullsLow())
            .thenComparing(createTime)
            .thenComparing(name);
    }
    
    static Comparator<MovieTask> defaultPhotoComparator() {
        Function<MovieTask, Integer> priority = link -> link.getSpec()
            .getPriority();
        Function<MovieTask, Instant> createTime = link -> link.getMetadata()
            .getCreationTimestamp();
        Function<MovieTask, String> name = link -> link.getMetadata().getName();
        return Comparator.comparing(priority, Comparators.nullsLow())
            .thenComparing(Comparator.comparing(createTime).reversed())
            .thenComparing(name);
    }
    
    int pageNullSafe(Integer page) {
        return ObjectUtils.defaultIfNull(page, 1);
    }
    
    int sizeNullSafe(Integer size) {
        return ObjectUtils.defaultIfNull(size, 10);
    }
}
