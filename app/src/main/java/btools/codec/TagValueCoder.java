package btools.codec;

import btools.util.BitCoderContext;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/* JADX INFO: loaded from: classes.dex */
public final class TagValueCoder {
    private BitCoderContext bc;
    private Map<TagValueSet, TagValueSet> identityMap;
    private int nextTagValueSetId;
    private int pass;
    private Object tree;

    public static final class TreeNode {
        public Object child1;
        public Object child2;
    }

    public void encodeTagValueSet(byte[] data) {
        if (this.pass == 1) {
            return;
        }
        TagValueSet tvsProbe = new TagValueSet(this.nextTagValueSetId);
        tvsProbe.data = data;
        TagValueSet tvs = this.identityMap.get(tvsProbe);
        if (this.pass == 3) {
            this.bc.encodeBounded(tvs.range - 1, tvs.code);
            return;
        }
        if (this.pass == 2) {
            if (tvs == null) {
                tvs = tvsProbe;
                this.nextTagValueSetId++;
                this.identityMap.put(tvs, tvs);
            }
            tvs.frequency++;
        }
    }

    public TagValueWrapper decodeTagValueSet() {
        Object node = this.tree;
        while (node instanceof TreeNode) {
            TreeNode tn = (TreeNode) node;
            boolean nextBit = this.bc.decodeBit();
            node = nextBit ? tn.child2 : tn.child1;
        }
        return (TagValueWrapper) node;
    }

    public void encodeDictionary(BitCoderContext bc) {
        int i = this.pass + 1;
        this.pass = i;
        if (i == 3) {
            if (this.identityMap.size() == 0) {
                int i2 = this.nextTagValueSetId;
                this.nextTagValueSetId = i2 + 1;
                TagValueSet dummy = new TagValueSet(i2);
                this.identityMap.put(dummy, dummy);
            }
            Queue<TagValueSet> queue = new PriorityQueue<>(this.identityMap.size() * 2, new TagValueSet.FrequencyComparator());
            queue.addAll(this.identityMap.values());
            while (queue.size() > 1) {
                int i3 = this.nextTagValueSetId;
                this.nextTagValueSetId = i3 + 1;
                TagValueSet node = new TagValueSet(i3);
                node.child1 = queue.poll();
                node.child2 = queue.poll();
                node.frequency = node.child1.frequency + node.child2.frequency;
                queue.add(node);
            }
            TagValueSet root = queue.poll();
            root.encode(bc, 1, 0);
        }
        this.bc = bc;
    }

    public TagValueCoder(BitCoderContext bc, DataBuffers buffers, TagValueValidator validator) {
        this.tree = decodeTree(bc, buffers, validator);
        this.bc = bc;
    }

    public TagValueCoder() {
        this.identityMap = new HashMap();
    }

    private Object decodeTree(BitCoderContext bc, DataBuffers buffers, TagValueValidator validator) {
        byte[] res;
        boolean isNode = bc.decodeBit();
        if (isNode) {
            TreeNode node = new TreeNode();
            node.child1 = decodeTree(bc, buffers, validator);
            node.child2 = decodeTree(bc, buffers, validator);
            return node;
        }
        byte[] buffer = buffers.tagbuf1;
        BitCoderContext ctx = buffers.bctx1;
        ctx.reset(buffer);
        int inum = 0;
        int lastEncodedInum = 0;
        boolean hasdata = false;
        while (true) {
            int delta = bc.decodeVarBits();
            if (!hasdata && delta == 0) {
                return null;
            }
            if (delta == 0) {
                ctx.encodeVarBits(0);
                int len = ctx.closeAndGetEncodedLength();
                if (validator == null) {
                    res = new byte[len];
                    System.arraycopy(buffer, 0, res, 0, len);
                } else {
                    res = validator.unify(buffer, 0, len);
                }
                int accessType = validator == null ? 2 : validator.accessType(res);
                if (accessType <= 0) {
                    return null;
                }
                TagValueWrapper w = new TagValueWrapper();
                w.data = res;
                w.accessType = accessType;
                return w;
            }
            inum += delta;
            int data = bc.decodeVarBits();
            if (validator == null || validator.isLookupIdxUsed(inum)) {
                hasdata = true;
                ctx.encodeVarBits(inum - lastEncodedInum);
                ctx.encodeVarBits(data);
                lastEncodedInum = inum;
            }
        }
    }

    public static final class TagValueSet {
        public TagValueSet child1;
        public TagValueSet child2;
        public int code;
        public byte[] data;
        public int frequency;
        private int id;
        public int range;

        public TagValueSet(int id) {
            this.id = id;
        }

        public void encode(BitCoderContext bc, int range, int code) {
            this.range = range;
            this.code = code;
            boolean isNode = this.child1 != null;
            bc.encodeBit(isNode);
            if (isNode) {
                this.child1.encode(bc, range << 1, code);
                this.child2.encode(bc, range << 1, code + range);
            } else {
                if (this.data == null) {
                    bc.encodeVarBits(0);
                    return;
                }
                BitCoderContext src = new BitCoderContext(this.data);
                while (true) {
                    int delta = src.decodeVarBits();
                    bc.encodeVarBits(delta);
                    if (delta != 0) {
                        int data = src.decodeVarBits();
                        bc.encodeVarBits(data);
                    } else {
                        return;
                    }
                }
            }
        }

        public boolean equals(Object o) {
            if (!(o instanceof TagValueSet)) {
                return false;
            }
            TagValueSet tvs = (TagValueSet) o;
            if (this.data == null) {
                return tvs.data == null;
            }
            if (tvs.data == null) {
                return this.data == null;
            }
            if (this.data.length != tvs.data.length) {
                return false;
            }
            for (int i = 0; i < this.data.length; i++) {
                if (this.data[i] != tvs.data[i]) {
                    return false;
                }
            }
            return true;
        }

        public int hashCode() {
            if (this.data == null) {
                return 0;
            }
            int h = 17;
            for (int i = 0; i < this.data.length; i++) {
                h = (h << 8) + this.data[i];
            }
            return h;
        }

        public static class FrequencyComparator implements Comparator<TagValueSet> {
            @Override // java.util.Comparator
            public int compare(TagValueSet tvs1, TagValueSet tvs2) {
                if (tvs1.frequency < tvs2.frequency) {
                    return -1;
                }
                if (tvs1.frequency > tvs2.frequency) {
                    return 1;
                }
                if (tvs1.id < tvs2.id) {
                    return -1;
                }
                if (tvs1.id > tvs2.id) {
                    return 1;
                }
                if (tvs1 != tvs2) {
                    throw new RuntimeException("identity corruption!");
                }
                return 0;
            }
        }
    }
}
