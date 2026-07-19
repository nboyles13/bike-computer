package btools.util;

/* JADX INFO: loaded from: classes.dex */
public interface ProgressListener {
    boolean isCanceled();

    void updateProgress(String str, int i);
}
