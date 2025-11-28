
package com.fortitude.shamsulkarim.ieltsfordory.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.AudioRepository;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.FirebaseMediaRepository;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.LearningProgressRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.CardViewFavoriteOneLanguageBinding;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sk on 1/1/17.
 */

public class FavoriteRecyclerViewAdapter extends RecyclerView.Adapter<FavoriteRecyclerViewAdapter.WordViewHolder>
        implements Filterable {

    public String audioPath = null;
    public File localFile = null;
    private final List<Boolean> isFav = new ArrayList<>();
    public List<Word> words, filterList;
    private CustomFilterFavorite filter;
    private final Context context;

    private final LearningProgressRepository repository;

    private final AppPreferences prefs;
    private int favoriteCount;
    private final FirebaseMediaRepository firebaseMediaRepository;
    private final AdapterCallback adapterCallback;

    public FavoriteRecyclerViewAdapter(Context context, List<Word> words, AdapterCallback adapterCallback) {

        try {
            this.adapterCallback = adapterCallback;

        } catch (ClassCastException e) {
            throw new ClassCastException();
        }

        this.context = context;
        repository = new LearningProgressRepository(context);

        firebaseMediaRepository = new FirebaseMediaRepository();
        this.words = words;
        this.filterList = words;

        prefs = AppPreferences.get(context);
        if (!prefs.contains(AppPreferences.KEY_FAVORITE_COUNT_PROFILE)) {

            prefs.setFavoriteCountProfile(0);

        } else {

            favoriteCount = prefs.getFavoriteCountProfile();

        }

        addFav();
    }

    private void addFav() {

        for (int i = 0; i < words.size(); i++) {

            isFav.add(true);

        }

    }

    @Override
    public @NotNull WordViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        CardViewFavoriteOneLanguageBinding binding = CardViewFavoriteOneLanguageBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WordViewHolder(binding);
    }

    public void onBindViewHolder(@NotNull WordViewHolder holder, int position) {

        Word word = words.get(position);
        holder.binding.favoriteFavorite.setIconResource(R.drawable.ic_favorite_icon_active);

        holder.binding.favoriteCardTranslation.setText(word.getTranslation());
        holder.binding.favoriteCardWord.setText(word.getPronun());
        holder.binding.favoriteCardGrammar.setText(word.getGrammar());
        holder.binding.favoriteCardExample1.setText(word.getExample1());
        holder.binding.favoriteCardExample2.setText(word.getExample2());
        holder.binding.favoriteCardExample3.setText(word.getExample3());

    }

    public void onDestroy() {

    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilterFavorite(filterList, this);
        }

        return filter;
    }

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final CardViewFavoriteOneLanguageBinding binding;

        public WordViewHolder(CardViewFavoriteOneLanguageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.favoriteFavorite.setOnClickListener(this);
            binding.favoriteSpeaker.setOnClickListener(this);
        }

        public void downloadAudio(String wordName) {

            binding.spinKit.setVisibility(View.VISIBLE);

            firebaseMediaRepository.downloadAudio(wordName, new AudioRepository.Callback() {
                @Override
                public void onAudioReady(File audioFile) {
                    audioPath = audioFile.getAbsolutePath();

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

        @Override
        public void onClick(View view) {

            Word word = words.get(getBindingAdapterPosition());

            if (view == binding.favoriteSpeaker) {

                String wordName = words.get(getBindingAdapterPosition()).getWord().toLowerCase();

                if (ConnectivityHelper.isConnectedToNetwork(context)) {
                    // Show the connected screen
                    downloadAudio(wordName);
                    // Toast.makeText(context,"Connectedssss",Toast.LENGTH_LONG).show();

                } else {

                    // Show disconnected screen
                    try {
                        adapterCallback.onMethodCallback(wordName);
                    } catch (ClassCastException e) {
                        // do something
                        Log.e("AdapterCallback", Objects.requireNonNull(e.getMessage()));
                    }

                    Toast.makeText(context, "No internet", Toast.LENGTH_LONG).show();

                }

            }

            if (view.getId() == R.id.favorite_favorite) {

                if (word.isFavorite.equalsIgnoreCase("True")) {

                    if (favoriteCount > 0) {

                        favoriteCount--;
                        prefs.setFavoriteCountProfile(favoriteCount);

                    }

                    repository.updateFavoriteStatus(word, "False");

                } else {
                    favoriteCount++;
                    prefs.setFavoriteCountProfile(favoriteCount);

                    isFav.set(getBindingAdapterPosition(), true);
                    binding.favoriteFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
                    binding.favoriteFavorite.setTag(null);

                    repository.updateFavoriteStatus(word, "True");

                }

                words.remove(getBindingAdapterPosition());
                isFav.remove(getBindingAdapterPosition());
                notifyItemRemoved(getBindingAdapterPosition());
            }
        }

    }

    public interface AdapterCallback {

        void onMethodCallback(String word);
    }

}
