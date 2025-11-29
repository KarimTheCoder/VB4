package com.fortitude.shamsulkarim.ieltsfordory.ui.words;

import android.content.Context;
import android.media.MediaPlayer;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.AudioRepository;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.FirebaseMediaRepository;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.LearningProgressRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.OneLanguageBinding;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.fortitude.shamsulkarim.ieltsfordory.adapters.CustomFilter;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;

import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class WordRecyclerViewAdapter extends RecyclerView.Adapter<WordRecyclerViewAdapter.WordViewHolder>
        implements Filterable {
    public ArrayList<Object> words;
    private final ArrayList<Object> filterList;
    private CustomFilter filter;
    private final Context context;
    private AppPreferences prefs;
    private int favoriteCount;

    private FirebaseMediaRepository firebaseMediaRepository;
    private LearningProgressRepository repository;
    private WordAdapterCallback wordAdapterCallback;

    public WordRecyclerViewAdapter(Context context, ArrayList<Object> words, WordAdapterCallback wordAdapterCallback) {
        this.context = context;
        init(context, wordAdapterCallback);
        initDatabase(context);
        this.words = words;
        this.filterList = words;
    }

    private void initDatabase(Context context) {
        repository = new LearningProgressRepository(context);
    }

    private void init(Context context, WordAdapterCallback wordAdapterCallback) {
        try {
            this.wordAdapterCallback = wordAdapterCallback;
        } catch (ClassCastException e) {
            Log.e("WordAdapter init", e.getMessage());
        }
        firebaseMediaRepository = new FirebaseMediaRepository();
        prefs = AppPreferences.get(context);
        if (!prefs.contains(AppPreferences.KEY_FAVORITE_COUNT_PROFILE)) {
            prefs.setFavoriteCountProfile(0);
        } else {
            favoriteCount = prefs.getFavoriteCountProfile();
        }
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        OneLanguageBinding binding = OneLanguageBinding.inflate(LayoutInflater.from(parent.getContext()), parent,
                false);
        return new WordViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = (Word) words.get(position);
        if (((Word) words.get(position)).isFavorite.equalsIgnoreCase("true")) {
            holder.binding.favorite.setIconResource(R.drawable.ic_favorite_icon_active);
        } else {
            holder.binding.favorite.setIconResource(R.drawable.ic_favorite_icon);
        }
        holder.binding.favoriteCardTranslation.setText(word.getTranslation());
        holder.binding.favoriteCardWord.setText(word.getPronun());
        holder.binding.cardGrammar.setText(word.getGrammar());
        holder.binding.cardExample1.setText(word.getExample1());
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter(filterList, this);
        }
        return filter;
    }

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final OneLanguageBinding binding;
        final Boolean isVoicePronunciation;

        public WordViewHolder(OneLanguageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.recyclerViewCard.setPreventCornerOverlap(false);
            Sprite doubleBounce = new Wave();
            binding.spinKit.setIndeterminateDrawable(doubleBounce);
            binding.spinKit.setVisibility(View.INVISIBLE);
            isVoicePronunciation = prefs.getPronunState();
            binding.favorite.setIconResource(R.drawable.ic_favorite_icon);
            binding.favorite.setTag(null);
            binding.favorite.setOnClickListener(this);
            binding.favoriteSpeaker.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Word word = (Word) words.get(getBindingAdapterPosition());
            if (view == binding.favoriteSpeaker) {
                String wordName = word.getWord().toLowerCase();
                if (ConnectivityHelper.isConnectedToNetwork(context) && isVoicePronunciation) {
                    downloadAudio(wordName);
                } else {
                    try {
                        wordAdapterCallback.onMethodCallback(wordName);
                    } catch (ClassCastException e) {
                        Log.e("WordAdapterCallback", Objects.requireNonNull(e.getMessage()));
                    }
                }
            }
            if (view.getId() == R.id.favorite) {
                if (binding.favorite.getTag() == null) {
                    favoriteCount++;
                    prefs.setFavoriteCountProfile(favoriteCount);
                    repository.updateFavoriteStatus(word, "True");
                    binding.favorite.setIconResource(R.drawable.ic_favorite_icon_active);
                    binding.favorite.setTag(R.drawable.ic_favorite_icon_active);
                } else {
                    if (favoriteCount > 0) {
                        favoriteCount--;
                        prefs.setFavoriteCountProfile(favoriteCount);
                    }
                    repository.updateFavoriteStatus(word, "false");
                    binding.favorite.setIconResource(R.drawable.ic_favorite_icon);
                    binding.favorite.setTag(null);
                }
            }
        }

        public void downloadAudio(String wordName) {
            binding.spinKit.setVisibility(View.VISIBLE);
            firebaseMediaRepository.downloadAudio(wordName, new AudioRepository.Callback() {
                @Override
                public void onAudioReady(File audioFile) {
                    String audioPath = audioFile.getAbsolutePath();
                    MediaPlayer mp = new MediaPlayer();
                    try {
                        mp.setDataSource(audioPath);
                        mp.prepare();
                        mp.start();
                        binding.favoriteSpeaker.setEnabled(false);
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                binding.favoriteSpeaker.setEnabled(true);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    binding.spinKit.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    binding.spinKit.setVisibility(View.INVISIBLE);
                    binding.favoriteSpeaker.setEnabled(true);
                    e.printStackTrace();
                }
            });
        }
    }

    public interface WordAdapterCallback {
        void onMethodCallback(String word);
    }

    public void onDestroy() {
    }
}
