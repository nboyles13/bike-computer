package btools.codec;

/* JADX INFO: loaded from: classes.dex */
public final class NoisyDiffCoder {
    private StatCoderContext bc;
    private int[] freqs;
    private int noisybits;
    private int pass;
    private int tot;

    public NoisyDiffCoder(StatCoderContext bc) {
        this.noisybits = bc.decodeVarBits();
        this.bc = bc;
    }

    public NoisyDiffCoder() {
    }

    public void encodeSignedValue(int value) {
        if (this.pass == 3) {
            this.bc.encodeNoisyDiff(value, this.noisybits);
        } else if (this.pass == 2) {
            count(value < 0 ? -value : value);
        }
    }

    public int decodeSignedValue() {
        return this.bc.decodeNoisyDiff(this.noisybits);
    }

    public void encodeDictionary(StatCoderContext bc) {
        int i = this.pass + 1;
        this.pass = i;
        if (i == 3) {
            int i2 = 0;
            while (true) {
                this.noisybits = i2;
                if (this.noisybits >= 14 || this.tot <= 0 || this.freqs[this.noisybits] < (this.tot >> 1)) {
                    break;
                } else {
                    i2 = this.noisybits + 1;
                }
            }
            bc.encodeVarBits(this.noisybits);
        }
        this.bc = bc;
    }

    private void count(int value) {
        if (this.freqs == null) {
            this.freqs = new int[14];
        }
        int bm = 1;
        for (int i = 0; i < 14 && value >= bm; i++) {
            int[] iArr = this.freqs;
            iArr[i] = iArr[i] + 1;
            bm <<= 1;
        }
        this.tot++;
    }
}
