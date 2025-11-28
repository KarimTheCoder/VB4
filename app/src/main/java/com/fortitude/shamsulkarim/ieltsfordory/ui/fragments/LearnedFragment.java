package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.WordRecyclerViewAdapter;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.FragmentLearnedWordsBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.practice.Practice;
import com.fortitude.shamsulkarim.ieltsfordory.ui.train.PretrainActivity;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.fortitude.shamsulkarim.ieltsfordory.utility.tts.TtsController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class LearnedFragment extends Fragment
        implements WordRecyclerViewAdapter.WordAdapterCallback {

    private FragmentLearnedWordsBinding binding;
    private VocabularyRepository repository;
    private WordRecyclerViewAdapter adapter;
    private final ArrayList<Object> words = new ArrayList<>();
    private boolean isShowingFabOption = false;
    private AppPreferences prefs;
    private int selection;
    private String level;
    private TtsController ttsController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLearnedWordsBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(requireContext().getColor(R.color.colorPrimary));

        repository = new VocabularyRepository(requireContext());
        initialization();

        binding.recyclerViewLearnedWords.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    System.out.println("Scrolled Downwards");
                    fabAnimation(false);

                    if (isShowingFabOption) {
                        isShowingFabOption = false;
                    }

                } else if (dy < 0) {
                    System.out.println("Scrolled Upwards");
                    fabAnimation(true);

                } else {
                    System.out.println("No Vertical Scrolled");
                }
            }
        });

        // Spinner
        selection = prefs.getPrevLearnedSelection();

        if (selection == 0) {
            level = "beginner";
            getBeginnerWordData();
        }
        if (selection == 1) {
            level = "intermediate";
            getIntermediateWordData();
        }

        if (selection == 2) {
            level = "advance";
            getAdvanceWordData();
        }
        binding.wordSpinner.setSelection(selection);

        // SEARCH
        settingUpSearch();

        return v;
    }

    // INITIALIZATION

    private void initialization() {
        setUpFab();

        prefs = AppPreferences.get(requireContext());

        ttsController = new TtsController(requireContext());
        binding.havenotlearned.setVisibility(View.INVISIBLE);
        binding.noLearnedImage.setVisibility(View.INVISIBLE);
        setSpinner();

        binding.nlStartLearning.setOnClickListener(view -> {
            if (level.equalsIgnoreCase("beginner")) {
                prefs.setLevel("beginner");
                requireContext().startActivity(new Intent(view.getContext(), PretrainActivity.class));
            }

            if (level.equalsIgnoreCase("intermediate")) {
                prefs.setLevel("intermediate");
                requireContext().startActivity(new Intent(view.getContext(), PretrainActivity.class));
            }

            if (level.equalsIgnoreCase("advance")) {
                prefs.setLevel("advance");
                requireContext().startActivity(new Intent(view.getContext(), PretrainActivity.class));
            }
        });
        binding.nlStartLearning.setBackgroundResource(R.drawable.gradient);

        binding.learnedToolbar.setTitle("LEARNED");
        binding.learnedToolbar.setTitleTextColor(requireContext().getColor(R.color.colorPrimary));
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(binding.learnedToolbar);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewLearnedWords.setLayoutManager(layoutManager);
        binding.recyclerViewLearnedWords.setHasFixedSize(true);
    }

    private void settingUpSearch() {
        binding.mSearch.setDimBackground(false);
        binding.mSearch.setShowSearchKey(true);
        binding.mSearch.setOnClearSearchActionListener(() -> binding.wordSpinner.setVisibility(View.VISIBLE));
        binding.mSearch.setOnFocusChangeListener((v, hasFocus) -> binding.wordSpinner.setVisibility(View.VISIBLE));
        binding.mSearch.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                binding.wordSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuClosed() {

            }
        });
        binding.mSearch.setOnClearSearchActionListener(() -> binding.wordSpinner.setVisibility(View.VISIBLE));

        binding.mSearch.setOnQueryChangeListener((oldQuery, newQuery) -> {
            adapter.getFilter().filter(newQuery);
            binding.wordSpinner.setVisibility(View.INVISIBLE);
        });
    }

    /// setup fab

    private void setUpFab() {
        binding.fab.setColorNormal(requireContext().getColor(R.color.colorPrimary));
        binding.fab.setColorPressed(requireContext().getColor(R.color.colorPrimaryDark));
        int sheetColor = requireContext().getColor(R.color.colorPrimary);
        int fabColor = requireContext().getColor(R.color.beginnerS);
        MaterialSheetFab materialSheetFab = new MaterialSheetFab<>(binding.fab, binding.fabSheet, binding.overlay,
                sheetColor, fabColor);

        binding.advanceFab.setOnClickListener(view -> {
            prefs.setPracticeMode("learned");
            prefs.setLevel("advance");

            if (words.size() >= 5) {
                requireContext().startActivity(new Intent(getContext(), Practice.class));
            } else {
                Toast.makeText(getContext(), "There must be atleast five words", Toast.LENGTH_SHORT).show();
            }
        });

        binding.intermediateFab.setOnClickListener(view -> {
            prefs.setPracticeMode("learned");
            prefs.setLevel("intermediate");

            if (words.size() >= 5) {
                requireContext().startActivity(new Intent(getContext(), Practice.class));
            } else {
                Toast.makeText(getContext(), "There must be atleast five words", Toast.LENGTH_SHORT).show();
            }
        });

        binding.beginnerFab.setOnClickListener(view -> {
            prefs.setPracticeMode("learned");
            prefs.setLevel("beginner");

            getBeginnerWordData();

            if (words.size() >= 5) {
                requireContext().startActivity(new Intent(getContext(), Practice.class));
            } else {
                Toast.makeText(getContext(), "There must be atleast five words", Toast.LENGTH_SHORT).show();
            }
            isShowingFabOption = false;
        });
    }

    /// // Spinner--------------------------

    protected void fabAnimation(boolean isVisible) {
        if (isVisible) {
            binding.fab.animate().translationY(0f);
        } else {
            binding.fab.animate().translationY(60f + 500);
        }
    }

    // ---------------------------------------

    private void getBeginnerWordData() {
        words.clear();
        words.addAll(repository.getBeginnerLearnedWords());

        if (words.size() <= 0) {
            binding.havenotlearned.setVisibility(View.VISIBLE);
            binding.noLearnedImage.setVisibility(View.VISIBLE);
            binding.nlStartLearning.setVisibility(View.VISIBLE);
            binding.fab.setVisibility(View.INVISIBLE);
        } else {
            binding.havenotlearned.setVisibility(View.INVISIBLE);
            binding.noLearnedImage.setVisibility(View.INVISIBLE);
            binding.nlStartLearning.setVisibility(View.INVISIBLE);
            binding.fab.setVisibility(View.VISIBLE);
        }

        adapter = new WordRecyclerViewAdapter(getContext(), words, this);
        binding.recyclerViewLearnedWords.setAdapter(adapter);
    }

    private void getIntermediateWordData() {
        words.clear();
        words.addAll(repository.getIntermediateLearnedWords());

        if (words.size() <= 0) {
            binding.havenotlearned.setVisibility(View.VISIBLE);
            binding.noLearnedImage.setVisibility(View.VISIBLE);
            binding.nlStartLearning.setVisibility(View.VISIBLE);
            binding.fab.setVisibility(View.INVISIBLE);
        } else {
            binding.havenotlearned.setVisibility(View.INVISIBLE);
            binding.noLearnedImage.setVisibility(View.INVISIBLE);
            binding.nlStartLearning.setVisibility(View.INVISIBLE);
            binding.fab.setVisibility(View.VISIBLE);
        }

        adapter = new WordRecyclerViewAdapter(getContext(), words, this);
        binding.recyclerViewLearnedWords.setAdapter(adapter);
    }

    private void getAdvanceWordData() {
        words.clear();
        words.addAll(repository.getAdvanceLearnedWords());

        if (words.size() <= 0) {
            binding.havenotlearned.setVisibility(View.VISIBLE);
            binding.noLearnedImage.setVisibility(View.VISIBLE);
            binding.nlStartLearning.setVisibility(View.VISIBLE);
            binding.fab.setVisibility(View.INVISIBLE);
        } else {
            binding.havenotlearned.setVisibility(View.INVISIBLE);
            binding.noLearnedImage.setVisibility(View.INVISIBLE);
            binding.nlStartLearning.setVisibility(View.INVISIBLE);
            binding.fab.setVisibility(View.VISIBLE);
        }

        adapter = new WordRecyclerViewAdapter(getContext(), words, this);
        binding.recyclerViewLearnedWords.setAdapter(adapter);
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.spinner_options, R.layout.settings_spinner);
        adapter.setDropDownViewResource(R.layout.settings_spinner_dropdown);
        binding.wordSpinner.setAdapter(adapter);

        binding.wordSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    prefs.setPrevLearnedSelection(0);
                    getBeginnerWordData();
                }
                if (i == 1) {
                    prefs.setPrevLearnedSelection(1);
                    getIntermediateWordData();
                }

                if (i == 2) {
                    prefs.setPrevLearnedSelection(2);
                    getAdvanceWordData();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.onDestroy();
        }
        if (ttsController != null) {
            ttsController.shutdown();
            ttsController = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ttsController != null) {
            ttsController.stop();
        }
    }

    @Override
    public void onMethodCallback(String word) {
        if (ttsController != null && ttsController.isReady()) {
            ttsController.speak(word, true);
        }
    }
}
