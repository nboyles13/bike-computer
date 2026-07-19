package btools.codec;

/* JADX INFO: loaded from: classes.dex */
public interface WaypointMatcher {
    void end();

    boolean hasMatch(int i, int i2);

    boolean start(int i, int i2, int i3, int i4, boolean z);

    void transferNode(int i, int i2);
}
