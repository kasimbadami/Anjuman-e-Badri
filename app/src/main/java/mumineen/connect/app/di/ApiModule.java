package mumineen.connect.app.di;

import mumineen.connect.app.model.AppApi;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class ApiModule {

    private String API_BASE_URL ="https://technologyworkshops.net/";

    @Provides
    public AppApi providesAppApi()
    {
        return (new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(AppApi.class));
    }
}
