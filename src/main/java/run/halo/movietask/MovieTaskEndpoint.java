package run.halo.movietask;

import lombok.AllArgsConstructor;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.router.QueryParamBuildUtil;
import run.halo.movietask.service.MovieTaskService;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;

/**
 * A custom endpoint for {@link Photo}.
 *
 * @author LIlGG
 * @since 1.0.0
 */
@Component
@AllArgsConstructor
public class MovieTaskEndpoint implements CustomEndpoint {
    
    private final MovieTaskService movieTaskService;
    
    @Override
    public RouterFunction<ServerResponse> endpoint() {
        final var tag = "api.plugin.halo.run/v1alpha1/Photo";
        return SpringdocRouteBuilder.route().GET("plugins/PluginPhotos/photos",
            this::listPhoto, builder -> {
                builder.operationId("ListPhotos")
                    .description("List photos.")
                    .tag(tag)
                    .response(responseBuilder().implementation(
                        ListResult.generateGenericClass(MovieTask.class)));
                QueryParamBuildUtil.buildParametersFromType(builder,
                    MovieTaskQuery.class
                );
            }
        ).build();
    }
    
    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion("api.plugin.halo.run/v1alpha1");
    }
    
    private Mono<ServerResponse> listPhoto(ServerRequest serverRequest) {
        MovieTaskQuery query = new MovieTaskQuery(serverRequest.queryParams());
        return movieTaskService.listPhoto(query).flatMap(
            photos -> ServerResponse.ok().bodyValue(photos));
    }
    
}
