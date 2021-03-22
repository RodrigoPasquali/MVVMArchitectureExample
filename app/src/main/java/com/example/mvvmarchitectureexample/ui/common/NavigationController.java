package com.example.mvvmarchitectureexample.ui.common;

import androidx.fragment.app.FragmentManager;

import com.example.mvvmarchitectureexample.MainActivity;
import com.example.mvvmarchitectureexample.R;
import com.example.mvvmarchitectureexample.ui.repo.RepoFragment;
import com.example.mvvmarchitectureexample.ui.search.SearchFragment;

import javax.inject.Inject;

public class NavigationController {
    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity){
        this.containerId = R.id.container;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToSearch(){
        SearchFragment searchFragment = new SearchFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, searchFragment)
                .commitAllowingStateLoss();
    }
}
