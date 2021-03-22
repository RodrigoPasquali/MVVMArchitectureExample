package com.example.mvvmarchitectureexample.ui.repo;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mvvmarchitectureexample.R;
import com.example.mvvmarchitectureexample.binding.FragmentDataBindingComponent;
import com.example.mvvmarchitectureexample.databinding.FragmentRepoBinding;
import com.example.mvvmarchitectureexample.di.Injectable;
import com.example.mvvmarchitectureexample.model.Contributor;
import com.example.mvvmarchitectureexample.model.Repo;
import com.example.mvvmarchitectureexample.repository.Resource;
import com.example.mvvmarchitectureexample.ui.common.NavigationController;
import com.example.mvvmarchitectureexample.ui.common.RetryCall;
import com.example.mvvmarchitectureexample.util.AutoClearedValue;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class RepoFragment extends Fragment implements Injectable {
    private static final String REPO_OWNER_KEY = "repo_owner";
    private static final String REPO_NAME_KEY = "repo_name";
    private RepoViewModel repoViewModel;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    AutoClearedValue<FragmentRepoBinding> binding;
    AutoClearedValue<ContributorAdapter> adapter;

    public RepoFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        repoViewModel = ViewModelProviders.of(this, viewModelFactory).get(RepoViewModel.class);
        Bundle args = getArguments();

        if((args != null) && (args.containsKey(REPO_OWNER_KEY)) && (args.containsKey(REPO_NAME_KEY))){
            repoViewModel.setId(args.getString(REPO_OWNER_KEY), args.getString(REPO_NAME_KEY));
        } else {
            repoViewModel.setId(null, null);
        }

        LiveData<Resource<Repo>> repo = repoViewModel.getRepo();
        repo.observe(this, new Observer<Resource<Repo>>() {
            @Override
            public void onChanged(Resource<Repo> resource) {
                binding.get().setRepo((resource == null) ? null : resource.data);
                binding.get().setRepoResource(resource);
                binding.get().executePendingBindings();
            }
        });

        ContributorAdapter adapter = new ContributorAdapter(dataBindingComponent, new ContributorAdapter.ContributorClickCallback() {
            @Override
            public void onClick(Contributor contributor) {
//                navigationController.navigateToUser(contributor.getLogin());
            }
        });

        this.adapter = new AutoClearedValue<>(this, adapter);
        binding.get().contributorList.setAdapter(adapter);
        initContributorList(repoViewModel);
    }

    private void initContributorList(RepoViewModel viewModel) {
        viewModel.getContributors().observe(this, new Observer<Resource<List<Contributor>>>() {
            @Override
            public void onChanged(Resource<List<Contributor>> listResource) {
                if((listResource != null) && (listResource.data != null)) {
                    adapter.get().replace(listResource.data);
                } else {
                    adapter.get().replace(Collections.emptyList());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //FragmentSearchBinding se genera automaticamente a partir del uso de databinding
        //del xml fragment_repo
        FragmentRepoBinding dataBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_repo, container, false);

        dataBinding.setRetryCallback(new RetryCall() {
            @Override
            public void retry() {
                repoViewModel.retry();
            }
        });

        binding = new AutoClearedValue<>(this, dataBinding);

        return dataBinding.getRoot();
    }

    public static RepoFragment create(String owner, String name){
        RepoFragment repoFragment = new RepoFragment();
        Bundle args = new Bundle();
        args.putString(REPO_OWNER_KEY, owner);
        args.putString(REPO_NAME_KEY, name);
        repoFragment.setArguments(args);

        return repoFragment;
    }
}