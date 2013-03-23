package com.egor.hercule2000;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class CommandeManuelle extends Activity {

	protected static final int SEEK_BAR_CHANGMENT = 0001;
	protected static final int BUTTON_BASE_PLUS = 0002;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_commande_manuelle);
		// Show the Up button in the action bar.
		setupActionBar();

		ConnexionDialogFragment connexionDialog = new ConnexionDialogFragment();
		connexionDialog.show(getFragmentManager(), null);

	}

	// Le Handler (Thread) charger de modifier le IHM
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SEEK_BAR_CHANGMENT:

				break;
			case BUTTON_BASE_PLUS:

				break;
			}
		};
	};

	/* Menu et Navigation */

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.commande_manuelle, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
