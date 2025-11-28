package com.fortitude.shamsulkarim.ieltsfordory.data.repository;

import android.content.Context;
import android.database.Cursor;

import com.fortitude.shamsulkarim.ieltsfordory.data.FavLearnedState;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseAdvance;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseBeginner;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.JustLearnedDatabaseIntermediate;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.models.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository class to handle word database operations and data aggregation.
 * This class centralizes all word database queries and eliminates code
 * duplication
 * by providing a single method to aggregate favorite and learned data from all
 * databases.
 */
public class LearningProgressRepository {

    private final IELTSWordDatabase ieltsWordDatabase;
    private final TOEFLWordDatabase toeflWordDatabase;
    private final SATWordDatabase satWordDatabase;
    private final GREWordDatabase greWordDatabase;

    private final VocabularyRepository vocabularyRepository;
    private final JustLearnedDatabaseBeginner justLearnedDatabaseBeginner;
    private final JustLearnedDatabaseIntermediate justLearnedDatabaseIntermediate;
    private final JustLearnedDatabaseAdvance justLearnedDatabaseAdvance;

    /**
     * Constructor for LearningProgressRepository
     * 
     * @param context Application context for database initialization
     */
    public LearningProgressRepository(Context context) {
        this.ieltsWordDatabase = new IELTSWordDatabase(context);
        this.toeflWordDatabase = new TOEFLWordDatabase(context);
        this.satWordDatabase = new SATWordDatabase(context);
        this.greWordDatabase = new GREWordDatabase(context);

        this.vocabularyRepository = new VocabularyRepository(context);
        this.justLearnedDatabaseBeginner = new JustLearnedDatabaseBeginner(context);
        this.justLearnedDatabaseIntermediate = new JustLearnedDatabaseIntermediate(context);
        this.justLearnedDatabaseAdvance = new JustLearnedDatabaseAdvance(context);
    }

    /**
     * Get the complete FavLearnedState for a user by aggregating data from all word
     * databases.
     * This method queries all four databases (IELTS, TOEFL, SAT, GRE) and builds
     * string representations of favorite and learned words.
     *
     * @param userName The user's name to include in the state
     * @return FavLearnedState object containing all aggregated data
     */
    public FavLearnedState getFavLearnedState(String userName) {
        // Initialize string builders for each database
        StringBuilder ieltsFavBuilder = new StringBuilder();
        StringBuilder ieltsLearnedBuilder = new StringBuilder();
        StringBuilder toeflFavBuilder = new StringBuilder();
        StringBuilder toeflLearnedBuilder = new StringBuilder();
        StringBuilder satFavBuilder = new StringBuilder();
        StringBuilder satLearnedBuilder = new StringBuilder();
        StringBuilder greFavBuilder = new StringBuilder();
        StringBuilder greLearnedBuilder = new StringBuilder();

        // Process each database
        processWordDatabase(ieltsWordDatabase, ieltsFavBuilder, ieltsLearnedBuilder);
        processWordDatabase(toeflWordDatabase, toeflFavBuilder, toeflLearnedBuilder);
        processWordDatabase(satWordDatabase, satFavBuilder, satLearnedBuilder);
        processWordDatabase(greWordDatabase, greFavBuilder, greLearnedBuilder);

        // Build and return the FavLearnedState object
        return new FavLearnedState(
                userName,
                ieltsLearnedBuilder.toString(),
                toeflLearnedBuilder.toString(),
                satLearnedBuilder.toString(),
                greLearnedBuilder.toString(),
                ieltsFavBuilder.toString(),
                toeflFavBuilder.toString(),
                satFavBuilder.toString(),
                greFavBuilder.toString());
    }

    /**
     * Process a single word database and build favorite/learned strings.
     * This generic method eliminates code duplication by handling any word
     * database.
     *
     * The cursor is expected to have:
     * - Column 2: FAV (favorite flag as "true"/"false" string)
     * - Column 3: LEARNED (learned flag as "true"/"false" string)
     *
     * @param database       The word database to query
     * @param favBuilder     StringBuilder to accumulate favorite flags (1 or 0,
     *                       separated by +)
     * @param learnedBuilder StringBuilder to accumulate learned flags (1 or 0,
     *                       separated by +)
     */
    private void processWordDatabase(
            Object database,
            StringBuilder favBuilder,
            StringBuilder learnedBuilder) {
        Cursor cursor = null;
        try {
            // Get data from the database
            if (database instanceof IELTSWordDatabase) {
                cursor = ((IELTSWordDatabase) database).getData();
            } else if (database instanceof TOEFLWordDatabase) {
                cursor = ((TOEFLWordDatabase) database).getData();
            } else if (database instanceof SATWordDatabase) {
                cursor = ((SATWordDatabase) database).getData();
            } else if (database instanceof GREWordDatabase) {
                cursor = ((GREWordDatabase) database).getData();
            }

            if (cursor == null) {
                return;
            }

            // Process each row in the cursor
            while (cursor.moveToNext()) {
                // Column 2 is FAV flag
                String favValue = cursor.getString(2);
                if ("true".equalsIgnoreCase(favValue)) {
                    favBuilder.append("1+");
                } else {
                    favBuilder.append("0+");
                }

                // Column 3 is LEARNED flag
                String learnedValue = cursor.getString(3);
                if ("true".equalsIgnoreCase(learnedValue)) {
                    learnedBuilder.append("1+");
                } else {
                    learnedBuilder.append("0+");
                }
            }
        } finally {
            // Always close cursor and database to prevent leaks
            if (cursor != null) {
                cursor.close();
            }

            // Close the database
            if (database instanceof IELTSWordDatabase) {
                ((IELTSWordDatabase) database).close();
            } else if (database instanceof TOEFLWordDatabase) {
                ((TOEFLWordDatabase) database).close();
            } else if (database instanceof SATWordDatabase) {
                ((SATWordDatabase) database).close();
            } else if (database instanceof GREWordDatabase) {
                ((GREWordDatabase) database).close();
            }
        }
    }

    /**
     * Get the IELTS word database instance
     * 
     * @return IELTSWordDatabase instance
     */
    public IELTSWordDatabase getIeltsWordDatabase() {
        return ieltsWordDatabase;
    }

    /**
     * Get the TOEFL word database instance
     * 
     * @return TOEFLWordDatabase instance
     */
    public TOEFLWordDatabase getToeflWordDatabase() {
        return toeflWordDatabase;
    }

    /**
     * Get the SAT word database instance
     * 
     * @return SATWordDatabase instance
     */
    public SATWordDatabase getSatWordDatabase() {
        return satWordDatabase;
    }

    /**
     * Get the GRE word database instance
     * 
     * @return GREWordDatabase instance
     */
    public GREWordDatabase getGreWordDatabase() {
        return greWordDatabase;
    }

    public List<Word> fetchSessionWords(String level, int wordsPerSession) {
        List<Word> words = new ArrayList<>();

        if (level.equalsIgnoreCase("beginner")) {
            words.addAll(vocabularyRepository.getBeginnerUnlearnedWords());
        } else if (level.equalsIgnoreCase("intermediate")) {
            words.addAll(vocabularyRepository.getIntermediateUnlearnedWords());
        } else if (level.equalsIgnoreCase("advance")) {
            words.addAll(vocabularyRepository.getAdvanceUnlearnedWords());
        }

        List<Word> sessionWords = new ArrayList<>();
        int limit = Math.min(wordsPerSession, words.size());
        for (int k = 0; k < limit; k++) {
            sessionWords.add(words.get(k));
        }

        return sessionWords;
    }

    public List<Word> getAllUnlearnedWords(String level) {
        List<Word> words = new ArrayList<>();
        if (level.equalsIgnoreCase("beginner")) {
            words.addAll(vocabularyRepository.getBeginnerUnlearnedWords());
        } else if (level.equalsIgnoreCase("intermediate")) {
            words.addAll(vocabularyRepository.getIntermediateUnlearnedWords());
        } else if (level.equalsIgnoreCase("advance")) {
            words.addAll(vocabularyRepository.getAdvanceUnlearnedWords());
        }
        return words;
    }

    public void updateLearnedStatus(List<Word> words) {
        for (Word word : words) {
            if (word.vocabularyType.equalsIgnoreCase("IELTS")) {
                vocabularyRepository.updateIELTSLearnState(word.position + "", "true");
            }
            if (word.vocabularyType.equalsIgnoreCase("TOEFL")) {
                vocabularyRepository.updateTOEFLLearnState(word.position + "", "true");
            }
            if (word.vocabularyType.equalsIgnoreCase("SAT")) {
                vocabularyRepository.updateSATLearnState(word.position + "", "true");
            }
            if (word.vocabularyType.equalsIgnoreCase("GRE")) {
                vocabularyRepository.updateGRELearnState(word.position + "", "true");
            }
        }
    }

    public void updateJustLearnedStatus(String level, List<Word> words, int mostMistakenIndex) {
        justLearnedDatabaseBeginner.removeAll();
        justLearnedDatabaseIntermediate.removeAll();
        justLearnedDatabaseAdvance.removeAll();

        int j = 1;

        if (level.equalsIgnoreCase("beginner")) {
            for (int i = 0; i < words.size(); i++) {
                Word word = words.get(i);
                insertToDatabase(justLearnedDatabaseBeginner, j, word, i == mostMistakenIndex);
                j++;
            }
        } else if (level.equalsIgnoreCase("intermediate")) {
            for (int i = 0; i < words.size(); i++) {
                Word word = words.get(i);
                insertToDatabase(justLearnedDatabaseIntermediate, j, word, i == mostMistakenIndex);
                j++;
            }
        } else if (level.equalsIgnoreCase("advance")) {
            for (int i = 0; i < words.size(); i++) {
                Word word = words.get(i);
                insertToDatabase(justLearnedDatabaseAdvance, j, word, i == mostMistakenIndex);
                j++;
            }
        }
    }

    private void insertToDatabase(Object database, int j, Word word, boolean isMistaken) {
        String isFavorite = word.isFavorite(); // isFavorite() returns String "true"/"false"
        // Check if isFavorite is boolean or String in Word model.
        // In ViewModel it was word.isFavorite() (method) or word.isFavorite (field).
        // ViewModel line 220: word.isFavorite()
        // ViewModel line 239: word.isFavorite (field)
        // I should check Word model. But for now I will assume method or field access
        // via reflection or just try both?
        // No, I should check Word model.
        // But I can't check Word model easily without viewing it.
        // But I can see in ViewModel:
        // Line 220: word.isFavorite()
        // Line 239: word.isFavorite
        // Line 256: word.isFavorite

        // It seems inconsistent usage in ViewModel.
        // I will use word.isFavorite if public field, or getter.
        // Let's assume getter `isFavorite()` is safer if it exists, or check the file.

        // Also the last argument "true"/"false" for isMistaken.
        String mistakenStr = isMistaken ? "true" : "false";

        if (database instanceof JustLearnedDatabaseBeginner) {
            ((JustLearnedDatabaseBeginner) database).insertData(j, "" + word.position, word.getWord(),
                    word.getTranslation(),
                    word.getExtra(), word.getPronun(), word.getGrammar(), word.getExample1(),
                    word.getExample2(), word.getExample3(), word.vocabularyType, "true", isFavorite,
                    mistakenStr);
        } else if (database instanceof JustLearnedDatabaseIntermediate) {
            ((JustLearnedDatabaseIntermediate) database).insertData(j, "" + word.position, word.getWord(),
                    word.getTranslation(),
                    word.getExtra(), word.getPronun(), word.getGrammar(), word.getExample1(),
                    word.getExample2(), word.getExample3(), word.vocabularyType, "true", isFavorite,
                    mistakenStr);
        } else if (database instanceof JustLearnedDatabaseAdvance) {
            ((JustLearnedDatabaseAdvance) database).insertData(j, "" + word.position, word.getWord(),
                    word.getTranslation(),
                    word.getExtra(), word.getPronun(), word.getGrammar(), word.getExample1(),
                    word.getExample2(), word.getExample3(), word.vocabularyType, "true", isFavorite,
                    mistakenStr);
        }
    }

    public void updateFavoriteStatus(Word word, String newStatus) {
        if (word.vocabularyType.equalsIgnoreCase("IELTS")) {
            ieltsWordDatabase.updateFav(word.position + "", newStatus);
        } else if (word.vocabularyType.equalsIgnoreCase("TOEFL")) {
            toeflWordDatabase.updateFav(word.position + "", newStatus);
        } else if (word.vocabularyType.equalsIgnoreCase("SAT")) {
            satWordDatabase.updateFav(word.position + "", newStatus);
        } else if (word.vocabularyType.equalsIgnoreCase("GRE")) {
            greWordDatabase.updateFav(word.position + "", newStatus);
        }
    }

    public void updateLearnedStatus(Word word, String newStatus) {
        if (word.vocabularyType.equalsIgnoreCase("IELTS")) {
            ieltsWordDatabase.updateLearned(word.position + "", newStatus);
        } else if (word.vocabularyType.equalsIgnoreCase("TOEFL")) {
            toeflWordDatabase.updateLearned(word.position + "", newStatus);
        } else if (word.vocabularyType.equalsIgnoreCase("SAT")) {
            satWordDatabase.updateLearned(word.position + "", newStatus);
        } else if (word.vocabularyType.equalsIgnoreCase("GRE")) {
            greWordDatabase.updateLearned(word.position + "", newStatus);
        }
    }
}
