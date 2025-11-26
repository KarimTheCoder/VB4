package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.HomeFragmentBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.train.PretrainActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shamsul Karim on 13-Dec-16.
 */

public class HomeFragment extends Fragment {

    private HomeFragmentBinding binding;
    private VocabularyRepository repository;
    private AppPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        prefs = AppPreferences.get(requireContext());
        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(requireContext().getColor(R.color.primary_background_color));

        repository = new VocabularyRepository(requireContext());

        if (!prefs.isHomeVisited()) {
            prefs.setHomeVisited(true);
        }

        binding.advanceCardHome.setPreventCornerOverlap(false);
        binding.intermediateCardHome.setPreventCornerOverlap(false);
        binding.beginnerCardHome.setPreventCornerOverlap(false);

        binding.advanceCardHome.setOnClickListener(view -> {
            prefs.setLevel("advance");
            requireContext().startActivity(new Intent(requireContext(), PretrainActivity.class));
        });

        binding.intermediateCardHome.setOnClickListener(view -> {
            prefs.setLevel("intermediate");
            requireContext().startActivity(new Intent(requireContext(), PretrainActivity.class));
        });

        binding.beginnerCardHome.setOnClickListener(view -> {
            prefs.setLevel("beginner");
            requireContext().startActivity(new Intent(requireContext(), PretrainActivity.class));
        });

        setProgress();
        freeVersion();

        return v;
    }

    private void freeVersion() {
        if (!BuildConfig.FLAVOR.equalsIgnoreCase("pro")) {
            checkTrialStatus();
        }
    }

    private void setProgress() {
        setBeginnerProgress();
        setIntermediateProgress();
        setAdvanceProgress();
    }

    private void setAdvanceProgress() {
        int learned;
        int percentage;
        int totalAdvCount = repository.getTotalAdvanceCount();

        learned = repository.getAdvanceLearnedCount();
        percentage = (learned * 100) / totalAdvCount;

        binding.advancePie.setMaxPercentage(100);
        binding.advancePie.setPercentage(percentage);
    }

    private void setIntermediateProgress() {
        int percentage;
        int learned;
        int totalInterCount = repository.getTotalIntermediateCount();

        learned = repository.getIntermediateLearnedCount();
        percentage = (learned * 100) / totalInterCount;

        binding.intermediatePie.setMaxPercentage(100);
        binding.intermediatePie.setPercentage(percentage);
    }

    private void setBeginnerProgress() {
        int totalBeginnerCount = repository.getTotalBeginnerCount();
        int learned = repository.getBeginnerLearnedCount();
        int percentage = (learned * 100) / totalBeginnerCount;

        binding.profilePieView.setMaxPercentage(100);
        binding.profilePieView.setPercentage(percentage);
    }

    // Check Trial State
    private void checkTrialStatus() {
        String trialStatus;

        if (prefs.getTrialEndDate() != 0) {
            Date today = Calendar.getInstance().getTime();

            long endMillies = prefs.getTrialEndDate();
            long todayMillies = today.getTime();
            long leftMillies = endMillies - todayMillies;

            if (!prefs.isPremium()) {
                if (leftMillies >= 0) {
                    trialStatus = "active";
                } else {
                    showTrialFinished();
                    trialStatus = "ended";
                }
                binding.trialStatus.setText(getString(R.string.trial_mode, trialStatus));
            } else {
                trialStatus = "PREMIUM+";
                binding.trialStatus.setText(trialStatus);
                binding.trialStatus.setTextColor(requireContext().getColor(R.color.green));
            }
        }
    }

    private void showTrialFinished() {
        if (!prefs.isHomeFragmentTrialEndShown()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle(R.string.trial_ended);
            builder.setMessage(R.string.trial_ended_description);
            builder.setIcon(R.drawable.ic_information);

            // 1. The "Upgrade" Button (Primary Action -> Positive)
            builder.setPositiveButton("Upgrade", (dialog, which) -> {
                startActivity(new Intent(requireContext(), PretrainActivity.class));
            });

            // 2. The "Continue" Button (Secondary Action -> Negative)
            builder.setNegativeButton("Continue with basic", (dialog, which) -> {
                Toast.makeText(requireContext(), "Continue with Basic", Toast.LENGTH_SHORT).show();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                prefs.setDarkMode(0);
            });

            // 3. Show the dialog
            AlertDialog dialog = builder.show();

            // 4. Apply Custom Colors (Optional - to match your old look)
            // Set "Upgrade" to Green
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(requireContext().getColor(R.color.green));
            // Set "Continue" to Secondary Color
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(requireContext().getColor(R.color.secondary_text_color));

            prefs.setHomeFragmentTrialEndShown(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
