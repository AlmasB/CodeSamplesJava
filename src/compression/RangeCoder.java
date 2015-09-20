package com.almasb.common.compression;

import java.io.IOException;

/* package-private */class RangeCoder {

    /* package-private */static class BitTreeEncoder {
        short[] Models;
        int NumBitLevels;

        public BitTreeEncoder(int numBitLevels) {
            NumBitLevels = numBitLevels;
            Models = new short[1 << numBitLevels];
        }

        public void Init() {
            Decoder.InitBitModels(Models);
        }

        public void Encode(Encoder rangeEncoder, int symbol) throws IOException {
            int m = 1;
            for (int bitIndex = NumBitLevels; bitIndex != 0;) {
                bitIndex--;
                int bit = (symbol >>> bitIndex) & 1;
                rangeEncoder.Encode(Models, m, bit);
                m = (m << 1) | bit;
            }
        }

        public void ReverseEncode(Encoder rangeEncoder, int symbol)
                throws IOException {
            int m = 1;
            for (int i = 0; i < NumBitLevels; i++) {
                int bit = symbol & 1;
                rangeEncoder.Encode(Models, m, bit);
                m = (m << 1) | bit;
                symbol >>= 1;
            }
        }

        public int GetPrice(int symbol) {
            int price = 0;
            int m = 1;
            for (int bitIndex = NumBitLevels; bitIndex != 0;) {
                bitIndex--;
                int bit = (symbol >>> bitIndex) & 1;
                price += Encoder.GetPrice(Models[m], bit);
                m = (m << 1) + bit;
            }
            return price;
        }

        public int ReverseGetPrice(int symbol) {
            int price = 0;
            int m = 1;
            for (int i = NumBitLevels; i != 0; i--) {
                int bit = symbol & 1;
                symbol >>>= 1;
                price += Encoder.GetPrice(Models[m], bit);
                m = (m << 1) | bit;
            }
            return price;
        }

        public static int ReverseGetPrice(short[] Models, int startIndex,
                int NumBitLevels, int symbol) {
            int price = 0;
            int m = 1;
            for (int i = NumBitLevels; i != 0; i--) {
                int bit = symbol & 1;
                symbol >>>= 1;
                price += Encoder.GetPrice(Models[startIndex + m], bit);
                m = (m << 1) | bit;
            }
            return price;
        }

        public static void ReverseEncode(short[] Models, int startIndex,
                Encoder rangeEncoder, int NumBitLevels, int symbol)
                throws IOException {
            int m = 1;
            for (int i = 0; i < NumBitLevels; i++) {
                int bit = symbol & 1;
                rangeEncoder.Encode(Models, startIndex + m, bit);
                m = (m << 1) | bit;
                symbol >>= 1;
            }
        }
    }

    /* package-private */static class BitTreeDecoder {
        short[] Models;
        int NumBitLevels;

        public BitTreeDecoder(int numBitLevels) {
            NumBitLevels = numBitLevels;
            Models = new short[1 << numBitLevels];
        }

        public void Init() {
            Decoder.InitBitModels(Models);
        }

        public int Decode(Decoder rangeDecoder) throws java.io.IOException {
            int m = 1;
            for (int bitIndex = NumBitLevels; bitIndex != 0; bitIndex--)
                m = (m << 1) + rangeDecoder.DecodeBit(Models, m);
            return m - (1 << NumBitLevels);
        }

        public int ReverseDecode(Decoder rangeDecoder)
                throws java.io.IOException {
            int m = 1;
            int symbol = 0;
            for (int bitIndex = 0; bitIndex < NumBitLevels; bitIndex++) {
                int bit = rangeDecoder.DecodeBit(Models, m);
                m <<= 1;
                m += bit;
                symbol |= (bit << bitIndex);
            }
            return symbol;
        }

        public static int ReverseDecode(short[] Models, int startIndex,
                Decoder rangeDecoder, int NumBitLevels)
                throws java.io.IOException {
            int m = 1;
            int symbol = 0;
            for (int bitIndex = 0; bitIndex < NumBitLevels; bitIndex++) {
                int bit = rangeDecoder.DecodeBit(Models, startIndex + m);
                m <<= 1;
                m += bit;
                symbol |= (bit << bitIndex);
            }
            return symbol;
        }
    }

    /* package-private */static class Encoder {
        static final int kTopMask = ~((1 << 24) - 1);

        static final int kNumBitModelTotalBits = 11;
        static final int kBitModelTotal = (1 << kNumBitModelTotalBits);
        static final int kNumMoveBits = 5;

        java.io.OutputStream Stream;

        long Low;
        int Range;
        int _cacheSize;
        int _cache;

        long _position;

        public void SetStream(java.io.OutputStream stream) {
            Stream = stream;
        }

        public void ReleaseStream() {
            Stream = null;
        }

        public void Init() {
            _position = 0;
            Low = 0;
            Range = -1;
            _cacheSize = 1;
            _cache = 0;
        }

        public void FlushData() throws IOException {
            for (int i = 0; i < 5; i++)
                ShiftLow();
        }

        public void FlushStream() throws IOException {
            Stream.flush();
        }

        public void ShiftLow() throws IOException {
            int LowHi = (int) (Low >>> 32);
            if (LowHi != 0 || Low < 0xFF000000L) {
                _position += _cacheSize;
                int temp = _cache;
                do {
                    Stream.write(temp + LowHi);
                    temp = 0xFF;
                } while (--_cacheSize != 0);
                _cache = (((int) Low) >>> 24);
            }
            _cacheSize++;
            Low = (Low & 0xFFFFFF) << 8;
        }

        public void EncodeDirectBits(int v, int numTotalBits)
                throws IOException {
            for (int i = numTotalBits - 1; i >= 0; i--) {
                Range >>>= 1;
                if (((v >>> i) & 1) == 1)
                    Low += Range;
                if ((Range & Encoder.kTopMask) == 0) {
                    Range <<= 8;
                    ShiftLow();
                }
            }
        }

        public long GetProcessedSizeAdd() {
            return _cacheSize + _position + 4;
        }

        static final int kNumMoveReducingBits = 2;
        public static final int kNumBitPriceShiftBits = 6;

        public static void InitBitModels(short[] probs) {
            for (int i = 0; i < probs.length; i++)
                probs[i] = (kBitModelTotal >>> 1);
        }

        public void Encode(short[] probs, int index, int symbol)
                throws IOException {
            int prob = probs[index];
            int newBound = (Range >>> kNumBitModelTotalBits) * prob;
            if (symbol == 0) {
                Range = newBound;
                probs[index] = (short) (prob + ((kBitModelTotal - prob) >>> kNumMoveBits));
            }
            else {
                Low += (newBound & 0xFFFFFFFFL);
                Range -= newBound;
                probs[index] = (short) (prob - ((prob) >>> kNumMoveBits));
            }
            if ((Range & kTopMask) == 0) {
                Range <<= 8;
                ShiftLow();
            }
        }

        private static int[] ProbPrices = new int[kBitModelTotal >>> kNumMoveReducingBits];

        static {
            int kNumBits = (kNumBitModelTotalBits - kNumMoveReducingBits);
            for (int i = kNumBits - 1; i >= 0; i--) {
                int start = 1 << (kNumBits - i - 1);
                int end = 1 << (kNumBits - i);
                for (int j = start; j < end; j++)
                    ProbPrices[j] = (i << kNumBitPriceShiftBits)
                            + (((end - j) << kNumBitPriceShiftBits) >>> (kNumBits
                                    - i - 1));
            }
        }

        static public int GetPrice(int Prob, int symbol) {
            return ProbPrices[(((Prob - symbol) ^ ((-symbol))) & (kBitModelTotal - 1)) >>> kNumMoveReducingBits];
        }

        static public int GetPrice0(int Prob) {
            return ProbPrices[Prob >>> kNumMoveReducingBits];
        }

        static public int GetPrice1(int Prob) {
            return ProbPrices[(kBitModelTotal - Prob) >>> kNumMoveReducingBits];
        }
    }

    /* package-private */static class Decoder {
        static final int kTopMask = ~((1 << 24) - 1);

        static final int kNumBitModelTotalBits = 11;
        static final int kBitModelTotal = (1 << kNumBitModelTotalBits);
        static final int kNumMoveBits = 5;

        int Range;
        int Code;

        java.io.InputStream Stream;

        public final void SetStream(java.io.InputStream stream) {
            Stream = stream;
        }

        public final void ReleaseStream() {
            Stream = null;
        }

        public final void Init() throws IOException {
            Code = 0;
            Range = -1;
            for (int i = 0; i < 5; i++)
                Code = (Code << 8) | Stream.read();
        }

        public final int DecodeDirectBits(int numTotalBits) throws IOException {
            int result = 0;
            for (int i = numTotalBits; i != 0; i--) {
                Range >>>= 1;
                int t = ((Code - Range) >>> 31);
                Code -= Range & (t - 1);
                result = (result << 1) | (1 - t);

                if ((Range & kTopMask) == 0) {
                    Code = (Code << 8) | Stream.read();
                    Range <<= 8;
                }
            }
            return result;
        }

        public int DecodeBit(short[] probs, int index) throws IOException {
            int prob = probs[index];
            int newBound = (Range >>> kNumBitModelTotalBits) * prob;
            if ((Code ^ 0x80000000) < (newBound ^ 0x80000000)) {
                Range = newBound;
                probs[index] = (short) (prob + ((kBitModelTotal - prob) >>> kNumMoveBits));
                if ((Range & kTopMask) == 0) {
                    Code = (Code << 8) | Stream.read();
                    Range <<= 8;
                }
                return 0;
            }
            else {
                Range -= newBound;
                Code -= newBound;
                probs[index] = (short) (prob - ((prob) >>> kNumMoveBits));
                if ((Range & kTopMask) == 0) {
                    Code = (Code << 8) | Stream.read();
                    Range <<= 8;
                }
                return 1;
            }
        }

        public static void InitBitModels(short[] probs) {
            for (int i = 0; i < probs.length; i++)
                probs[i] = (kBitModelTotal >>> 1);
        }
    }
}
