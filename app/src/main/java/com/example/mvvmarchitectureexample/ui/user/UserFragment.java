package com.example.mvvmarchitectureexample.ui.user;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mvvmarchitectureexample.R;
import com.example.mvvmarchitectureexample.binding.FragmentDataBindingComponent;
import com.example.mvvmarchitectureexample.databinding.FragmentUserBinding;
import com.example.mvvmarchitectureexample.di.Injectable;
import com.example.mvvmarchitectureexample.model.Repo;
import com.example.mvvmarchitectureexample.model.User;
import com.example.mvvmarchitectureexample.repository.Resource;
import com.example.mvvmarchitectureexample.ui.common.NavigationController;
import com.example.mvvmarchitectureexample.ui.common.RepoListAdapter;
import com.example.mvvmarchitectureexample.ui.common.RetryCall;
import com.example.mvvmarchitectureexample.util.AutoClearedValue;

import java.util.List;

import javax.inject.Inject;

public class UserFragment extends Fragment implements Injectable {
    private static final String LOGIN_KEY = "login";
    private UserViewModel userViewModel;
    private AutoClearedValue<RepoListAdapter> adapter;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    AutoClearedValue<FragmentUserBinding> binding;

    public UserFragment() {
        // Required empty public constructor
    }

    public static UserFragment create(String login){
        UserFragment userFragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LOGIN_KEY, login);
        userFragment.setArguments(bundle);

        return userFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //FragmentUserBinding se genera automaticamente a partir del uso de databinding
        //del xml fragment_user
        FragmentUserBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user,
                container, false, dataBindingComponent);

        dataBinding.setRetryCall(new RetryCall() {
            @Override
            public void retry() {
                userViewModel.retry();
            }
        });

        binding = new AutoClearedValue<>(this, dataBinding);

        return dataBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        userViewModel.setLogin(getArguments().getString(LOGIN_KEY));
        userViewModel.getUser().observe(this, new Observer<Resource<User>>() {
            @Override
            public void onChanged(Resource<User> userResource) {
                binding.get().setUser((userResource == null) ? null : userResource.data);
                binding.get().setUserResource(userResource);
                binding.get().executePendingBindings();
            }
        });

        RepoListAdapter rvAdapter = new RepoListAdapter(dataBindingComponent,
                new RepoListAdapter.RepoClickCallback() {
            @Override
            public void onClick(Repo repo) {
                navigationController.navigateToRepo(repo.getOwner().getLogin(), repo.getName());
            }
        }, false);

        binding.get().rvRepoList.setAdapter(rvAdapter);
        this.adapter = new AutoClearedValue<>(this, rvAdapter);
        initRepoList();
    }

    private void initRepoList(){
        userViewModel.getRepos().observe(this, new Observer<Resource<List<Repo>>>() {
            @Override
            public void onChanged(Resource<List<Repo>> repos) {
                if(repos == null) {
                    adapter.get().replace(null);
                } else {
                    adapter.get().replace(repos.data);
                }
            }
        });
    }
}