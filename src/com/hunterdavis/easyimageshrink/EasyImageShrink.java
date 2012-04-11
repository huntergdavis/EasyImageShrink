package com.hunterdavis.easyimageshrink;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class EasyImageShrink extends Activity {
	int SELECT_PICTURE = 42;
	Uri selectedImageUri;
	int selectedWidth =0;
	int selectedHeight = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// loadImageResourceIntoGallery(R.drawable.squid);
		// loadImageResourceIntoGallery(R.drawable.genocide);
		// loadImageResourceIntoGallery(R.drawable.balance);
		setContentView(R.layout.main);
		
		
		EditText widthText = (EditText) findViewById(R.id.width);
		widthText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				int oldWidth = selectedWidth;
				
				// here we call the text changed update sql function
				try {
					selectedWidth = Integer.parseInt(s.toString());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					selectedWidth = oldWidth;
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});		
		
		EditText heightText = (EditText) findViewById(R.id.height);
		heightText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				// here we call the text changed update sql function
				int oldHeight = selectedHeight;
				try {
					
					selectedHeight = Integer.parseInt(s.toString());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					selectedHeight = oldHeight;
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
		
		// Create an anonymous implementation of OnClickListener
		OnClickListener loadButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked

				// in onCreate or any event where your want the user to
				// select a file
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Source Photo"),
						SELECT_PICTURE);
			}
		};		
		
		// Create an anonymous implementation of OnClickListener
		OnClickListener saveButtonListner = new OnClickListener() {
			public void onClick(View v) {
				// do something when the button is clicked
				String fileLocation = getRealPathFromURI(selectedImageUri);
				imageHelper.scaleImageToNewSizeAndSave(getBaseContext(), selectedImageUri, fileLocation, selectedWidth, selectedHeight);
			}
		};				
		
		Button loadButton = (Button) findViewById(R.id.loadButton);
		loadButton.setOnClickListener(loadButtonListner);
		
		Button saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(saveButtonListner);
		
		
	}
	void setWidthHeightDisplay(int width, int height)
	{
		EditText t = new EditText(this);
		t = (EditText) findViewById(R.id.width);
		t.setText(String.valueOf(width));
		t = (EditText) findViewById(R.id.height);
		t.setText(String.valueOf(height));
		selectedWidth = width;
		selectedHeight = height;
		
	}
	
	public String getRealPathFromURI(Uri contentUri) {
		// can post image
		String [] proj={MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery( contentUri,
		proj, // Which columns to return
		null, // WHERE clause; which rows to return (all rows)
		null, // WHERE clause selection arguments (none)
		null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
		}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				selectedImageUri = data.getData();
				ImageView imgView = (ImageView) findViewById(R.id.ImageView01);
					imageHelperData scaleDisplay = imageHelper.scaleURIAndDisplay(
							getBaseContext(), selectedImageUri, imgView);
					setWidthHeightDisplay(scaleDisplay.width, scaleDisplay.height);
			}
		}
	}	
	

}
