package io.github.kbiakov.weathera;

import android.app.Application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.splunk.mint.Mint;

import io.github.kbiakov.weathera.helpers.db.HelperFactory;


public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // collect information about crashes
        Mint.initAndStartSession(this, getString(R.string.mint_api_key));

        // for ORMLite database
        HelperFactory.setHelper(this);

        // init Universal Image Loader to async image fetching
        initUIL();
    }

    private void initUIL() {
        DisplayImageOptions options = new DisplayImageOptions
                .Builder()
                .cacheInMemory(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .defaultDisplayImageOptions(options)
                .build();

        ImageLoader.getInstance().init(config);
    }

    /*
    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
    }
    */

}
