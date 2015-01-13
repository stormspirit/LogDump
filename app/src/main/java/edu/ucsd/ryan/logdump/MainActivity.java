package edu.ucsd.ryan.logdump;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import edu.ucsd.ryan.logdump.service.LogCollectionService;
import edu.ucsd.ryan.logdump.fragments.FilterDialogFragment;
import edu.ucsd.ryan.logdump.fragments.FilterDrawerFragment;
import edu.ucsd.ryan.logdump.fragments.LogViewFragment;
import edu.ucsd.ryan.logdump.utils.FilterDBRunnable;
import edu.ucsd.ryan.logdump.utils.PackageHelper;


public class MainActivity extends ActionBarActivity
        implements FilterDrawerFragment.FilterDrawerCallbacks,
        FilterDialogFragment.FilterDialogListener,
        LogViewFragment.OnLogEntrySelectedListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private FilterDrawerFragment mFilterDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private boolean mBound;
    private LogCollectionService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new FilterDBRunnable(MainActivity.this)).start();

        mFilterDrawerFragment = (FilterDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mFilterDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        bindService(new Intent(this, LogCollectionService.class),
                mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    public void onFilterDrawerItemSelected(String filter) {
        // update the main content by replacing fragments
        mTitle = PackageHelper.getInstance(MainActivity.this).getName(filter);
        LogViewFragment fragment = LogViewFragment.newInstance(filter);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mFilterDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add_section:
                showFilterDialog();
                break;

            case R.id.action_delete_section:
                mFilterDrawerFragment.toggleDeleteButton();
                break;

            case R.id.action_refresh:

                break;

            case R.id.action_settings:
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void showFilterDialog() {
        FilterDialogFragment fragment = FilterDialogFragment.newInstance(R.string.title_filter_dialog);
        fragment.show(getFragmentManager(), "FilterDialogFragment");
    }

    @Override
    public void onFilterDialogPositiveClick(DialogFragment dialog) {
        mFilterDrawerFragment.refreshDrawerList();
        if (mBound) {
            mService.updateFilters();
        }
    }

    @Override
    public void onFilterDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onLogEntrySelected(Cursor cursor) {

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBound = true;
            LogCollectionService.LocalBinder binder = (LogCollectionService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mService = null;
        }
    };
}