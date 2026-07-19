package btools.codec;

/* JADX INFO: loaded from: classes.dex */
public interface TagValueValidator {
    int accessType(byte[] bArr);

    boolean checkStartWay(byte[] bArr);

    boolean isLookupIdxUsed(int i);

    void setDecodeForbidden(boolean z);

    byte[] unify(byte[] bArr, int i, int i2);
}
