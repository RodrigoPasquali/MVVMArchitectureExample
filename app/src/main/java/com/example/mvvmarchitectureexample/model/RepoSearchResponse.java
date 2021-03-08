package com.example.mvvmarchitectureexample.model;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class RepoSearchResponse {
    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("items")
    private List<Repo> items;
    private Integer nextPage;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<Repo> getItems() {
        return items;
    }

    public void setItems(List<Repo> items) {
        this.items = items;
    }

    public Integer getNextPage() {
        return nextPage;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    public List<Integer> getRepoIds() {
        List<Integer> listRepoIds = new LinkedList<>();

        for(Repo item : items) {
            listRepoIds.add(item.getId());
        }

        return listRepoIds;
    }
}
