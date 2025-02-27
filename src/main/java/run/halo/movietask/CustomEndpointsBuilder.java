package run.halo.movietask;

import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CustomEndpointsBuilder {

    private final Map<GroupVersion, List<RouterFunction<ServerResponse>>> routerFunctionsMap;

    public CustomEndpointsBuilder() {
        routerFunctionsMap = new HashMap<>();
    }

    public CustomEndpointsBuilder add(CustomEndpoint customEndpoint) {
        routerFunctionsMap
            .computeIfAbsent(customEndpoint.groupVersion(), gv -> new LinkedList<>())
            .add(customEndpoint.endpoint());
        return this;
    }

    public RouterFunction<ServerResponse> build() {
        SpringdocRouteBuilder routeBuilder = SpringdocRouteBuilder.route();
        routerFunctionsMap.forEach((gv, routerFunctions) -> {
            routeBuilder.nest(RequestPredicates.path("/apis/" + gv),
                () -> routerFunctions.stream().reduce(RouterFunction::and).orElse(null),
                builder -> builder.operationId("CustomEndpoints")
                    .description("Custom Endpoint")
                    .tag(gv + "/CustomEndpoint")
            );
        });
        routerFunctionsMap.clear();
        return routeBuilder.build();
    }
}
