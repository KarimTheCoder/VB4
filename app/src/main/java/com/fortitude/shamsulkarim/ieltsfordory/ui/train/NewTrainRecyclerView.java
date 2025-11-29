package com.fortitude.shamsulkarim.ieltsfordory.ui.train;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig;
import com.fortitude.shamsulkarim.ieltsfordory.R;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;
import com.fortitude.shamsulkarim.ieltsfordory.data.prefs.AppPreferences;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.FirebaseMediaRepository;
import com.fortitude.shamsulkarim.ieltsfordory.data.repository.LearningProgressRepository;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ImageFromServerBinding;
import com.fortitude.shamsulkarim.ieltsfordory.databinding.TrainEnglishOnlyBinding;
import com.fortitude.shamsulkarim.ieltsfordory.utility.connectivity.ConnectivityHelper;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.cketti.mailto.EmailIntentBuilder;

/**
 * Created by karim on 7/1/17.
 */

public class NewTrainRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Word word;
    private Bitmap bitmap;
    private final Context ctx;
    private final String[] imageQualityArray = { "High", "Medium", "Low" };
    private final String[] imageFolderName = { "High", "Medium", "Low" };
    private final static int DEFINATION_VIEW = 0;
    private final static int EXAMPLE_VIEW = 1;
    private final static int SERVER_IMAGE_VIEW = 2;
    private final static int AD_LAYOUT = 3;
    private final AppPreferences prefs;
    private final boolean connected;
    private final LearningProgressRepository learningProgressRepository;
    private TrainAdapterCallback trainAdapterCallback;
    private final FirebaseMediaRepository firebaseMediaRepository;

    public NewTrainRecyclerView(Context context, Word word, TrainAdapterCallback trainAdapterCallback) {

        try {
            this.trainAdapterCallback = trainAdapterCallback;
        } catch (ClassCastException e) {
            Log.e("TrainAdapterCallback", e.getMessage());
        }

        firebaseMediaRepository = new FirebaseMediaRepository();

        this.word = word;
        this.ctx = context;
        prefs = AppPreferences.get(ctx);
        learningProgressRepository = new LearningProgressRepository(context);
        connected = isOnline();

    }

    private boolean isOnline() {

        return ConnectivityHelper.isConnectedToNetwork(ctx);

    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {

        if (viewType == DEFINATION_VIEW) {
            TrainEnglishOnlyBinding binding = TrainEnglishOnlyBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent, false);
            return new DefinationAdapter(binding);
        } else {
            ImageFromServerBinding binding = ImageFromServerBinding.inflate(LayoutInflater.from(parent.getContext()),
                    parent, false);
            return new ImageViewHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {

            case DEFINATION_VIEW:

                SpannableStringBuilder spanEx1 = new SpannableStringBuilder(
                        word.getExample1() + "\n" + word.getExample1SL());

                // Span to set text color to some RGB value
                final ForegroundColorSpan fcs = new ForegroundColorSpan(ctx.getColor(R.color.primary_text_color));
                final ForegroundColorSpan lowColor = new ForegroundColorSpan(ctx.getColor(R.color.third_text_color));
                spanEx1.setSpan(fcs, 0, word.getExample1().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                SpannableStringBuilder spanEx2 = new SpannableStringBuilder(
                        word.getExample2() + "\n" + word.getExample2SL());
                spanEx2.setSpan(fcs, 0, word.getExample2().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                SpannableStringBuilder spanDef = new SpannableStringBuilder(
                        word.getTranslation() + "\n" + word.getTranslationSL());
                spanDef.setSpan(lowColor, word.getTranslation().length(),
                        1 + word.getTranslation().length() + word.getTranslationSL().length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                SpannableStringBuilder spanWord = new SpannableStringBuilder(word.getWord() + "\n" + word.getWordSL());
                spanWord.setSpan(lowColor, word.getWord().length(),
                        1 + word.getWordSL().length() + word.getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanWord.setSpan(new RelativeSizeSpan(0.8f), word.getWord().length(),
                        1 + word.getWordSL().length() + word.getWord().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                DefinationAdapter definationAdapter = (DefinationAdapter) holder;

                if (word.getIsFavorite().equalsIgnoreCase("true")) {

                    definationAdapter.binding.trainFavoriteIcon.setIconResource(R.drawable.ic_favorite_icon_active);
                }

                if (!prefs.getSecondLanguage().equalsIgnoreCase("spanish")) {

                    definationAdapter.binding.translationTrain.setText(word.getTranslation());
                    definationAdapter.binding.pronunciationTrain.setText(word.getPronun());
                    definationAdapter.binding.example1.setText(word.getExample1());
                    definationAdapter.binding.example2.setText(word.getExample2());
                    definationAdapter.binding.grammarTrain.setText(word.getGrammar());
                } else {
                    definationAdapter.binding.example1.setText(spanEx1);
                    definationAdapter.binding.example2.setText(spanEx2);
                    definationAdapter.binding.pronunciationTrain.setText(word.getPronun());
                    definationAdapter.binding.grammarTrain.setText(word.getGrammar());
                    definationAdapter.binding.translationTrain.setText(spanDef);
                }

                break;

            case SERVER_IMAGE_VIEW:

                int imageQualityNum = prefs.getImageQuality();

                String imageQualityString = imageFolderName[imageQualityNum];
                imageQualityString = imageQualityString.toLowerCase();

                final ImageViewHolder imageViewHolder = (ImageViewHolder) holder;

                if (connected) {
                    String wordName;

                    if (word.vocabularyType.equalsIgnoreCase("TOEFL")) {
                        wordName = word.getWord();
                    } else {
                        wordName = word.getWord().toLowerCase();
                    }

                    imageViewHolder.binding.serverImageState.setImageResource(R.drawable.waiting_for_image);
                    imageViewHolder.binding.stateText.setText(R.string.waitingForImage);
                    imageViewHolder.binding.imageText.setText(" ");

                    final ForegroundColorSpan fcss = new ForegroundColorSpan(ctx.getColor(R.color.primary_text_color));
                    final SpannableStringBuilder spanEx3 = new SpannableStringBuilder(
                            word.getExample3() + "\n" + word.getExample3SL());
                    spanEx3.setSpan(fcss, 0, word.getExample3().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    firebaseMediaRepository.downloadImage(wordName, imageQualityString,
                            new FirebaseMediaRepository.ImageCallback() {
                                @Override
                                public void onImageReady(byte[] bytes) {
                                    // Use the bytes to display the image
                                    // String path=
                                    // Environment.getExternalStorageDirectory()+"/"+editTextName.getText().toString();
                                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    imageViewHolder.binding.serverImage.setImageBitmap(bitmap);

                                    imageViewHolder.binding.serverImageState.setImageResource(0);
                                    if (prefs.getSecondLanguage().equalsIgnoreCase("spanish")) {
                                        imageViewHolder.binding.imageText.setText(spanEx3);
                                    } else {
                                        imageViewHolder.binding.imageText.setText(word.getExample3());
                                    }

                                    imageViewHolder.binding.stateText.setText(" ");
                                    Log.d("NewTrainRecyclerView", "Image loaded successfully for: " + wordName);
                                }

                                @Override
                                public void onError(Exception e) {
                                    // Handle error
                                    Log.e("NewTrainRecyclerView", "Error loading image for: " + wordName, e);
                                    imageViewHolder.binding.serverImageState.setImageResource(R.drawable.no_internet);
                                    imageViewHolder.binding.stateText.setText("Error loading image");
                                }
                            });

                } else {
                    imageViewHolder.binding.imageText.setText(" ");
                    imageViewHolder.binding.serverImageState.setImageResource(R.drawable.no_internet);
                    imageViewHolder.binding.stateText.setText(R.string.noInternet);
                }

                break;

        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {

            return DEFINATION_VIEW;
        } else if (position == 1) {

            return SERVER_IMAGE_VIEW;
        } else {

            return AD_LAYOUT;
        }
    }

    public class DefinationAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

        public String audioPath = null;
        public File localFile = null;
        public final TrainEnglishOnlyBinding binding;
        final String wordName = word.getWord().toLowerCase();
        final Boolean isVoicePronunciation;

        public DefinationAdapter(TrainEnglishOnlyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            Sprite doubleBounce = new Wave();
            binding.spinKit.setIndeterminateDrawable(doubleBounce);
            binding.spinKit.setVisibility(View.INVISIBLE);
            isVoicePronunciation = prefs.getPronunState();
            binding.trainFavoriteIcon.setOnClickListener(this);
            binding.trainSpeakerIcon.setOnClickListener(this);

            if (ConnectivityHelper.isConnectedToNetwork(binding.getRoot().getContext()) && isVoicePronunciation) {
                binding.trainSpeakerIcon.setEnabled(false);
                downloadAudio();
            } else {
                binding.trainSpeakerIcon.setEnabled(true);
                binding.spinKit.setVisibility(View.INVISIBLE);

                Toast.makeText(ctx, "Internet required for real human pronunciation.", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onClick(View v) {

            if (v == binding.trainSpeakerIcon) {

                MediaPlayer mp = new MediaPlayer();

                if (ConnectivityHelper.isConnectedToNetwork(ctx) && isVoicePronunciation) {
                    // Show the connected screen
                    try {

                        if (audioPath != null) {
                            mp.setDataSource(audioPath);
                            mp.prepare();
                            mp.start();
                        } else {
                            Log.i("Audio", "Audio Path: " + null);
                        }

                        binding.trainSpeakerIcon.setEnabled(false);

                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                binding.trainSpeakerIcon.setEnabled(true);
                                Log.i("Audio", "Audio Path: " + audioPath);
                            }
                        });
                    } catch (NullPointerException | IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        trainAdapterCallback.onMethodCallback(wordName);
                    } catch (ClassCastException e) {
                        Log.e("TrainAdapterCallback", Objects.requireNonNull(e.getMessage()));
                    }
                }

            }

            if (v == binding.trainFavoriteIcon) {

                String newStatus = word.getIsFavorite().equalsIgnoreCase("false") ? "true" : "false";
                learningProgressRepository.updateFavoriteStatus(word, newStatus);

                if (newStatus.equals("true")) {
                    binding.trainFavoriteIcon.setIconResource(R.drawable.ic_favorite_icon_active);
                } else {
                    binding.trainFavoriteIcon.setIconResource(R.drawable.ic_favorite_icon);
                }
                word.setIsFavorite(newStatus);

            }

        }

        public void downloadAudio() {
            binding.spinKit.setVisibility(View.VISIBLE);

            firebaseMediaRepository.downloadAudio(wordName,
                    new com.fortitude.shamsulkarim.ieltsfordory.data.repository.AudioRepository.Callback() {
                        @Override
                        public void onAudioReady(File audioFile) {
                            audioPath = audioFile.getAbsolutePath();
                            binding.trainSpeakerIcon.setEnabled(true);
                            binding.spinKit.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle error
                            binding.spinKit.setVisibility(View.INVISIBLE);
                        }
                    });

        }

    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageFromServerBinding binding;

        public ImageViewHolder(ImageFromServerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.trainReportmistake.setOnClickListener(this);
            binding.imageQuality.setOnClickListener(this);

            if (!prefs.contains(AppPreferences.KEY_IMAGE_QUALITY)) {
                prefs.setImageQuality(1);

            }

            int imageQualityNum = prefs.getImageQuality();

            binding.imageQualityText.setText(imageQualityArray[imageQualityNum]);

        }

        @Override
        public void onClick(View v) {
            if (v == binding.imageQuality) {

                if (!prefs.contains(AppPreferences.KEY_IMAGE_QUALITY)) {
                    prefs.setImageQuality(1);

                } else {

                    int imageQualityNum = 1 + prefs.getImageQuality();

                    if (imageQualityNum == 3) {
                        imageQualityNum = 0;
                    }
                    prefs.setImageQuality(imageQualityNum);

                }

                int imageQualityNum = prefs.getImageQuality();

                binding.imageQualityText.setText(imageQualityArray[imageQualityNum]);

            }

            if (v == binding.trainReportmistake) {

                try {
                    EmailIntentBuilder.from(ctx).to("fortitudedevs@gmail.com")
                            .subject("Mistake found! APP: VB4" + " FL: " + BuildConfig.FLAVOR + " VC: "
                                    + BuildConfig.VERSION_CODE + " VN: " + BuildConfig.VERSION_NAME)
                            .body("Word: " + word.getWord() + "\nDefinition: " + word.getTranslation() + "\nExample 1: "
                                    + word.example1 + "\nExample 2: " + word.example2 + "\nExample 3: " + word.example3
                                    + "\nVocabulary type: " + word.vocabularyType + "\nPOS: " + word.grammar
                                    + "\nSyllable: " + word.pronun + "\nPosition: " + word.position
                                    + "\nPlease describe the mistake here: ")
                            .start();
                } catch (NullPointerException i) {

                    Log.i("RepostMistake", Objects.requireNonNull(i.getMessage()));

                }
            }
        }
    }

    private static class BannerAdLayout extends RecyclerView.ViewHolder {

        public BannerAdLayout(View itemView) {
            super(itemView);
            Context context = itemView.getContext();

        }
    }

    public interface TrainAdapterCallback {

        void onMethodCallback(String word);

    }
}
