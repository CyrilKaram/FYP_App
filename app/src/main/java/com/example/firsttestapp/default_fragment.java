package com.example.firsttestapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.net.URL;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link default_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class default_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ItemViewModel viewModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button defbut; ///////////////////
    private Integer x;

    public default_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment default_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static default_fragment newInstance(String param1, String param2) {
        default_fragment fragment = new default_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_default_fragment, container, false);
        defbut = (Button) view.findViewById(R.id.launch_default);
//        x=0;
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ItemViewModel.class);

        defbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView randomView = view.getRootView().findViewById(R.id.default_description);
                Date currentTime = Calendar.getInstance().getTime();
                x = currentTime.getHours(); //Integer between 1 and 24
                randomView.setText(x.toString());
                viewModel.selectItem(1);
                NavHostFragment.findNavController(default_fragment.this)
                        .navigate(R.id.action_default_fragment_to_FirstFragment);

//                randomView.setText("Ping Started");/////////////PING//////////
//                try {
//                    Ping ping = new Ping(new URL("https://www.google.com:443/"),getActivity().getApplicationContext());
//                    randomView.setText(ping.getip());
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                    randomView.setText("Error");
//                }///////////////////PING///////////////////


//                x++;
            }
        });
    }
}