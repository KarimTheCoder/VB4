package com.fortitude.shamsulkarim.ieltsfordory.ui.favorites;

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

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord;
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.usecase.GetFavoriteWordsUseCase;
import org.koin.java.KoinJavaComponent;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.FragmentFavoriteWordsBinding;
import com.fortitude.shamsulkarim.ieltsfordory.ui.practice.Practice;
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.IsTtsReadyUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.SpeakTextUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.StopTtsUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.tts.usecase.ShutdownTtsUseCase;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment
        implements FavoriteRecyclerViewAdapter.AdapterCallback {

    private FragmentFavoriteWordsBinding binding;
    private GetFavoriteWordsUseCase getFavoriteWordsUseCase;
    private FavoriteRecyclerViewAdapter adapter;
    static public final List<VocabularyWord> words = new ArrayList<>();
    private float fabY;
    private AppPreferences prefs;
    private boolean isFabOptionOn = false;
    private SpeakTextUseCase speakTextUseCase;
    private IsTtsReadyUseCase isTtsReadyUseCase;
    private StopTtsUseCase stopTtsUseCase;
    private ShutdownTtsUseCase shutdownTtsUseCase;
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

        getFavoriteWordsUseCase = KoinJavaComponent.get(GetFavoriteWordsUseCase.class);
        speakTextUseCase = KoinJavaComponent.get(SpeakTextUseCase.class);
        isTtsReadyUseCase = KoinJavaComponent.get(IsTtsReadyUseCase.class);
        stopTtsUseCase = KoinJavaComponent.get(StopTtsUseCase.class);
        shutdownTtsUseCase = KoinJavaComponent.get(ShutdownTtsUseCase.class);

        binding.fabFavorite.setColorNormal(requireContext().getColor(R.color.colorPrimary));
        binding.fabFavorite.setColorPressed(requireContext().getColor(R.color.colorPrimaryDark));
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
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fabAnimation(false);
                    if (isFabOptionOn) {
                        binding.fabFavorite.animate().rotation(-20f);
                        isFabOptionOn = false;
                    }
                } else if (dy < 0) {
                    fabAnimation(true);
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

        binding.mSearch.setDimBackground(false);
        binding.mSearch.setShowSearchKey(true);
        binding.mSearch.setOnQueryChangeListener((oldQuery, newQuery) -> adapter.getFilter().filter(newQuery));
        return v;
    }

    public void addFavoriteWord() {
        words.clear();
        words.addAll(getFavoriteWordsUseCase.execute());
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
        if (shutdownTtsUseCase != null) {
            shutdownTtsUseCase.execute();
        }
        if (prefs != null) {
            prefs.setFavoriteScrollPos(lastRecyclerViewPosition);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (stopTtsUseCase != null) {
            stopTtsUseCase.execute();
        }
    }

    @Override
    public void onMethodCallback(String wordName) {
        if (isTtsReadyUseCase != null && isTtsReadyUseCase.execute()) {
            speakTextUseCase.execute(wordName, true);
        }
        Toast.makeText(getContext(), "Hello there, this is a callback", Toast.LENGTH_LONG).show();
    }
}
