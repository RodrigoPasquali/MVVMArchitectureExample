package com.example.mvvmarchitectureexample.repository;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.mvvmarchitectureexample.AppExecutors;
import com.example.mvvmarchitectureexample.api.ApiResponse;
import com.example.mvvmarchitectureexample.api.WebServiceApi;
import com.example.mvvmarchitectureexample.db.GitHubDb;
import com.example.mvvmarchitectureexample.db.RepoDao;
import com.example.mvvmarchitectureexample.model.Contributor;
import com.example.mvvmarchitectureexample.model.Repo;
import com.example.mvvmarchitectureexample.model.RepoSearchResponse;
import com.example.mvvmarchitectureexample.model.RepoSearchResult;
import com.example.mvvmarchitectureexample.util.AbsentLiveData;
import com.example.mvvmarchitectureexample.util.RateLimiter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
@Singleton
public class RepoRepository {
    private final GitHubDb gitHubDb;
    private final RepoDao repoDao;
    private final WebServiceApi githubService;
    private final AppExecutors appExecutors;

    private final RateLimiter<String> repoListRateLimiter;

    public RepoRepository(GitHubDb gitHubDb, RepoDao repoDao, WebServiceApi githubService,
                          AppExecutors appExecutors) {
        this.gitHubDb = gitHubDb;
        this.repoDao = repoDao;
        this.githubService = githubService;
        this.appExecutors = appExecutors;

        int timeout = 10;
        TimeUnit timeUnit = TimeUnit.MINUTES;
        this.repoListRateLimiter = new RateLimiter<>(timeout, timeUnit);
    }

    public LiveData<Resource<List<Repo>>> loadRepos(String owner) {
        return new NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
            @Override
            protected LiveData<List<Repo>> loadFromDb() {
                LiveData<List<Repo>> repositoriesLoaded = repoDao.loadRepositories(owner);
                return repositoriesLoaded;
            }

            @Override
            protected boolean shouldFetch(List<Repo> data) {
                boolean shouldFetch = (data == null)
                        || (data.isEmpty())
                        || repoListRateLimiter.shouldFetched( owner) ;
                return shouldFetch;
            }

            @Override
            protected LiveData<ApiResponse<List<Repo>>> createCall() {
                LiveData<ApiResponse<List<Repo>>> repositoriesFetched = githubService.getRepos(owner);
                return repositoriesFetched;
            }

            @Override
            protected void saveCallResult(List<Repo> repoList) {
                repoDao.insertRepos(repoList);
            }

            @Override
            protected void onFetchFailed() {
                repoListRateLimiter.reset(owner);
            }
        }.asLiveData();
    }

    public LiveData<Resource<Repo>> loadRepo(String owner, String name) {
        return new NetworkBoundResource<Repo, Repo>(appExecutors) {
            @Override
            protected LiveData<Repo> loadFromDb() {
                LiveData<Repo> repositoryLoaded = repoDao.load(owner, name);
                return repositoryLoaded;
            }

            @Override
            protected boolean shouldFetch(Repo data) {
                boolean shouldFetch = data == null;
                return shouldFetch;
            }

            @Override
            protected LiveData<ApiResponse<Repo>> createCall() {
                LiveData<ApiResponse<Repo>> repositoryFetched = githubService.getRepo(owner, name);
                return repositoryFetched;
            }

            @Override
            protected void saveCallResult(Repo repo) {
                repoDao.insert(repo);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Contributor>>> loadContributors(String owner, String name) {
        return new NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors) {
            @Override
            protected LiveData<List<Contributor>> loadFromDb() {
                LiveData<List<Contributor>> contributorsLoaded = repoDao.loadContributors(name, owner);
                return contributorsLoaded;
            }

            @Override
            protected boolean shouldFetch(List<Contributor> data) {
                boolean shouldFetch = (data == null) || (data.isEmpty());
                return shouldFetch;
            }

            @Override
            protected LiveData<ApiResponse<List<Contributor>>> createCall() {
                LiveData<ApiResponse<List<Contributor>>> contributorsFetched = githubService.getContributors(owner, name);
                return contributorsFetched;
            }

            @Override
            protected void saveCallResult(List<Contributor> contributors) {
                int stars = 0;
                String description = "";
                for(Contributor contributor : contributors) {
                    contributor.setRepoName(name);
                    contributor.setRepoOwner(owner);
                }

                gitHubDb.beginTransaction();
                try{
                    repoDao.createRepoIfNotExists(new Repo(Repo.getID_UNKNOWN(), name,
                            owner + "/" + name, description, stars,
                                     new Repo.Owner(owner, null)));
                } finally {
                    gitHubDb.endTransaction();
                }
            }
        }.asLiveData();
    }

    public LiveData<Resource<Boolean>> searchNextPage(String query) {
        FetchNextSearchPageTask fetchNextSearchPageTask = new FetchNextSearchPageTask(
                query, githubService, gitHubDb);
        appExecutors.getNetworkIO().execute(fetchNextSearchPageTask);
        return fetchNextSearchPageTask.getLiveData();
    }

    public LiveData<Resource<List<Repo>>> search(String query){
        return new NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors) {
            @Override
            protected LiveData<List<Repo>> loadFromDb() {
                return Transformations.switchMap(repoDao.search(query), new Function<RepoSearchResult, LiveData<List<Repo>>>() {
                    @Override
                    public LiveData<List<Repo>> apply(RepoSearchResult searchData) {
                        if(searchData == null) {
                            return AbsentLiveData.create();
                        } else {
                            return repoDao.loadOrdered(searchData.getRepoIds());
                        }
                    }
                });
            }

            @Override
            protected boolean shouldFetch(List<Repo> data) {
                boolean shouldFetch = data == null;
                return shouldFetch;
            }

            @Override
            protected LiveData<ApiResponse<RepoSearchResponse>> createCall() {
                LiveData<ApiResponse<RepoSearchResponse>> responseLiveData = githubService.searchRepos(query);
                return responseLiveData;
            }

            @Override
            protected void saveCallResult(RepoSearchResponse item) {
                RepoSearchResult repoSearchResult = new RepoSearchResult(
                        query, item.getRepoIds(), item.getTotalCount(), item.getNextPage());

                gitHubDb.beginTransaction();
                try {
                    repoDao.insertRepos(item.getItems());
                    repoDao.insert(repoSearchResult);
                    gitHubDb.setTransactionSuccessful();
                } finally {
                    gitHubDb.endTransaction();
                }
            }

            @Override
            protected RepoSearchResponse processResponse(ApiResponse<RepoSearchResponse> response) {
                RepoSearchResponse body = response.getBody();
                if(body != null) {
                    body.setNextPage(response.getNextPage());
                }
                return body;
            }
        }.asLiveData();
    }
}
