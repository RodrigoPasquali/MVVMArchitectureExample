package com.example.mvvmarchitectureexample.repository;

import androidx.lifecycle.LiveData;

import com.example.mvvmarchitectureexample.AppExecutors;
import com.example.mvvmarchitectureexample.api.ApiResponse;
import com.example.mvvmarchitectureexample.api.WebServiceApi;
import com.example.mvvmarchitectureexample.db.UserDao;
import com.example.mvvmarchitectureexample.model.User;

public class UserRepository {
    private final AppExecutors appExecutors;
    private final UserDao userDao;
    private final WebServiceApi githubService;

    public UserRepository(AppExecutors appExecutors, UserDao userDao, WebServiceApi githubService) {
        this.appExecutors = appExecutors;
        this.userDao = userDao;
        this.githubService = githubService;
    }

    public LiveData<Resource<User>> loadUser(String login) {
        return new NetworkBoundResource<User, User>(appExecutors) {

            @Override
            protected LiveData<User> loadFromDb() {
                return userDao.findByLogin(login);
            }

            @Override
            protected boolean shouldFetch(User data) {
                return data == null;
            }

            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                return githubService.getUser(login);
            }

            @Override
            protected void saveCallResult(User user) {
                userDao.insert(user);
            }
        }.asLiveData();
    }
}
