package run.halo.movietask;

import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;

/**
 * @author ryanwang
 * @since 2.0.0
 */
@Component
public class MovieTaskPlugin extends BasePlugin {
    private final SchemeManager schemeManager;

    public MovieTaskPlugin(PluginWrapper wrapper, SchemeManager schemeManager) {
        super(wrapper);
        this.schemeManager = schemeManager;
    }
    
    @Override
    public void start() {
        schemeManager.register(MovieTask.class);
        schemeManager.register(MovieTaskGroup.class);
    }
    
    @Override
    public void stop() {
        schemeManager.unregister(schemeManager.get(MovieTask.class));
        schemeManager.unregister(schemeManager.get(MovieTaskGroup.class));
    }
}
