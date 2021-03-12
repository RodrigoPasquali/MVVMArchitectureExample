package com.example.mvvmarchitectureexample.repository;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mvvmarchitectureexample.api.ApiResponse;
import com.example.mvvmarchitectureexample.api.WebServiceApi;
import com.example.mvvmarchitectureexample.db.GitHubDb;
import com.example.mvvmarchitectureexample.model.Repo;
import com.example.mvvmarchitectureexample.model.RepoSearchResponse;
import com.example.mvvmarchitectureexample.model.RepoSearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class FetchNextSearchPageTask implements Runnable {
    private final MutableLiveData<Resource<Boolean>> liveData = new MutableLiveData<>();
    private final String query;
    private final WebServiceApi githubService;
    private final GitHubDb gitHubDb;

    public FetchNextSearchPageTask(String query, WebServiceApi githubService, GitHubDb gitHubDb) {
        this.query = query;
        this.githubService = githubService;
        this.gitHubDb = gitHubDb;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        RepoSearchResult current = gitHubDb.repoDao().findSearchResult(query);
        final Integer nextPage = current.getNextPage();

        checkResult(current);
        checkNextPage(nextPage);
        searchRemote(nextPage, current);
    }

    private void checkNextPage(Integer nextPage) {
        if(nextPage == null) {
            liveData.postValue(Resource.success(false));
            return;
        }
    }

    private void checkResult(RepoSearchResult result) {
        if(result == null) {
            liveData.postValue(null);
            return;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void searchRemote(int nextPage, RepoSearchResult current) {
        try {
            Response<RepoSearchResponse> response = githubService.searchRepos(query, nextPage).execute();
            ApiResponse<RepoSearchResponse> apiResponse = new ApiResponse<>(response);

            if(apiResponse.isSuccessful()) {
                responseSuccess(current, apiResponse);
            } else {
                liveData.postValue(Resource.error(true, apiResponse.getErrorMessage()));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void responseSuccess(RepoSearchResult current, ApiResponse<RepoSearchResponse> apiResponse) {
        List<Integer> repoIds = new ArrayList<>();
        repoIds.addAll(current.getRepoIds());
        repoIds.addAll(apiResponse.getBody().getRepoIds());
        RepoSearchResult mergeRepoResults = new RepoSearchResult(
                query,
                repoIds,
                apiResponse.getBody().getTotalCount(),
                apiResponse.getNextPage());

        insertIntoDb(mergeRepoResults, apiResponse.getBody().getItems());

        liveData.postValue(Resource.success(apiResponse.getNextPage() != null));
    }

    private void insertIntoDb(RepoSearchResult repoSearchResult, List<Repo> repos){
        try {
            gitHubDb.beginTransaction();
            gitHubDb.repoDao().insert(repoSearchResult);
            gitHubDb.repoDao().insertRepos(repos);
            gitHubDb.setTransactionSuccessful();
        } finally {
            gitHubDb.endTransaction();
        }
    }

    LiveData<Resource<Boolean>> getLiveData() {
        return liveData;
    }
}
