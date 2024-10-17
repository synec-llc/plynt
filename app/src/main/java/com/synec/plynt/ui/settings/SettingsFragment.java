package com.synec.plynt.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.synec.plynt.R;

public class SettingsFragment extends Fragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {






        // Ensure the Bottom Navigation still shows Account as active
//        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
//        if (bottomNavigationView != null) {
//            bottomNavigationView.setSelectedItemId(R.id.navigation_account);
//        } else {
//            Log.e("AccountFragment", "BottomNavigationView is null");
//        }
//
//
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}
