package com.example.mvvmarchitectureexample.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

//Ayuda a crear de forma dinamica los ViewModel
@Singleton
public class GithubViewModelFactory implements ViewModelProvider.Factory {
    //Asocia un Provider a una clave determinada para luego inyectarlo en un mapa
    private final Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

    @Inject
    public GithubViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators){
        this.creators = creators;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Provider<? extends ViewModel> creator = creators.get(modelClass);

        //chequea si mapa de proveedores posee la clave
        if(creator == null){
            for(Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : creators.entrySet()){
                if(modelClass.isAssignableFrom(entry.getKey())) { //Chequea si la clave pertenece a una subclase
                    creator = entry.getValue();
                    break;
                }
            }
        }

        if(creator == null){
            throw new IllegalArgumentException("unknow model class " + modelClass);
        }

        try {
            return (T) creator.get();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}