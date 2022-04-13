package com.example.firsttestapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.firsttestapp.databinding.FragmentFirstBinding;
import com.google.android.material.snackbar.Snackbar;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    TextView showCountTextView;
    private Button defbut;
    private Button backbut;
    private Button vidbut;
    private Button downbut;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        View fragmentFirstLayout = inflater.inflate(R.layout.fragment_first, container, false);
        defbut = (Button) fragmentFirstLayout.findViewById(R.id.Approach_1);
        backbut = (Button) fragmentFirstLayout.findViewById(R.id.Approach_2);
        vidbut = (Button) fragmentFirstLayout.findViewById(R.id.Approach_3);
        downbut = (Button) fragmentFirstLayout.findViewById(R.id.Approach_4);
        showCountTextView = fragmentFirstLayout.findViewById(R.id.textview_first);
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return fragmentFirstLayout;

    }

    public void setText(String yourText){
        showCountTextView.setText(yourText);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        defbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_default_fragment);
                ((MainActivity) getActivity()).getFloatingActionButton().hide();
            }
        });

        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_background_fragment);
                ((MainActivity) getActivity()).getFloatingActionButton().hide();
            }
        });

        vidbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_video_fragment);
                ((MainActivity) getActivity()).getFloatingActionButton().hide();
            }
        });

        downbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_download_fragment);
                ((MainActivity) getActivity()).getFloatingActionButton().hide();
            }
        });
    }

    private void countMe(View view) {
        String countString = showCountTextView.getText().toString();
        Integer count = Integer.parseInt(countString);
        count++;
        showCountTextView.setText(count.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}