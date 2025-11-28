package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.WordRecyclerViewAdapter;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.FragmentNewWordBinding;
import com.fortitude.shamsulkarim.ieltsfordory.utility.tts.TtsController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;

public class AllWordsFragment extends Fragment
        implements WordRecyclerViewAdapter.WordAdapterCallback {

    private FragmentNewWordBinding binding;
    private VocabularyRepository repository;
    private WordRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private final ArrayList<Object> words = new ArrayList<>();
    private AppPreferences prefs;
    private TtsController ttsController;

    public AllWordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentNewWordBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(requireContext().getColor(R.color.colorPrimary));

        repository = new VocabularyRepository(requireContext());
        prefs = AppPreferences.get(requireContext());
        ttsController = new TtsController(requireContext());

        binding.wordToolbar.setTitle("WORDS");
        binding.wordToolbar.setTitleTextColor(requireContext().getColor(R.color.beginnerS));
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(binding.wordToolbar);
        }

        setupSpinner();
        setupRecycler();

        int selection = 0;
        if (!prefs.contains(AppPreferences.KEY_PREV_SELECTION)) {
            prefs.setPrevWordSelection(0);
            prefs.setWordFirstVisiblePos(0);
            prefs.setWordFirstOffsetTop(0);
        } else {
            selection = prefs.getPrevWordSelection();
        }

        showWordsForSelection(selection);
        attachScrollPersistence();
        restoreScrollPosition();
        binding.wordSpinner.setSelection(selection);

        // SEARCH
        setupSearch();

        return v;
    }

    private void setupSearch() {
        binding.mSearch.setDimBackground(false);
        binding.mSearch.setShowSearchKey(true);

        binding.mSearch.setOnClearSearchActionListener(() -> binding.wordSpinner.setVisibility(View.VISIBLE));

        binding.mSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && binding.mSearch.getQuery().isEmpty()) {
                binding.wordSpinner.setVisibility(View.VISIBLE);
            }
        });

        binding.mSearch.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                binding.wordSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuClosed() {
            }
        });

        binding.mSearch.setOnQueryChangeListener((oldQuery, newQuery) -> {
            adapter.getFilter().filter(newQuery);
            binding.wordSpinner.setVisibility(View.INVISIBLE);
        });
    }

    private void setupRecycler() {
        layoutManager = new LinearLayoutManager(getContext());
        binding.newRecyclerView.setLayoutManager(layoutManager);
        binding.newRecyclerView.setHasFixedSize(true);
        adapter = new WordRecyclerViewAdapter(getContext(), words, this);
        binding.newRecyclerView.setAdapter(adapter);
    }

    private void showWordsForSelection(int selection) {
        words.clear();
        if (selection == 0) {
            words.addAll(repository.getBeginnerVocabulary());
        } else if (selection == 1) {
            words.addAll(repository.getIntermediateVocabulary());
        } else if (selection == 2) {
            words.addAll(repository.getAdvanceVocabulary());
        }
        adapter.notifyDataSetChanged();
    }

    private void attachScrollPersistence() {
        binding.newRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                if (lm != null) {
                    int pos = lm.findFirstVisibleItemPosition();
                    View first = lm.findViewByPosition(pos);
                    int offset = first == null ? 0 : first.getTop();
                    prefs.setWordFirstVisiblePos(pos);
                    prefs.setWordFirstOffsetTop(offset);
                }
            }
        });
    }

    private void restoreScrollPosition() {
        int pos = prefs.getWordFirstVisiblePos();
        int offset = prefs.getWordFirstOffsetTop();
        if (layoutManager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(pos, offset);
        }
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.spinner_options, R.layout.settings_spinner);
        spinnerAdapter.setDropDownViewResource(R.layout.settings_spinner_dropdown);
        binding.wordSpinner.setAdapter(spinnerAdapter);

        binding.wordSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i >= 0 && i <= 2) {
                    prefs.setPrevWordSelection(i);
                    showWordsForSelection(i);
                    if (layoutManager instanceof LinearLayoutManager) {
                        ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(0, 0);
                    }
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
