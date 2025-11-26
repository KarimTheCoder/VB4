package com.fortitude.shamsulkarim.ieltsfordory.ui.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ActivityNewSettingBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.MainActivity;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.fortitude.shamsulkarim.ieltsfordory.utility.signin.SignInAndSync;
import com.fortitude.shamsulkarim.ieltsfordory.utility.signin.SignInAndSyncCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class SettingActivity extends AppCompatActivity implements SignInAndSyncCallback {

    private ActivityNewSettingBinding binding;
    private SettingViewModel viewModel;
    private SignInAndSync signInAndSync;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private AppPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.toolbar_background_color));

        prefs = AppPreferences.get(this);
        signInAndSync = new SignInAndSync(this, this);
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);

        setupUI();
        setupObservers();
        setupSignIn();
    }

    private void setupUI() {
        binding.nsToolbar.setTitle("Settings");

        if (BuildConfig.FLAVOR.equalsIgnoreCase("huawei")) {
            binding.userStatus.setVisibility(View.GONE);
        }

        setupSpinners();

        binding.nsSave.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });

        binding.privacyPolicyCard.setOnClickListener(v -> {
            Uri appUrl = Uri.parse("https://banglish1.wixsite.com/vbprivacypolicy");
            startActivity(new Intent(Intent.ACTION_VIEW, appUrl));
        });

        binding.restoreCard
                .setOnClickListener(v -> Toast.makeText(this, "Restore is unavailable", Toast.LENGTH_SHORT).show());

        binding.ieltsCheckbox.setOnClickListener(v -> viewModel.setIeltsActive(binding.ieltsCheckbox.isChecked()));
        binding.toeflCheckbox.setOnClickListener(v -> viewModel.setToeflActive(binding.toeflCheckbox.isChecked()));
        binding.satCheckbox.setOnClickListener(v -> viewModel.setSatActive(binding.satCheckbox.isChecked()));
        binding.greCheckbox.setOnClickListener(v -> viewModel.setGreActive(binding.greCheckbox.isChecked()));

        binding.soundSwitch.setOnClickListener(v -> viewModel.setSound(binding.soundSwitch.isChecked()));
        binding.pronunSwitch.setOnClickListener(v -> viewModel.setPronun(binding.pronunSwitch.isChecked()));

        binding.languageSpanish.setOnClickListener(v -> viewModel.toggleLanguage());
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> wpsAdapter = ArrayAdapter.createFromResource(this,
                R.array.words_per_session_array, R.layout.settings_spinner);
        wpsAdapter.setDropDownViewResource(R.layout.settings_spinner_dropdown);
        binding.wpsSpinner.setAdapter(wpsAdapter);
        binding.wpsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int i, long id) {
                viewModel.setWordsPerSessionFromPos(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> p) {
            }
        });

        ArrayAdapter<CharSequence> rpsAdapter = ArrayAdapter.createFromResource(this,
                R.array.words_per_session_array, R.layout.settings_spinner);
        rpsAdapter.setDropDownViewResource(R.layout.settings_spinner_dropdown);
        binding.profileWpsSpinner.setAdapter(rpsAdapter);
        binding.profileWpsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int i, long id) {
                viewModel.setRepeatationPerSessionFromPos(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> p) {
            }
        });

        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(this,
                R.array.theme_options, R.layout.settings_spinner);
        themeAdapter.setDropDownViewResource(R.layout.settings_spinner_dropdown);
        binding.themeSpinner.setAdapter(themeAdapter);
        binding.themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int i, long id) {
                handleThemeSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> p) {
            }
        });

        binding.imageQualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int i, long id) {
                viewModel.setImageQuality(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> p) {
            }
        });
    }

    private void handleThemeSelection(int i) {
        if (!prefs.isPremium() && !prefs.isTrialActive() && BuildConfig.FLAVOR.equalsIgnoreCase("free")) {
            Toast.makeText(this, "Please upgrade to enjoy dark mode feature", Toast.LENGTH_SHORT).show();
        } else {
            if (i == 0)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            else if (i == 1)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            viewModel.setDarkMode(i);
        }
    }

    private void setupObservers() {
        viewModel.getState().observe(this, state -> {
            if (state instanceof SettingsUiState.SettingsLoaded) {
                applySnapshot(((SettingsUiState.SettingsLoaded) state).snapshot);
            } else if (state instanceof SettingsUiState.Error) {
                Toast.makeText(this, ((SettingsUiState.Error) state).message, Toast.LENGTH_SHORT).show();
            } else if (state instanceof SettingsUiState.SignedIn) {
                SettingsUiState.SignedIn s = (SettingsUiState.SignedIn) state;
                updateSignInUI(s.name, s.email, true);
            } else if (state instanceof SettingsUiState.SignedOut) {
                updateSignInUI(getString(R.string.doggo), getString(R.string.sign_in_description), false);
            }
        });
    }

    private void applySnapshot(SettingsSnapshot s) {
        if (binding.pronunSwitch.isChecked() != s.pronun)
            binding.pronunSwitch.setChecked(s.pronun);
        if (binding.soundSwitch.isChecked() != s.sound)
            binding.soundSwitch.setChecked(s.sound);

        if (binding.themeSpinner.getSelectedItemPosition() != s.darkMode)
            binding.themeSpinner.setSelection(s.darkMode);
        if (binding.imageQualitySpinner.getSelectedItemPosition() != s.imageQuality)
            binding.imageQualitySpinner.setSelection(s.imageQuality);

        int[] wpsVals = new int[] { 25, 20, 15, 10, 5, 4, 3 };
        int wpsIdx = indexForValue(wpsVals, s.wordsPerSession);
        if (binding.wpsSpinner.getSelectedItemPosition() != wpsIdx)
            binding.wpsSpinner.setSelection(wpsIdx);

        int[] rpsVals = new int[] { 25, 20, 15, 10, 5, 4, 3 };
        int rpsIdx = indexForValue(rpsVals, s.repeatationPerSession);
        if (binding.profileWpsSpinner.getSelectedItemPosition() != rpsIdx)
            binding.profileWpsSpinner.setSelection(rpsIdx);

        if (binding.ieltsCheckbox.isChecked() != s.ieltsActive)
            binding.ieltsCheckbox.setChecked(s.ieltsActive);
        if (binding.toeflCheckbox.isChecked() != s.toeflActive)
            binding.toeflCheckbox.setChecked(s.toeflActive);
        if (binding.satCheckbox.isChecked() != s.satActive)
            binding.satCheckbox.setChecked(s.satActive);
        if (binding.greCheckbox.isChecked() != s.greActive)
            binding.greCheckbox.setChecked(s.greActive);

        if (s.isSpanish) {
            binding.languageSpanish.setBackgroundColor(getColor(R.color.colorPrimary));
            binding.languageSpanish.setTextColor(getColor(R.color.text_white_87));
        } else {
            binding.languageSpanish.setBackgroundColor(getColor(R.color.card_background_color));
            binding.languageSpanish.setTextColor(getColor(R.color.primary_text_color));
        }
    }

    private int indexForValue(int[] values, int value) {
        for (int idx = 0; idx < values.length; idx++) {
            if (values[idx] == value)
                return idx;
        }
        return 0;
    }

    private void setupSignIn() {
        binding.nsSignIn.setBackgroundColor(getColor(R.color.card_background_color));
        binding.nsSignIn.setTextColor(getColor(R.color.primary_text_color));

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        signInAndSync.authenticateUser(result.getData());
                    }
                });

        binding.nsSignIn.setOnClickListener(v -> {
            if (signInAndSync.getmAuth().getCurrentUser() == null) {
                if (ConnectivityHelper.isConnectedToNetwork(this)) {
                    progressStatus(false);
                    binding.nsSignIn.setEnabled(false);
                    activityResultLauncher.launch(signInAndSync.getSignInIntent());
                } else {
                    Toast.makeText(this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
                }
            } else {
                signOut();
            }
        });
    }

    private void signOut() {
        progressStatus(false);
        signInAndSync.getmAuth().signOut();
        signInAndSync.getSignInClient().signOut().addOnCompleteListener(this, task -> {
            viewModel.onSignedOut();
            progressStatus(true);
            Toast.makeText(getApplicationContext(), "Sign-out complete!", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateSignInUI(String name, String email, boolean signedIn) {
        binding.nsUsername.setText(name);
        binding.userDetail.setText(email);
        binding.nsSignIn.setText(signedIn ? "Sign out" : getString(R.string.sign_in));
        binding.nsSignIn.setEnabled(true);
        progressStatus(true);
        prefs.setSignedIn(signedIn);
        if (signedIn)
            prefs.setUserName(name);
    }

    @Override
    public void updateUI() {
        FirebaseUser user = signInAndSync.getmAuth().getCurrentUser();
        if (user != null) {
            viewModel.onAuthenticated(user.getDisplayName(), user.getEmail());
        }
    }

    @Override
    public void progressStatus(boolean isCompleted) {
        binding.progressbar.setVisibility(isCompleted ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (signInAndSync.getmAuth().getCurrentUser() != null) {
            updateUI();
        }
    }
}
