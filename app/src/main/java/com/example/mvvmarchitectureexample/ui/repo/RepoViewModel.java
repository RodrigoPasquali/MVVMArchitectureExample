package com.example.mvvmarchitectureexample.ui.repo;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mvvmarchitectureexample.model.Contributor;
import com.example.mvvmarchitectureexample.model.Repo;
import com.example.mvvmarchitectureexample.repository.RepoRepository;
import com.example.mvvmarchitectureexample.repository.Resource;
import com.example.mvvmarchitectureexample.util.AbsentLiveData;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class RepoViewModel extends ViewModel {
    final MutableLiveData<RepoId> repoId;
    private final LiveData<Resource<Repo>> repo;
    private final LiveData<Resource<List<Contributor>>> contributors;

    @Inject
    public RepoViewModel(RepoRepository repository){
        this.repoId = new MutableLiveData<>();
        repo = Transformations.switchMap(repoId, new Function<RepoId, LiveData<Resource<Repo>>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public LiveData<Resource<Repo>> apply(RepoId pRepoId) {
                LiveData<Resource<Repo>> liveDataRepo;

                if(pRepoId.isEmpty()){
                    liveDataRepo = AbsentLiveData.create();
                }else{
                    liveDataRepo = repository.loadRepo(pRepoId.owner, pRepoId.name);
                }

                return liveDataRepo;
            }
        });

        contributors = Transformations.switchMap(repoId, new Function<RepoId,
                                                 LiveData<Resource<List<Contributor>>>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public LiveData<Resource<List<Contributor>>> apply(RepoId pRepoId) {
                LiveData<Resource<List<Contributor>>> liveDataContributor;

                if(pRepoId.isEmpty()){
                    liveDataContributor = AbsentLiveData.create();
                }else{
                    liveDataContributor = repository.loadContributors(pRepoId.owner, pRepoId.name);
                }

                return liveDataContributor;
            }
        });
    }

    public LiveData<Resource<Repo>> getRepo(){
        return repo;
    }

    public LiveData<Resource<List<Contributor>>> getContributors(){
        return contributors;
    }

    public void retry(){
        RepoId currentRepoId = repoId.getValue();

        if((currentRepoId != null) && (!currentRepoId.isEmpty())){
            repoId.setValue(currentRepoId);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setId(String owner, String name){
        RepoId updateRepoId = new RepoId(owner, name);

        if(!(Objects.equals(repoId.getValue(), updateRepoId))){
            repoId.setValue(updateRepoId);
        }
    }

    static class RepoId{
        public final String owner;
        public final String name;

        RepoId(String owner, String name){
            this.owner = (owner == null) ? null :owner.trim();
            this.name = (name == null) ? null : name.trim();
        }

        boolean isEmpty(){
            return (owner == null) || (name == null )|| (owner.length() == 0) || (name.length() == 0);
        }
    }
}
