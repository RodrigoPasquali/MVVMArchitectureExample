package com.example.mvvmarchitectureexample.model;

import androidx.room.Entity;
import androidx.room.TypeConverters;

import com.example.mvvmarchitectureexample.db.GithubTypeConverters;

import java.util.List;

@Entity(primaryKeys = "query")
@TypeConverters({GithubTypeConverters.class})
public class RepoSearchResult {
    private String query;
    private List<Integer> repoIds;
    private int totalCount;
    private Integer nextPage;

    public RepoSearchResult(String query, List<Integer> repoIds, int totalCount, Integer nextPage) {
        this.query = query;
        this.repoIds = repoIds;
        this.totalCount = totalCount;
        this.nextPage = nextPage;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Integer> getRepoIds() {
        return repoIds;
    }

    public void setRepoIds(List<Integer> repoIds) {
        this.repoIds = repoIds;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }
}
