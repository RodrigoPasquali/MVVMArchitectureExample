package com.example.mvvmarchitectureexample.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mvvmarchitectureexample.model.Contributor;
import com.example.mvvmarchitectureexample.model.Repo;
import com.example.mvvmarchitectureexample.model.RepoSearchResult;
import com.example.mvvmarchitectureexample.model.User;

@Database(entities = {User.class, Repo.class, RepoSearchResult.class, Contributor.class},
          version = 1)
public abstract class GitHubDb extends RoomDatabase {
    abstract public UserDao userDao();

    abstract public RepoDao repoDao();
}
