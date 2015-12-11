package com.sanjusingh.movies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            String[] posters={
                    "http://image.tmdb.org/t/p/w185//z2sJd1OvAGZLxgjBdSnQoLCfn3M.jpg",
                    "http://image.tmdb.org/t/p/w185//D6e8RJf2qUstnfkTslTXNTUAlT.jpg",
                    "http://image.tmdb.org/t/p/w185//jjBgi2r5cRt36xF6iNUEhzscEcb.jpg",
                    "http://image.tmdb.org/t/p/w185//5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg",
                    "http://image.tmdb.org/t/p/w185//fYzpM9GmpBlIC893fNjoWCwE24H.jpg",
                    "http://image.tmdb.org/t/p/w185//s5uMY8ooGRZOL0oe4sIvnlTsYQO.jpg",
                    "http://image.tmdb.org/t/p/w185//nN4cEJMHJHbJBsp3vvvhtNWLGqg.jpg",
                    "http://image.tmdb.org/t/p/w185//kqjL17yufvn9OVLyXYpvtyrFfak.jpg",
                    "http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg",
                    "http://image.tmdb.org/t/p/w185//mSvpKOWbyFtLro9BjfEGqUw5dXE.jpg",
                    "http://image.tmdb.org/t/p/w185//g23cs30dCMiG4ldaoVNP1ucjs6.jpg",
                    "http://image.tmdb.org/t/p/w185//A7HtCxFe7Ms8H7e7o2zawppbuDT.jpg"
            };

            List<String> postersUrl = new ArrayList<String>(Arrays.asList(posters));

            ImageAdapter imageAdapter = new ImageAdapter(getActivity(), postersUrl);

            GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
            gridView.setAdapter(imageAdapter);

            return rootView;
        }
    }
}
