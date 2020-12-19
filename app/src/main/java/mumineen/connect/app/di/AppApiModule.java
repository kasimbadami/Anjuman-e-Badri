package mumineen.connect.app.di;

import mumineen.connect.app.model.AppApiService;
import dagger.Module;
import dagger.Provides;

@Module
public class AppApiModule {

    @Provides
    public AppApiService providesAppApiService()
    {
        return new AppApiService();
    }
}
