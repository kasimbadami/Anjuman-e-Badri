package mumineen.connect.app.di;

import javax.inject.Singleton;

import mumineen.connect.app.model.AppApiService;
import dagger.Component;

@Singleton
@Component(modules = ApiModule.class)
public interface ApiComponent {
    void inject(AppApiService appApiService);
}
