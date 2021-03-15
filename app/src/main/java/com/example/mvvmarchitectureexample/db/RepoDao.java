package com.example.mvvmarchitectureexample.db;

import android.util.SparseIntArray;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mvvmarchitectureexample.model.Contributor;
import com.example.mvvmarchitectureexample.model.Repo;
import com.example.mvvmarchitectureexample.model.RepoSearchResult;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Dao
public abstract class RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Repo... repos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertContributors(List<Contributor> contributors);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRepos(List<Repo> repos);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract long createRepoIfNotExists(Repo repo);

    @Query("SELECT * " +
            "FROM repo " +
            "WHERE owner_login = :login " +
            "AND name = :name")
    public abstract LiveData<Repo> load(String login, String name);

    @Query("SELECT login, avatarUrl, repoName, repoOwner, contributions " +
            "FROM contributor " +
            "WHERE repoName = :repoName " +
            "AND repoOwner = :repoOwner " +
            "ORDER BY contributions DESC")
    public abstract LiveData<List<Contributor>> loadContributors(String repoName, String repoOwner);

    @Query("SELECT * FROM Repo WHERE owner_login = :owner ORDER BY startCount DESC")
    public abstract LiveData<List<Repo>> loadRepositories(String owner);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(RepoSearchResult repoSearchResult);

    @Query("SELECT * " +
            "FROM RepoSearchResult " +
            "WHERE query = :query")
    public abstract LiveData<RepoSearchResult> search(String query);

    @Query("SELECT * " +
            "FROM Repo " +
            "WHERE id in(:repoIds)")
    protected abstract LiveData<List<Repo>> loadById(List<Integer> repoIds);

    @Query("SELECT * " +
            "FROM RepoSearchResult " +
            "WHERE query = :query")
    public abstract RepoSearchResult findSearchResult(String query);

    public LiveData<List<Repo>> loadOrdered(List<Integer> reposIds) {
        //Mas util que un Hashmap para mapear IntegerxInteger
        SparseIntArray order = new SparseIntArray();
        int index = 0;
        for(Integer repoId : reposIds) {
            order.put(repoId, index);
            index++;
        }

        return Transformations.map(loadById(reposIds), new Function<List<Repo>, List<Repo>>() {
            @Override
            public List<Repo> apply(List<Repo> repos) {
                Collections.sort(repos, new Comparator<Repo>() {
                    @Override
                    public int compare(Repo repo1, Repo repo2) {
                        int position1 = order.get(repo1.getId());
                        int position2 = order.get(repo2.getId());
                        return 0;
                    }
                });
                return repos;
            }
        });
    }
}
