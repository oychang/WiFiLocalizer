package edu.fiu.mpact.wifilocalizer;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;

import java.math.BigInteger;

import uk.co.senab.photoview.PhotoMarker;


public class Utils {
    public final class Constants {
        // Fully qualified project name for use in intent data passing
        // This doesn't have to actually be correct, but try to be.
        private static final String PKG = "edu.fiu.mpact.wifilocalizer";
        public static final String MAP_ID_EXTRA = PKG + ".map_id";

        // Shared preferences file
        public static final String PREF_FILE = "SharedHintsPreferences";
        // Unique keys to address the hint boxes
        public static final String PREF_MAIN_HINT = "hint1";
        public static final String PREF_VIEW_HINT = "hint2";
        public static final String PREF_LOCALIZE_HINT = "hint3";
        public static final String PREF_TRAIN_HINT = "hint4";

        public static final String PINEAPPLE_URL = "http://172.16.42.1";
        public static final String PINEAPPLE_SCRAPER_PORT = "8000";
        public static final String PINEAPPLE_SERVER_URL = PINEAPPLE_URL + ":" + PINEAPPLE_SCRAPER_PORT;

        public static final String METADATA_URL = "http://eic15.eng.fiu.edu/wifiloc/getmeta.php";
        public static final String POINTS_URL = "http://eic15.eng.fiu.edu/wifiloc/getpoints.php";
        public static final String DELETE_URL = "http://eic15.eng.fiu.edu/wifiloc/deletereading.php";
        public static final String INSERT_URL = "http://eic15.eng.fiu.edu/wifiloc/insertreading.php";
    }

    public static ContentValues createNewMapContentValues(Context context, String name, int resID) {
        return createNewMapContentValues(name, Utils.resourceToUri(context, resID));
    }

    public static ContentValues createNewMapContentValues(String name, Uri uri) {
        final ContentValues values = new ContentValues();
        values.put(Database.Maps.NAME, name);
        values.put(Database.Maps.DATA, uri.toString());
        values.put(Database.Maps.DATE_ADDED, System.currentTimeMillis());
        return values;
    }

    /**
     * Create hint dialogs with an optional checkbox to hide all. If the box has already been
     * shown once we will not show.
     *
     * @param context context of the running activity
     * @param key     the SharedPreferences key of the boolean setting to check
     * @param builder     Builder ready to call .build() on
     * @return the value of the preference given by key
     */
    public static boolean showHelpOnFirstRun(Context context, String key, ShowcaseView.Builder builder) {
        final SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);

        // Check if we've already bugged about this
        if (prefs.getBoolean(key, false)) return false;

        // Build and show the dialog
        builder.build();

        // Mark down this dialog as shown
        setSharedPreference(prefs, key, true);

        return true;
    }

    public static AlertDialog.Builder buildDialog(Context context, int res) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        return dialog.setPositiveButton(android.R.string.yes, null).setMessage(res);
    }

    public static void setSharedPreference(SharedPreferences prefs, String key, boolean value) {
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static Uri resourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    // ***********************************************************************


    public static class EncTrainDistMatchPair {
        public LocalizationData.Location trainLocation;
        public BigInteger dist;
        public int matches;

        public EncTrainDistMatchPair(LocalizationData.Location t, BigInteger d, int m) {
            trainLocation = t;
            dist = d;
            matches = m;
        }
    }


    public static class EncTrainDistPair {
        public LocalizationData.Location trainLocation;
        public BigInteger dist;

        public EncTrainDistPair(LocalizationData.Location t, BigInteger d) {
            trainLocation = t;
            dist = d;
        }
    }


    public static class TrainDistPair implements Comparable<TrainDistPair> {
        public LocalizationData.Location trainLocation;
        public double dist;

        public TrainDistPair(LocalizationData.Location t, double d) {
            trainLocation = t;
            dist = d;
        }

        @Override
        public int compareTo(@NonNull TrainDistPair another) {
            return dist < another.dist ? -1 : dist > another.dist ? 1 : 0;
        }
    }

    // ************************************************************************

    /**
     * Returns a pair of (width, height) representing the dimensions of an image. Right now just
     * assumes all images are the same size as R.drawable.ec_1
     *
     * @param unused unused
     * @param c any context to give to BitmapFactory.decodeResource
     * @return the pair of pixel width x height
     */
    @SuppressWarnings("unused")
    public static int[] getImageSize(Uri unused, Context c) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(c.getResources(), R.drawable.ec_1, options);

        return new int[] {options.outWidth, options.outHeight};
    }

    /**
     * Creates a new ImageView instance associated as a child of layout.
     *
     * @param context which context to associate with ImageView
     * @param layout  parent layout
     * @return view with image resource set to a drawable
     */
    private static ImageView createNewMarker(Context context, RelativeLayout layout, int resId) {
        final ImageView ret = new ImageView(context);
        final int markerSize = context.getResources().getInteger(R.integer.map_marker_size) * 2;

        ret.setImageResource(resId);
        layout.addView(ret, new LayoutParams(markerSize, markerSize));

        return ret;
    }

    public static PhotoMarker createNewMarker(Context context, RelativeLayout wrapper, float x, float y, int resId) {
        return new PhotoMarker(createNewMarker(context, wrapper, resId), x, y, context
                .getResources().getInteger(R.integer.map_marker_size));
    }

    public static PhotoMarker createNewMarker(Context context, RelativeLayout wrapper, float x, float y) {
        return createNewMarker(context, wrapper, x, y, R.drawable.x);
    }
}
