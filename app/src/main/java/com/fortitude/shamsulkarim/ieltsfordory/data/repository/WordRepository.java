package com.fortitude.shamsulkarim.ieltsfordory.data.repository;

import android.content.Context;
import android.database.Cursor;

import com.fortitude.shamsulkarim.ieltsfordory.data.FavLearnedState;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.GREWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.IELTSWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.SATWordDatabase;
import com.fortitude.shamsulkarim.ieltsfordory.data.databases.TOEFLWordDatabase;

/**
 * Repository class to handle word database operations and data aggregation.
 * This class centralizes all word database queries and eliminates code
 * duplication
 * by providing a single method to aggregate favorite and learned data from all
 * databases.
 */
public class WordRepository {

    private final IELTSWordDatabase ieltsWordDatabase;
    private final TOEFLWordDatabase toeflWordDatabase;
    private final SATWordDatabase satWordDatabase;
    private final GREWordDatabase greWordDatabase;

    /**
     * Constructor for WordRepository
     * 
     * @param context Application context for database initialization
     */
    public WordRepository(Context context) {
        this.ieltsWordDatabase = new IELTSWordDatabase(context);
        this.toeflWordDatabase = new TOEFLWordDatabase(context);
        this.satWordDatabase = new SATWordDatabase(context);
        this.greWordDatabase = new GREWordDatabase(context);
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
}
