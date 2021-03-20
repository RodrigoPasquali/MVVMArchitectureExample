package com.example.mvvmarchitectureexample.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mvvmarchitectureexample.ui.repo.RepoViewModel;
import com.example.mvvmarchitectureexample.ui.search.SearchViewModel;
import com.example.mvvmarchitectureexample.ui.user.UserViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

//Se encarga de prober las instancias necesarias de objetos view model
@Module
abstract class ViewModelModule {
    @Binds //Misma accion que un Provider, indica que se probe un objeto del tipo UserViewModel
    @IntoMap //Injecta el objeto en un mapa
    @ViewModelKey(UserViewModel.class) //Por cada ViewModel se pasa una clave distinta
    abstract ViewModel bindUserViewModel(UserViewModel userViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel.class)
    abstract ViewModel bindSearchViewModel(SearchViewModel searchViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RepoViewModel.class)
    abstract ViewModel bindRepoViewModel(RepoViewModel repoViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(GithubViewModelFactory factory);
}
