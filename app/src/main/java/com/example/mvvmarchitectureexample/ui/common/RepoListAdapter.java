package com.example.mvvmarchitectureexample.ui.common;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;

import com.example.mvvmarchitectureexample.R;
import com.example.mvvmarchitectureexample.databinding.RepoItemBinding;
import com.example.mvvmarchitectureexample.model.Repo;

import java.util.Objects;

public class RepoListAdapter extends DataBoundListAdapter<Repo, RepoItemBinding> {
    private final DataBindingComponent dataBindingComponent;
    private final RepoClickCallback repoClickCallback;
    private final boolean showFullName;

    public RepoListAdapter(DataBindingComponent dataBindingComponent,
                           RepoClickCallback repoClickCallback, boolean showFullName) {
        this.dataBindingComponent = dataBindingComponent;
        this.repoClickCallback = repoClickCallback;
        this.showFullName = showFullName;
    }

    @Override
    protected RepoItemBinding createBinding(ViewGroup parent) {
        RepoItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.repo_item, parent, false, dataBindingComponent);

        binding.setShowFullName(showFullName);
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Repo repo = binding.getRepo();

                if((repo != null) && (repoClickCallback != null)) {
                    repoClickCallback.onClick(repo);
                }
            }
        });

        return binding;
    }

    @Override
    protected void bind(RepoItemBinding binding, Repo item) {
        binding.setRepo(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected boolean areItemsTheSame(Repo oldItem, Repo newItem) {
        boolean areTheSame = (Objects.equals(oldItem.getOwner(), newItem.getOwner())) &&
                             (Objects.equals(oldItem.getName(), newItem.getName()));

        return areTheSame;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected boolean areContentsTheSame(Repo oldItem, Repo newItem) {
        boolean areTheSame = (Objects.equals(oldItem.getDescription(), newItem.getDescription())) &&
                             (oldItem.getStartCount() == newItem.getStartCount());

        return areTheSame;
    }

    public interface RepoClickCallback{
        void onClick(Repo repo);
    }
}
