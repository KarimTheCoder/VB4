package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.train

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.fortitude.shamsulkarim.ieltsfordory.BuildConfig
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.data.preferences.AppPreferences
import com.fortitude.shamsulkarim.ieltsfordory.databinding.ImageFromServerBinding
import com.fortitude.shamsulkarim.ieltsfordory.databinding.TrainEnglishOnlyBinding
import com.fortitude.shamsulkarim.ieltsfordory.domain.connectivity.usecase.IsConnectedUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.learning.usecase.UpdateFavoriteStatusUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.AudioRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.ImageRepository
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.model.AudioData
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.model.ImageData
import com.fortitude.shamsulkarim.ieltsfordory.domain.media.usecase.DownloadAudioUseCase
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.github.ybq.android.spinkit.style.Wave
import de.cketti.mailto.EmailIntentBuilder
import org.koin.java.KoinJavaComponent.get
import java.io.File
import java.io.IOException

/**
 * RecyclerView Adapter for displaying word details during training.
 * Shows word definition, pronunciation, examples, and images.
 */
class NewTrainRecyclerView(
    private val ctx: Context,
    private var word: VocabularyWord,
    private val trainAdapterCallback: TrainAdapterCallback?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val imageQualityArray = arrayOf("High", "Medium", "Low")
    private val imageFolderName = arrayOf("High", "Medium", "Low")
    
    private val prefs: AppPreferences = AppPreferences.get(ctx)
    private val isConnectedUseCase: IsConnectedUseCase = get(IsConnectedUseCase::class.java)
    private val updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase = get(UpdateFavoriteStatusUseCase::class.java)
    private val downloadAudioUseCase: DownloadAudioUseCase = get(DownloadAudioUseCase::class.java)
    private val imageRepository: ImageRepository = get(ImageRepository::class.java)
    
    private val connected: Boolean = isConnectedUseCase.execute()

    companion object {
        private const val DEFINATION_VIEW = 0
        private const val EXAMPLE_VIEW = 1
        private const val SERVER_IMAGE_VIEW = 2
        private const val AD_LAYOUT = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == DEFINATION_VIEW) {
            val binding = TrainEnglishOnlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DefinitionAdapter(binding)
        } else {
            val binding = ImageFromServerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ImageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            DEFINATION_VIEW -> bindDefinitionView(holder as DefinitionAdapter)
            SERVER_IMAGE_VIEW -> bindImageView(holder as ImageViewHolder)
        }
    }

    private fun bindDefinitionView(holder: DefinitionAdapter) {
        val example1 = word.example1 ?: ""
        val example1SL = word.example1SecondLang ?: ""
        val example2 = word.example2 ?: ""
        val example2SL = word.example2SecondLang ?: ""
        val translationSL = word.translationSecondLang ?: ""
        val wordSL = word.wordSecondLang ?: ""
        val pronunciation = word.pronunciation ?: ""
        val grammar = word.grammar ?: ""

        val fcs = ForegroundColorSpan(ctx.getColor(R.color.primary_text_color))
        val lowColor = ForegroundColorSpan(ctx.getColor(R.color.third_text_color))

        val spanEx1 = SpannableStringBuilder("$example1\n$example1SL").apply {
            setSpan(fcs, 0, example1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val spanEx2 = SpannableStringBuilder("$example2\n$example2SL").apply {
            setSpan(fcs, 0, example2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val spanDef = SpannableStringBuilder("${word.translation}\n$translationSL").apply {
            setSpan(lowColor, word.translation.length, word.translation.length + 1 + translationSL.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val spanWord = SpannableStringBuilder("${word.word}\n$wordSL").apply {
            setSpan(lowColor, word.word.length, word.word.length + 1 + wordSL.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(0.8f), word.word.length, word.word.length + 1 + wordSL.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        if (word.isFavorite) {
            holder.binding.trainFavoriteIcon.setIconResource(R.drawable.ic_favorite_icon_active)
        }

        if (!prefs.secondLanguage.equals("spanish", ignoreCase = true)) {
            holder.binding.translationTrain.text = word.translation
            holder.binding.pronunciationTrain.text = pronunciation
            holder.binding.example1.text = example1
            holder.binding.example2.text = example2
            holder.binding.grammarTrain.text = grammar
        } else {
            holder.binding.example1.text = spanEx1
            holder.binding.example2.text = spanEx2
            holder.binding.pronunciationTrain.text = pronunciation
            holder.binding.grammarTrain.text = grammar
            holder.binding.translationTrain.text = spanDef
        }
    }

    private fun bindImageView(holder: ImageViewHolder) {
        val imageQualityNum = prefs.imageQuality
        val imageQualityString = imageFolderName[imageQualityNum].lowercase()

        if (connected) {
            val wordName = if (word.source.name.equals("TOEFL", ignoreCase = true)) {
                word.word
            } else {
                word.word.lowercase()
            }

            holder.binding.serverImageState.setImageResource(R.drawable.waiting_for_image)
            holder.binding.stateText.setText(R.string.waitingForImage)
            holder.binding.imageText.text = " "

            val fcss = ForegroundColorSpan(ctx.getColor(R.color.primary_text_color))
            val example3 = word.example3 ?: ""
            val example3SL = word.example3SecondLang ?: ""
            val spanEx3 = SpannableStringBuilder("$example3\n$example3SL").apply {
                setSpan(fcss, 0, example3.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            imageRepository.downloadImage(wordName, imageQualityString, object : ImageRepository.Callback {
                override fun onSuccess(data: ImageData) {
                    val bytes = data.bytes
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    holder.binding.serverImage.setImageBitmap(bitmap)
                    holder.binding.serverImageState.setImageResource(0)
                    
                    if (prefs.secondLanguage.equals("spanish", ignoreCase = true)) {
                        holder.binding.imageText.text = spanEx3
                    } else {
                        holder.binding.imageText.text = word.example3 ?: ""
                    }
                    holder.binding.stateText.text = " "
                    Log.d("NewTrainRecyclerView", "Image loaded successfully for: $wordName")
                }

                override fun onError(e: Exception) {
                    Log.e("NewTrainRecyclerView", "Error loading image for: $wordName", e)
                    holder.binding.serverImageState.setImageResource(R.drawable.no_internet)
                    holder.binding.stateText.text = "Error loading image"
                }
            })
        } else {
            holder.binding.imageText.text = " "
            holder.binding.serverImageState.setImageResource(R.drawable.no_internet)
            holder.binding.stateText.setText(R.string.noInternet)
        }
    }

    override fun getItemCount(): Int = 2

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> DEFINATION_VIEW
            1 -> SERVER_IMAGE_VIEW
            else -> AD_LAYOUT
        }
    }

    /**
     * ViewHolder for word definition, pronunciation, and examples.
     */
    inner class DefinitionAdapter(val binding: TrainEnglishOnlyBinding) : 
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        var audioPath: String? = null
        var localFile: File? = null
        private val wordName: String = word.word.lowercase()
        private val isVoicePronunciation: Boolean = prefs.pronunState

        init {
            binding.spinKit.apply {
                setIndeterminateDrawable(Wave())
                visibility = View.INVISIBLE
            }
            
            binding.trainFavoriteIcon.setOnClickListener(this)
            binding.trainSpeakerIcon.setOnClickListener(this)

            if (isConnectedUseCase.execute() && isVoicePronunciation) {
                binding.trainSpeakerIcon.isEnabled = false
                downloadAudio()
            } else {
                binding.trainSpeakerIcon.isEnabled = true
                binding.spinKit.visibility = View.INVISIBLE
                Toast.makeText(ctx, "Internet required for real human pronunciation.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onClick(v: View) {
            when (v) {
                binding.trainSpeakerIcon -> handleSpeakerClick()
                binding.trainFavoriteIcon -> handleFavoriteClick()
            }
        }

        private fun handleSpeakerClick() {
            val mp = MediaPlayer()

            if (isConnectedUseCase.execute() && isVoicePronunciation) {
                try {
                    audioPath?.let { path ->
                        mp.setDataSource(path)
                        mp.prepare()
                        mp.start()
                    } ?: Log.i("Audio", "Audio Path: null")

                    binding.trainSpeakerIcon.isEnabled = false
                    mp.setOnCompletionListener {
                        binding.trainSpeakerIcon.isEnabled = true
                        Log.i("Audio", "Audio Path: $audioPath")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    trainAdapterCallback?.onMethodCallback(wordName)
                } catch (e: ClassCastException) {
                    Log.e("TrainAdapterCallback", e.message ?: "Unknown error")
                }
            }
        }

        private fun handleFavoriteClick() {
            val newStatus = !word.isFavorite
            updateFavoriteStatusUseCase.execute(word, newStatus)

            if (newStatus) {
                binding.trainFavoriteIcon.setIconResource(R.drawable.ic_favorite_icon_active)
            } else {
                binding.trainFavoriteIcon.setIconResource(R.drawable.ic_favorite_icon)
            }
            word = word.withFavorite(newStatus)
        }

        private fun downloadAudio() {
            binding.spinKit.visibility = View.VISIBLE

            downloadAudioUseCase.execute(wordName, object : AudioRepository.Callback {
                override fun onSuccess(data: AudioData) {
                    audioPath = data.localPath
                    binding.trainSpeakerIcon.isEnabled = true
                    binding.spinKit.visibility = View.INVISIBLE
                }

                override fun onError(e: Exception) {
                    binding.spinKit.visibility = View.INVISIBLE
                }
            })
        }
    }

    /**
     * ViewHolder for displaying word images from server.
     */
    inner class ImageViewHolder(val binding: ImageFromServerBinding) : 
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.trainReportmistake.setOnClickListener(this)
            binding.imageQuality.setOnClickListener(this)

            if (!prefs.contains(AppPreferences.KEY_IMAGE_QUALITY)) {
                prefs.imageQuality = 1
            }

            val imageQualityNum = prefs.imageQuality
            binding.imageQualityText.text = imageQualityArray[imageQualityNum]
        }

        override fun onClick(v: View) {
            when (v) {
                binding.imageQuality -> handleQualityClick()
                binding.trainReportmistake -> handleReportMistakeClick()
            }
        }

        private fun handleQualityClick() {
            if (!prefs.contains(AppPreferences.KEY_IMAGE_QUALITY)) {
                prefs.imageQuality = 1
            } else {
                var imageQualityNum = prefs.imageQuality + 1
                if (imageQualityNum == 3) {
                    imageQualityNum = 0
                }
                prefs.imageQuality = imageQualityNum
            }

            binding.imageQualityText.text = imageQualityArray[prefs.imageQuality]
        }

        private fun handleReportMistakeClick() {
            try {
                EmailIntentBuilder.from(ctx)
                    .to("fortitudedevs@gmail.com")
                    .subject("Mistake found! APP: VB4 FL: ${BuildConfig.FLAVOR} VC: ${BuildConfig.VERSION_CODE} VN: ${BuildConfig.VERSION_NAME}")
                    .body(buildString {
                        appendLine("Word: ${word.word}")
                        appendLine("Definition: ${word.translation}")
                        appendLine("Example 1: ${word.example1 ?: ""}")
                        appendLine("Example 2: ${word.example2 ?: ""}")
                        appendLine("Example 3: ${word.example3 ?: ""}")
                        appendLine("Vocabulary type: ${word.source.name}")
                        appendLine("POS: ${word.grammar ?: ""}")
                        appendLine("Syllable: ${word.pronunciation ?: ""}")
                        appendLine("Position: ${word.id}")
                        appendLine("Please describe the mistake here: ")
                    })
                    .start()
            } catch (e: NullPointerException) {
                Log.i("RepostMistake", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Callback interface for training adapter events.
     */
    interface TrainAdapterCallback {
        fun onMethodCallback(word: String)
    }
}


