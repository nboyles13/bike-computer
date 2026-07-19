package btools.util;

/* JADX INFO: loaded from: classes.dex */
public abstract class LruMapNode {
    public int hash;
    LruMapNode next;
    LruMapNode nextInBin;
    LruMapNode previous;
}
