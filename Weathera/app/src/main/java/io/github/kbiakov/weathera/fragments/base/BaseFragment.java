package io.github.kbiakov.weathera.fragments.base;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.nostra13.universalimageloader.core.ImageLoader;

import io.github.kbiakov.weathera.R;


public class BaseFragment extends Fragment {

    @Override
    public void onPause() {
        // release UIL thread pool
        ImageLoader.getInstance().stop();

        super.onPause();
    }

    protected void showErrorDialog(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.error))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }

}
