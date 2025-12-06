package com.fortitude.shamsulkarim.ieltsfordory.ui.train;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord;
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateFavoriteStatusUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateLearnedStatusSingleUseCase;
import org.koin.java.KoinJavaComponent;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.TrainFinishedWordRecyclerViewBinding;

import java.util.ArrayList;
import java.util.List;

public class TrainFinishedWordRecyclerView extends RecyclerView.Adapter<TrainFinishedWordRecyclerView.WordViewHolder> {

    private final List<VocabularyWord> words;
    private final List<Boolean> favoriteStates;
    private final List<Boolean> learnedStates;
    private final static int WORD_VIEW_TYPE = 0;
    private final UpdateFavoriteStatusUseCase updateFavoriteStatusUseCase;
    private final UpdateLearnedStatusSingleUseCase updateLearnedStatusSingleUseCase;

    public TrainFinishedWordRecyclerView(Context context, List<VocabularyWord> words) {
        this.words = words;
        this.favoriteStates = new ArrayList<>();
        this.learnedStates = new ArrayList<>();
        for (VocabularyWord word : words) {
            favoriteStates.add(word.isFavorite());
            learnedStates.add(word.isLearned());
        }
        updateFavoriteStatusUseCase = KoinJavaComponent.get(UpdateFavoriteStatusUseCase.class);
        updateLearnedStatusSingleUseCase = KoinJavaComponent.get(UpdateLearnedStatusSingleUseCase.class);
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TrainFinishedWordRecyclerViewBinding binding = TrainFinishedWordRecyclerViewBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WordViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        holder.binding.trainFinishedRecyclerViewWord.setText(words.get(position).getWord());

        if (favoriteStates.get(position)) {
            holder.binding.trainFinishedRecyclerViewFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
        } else {
            holder.binding.trainFinishedRecyclerViewFavorite.setIconResource(R.drawable.ic_favorite_icon);
        }
    }

    public int getItemViewType(int position) {

        return WORD_VIEW_TYPE;

    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TrainFinishedWordRecyclerViewBinding binding;

        public WordViewHolder(TrainFinishedWordRecyclerViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.trainFinishedRecyclerViewFavorite.setOnClickListener(this);
            binding.trainFinishedRecyclerViewUnlearn.setOnClickListener(this);
        }

        private void setUnlearn() {
            int pos = getBindingAdapterPosition();
            VocabularyWord word = words.get(pos);
            boolean isLearned = learnedStates.get(pos);

            if (isLearned) {
                learnedStates.set(pos, false);
                updateLearnedStatusSingleUseCase.execute(word, false);
                binding.trainFinishedRecyclerViewUnlearn.setText("Learn");
            } else {
                learnedStates.set(pos, true);
                updateLearnedStatusSingleUseCase.execute(word, true);
                binding.trainFinishedRecyclerViewUnlearn.setText("Unlearn");
            }
        }

        @Override
        public void onClick(View v) {
            int pos = getBindingAdapterPosition();
            VocabularyWord word = words.get(pos);

            if (v == binding.trainFinishedRecyclerViewUnlearn) {
                setUnlearn();
            }

            if (v == binding.trainFinishedRecyclerViewFavorite) {
                boolean isFavorite = favoriteStates.get(pos);

                if (isFavorite) {
                    favoriteStates.set(pos, false);
                    binding.trainFinishedRecyclerViewFavorite.setIconResource(R.drawable.ic_favorite_icon);
                    updateFavoriteStatusUseCase.execute(word, false);
                } else {
                    favoriteStates.set(pos, true);
                    binding.trainFinishedRecyclerViewFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
                    updateFavoriteStatusUseCase.execute(word, true);
                }
            }
        }
    }
}
