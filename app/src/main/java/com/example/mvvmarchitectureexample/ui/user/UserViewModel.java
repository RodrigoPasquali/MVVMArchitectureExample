package com.example.mvvmarchitectureexample.ui.user;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mvvmarchitectureexample.model.Repo;
import com.example.mvvmarchitectureexample.model.User;
import com.example.mvvmarchitectureexample.repository.RepoRepository;
import com.example.mvvmarchitectureexample.repository.Resource;
import com.example.mvvmarchitectureexample.repository.UserRepository;
import com.example.mvvmarchitectureexample.util.AbsentLiveData;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<String> login = new MutableLiveData<>();
    private final LiveData<Resource<User>> user;
    private final LiveData<Resource<List<Repo>>> repos;

    @Inject
    public UserViewModel(UserRepository userRepository, RepoRepository repoRepository) {
        user = Transformations.switchMap(login, new Function<String, LiveData<Resource<User>>>() {
            @Override
            public LiveData<Resource<User>> apply(String login) {
                LiveData<Resource<User>> data;

                if (login == null) {
                    data = AbsentLiveData.create();
                } else {
                    data = userRepository.loadUser(login);
                }

                return data;
            }
        });

        repos = Transformations.switchMap(login, new Function<String, LiveData<Resource<List<Repo>>>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public LiveData<Resource<List<Repo>>> apply(String login) {
                LiveData<Resource<List<Repo>>> data;

                if (login == null) {
                    data = AbsentLiveData.create();
                } else {
                    data = repoRepository.loadRepos(login);
                }

                return data;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setLogin(String login) {
        if (!Objects.equals(this.login.getValue(), login)) {
            this.login.setValue(login);
        }
    }

    public LiveData<Resource<User>> getUser() {
        return user;
    }

    public LiveData<Resource<List<Repo>>> getRepos() {
        return repos;
    }

    public void retry() {
        if(this.login.getValue() != null) {
            this.login.setValue(this.login.getValue());
        }
    }
}
