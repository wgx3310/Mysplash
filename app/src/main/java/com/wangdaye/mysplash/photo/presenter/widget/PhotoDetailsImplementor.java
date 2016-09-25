package com.wangdaye.mysplash.photo.presenter.widget;

import android.content.Context;
import android.support.design.widget.Snackbar;

import com.wangdaye.mysplash.Mysplash;
import com.wangdaye.mysplash._common.data.data.PhotoDetails;
import com.wangdaye.mysplash._common.data.service.PhotoService;
import com.wangdaye.mysplash._common.i.model.PhotoDetailsModel;
import com.wangdaye.mysplash._common.i.presenter.PhotoDetailsPresenter;
import com.wangdaye.mysplash._common.i.view.PhotoDetailsView;
import com.wangdaye.mysplash._common.ui.dialog.RateLimitDialog;
import com.wangdaye.mysplash._common.utils.NotificationUtils;
import com.wangdaye.mysplash._common.utils.ValueUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Photo details implementor.
 * */

public class PhotoDetailsImplementor
        implements PhotoDetailsPresenter {
    // model & view.
    private PhotoDetailsModel model;
    private PhotoDetailsView view;

    // data
    private OnRequestPhotoDetailsListener listener;

    /** <br> life cycle. */

    public PhotoDetailsImplementor(PhotoDetailsModel model, PhotoDetailsView view) {
        this.model = model;
        this.view = view;
    }

    /** <br> presenter. */

    @Override
    public void requestPhotoDetails(Context c) {
        view.initRefreshStart();
        listener = new OnRequestPhotoDetailsListener(c);
        model.getService()
                .requestPhotoDetails(model.getPhoto(), listener);
    }

    @Override
    public void cancelRequest() {
        if (listener != null) {
            listener.cancel();
        }
        model.getService().cancel();
    }

    @Override
    public void showExifDescription(Context c, String title, String content) {
        NotificationUtils.showSnackbar(
                title + " : " + content,
                Snackbar.LENGTH_SHORT);
    }

    /** <br> interface. */

    private class OnRequestPhotoDetailsListener implements PhotoService.OnRequestPhotoDetailsListener {
        // data
        private Context c;
        private boolean canceled;
        
        OnRequestPhotoDetailsListener(Context c) {
            this.c = c;
            this.canceled = false;
        }

        public void cancel() {
            canceled = true;
        }

        @Override
        public void onRequestPhotoDetailsSuccess(Call<PhotoDetails> call, Response<PhotoDetails> response) {
            if (canceled) {
                return;
            }
            if (response.isSuccessful() && response.body() != null) {
                ValueUtils.writePhotoCount(
                        c,
                        response.body());
                model.setPhotoDetails(response.body());
                view.drawExif(model.getPhotoDetails());
                view.requestDetailsSuccess();
            } else {
                requestPhotoDetails(c);
                RateLimitDialog.checkAndNotify(
                        Mysplash.getInstance().getLatestActivity(),
                        response.headers().get("X-Ratelimit-Remaining"));
            }
        }

        @Override
        public void onRequestPhotoDetailsFailed(Call<PhotoDetails> call, Throwable t) {
            if (canceled) {
                return;
            }
            requestPhotoDetails(c);
        }
    }
}
