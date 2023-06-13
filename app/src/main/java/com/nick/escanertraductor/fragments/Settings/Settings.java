package com.nick.escanertraductor.fragments.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.nick.escanertraductor.R;

public class Settings extends Fragment {

    Switch switcher;
    boolean modoNoche;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        mContext = context;
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switcher = view.findViewById(R.id.switcher);

        sharedPreferences = getActivity().getSharedPreferences("MODO", Context.MODE_PRIVATE);
        modoNoche = sharedPreferences.getBoolean("noche", false);

        if(modoNoche) {
            switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modoNoche){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("noche", false);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("noche", true);
                }
                editor.apply();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}