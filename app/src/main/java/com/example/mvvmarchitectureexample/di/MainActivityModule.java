package com.example.mvvmarchitectureexample.di;

import com.example.mvvmarchitectureexample.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainActivityModule {
    //Funcion para que FragmentBuildersModule sea como un subcomponente de esta clase
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();
}
