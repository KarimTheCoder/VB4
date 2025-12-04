package com.fortitude.shamsulkarim.ieltsfordory.ui.train;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data_old.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateFavoriteStatusUseCase;
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateLearnedStatusSingleUseCase;
import org.koin.java.KoinJavaComponent;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.TrainFinishedWordRecyclerViewBinding;

import java.util.List;

public class TrainFinishedWordRecyclerView extends RecyclerView.Adapter<TrainFinishedWordRecyclerView.WordViewHolder> {

    private final List<Word> words;
    private final static int WORD_VIEW_TYPE = 0;
    private final UpdateFavoriteStatusUseCase updateFavoriteStatusUseCase;
    private final UpdateLearnedStatusSingleUseCase updateLearnedStatusSingleUseCase;

    public TrainFinishedWordRecyclerView(Context context, List<Word> words) {

        this.words = words;
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

        if (words.get(position).getIsFavorite().equalsIgnoreCase("true")) {
            holder.binding.trainFinishedRecyclerViewFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
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
        Word mostMistakenWord;

        public WordViewHolder(TrainFinishedWordRecyclerViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.trainFinishedRecyclerViewFavorite.setOnClickListener(this);
            binding.trainFinishedRecyclerViewUnlearn.setOnClickListener(this);
        }

        private void setUnlearn() {

            if (mostMistakenWord.isLearned.equalsIgnoreCase("true")) {

                mostMistakenWord.setIsLearned("false");
                updateLearnedStatusSingleUseCase.execute(mostMistakenWord, "false");
                binding.trainFinishedRecyclerViewUnlearn.setText("Learn");

            } else {

                mostMistakenWord.setIsLearned("true");
                updateLearnedStatusSingleUseCase.execute(mostMistakenWord, "true");
                binding.trainFinishedRecyclerViewUnlearn.setText("Unlearn");
            }

        }

        @Override
        public void onClick(View v) {

            mostMistakenWord = words.get(getBindingAdapterPosition());

            String isFavorite = words.get(getBindingAdapterPosition()).isFavorite;

            if (v == binding.trainFinishedRecyclerViewUnlearn) {

                setUnlearn();
            }

            if (v == binding.trainFinishedRecyclerViewFavorite) {

                if (isFavorite.equalsIgnoreCase("true")) {

                    binding.trainFinishedRecyclerViewFavorite.setIconResource(R.drawable.ic_favorite_icon);
                    words.get(getBindingAdapterPosition()).setIsFavorite("false");
                    updateFavoriteStatusUseCase.execute(words.get(getBindingAdapterPosition()), "false");

                } else {

                    binding.trainFinishedRecyclerViewFavorite.setIconResource(R.drawable.ic_favorite_icon_active);
                    words.get(getBindingAdapterPosition()).setIsFavorite("true");
                    updateFavoriteStatusUseCase.execute(words.get(getBindingAdapterPosition()), "true");
                }
            }

        }
    }

}
