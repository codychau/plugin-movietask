package run.halo.movietask;

import org.springframework.util.comparator.Comparators;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * A sorter for {@link Photo}.
 *
 * @author LIlGG
 * @since 1.0.0
 */
public enum MovieTaskSorter {
    DISPLAY_NAME,
    
    CREATE_TIME;
    
    static final Function<MovieTask, String> name = photo -> photo.getMetadata()
        .getName();
    
    /**
     * Converts {@link Comparator} from {@link MovieTaskSorter} and ascending.
     *
     * @param sorter    a {@link MovieTaskSorter}
     * @param ascending ascending if true, otherwise descending
     * @return a {@link Comparator} of {@link MovieTask}
     */
    public static Comparator<MovieTask> from(MovieTaskSorter sorter,
                                         Boolean ascending) {
        if (Objects.equals(true, ascending)) {
            return from(sorter);
        }
        return from(sorter).reversed();
    }
    
    /**
     * Converts {@link Comparator} from {@link MovieTaskSorter}.
     *
     * @param sorter a {@link MovieTaskSorter}
     * @return a {@link Comparator} of {@link Photo}
     */
    static Comparator<MovieTask> from(MovieTaskSorter sorter) {
        if (sorter == null) {
            return createTimeComparator();
        }
        if (CREATE_TIME.equals(sorter)) {
            Function<MovieTask, Instant> comparatorFunc
                = photo -> photo.getMetadata().getCreationTimestamp();
            return Comparator.comparing(comparatorFunc).thenComparing(name);
        }
        
        if (DISPLAY_NAME.equals(sorter)) {
            Function<MovieTask, String> comparatorFunc = moment -> moment.getSpec()
                .getDisplayName();
            return Comparator.comparing(comparatorFunc, Comparators.nullsLow())
                .thenComparing(name);
        }
        
        throw new IllegalStateException("Unsupported sort value: " + sorter);
    }
    
    /**
     * Converts {@link MovieTaskSorter} from string.
     *
     * @param sort sort string
     * @return a {@link MovieTaskSorter}
     */
    static MovieTaskSorter convertFrom(String sort) {
        for (MovieTaskSorter sorter : values()) {
            if (sorter.name().equalsIgnoreCase(sort)) {
                return sorter;
            }
        }
        return null;
    }
    
    /**
     * Creates a {@link Comparator} of {@link Photo} by creation time.
     *
     * @return a {@link Comparator} of {@link Photo}
     */
    static Comparator<MovieTask> createTimeComparator() {
        Function<MovieTask, Instant> comparatorFunc = photo -> photo.getMetadata()
            .getCreationTimestamp();
        return Comparator.comparing(comparatorFunc).thenComparing(name);
    }
}
