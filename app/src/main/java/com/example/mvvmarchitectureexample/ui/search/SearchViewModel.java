package com.example.mvvmarchitectureexample.ui.search;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mvvmarchitectureexample.model.Repo;
import com.example.mvvmarchitectureexample.repository.RepoRepository;
import com.example.mvvmarchitectureexample.repository.Resource;
import com.example.mvvmarchitectureexample.util.AbsentLiveData;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;

public class SearchViewModel extends ViewModel {
    private final MutableLiveData<String> query = new MutableLiveData<>();
    private final LiveData<Resource<List<Repo>>> results;
    private final NextPageHandler nextPageHandler;

    @Inject
    SearchViewModel(RepoRepository repository){
        nextPageHandler = new NextPageHandler(repository);
        results = Transformations.switchMap(query, new Function<String, LiveData<Resource<List<Repo>>>>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public LiveData<Resource<List<Repo>>> apply(String search) {
                LiveData<Resource<List<Repo>>> resultSearch;

                if(isValidString(search)){
                    resultSearch = AbsentLiveData.create();
                }else{
                    resultSearch =  repository.search(search);
                }

                return resultSearch;
            }
        });
    }

    public LiveData<Resource<List<Repo>>> getResults(){
        return results;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setQuery(String originalQuery){
        String input = originalQuery.toLowerCase(Locale.getDefault()).trim();

        if(!(Objects.equals(input, query.getValue()))){
            nextPageHandler.reset();
            query.setValue(input);
        }
    }

    public LiveData<LoadMoreState> getLoadMoreStatus(){
        return nextPageHandler.getLoadMoreState();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadNextPage(){
        String value = query.getValue();

        if(!isValidString(value)){
            nextPageHandler.queryNextPage(value);
        }
    }

    void refresh(){
        if(query.getValue() != null){
            query.setValue(query.getValue());
        }
    }

    private boolean isValidString(String string) {
        boolean isValid = true;

        if ((string == null) || (string.trim().length() == 0)) {
            isValid = false;
        }

        return isValid;
    }

    static class LoadMoreState{
        private final boolean running;
        private final String errorMessage;
        private boolean handlerError = false;

        LoadMoreState(boolean running, String errorMessage) {
            this.running = running;
            this.errorMessage = errorMessage;
        }

        boolean isRunning() {
            return running;
        }

        String getErrorMessage() {
            return errorMessage;
        }

        String getErrorMessageIfNoHandled() {
            if(!handlerError) {
                handlerError = true;

                return errorMessage;
            }

            return null;
        }
    }

    static class NextPageHandler implements Observer<Resource<Boolean>> {
        private LiveData<Resource<Boolean>> nextPageLiveData;
        private final MutableLiveData<LoadMoreState> loadMoreState = new MutableLiveData<>();
        private String query;
        private final RepoRepository repository;
        boolean hasMore;

        NextPageHandler(RepoRepository repository){
            this.repository = repository;
            reset();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        void queryNextPage(String query){
            if (!Objects.equals(this.query, query)) {
                unregister();
                this.query = query;
                nextPageLiveData = repository.searchNextPage(query);
                nextPageLiveData.observeForever(this);
            }
        }

        private void unregister(){
            if(nextPageLiveData != null){
                nextPageLiveData.removeObserver(this);
                nextPageLiveData = null;

                if(hasMore){
                    query = null;
                }
            }
        }

        private void reset(){
            unregister();
            hasMore = true;
            loadMoreState.setValue(new LoadMoreState(false, null));
        }

        MutableLiveData<LoadMoreState> getLoadMoreState(){
            return loadMoreState;
        }

        @Override
        public void onChanged(Resource<Boolean> result) {
            if(result == null){
                reset();
            }else {
                switch (result.status){
                    case SUCCESS:
                        hasMore = Boolean.TRUE.equals(result.data);
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(false, null));
                        break;

                    case ERROR:
                        hasMore = true;
                        unregister();
                        loadMoreState.setValue(new LoadMoreState(false, result.message));
                        break;
                }
            }
        }
    }
}
