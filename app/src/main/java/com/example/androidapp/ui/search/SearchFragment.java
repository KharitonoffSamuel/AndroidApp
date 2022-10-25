package com.example.androidapp.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidapp.R;
import com.example.androidapp.databinding.FragmentDashboardBinding;

public class SearchFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel dashboardViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;

        //SearchView searchView = new SearchView(R.id.findV)
        //CharSequence search = (CharSequence);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}