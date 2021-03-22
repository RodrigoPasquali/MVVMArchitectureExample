package com.example.mvvmarchitectureexample.ui.search;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.mvvmarchitectureexample.R;
import com.example.mvvmarchitectureexample.binding.FragmentDataBindingComponent;
import com.example.mvvmarchitectureexample.databinding.FragmentSearchBinding;
import com.example.mvvmarchitectureexample.di.Injectable;
import com.example.mvvmarchitectureexample.model.Repo;
import com.example.mvvmarchitectureexample.repository.Resource;
import com.example.mvvmarchitectureexample.ui.common.NavigationController;
import com.example.mvvmarchitectureexample.ui.common.RepoListAdapter;
import com.example.mvvmarchitectureexample.ui.common.RetryCall;
import com.example.mvvmarchitectureexample.util.AutoClearedValue;

import java.util.List;

import javax.inject.Inject;

public class SearchFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelProvider;
    @Inject
    NavigationController navigationController;
    DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);
    AutoClearedValue<FragmentSearchBinding> binding;
    AutoClearedValue<RepoListAdapter> adapter;
    private SearchViewModel searchViewModel;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //FragmentSearchBinding se genera automaticamente a partir del uso de databinding
        // del xml fragment_search
        FragmentSearchBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search,
                container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);
        return dataBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchViewModel = ViewModelProviders.of(this, viewModelProvider).get(SearchViewModel.class);
        initRecyclerView();

        RepoListAdapter rvAdapter = new RepoListAdapter(dataBindingComponent,
                new RepoListAdapter.RepoClickCallback() {
            @Override
            public void onClick(Repo repo) {
//                navigationController.navigateToRepo(repo.getOwner().getLogin(), repo.getName());
            }
        }, true);

        //a traves de databinding, toma los datos del xml(quita el guion bajo y aplica uppercase
        binding.get().repoList.setAdapter(rvAdapter);
        adapter = new AutoClearedValue<>(this, rvAdapter);

        initSearchInputListener();

        binding.get().setCallback(new RetryCall() {
            @Override
            public void retry() {
                searchViewModel.refresh();
            }
        });
    }

    private void initSearchInputListener(){
        binding.get().searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean value = false;

                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    SearchFragment.this.doSearch(v);
                    value = true;
                }

                return value;
            }
        });

        binding.get().searchInput.setOnKeyListener(new View.OnKeyListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean value = false;

                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    doSearch(v);
                    value = true;
                }

                return value;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void doSearch(View view){
        String query = binding.get().searchInput.getText().toString();
        dismissKeyboard(view.getWindowToken());
        binding.get().setQuery(query);
        searchViewModel.setQuery(query);
    }

    private void initRecyclerView(){
        binding.get().repoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastPosition = layoutManager.findLastVisibleItemPosition();

                if(lastPosition == adapter.get().getItemCount()-1){
                    searchViewModel.loadNextPage();
                }
            }
        });

        searchViewModel.getResults().observe(this, new Observer<Resource<List<Repo>>>() {
            @Override
            public void onChanged(Resource<List<Repo>> result) {
                binding.get().setSearchResource(result);
                binding.get().setResultCount(((result == null) || (result.data == null)) ? 0 : result.data.size());
                adapter.get().replace(result == null ? null : result.data);
                binding.get().executePendingBindings();
            }
        });

        searchViewModel.getLoadMoreStatus().observe(this, new Observer<SearchViewModel.LoadMoreState>() {
            @Override
            public void onChanged(SearchViewModel.LoadMoreState loadingMore) {
                if(loadingMore == null){
                    binding.get().setLoadingMore(false);
                } else {
                    binding.get().setLoadingMore(loadingMore.isRunning());
                    String error = loadingMore.getErrorMessageIfNoHandled();

                    if(error != null){
                        Log.d("TAG1","Error on LoadMore");
                    }
                }
                binding.get().executePendingBindings();
            }
        });
    }

    private void dismissKeyboard(IBinder windowToken){
        FragmentActivity activity = getActivity();

        if(activity != null){
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }
}
