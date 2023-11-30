package run.halo.movietask;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;
import run.halo.app.theme.router.PageUrlUtils;
import run.halo.app.theme.router.UrlContextListResult;
import run.halo.movietask.finders.MovieTaskFinder;
import run.halo.movietask.vo.MovieTaskGroupVo;
import run.halo.movietask.vo.MovieTaskVo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static run.halo.app.theme.router.PageUrlUtils.totalPage;

/**
 * Provides a <code>/photos</code> route for the topic end to handle routing.
 *
 * @author LIlGG
 */
@Component
@AllArgsConstructor
public class MovieTaskRouter {
    
    private static final String GROUP_PARAM = "group";
    
    private MovieTaskFinder photoFinder;
    
    private final ReactiveSettingFetcher settingFetcher;
    
    /**
     * Provides a <code>/photos</code> route for the topic end to handle routing.
     *
     * @return a {@link RouterFunction} instance
     */
    @Bean
    RouterFunction<ServerResponse> photoRouter() {
        return route(GET("/photos").or(GET("/photos/page/{page:\\d+}")),
            handlerFunction()
        );
    }
    
    private HandlerFunction<ServerResponse> handlerFunction() {
        return request -> ServerResponse.ok().render("photos",
            Map.of("groups", photoGroups(),
                "photos", photoList(request),
                ModelConst.TEMPLATE_ID, "photos",
                "title", getPhotosTitle()
            )
        );
    }
    
    private Mono<UrlContextListResult<MovieTaskVo>> photoList(ServerRequest request) {
        String path = request.path();
        int pageNum = pageNumInPathVariable(request);
        String group = groupPathQueryParam(request);
        return this.settingFetcher.get("base")
            .map(item -> item.get("pageSize").asInt(10))
            .defaultIfEmpty(10)
            .flatMap(pageSize -> photoFinder.list(pageNum, pageSize, group)
                .map(list -> new UrlContextListResult.Builder<MovieTaskVo>()
                    .listResult(list)
                    .nextUrl(appendGroupParam(
                        PageUrlUtils.nextPageUrl(path, totalPage(list)), group)
                    )
                    .prevUrl(appendGroupParam(PageUrlUtils.prevPageUrl(path), group))
                    .build()
                )
            );
        
    }
    
    private static String appendGroupParam(String path, String group) {
        return UriComponentsBuilder.fromPath(path)
            .queryParamIfPresent(GROUP_PARAM, Optional.ofNullable(group))
            .build()
            .toString();
    }
    
    private int pageNumInPathVariable(ServerRequest request) {
        String page = request.pathVariables().get("page");
        return NumberUtils.toInt(page, 1);
    }
    
    private String groupPathQueryParam(ServerRequest request) {
        return request.queryParam(GROUP_PARAM)
            .filter(StringUtils::isNotBlank)
            .orElse(null);
    }
    
    Mono<String> getPhotosTitle() {
        return this.settingFetcher.get("base").map(
            setting -> setting.get("title").asText("图库")).defaultIfEmpty(
            "图库");
    }
    
    private Mono<List<MovieTaskGroupVo>> photoGroups() {
        return photoFinder.groupBy().collectList();
    }
}
