package com.example.mvvmarchitectureexample.repository;

import android.os.Build;

import androidx.annotation.MainThread;
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.mvvmarchitectureexample.AppExecutors;
import com.example.mvvmarchitectureexample.api.ApiResponse;

import java.util.Objects;

public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final AppExecutors appExecutors;
    //Clase se encarga de obsevar los LiveData, sin que tener observar uno por uno.
    //Notifica cuando ocurre algun cambio en alguno de los LiveData
    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        result.setValue(Resource.loading(null));
        LiveData<ResultType> dbSource = loadFromDb();
        //Observa si hay algun cambio en la peticion a db, se agrega al MediatorLiveData
        result.addSource(dbSource, new Observer<ResultType>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChanged(ResultType data) {
                result.removeSource(dbSource);
                if(NetworkBoundResource.this.shouldFetch(data)) {
                    NetworkBoundResource.this.fetchFromNetwork(dbSource);
                } else {
                    result.addSource(dbSource, (ResultType newData) -> {
                        NetworkBoundResource.this.setValue(Resource.success(newData));
                    });
                }
            }
        });
    }

    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @MainThread
    protected abstract boolean shouldFetch(ResultType data);

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @MainThread
    private void setValue(Resource<ResultType> newValue) {
        if(!Objects.equals(result.getValue(), newValue)) {
            result.setValue(newValue);
        }
    }

    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.getBody();
    }

    @WorkerThread
    protected abstract void saveCallResult(RequestType item);

    protected void onFetchFailed() {};

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //Primer busca por datos en la base, para luego buscar datos en la web
    private void fetchFromNetwork(final LiveData<ResultType> dbSource){
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        result.addSource(dbSource, new Observer<ResultType>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onChanged(ResultType newData) {
                NetworkBoundResource.this.setValue(Resource.loading(newData));
            }
        });
        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);
            if(response.isSuccessful()){
                appExecutors.getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        NetworkBoundResource.this.saveCallResult(NetworkBoundResource.this.processResponse(response));
                        appExecutors.getMainThread().execute(() -> {
                            result.addSource(NetworkBoundResource.this.loadFromDb(), newData ->
                                    NetworkBoundResource.this.setValue(Resource.success(newData)));
                        });
                    }
                });
            }else{
                onFetchFailed();
                result.addSource(dbSource, newData ->
                        setValue(Resource.error(newData, response.getErrorMessage())));
            }
        });
    }
}
