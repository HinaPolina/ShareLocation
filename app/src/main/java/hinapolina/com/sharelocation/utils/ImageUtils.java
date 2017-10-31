package hinapolina.com.sharelocation.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by polina on 10/30/17.
 */

public class ImageUtils {
    public static Uri decodeFile(Context ctx, Uri uri, int DESIREDWIDTH, int DESIREDHEIGHT) {
        Bitmap scaledBitmap = null;
        // Part 1: Decode image
        Bitmap unscaledBitmap = null;
        try {
            unscaledBitmap = ScalingUtilities.decodeFile(ctx.getContentResolver(), uri, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return uri;
        }

        if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
            // Part 2: Scale image
            scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
        } else {
            unscaledBitmap.recycle();
            return uri;
        }

        String s = "img-" + RandomStringUtils.randomAlphabetic(10);

        File outputDir = ctx.getCacheDir(); // context being the Activity pointer
        File f = null;
        try {
            f = File.createTempFile(s, ".png", outputDir);
        } catch (IOException e) {
            e.printStackTrace();
            return uri;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }

        scaledBitmap.recycle();

        return Uri.fromFile(f);

    }
}
