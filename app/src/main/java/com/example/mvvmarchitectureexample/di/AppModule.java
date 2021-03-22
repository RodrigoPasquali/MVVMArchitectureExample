package com.example.mvvmarchitectureexample.di;

import android.app.Application;

import androidx.room.Room;

import com.example.mvvmarchitectureexample.api.WebServiceApi;
import com.example.mvvmarchitectureexample.db.GitHubDb;
import com.example.mvvmarchitectureexample.db.RepoDao;
import com.example.mvvmarchitectureexample.db.UserDao;
import com.example.mvvmarchitectureexample.util.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {
    private static final String BASE_URL = "https://api.github.com";
    private static final String  DATA_BASE_NAME = "github.db";

    @Singleton
    @Provides
    WebServiceApi provideGithubService() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(WebServiceApi.class);
    }

    @Singleton
    @Provides
    GitHubDb provideDb(Application app) {
        return Room.databaseBuilder(app, GitHubDb.class, DATA_BASE_NAME).build();
    }

    @Singleton
    @Provides
    UserDao provideUserDao(GitHubDb db) {
        return db.userDao();
    }

    @Singleton
    @Provides
    RepoDao provideRepoDao(GitHubDb db) {
        return db.repoDao();
    }
}
