package com.example.mvvmarchitectureexample.ui.common;

import androidx.fragment.app.FragmentManager;

import com.example.mvvmarchitectureexample.MainActivity;
import com.example.mvvmarchitectureexample.R;
import com.example.mvvmarchitectureexample.ui.repo.RepoFragment;
import com.example.mvvmarchitectureexample.ui.search.SearchFragment;
import com.example.mvvmarchitectureexample.ui.user.UserFragment;

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

    public void navigateToRepo(String owner, String name){
        RepoFragment fragment = RepoFragment.create(owner, name);
        String tag = "repo" + "/" + "/" + name;
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void navigateToUser(String login){
        String tag = "user" + "/" + login;
        UserFragment fragment = UserFragment.create(login);
        fragmentManager.beginTransaction()
                .replace(containerId, fragment, tag)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}
