package com.fortitude.shamsulkarim.ieltsfordory.ui.fragments;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.FavoriteRecyclerViewAdapter;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.VocabularyRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.FragmentFavoriteWordsBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.practice.Practice;
import com.fortitude.shamsulkarim.ieltsfordory.utility.tts.TtsController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment
        implements FavoriteRecyclerViewAdapter.AdapterCallback {

    private FragmentFavoriteWordsBinding binding;
    private VocabularyRepository repository;
    private FavoriteRecyclerViewAdapter adapter;
    static public final List<Word> words = new ArrayList<>();
    private float fabY;
    private AppPreferences prefs;
    private boolean isFabOptionOn = false;
    private TtsController ttsController;
    private int lastRecyclerViewPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoriteWordsBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        Window window = requireActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(requireContext().getColor(R.color.colorPrimary));

        repository = new VocabularyRepository(requireContext());
        ttsController = new TtsController(requireContext());

        binding.fabFavorite.setColorNormal(requireContext().getColor(R.color.colorPrimary));
        binding.fabFavorite.setColorPressed(requireContext().getColor(R.color.colorPrimaryDark));

        // Use post to get Y position after layout
        binding.fabFavorite.post(() -> fabY = binding.fabFavorite.getY());

        prefs = AppPreferences.get(requireContext());

        binding.favoriteToolbar.setTitle("FAVORITE");
        binding.favoriteToolbar.setTitleTextColor(requireContext().getColor(R.color.beginnerS));
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(binding.favoriteToolbar);
        }

        addFavoriteWord();

        int favoriteWordSize = words.size();

        if (favoriteWordSize >= 1) {
            binding.havenotlearned.setVisibility(View.INVISIBLE);
            binding.noFavoriteImage.setVisibility(View.INVISIBLE);
        } else {
            binding.havenotlearned.setVisibility(View.VISIBLE);
            binding.noFavoriteImage.setVisibility(View.VISIBLE);
        }

        binding.recyclerViewFavoriteWords.setHasFixedSize(true);
        adapter = new FavoriteRecyclerViewAdapter(getContext(), words, this);
        binding.recyclerViewFavoriteWords.setAdapter(adapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerViewFavoriteWords.setLayoutManager(layoutManager);

        lastRecyclerViewPosition = prefs.getFavoriteScrollPos();
        binding.recyclerViewFavoriteWords.scrollToPosition(lastRecyclerViewPosition);
        binding.recyclerViewFavoriteWords.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastRecyclerViewPosition = layoutManager.findFirstVisibleItemPosition();
            }

            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    System.out.println("Scrolled Downwards");
                    fabAnimation(false);

                    if (isFabOptionOn) {
                        binding.fabFavorite.animate().rotation(-20f);
                        isFabOptionOn = false;
                    }

                } else if (dy < 0) {
                    System.out.println("Scrolled Upwards");
                    fabAnimation(true);

                } else {
                    System.out.println("No Vertical Scrolled");
                }
            }
        });

        binding.fabFavorite.setOnClickListener(view -> {
            if (words.size() < 5) {
                Toast.makeText(view.getContext(), "At least five words needed", Toast.LENGTH_LONG).show();
            } else {
                prefs.setPracticeMode("favorite");
                Intent intent = new Intent(getContext(), Practice.class);
                requireContext().startActivity(intent);
            }
        });

        // SEARCH
        binding.mSearch.setDimBackground(false);
        binding.mSearch.setShowSearchKey(true);

        binding.mSearch.setOnQueryChangeListener((oldQuery, newQuery) -> adapter.getFilter().filter(newQuery));

        return v;
    }

    // ----------------------------------------------------------------------------------------------------

    public void addFavoriteWord() {
        words.clear();
        words.addAll(repository.getFavoriteWords());
    }

    protected void fabAnimation(boolean isVisible) {
        if (isVisible) {
            binding.fabFavorite.animate().cancel();
            binding.fabFavorite.animate().translationY(fabY);
        } else {
            binding.fabFavorite.animate().cancel();
            binding.fabFavorite.animate().translationY(fabY + 500);
        }
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
        if (prefs != null) {
            prefs.setFavoriteScrollPos(lastRecyclerViewPosition);
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
    public void onMethodCallback(String wordName) {
        if (ttsController != null && ttsController.isReady()) {
            ttsController.speak(wordName, true);
        }
        Toast.makeText(getContext(), "Hello there, this is a callback", Toast.LENGTH_LONG).show();
    }
}
