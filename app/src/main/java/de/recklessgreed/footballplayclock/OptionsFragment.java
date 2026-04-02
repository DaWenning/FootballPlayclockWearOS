package de.recklessgreed.footballplayclock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class OptionsFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the existing options layout
        View root = inflater.inflate(R.layout.fragment_options, container, false);

        PlayClockActivity parent = null;
        if (getActivity() instanceof PlayClockActivity) {
            parent = (PlayClockActivity) getActivity();
        }

        final PlayClockActivity finalParent = parent;
        // Wire the Back TextView to dismiss the dialog
        View exit = root.findViewById(R.id.exitSelector);
        if (exit != null) exit.setOnClickListener(v -> {
            // exit the entire activity, which will also close the dialog
            if (finalParent != null) finalParent.finish();
        });

        return root;

    }
}
