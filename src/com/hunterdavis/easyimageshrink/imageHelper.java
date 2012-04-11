package com.hunterdavis.easyimageshrink;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

public class imageHelper {

	public static Boolean scaleImageToNewSizeAndSave(Context context, Uri uri,
			String realLocation, int width, int height) {
		InputStream photoStream;
		try {
			photoStream = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap photoBitmap;

		photoBitmap = BitmapFactory.decodeStream(photoStream, null, options);
		if (photoBitmap == null) {
			return false;
		}

		Bitmap scaled = Bitmap.createScaledBitmap(photoBitmap, width, height,
				true);
		photoBitmap.recycle();
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		// actually save the file

		OutputStream outStream = null;
		String newFileName = null;

		String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME /* col1 */};
		Cursor c = context.getContentResolver().query(uri, projection, null,
				null, null);
		if (c != null && c.moveToFirst()) {
			String oldFileName = c.getString(0);
			int dotpos = oldFileName.lastIndexOf(".");
			if (dotpos > -1) {
				newFileName = oldFileName.substring(0, dotpos) + "-" + width
						+ "x" + height + ".png";
			}
		}

		if (newFileName != null) {
			File file = new File(extStorageDirectory, newFileName);
			try {
				outStream = new FileOutputStream(file);
				scaled.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				try {
					outStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				try {
					outStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}

				Toast.makeText(context, "Saved " + newFileName,
						Toast.LENGTH_LONG).show();
				new SingleMediaScanner(context, file);

			} catch (FileNotFoundException e) {
				// do something if errors out?
				return false;
			}
		}

		return true;
	}

	public static imageHelperData scaleURIAndDisplay(Context context, Uri uri,
			ImageView imgview) {
		double divisorDouble = 400;
		InputStream photoStream;
		imageHelperData returnData = new com.hunterdavis.easyimageshrink.imageHelperData();
		try {
			photoStream = context.getContentResolver().openInputStream(uri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			returnData.retVal = false;
			return returnData;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap photoBitmap;

		photoBitmap = BitmapFactory.decodeStream(photoStream, null, options);
		if (photoBitmap == null) {
			returnData.retVal = false;
			return returnData;
		}
		int h = photoBitmap.getHeight();
		int w = photoBitmap.getWidth();
		returnData.width = w*2;
		returnData.height = h*2;
		if ((w > h) && (w > divisorDouble)) {
			double ratio = divisorDouble / w;
			w = (int) divisorDouble;
			h = (int) (ratio * h);
		} else if ((h > w) && (h > divisorDouble)) {
			double ratio = divisorDouble / h;
			h = (int) divisorDouble;
			w = (int) (ratio * w);
		}

		Bitmap scaled = Bitmap.createScaledBitmap(photoBitmap, w, h, true);
		photoBitmap.recycle();
		imgview.setImageBitmap(scaled);
		returnData.retVal = true;
		return returnData;
	}
}
