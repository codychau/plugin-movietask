package run.halo.movietask.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Extension;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.movietask.MovieTask;
import run.halo.movietask.MovieTaskQuery;
import run.halo.movietask.MovieTaskSorter;
import run.halo.movietask.service.MovieTaskService;

import java.util.Comparator;
import java.util.function.Predicate;

import static run.halo.app.extension.router.selector.SelectorUtil.labelAndFieldSelectorToPredicate;

/**
 * Service implementation for {@link MovieTask}.
 *
 * @author LIlGG
 * @since 1.0.0
 */
@Component
public class MovieTaskServiceImpl implements MovieTaskService {

    private final ReactiveExtensionClient client;

    public MovieTaskServiceImpl(ReactiveExtensionClient client) {
        this.client = client;
    }
    
    public Mono<ListResult<MovieTask>> listPhoto(MovieTaskQuery query) {
        Comparator<MovieTask> comparator = MovieTaskSorter.from(query.getSort(),
            query.getSortOrder()
        );
        return this.client.list(MovieTask.class, photoListPredicate(query),
            comparator, query.getPage(), query.getSize()
        );
    }
    
    Predicate<MovieTask> photoListPredicate(MovieTaskQuery query) {
        Predicate<MovieTask> predicate = photo -> true;
        String keyword = query.getKeyword();
        
        if (keyword != null) {
            predicate = predicate.and(photo -> {
                String displayName = photo.getSpec().getDisplayName();
                return StringUtils.containsIgnoreCase(displayName, keyword);
            });
        }
        
        String groupName = query.getGroup();
        if (groupName != null) {
            predicate = predicate.and(photo -> {
                String group = photo.getSpec().getGroupName();
                return StringUtils.equals(group, groupName);
            });
        }
        
        Predicate<Extension> labelAndFieldSelectorPredicate
            = labelAndFieldSelectorToPredicate(query.getLabelSelector(),
            query.getFieldSelector()
        );
        return predicate.and(labelAndFieldSelectorPredicate);
    }
}
