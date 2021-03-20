package com.example.mvvmarchitectureexample.di;

import com.example.mvvmarchitectureexample.ui.repo.RepoFragment;
import com.example.mvvmarchitectureexample.ui.search.SearchFragment;
import com.example.mvvmarchitectureexample.ui.user.UserFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector //Fragmento potencialmente inyectable con Dagger
    abstract RepoFragment contributeRepoFragment();

    @ContributesAndroidInjector
    abstract UserFragment contributeUserFragment();

    @ContributesAndroidInjector
    abstract SearchFragment contributeSearchFragment();
}
