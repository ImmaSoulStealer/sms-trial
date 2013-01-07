package dtd.phs.noad_uninstaller;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import dtd.phs.lib.utils.Helpers;
import dtd.phs.lib.utils.Logger;

public class MainUninstaller extends Activity {

	private static final int FRAME_LOADING = 0;
	private static final int FRAME_DATA = 1;

	private AutoCompleteTextView atSearch;
	private FrameLayout mainFrames;
	private ListView listApps;
	private AppsAdapter adapter;
	private Button btUninstall;

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		//TODO: highlight the selected item !
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main_layout);

		atSearch = (AutoCompleteTextView) findViewById(R.id.atSearch);
		atSearch.setOnItemClickListener(searchItemClick);

		mainFrames = (FrameLayout) findViewById(R.id.main_frames);
		listApps = (ListView) findViewById(R.id.list_apps);
		adapter = new AppsAdapter(getApplicationContext());
		listApps.setAdapter(adapter);      

		listApps.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				adapter.changeSelectedState(position);
			}
		});

		btUninstall = (Button) findViewById(R.id.btUninstall);
		btUninstall.setOnClickListener(uninstallListener);
	}

	OnItemClickListener searchItemClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			Logger.logInfo("Autocomplete pos: " + position);
			TextView tv = (TextView) view;
			String name = tv.getText().toString();
			uninstallApp(name);
		}
	};


	View.OnClickListener uninstallListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<PHS_AppInfo> selectedApps = adapter.getSelectedApps();
			if ( selectedApps.size() != 0 ) {
				for(PHS_AppInfo app : selectedApps ) {
					Helpers.uninstall(MainUninstaller.this, app);
				}
			}
		}
	};


	@Override
	protected void onResume() {
		super.onResume();
		atSearch.setText("");
		refresh();
	}

	protected void uninstallApp(String name) {
		PHS_AppInfo app = adapter.findApp(name);
		if ( app != null ) {
			Helpers.uninstall(MainUninstaller.this, app);
		} else {
			Logger.logError("Cannot find app: " + name);
		}
	}

	private void refresh() {
		Helpers.showOnlyView(mainFrames, FRAME_LOADING);
		new Thread(new Runnable() {
			@Override
			public void run() {
				final ArrayList<PHS_AppInfo> apps = Helpers.getAppsInfo(getApplicationContext());
				runOnUiThread(new Runnable() {


					@Override
					public void run() {
						Helpers.showOnlyView(mainFrames, FRAME_DATA);
						adapter.setData(apps);
						//searchAdapter = new SearchAdapter(getApplicationContext(), apps);
						//atSearch.setAdapter(searchAdapter);
						ArrayAdapter<String> searchAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, getNames(apps));
						atSearch.setAdapter(searchAdapter);
					}

					private ArrayList<String> getNames(ArrayList<PHS_AppInfo> apps) {
						ArrayList<String> names = new ArrayList<String>();
						for(PHS_AppInfo app : apps) {
							names.add(app.getAppName());
						}
						return names;
					}
				});

			}
		}).start();

	}


}
