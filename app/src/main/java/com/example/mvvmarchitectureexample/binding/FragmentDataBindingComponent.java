package com.example.mvvmarchitectureexample.binding;

import androidx.databinding.DataBindingComponent;
import androidx.fragment.app.Fragment;

public class FragmentDataBindingComponent implements DataBindingComponent {
    private final FragmentBindingAdapters fragmentBindingAdapters;

    public FragmentDataBindingComponent(Fragment fragment) {
        this.fragmentBindingAdapters = new FragmentBindingAdapters(fragment);
    }

    @Override
    public FragmentBindingAdapters getFragmentBindingAdapters() {
        return fragmentBindingAdapters;
    }
}
