package org.esa.snap.remote.products.repository.listener;

/**
 * Created by jcoravu on 19/8/2019.
 */
public class DownloadProductProgressListener implements ro.cs.tao.ProgressListener {

    private final ProgressListener progressListener;

    public DownloadProductProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public void started(String taskName) {
    }

    @Override
    public void subActivityStarted(String subTaskName) {
    }

    @Override
    public void subActivityEnded(String subTaskName) {
    }

    @Override
    public void ended() {
    }

    @Override
    public void notifyProgress(double progressValue) {
        short value = (short)(progressValue * 100.0d);
        this.progressListener.notifyProgress(value);
    }

    @Override
    public void notifyProgress(String subTaskName, double subTaskProgress) {
    }

    @Override
    public void notifyProgress(String subTaskName, double subTaskProgress, double overallProgress) {
    }
}