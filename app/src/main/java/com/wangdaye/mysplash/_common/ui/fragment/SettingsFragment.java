package com.wangdaye.mysplash._common.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash.R;
import com.wangdaye.mysplash._common.data.api.PhotoApi;
import com.wangdaye.mysplash._common.data.tools.DownloadManager;
import com.wangdaye.mysplash._common.ui.dialog.ConfirmRebootDialog;
import com.wangdaye.mysplash._common.utils.BackToTopUtils;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.ValueUtils;
import com.wangdaye.mysplash.main.view.activity.MainActivity;

/**
 * Settings fragment.
 * */

public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    /** <br> life cycle. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.perference);
        initView();
    }

    /** <br> UI. */

    private void initView() {
        initBasicPart();
        initFilterPart();
        initDownloadPart();
    }

    private void initBasicPart() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // back to top.
        ListPreference backToTop = (ListPreference) findPreference(getString(R.string.key_back_to_top));
        String backToTopValue = sharedPreferences.getString(
                getString(R.string.key_back_to_top),
                "all");
        String backToTopName = ValueUtils.getBackToTopName(getActivity(), backToTopValue);
        backToTop.setSummary("Now : " + backToTopName);
        backToTop.setOnPreferenceChangeListener(this);

        // language.
        ListPreference language = (ListPreference) findPreference(getString(R.string.key_language));
        String languageValue = sharedPreferences.getString(
                getString(R.string.key_language),
                "follow_system");
        String languageName = ValueUtils.getLanguageName(getActivity(), languageValue);
        language.setSummary("Now : " + languageName);
        language.setOnPreferenceChangeListener(this);
    }

    private void initFilterPart() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // default order.
        ListPreference defaultOrder = (ListPreference) findPreference(getString(R.string.key_default_photo_order));
        String orderValue = sharedPreferences.getString(
                getString(R.string.key_default_photo_order),
                PhotoApi.ORDER_BY_LATEST);
        String orderName = ValueUtils.getOrderName(getActivity(), orderValue);
        defaultOrder.setSummary("Now : " + orderName);
        defaultOrder.setOnPreferenceChangeListener(this);

        // collection type.
        ListPreference collectionType = (ListPreference) findPreference(getString(R.string.key_default_collection_type));
        String typeValue = sharedPreferences.getString(
                getString(R.string.key_default_collection_type),
                "featured");
        String valueName = ValueUtils.getCollectionName(getActivity(), typeValue);
        collectionType.setSummary("Now : " + valueName);
        collectionType.setOnPreferenceChangeListener(this);
    }

    private void initDownloadPart() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // download scale.
        ListPreference downloadScale = (ListPreference) findPreference(getString(R.string.key_download_scale));
        String scaleValue = sharedPreferences.getString(
                getString(R.string.key_download_scale),
                "compact");
        String scaleName = ValueUtils.getScaleName(getActivity(), scaleValue);
        downloadScale.setSummary("Now : " + scaleName);
        downloadScale.setOnPreferenceChangeListener(this);
    }

    private void showRebootSnackbar() {
        NotificationUtils.showActionSnackbar(
                getString(R.string.feedback_notify_restart),
                getString(R.string.restart),
                Snackbar.LENGTH_SHORT,
                rebootListener);
    }

    /** <br> interface. */

    // on preference changed listener.

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        if (preference.getKey().equals(getString(R.string.key_back_to_top))) {
            // back to top.
            String backType = ValueUtils.getBackToTopName(getActivity(), (String) o);
            preference.setSummary("Now : " + backType);
            BackToTopUtils.getInstance(getActivity()).changeBackValue((String) o);
        } else if (preference.getKey().equals(getString(R.string.key_language))) {
            // language.
            String language = ValueUtils.getLanguageName(getActivity(), (String) o);
            preference.setSummary("Now : " + language);
            showRebootSnackbar();
        } else if (preference.getKey().equals(getString(R.string.key_default_photo_order))) {
            // default order.
            String order = ValueUtils.getOrderName(getActivity(), (String) o);
            preference.setSummary("Now : " + order);
            showRebootSnackbar();
        } else if (preference.getKey().equals(getString(R.string.key_default_collection_type))) {
            // collection type.
            String type = ValueUtils.getCollectionName(getActivity(), (String) o);
            preference.setSummary("Now : " + type);
            showRebootSnackbar();
        } else if (preference.getKey().equals(getString(R.string.key_download_scale))) {
            // download scale.
            String scale = ValueUtils.getScaleName(getActivity(), (String) o);
            preference.setSummary("Now : " + scale);
        }
        return true;
    }

    // on action click listener.

    private View.OnClickListener rebootListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (DownloadManager.getInstance().getMissionList().size() > 0) {
                ConfirmRebootDialog dialog = ConfirmRebootDialog.buildDialog(
                        ConfirmRebootDialog.RESTART_APP_TYPE);
                dialog.setOnConfirmRebootListener(new ConfirmRebootDialog.OnConfirmRebootListener() {
                    @Override
                    public void onConfirm() {
                        MainActivity a = Mysplash.getInstance().getMainActivity();
                        if (a != null) {
                            a.reboot();
                        }
                    }
                });
                dialog.show(getFragmentManager(), null);
            } else {
                MainActivity a = Mysplash.getInstance().getMainActivity();
                if (a != null) {
                    a.reboot();
                }
            }
        }
    };
}
