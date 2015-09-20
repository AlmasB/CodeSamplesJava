package com.almasb.common.compression;

import java.io.IOException;

/**
 * Most of the implementation comes from 7zip by Igor Pavlov
 *
 */
/*package-private*/ class LZ {

    /*package-private*/ static class InWindow {
        public byte[] _bufferBase; // pointer to buffer with data
        java.io.InputStream _stream;
        int _posLimit; // offset (from _buffer) of first byte when new block
        // reading must be done
        boolean _streamEndWasReached; // if (true) then _streamPos shows real
        // end of stream

        int _pointerToLastSafePosition;

        public int _bufferOffset;

        public int _blockSize; // Size of Allocated memory block
        public int _pos; // offset (from _buffer) of curent byte
        int _keepSizeBefore; // how many BYTEs must be kept in buffer before
        // _pos
        int _keepSizeAfter; // how many BYTEs must be kept buffer after _pos
        public int _streamPos; // offset (from _buffer) of first not read byte
        // from Stream

        public void MoveBlock() {
            int offset = _bufferOffset + _pos - _keepSizeBefore;
            // we need one additional byte, since MovePos moves on 1 byte.
            if (offset > 0)
                offset--;

            int numBytes = _bufferOffset + _streamPos - offset;

            // check negative offset ????
            for (int i = 0; i < numBytes; i++)
                _bufferBase[i] = _bufferBase[offset + i];
            _bufferOffset -= offset;
        }

        public void ReadBlock() throws IOException {
            if (_streamEndWasReached)
                return;
            while (true) {
                int size = (0 - _bufferOffset) + _blockSize - _streamPos;
                if (size == 0)
                    return;
                int numReadBytes = _stream.read(_bufferBase, _bufferOffset
                        + _streamPos, size);
                if (numReadBytes == -1) {
                    _posLimit = _streamPos;
                    int pointerToPostion = _bufferOffset + _posLimit;
                    if (pointerToPostion > _pointerToLastSafePosition)
                        _posLimit = _pointerToLastSafePosition - _bufferOffset;

                    _streamEndWasReached = true;
                    return;
                }
                _streamPos += numReadBytes;
                if (_streamPos >= _pos + _keepSizeAfter)
                    _posLimit = _streamPos - _keepSizeAfter;
            }
        }

        void Free() {
            _bufferBase = null;
        }

        public void Create(int keepSizeBefore, int keepSizeAfter,
                int keepSizeReserv) {
            _keepSizeBefore = keepSizeBefore;
            _keepSizeAfter = keepSizeAfter;
            int blockSize = keepSizeBefore + keepSizeAfter + keepSizeReserv;
            if (_bufferBase == null || _blockSize != blockSize) {
                Free();
                _blockSize = blockSize;
                _bufferBase = new byte[_blockSize];
            }
            _pointerToLastSafePosition = _blockSize - keepSizeAfter;
        }

        public void SetStream(java.io.InputStream stream) {
            _stream = stream;
        }

        public void ReleaseStream() {
            _stream = null;
        }

        public void Init() throws IOException {
            _bufferOffset = 0;
            _pos = 0;
            _streamPos = 0;
            _streamEndWasReached = false;
            ReadBlock();
        }

        public void MovePos() throws IOException {
            _pos++;
            if (_pos > _posLimit) {
                int pointerToPostion = _bufferOffset + _pos;
                if (pointerToPostion > _pointerToLastSafePosition)
                    MoveBlock();
                ReadBlock();
            }
        }

        public byte GetIndexByte(int index) {
            return _bufferBase[_bufferOffset + _pos + index];
        }

        // index + limit have not to exceed _keepSizeAfter;
        public int GetMatchLen(int index, int distance, int limit) {
            if (_streamEndWasReached)
                if ((_pos + index) + limit > _streamPos)
                    limit = _streamPos - (_pos + index);
            distance++;
            // Byte *pby = _buffer + (size_t)_pos + index;
            int pby = _bufferOffset + _pos + index;

            int i;
            for (i = 0; i < limit
                    && _bufferBase[pby + i] == _bufferBase[pby + i - distance]; i++)
                ;
            return i;
        }

        public int GetNumAvailableBytes() {
            return _streamPos - _pos;
        }

        public void ReduceOffsets(int subValue) {
            _bufferOffset += subValue;
            _posLimit -= subValue;
            _pos -= subValue;
            _streamPos -= subValue;
        }
    }

    /*package-private*/ static class BinTree extends InWindow {
        int _cyclicBufferPos;
        int _cyclicBufferSize = 0;
        int _matchMaxLen;

        int[] _son;
        int[] _hash;

        int _cutValue = 0xFF;
        int _hashMask;
        int _hashSizeSum = 0;

        boolean HASH_ARRAY = true;

        static final int kHash2Size = 1 << 10;
        static final int kHash3Size = 1 << 16;
        static final int kBT2HashSize = 1 << 16;
        static final int kStartMaxLen = 1;
        static final int kHash3Offset = kHash2Size;
        static final int kEmptyHashValue = 0;
        static final int kMaxValForNormalize = (1 << 30) - 1;

        int kNumHashDirectBytes = 0;
        int kMinMatchCheck = 4;
        int kFixHashSize = kHash2Size + kHash3Size;

        public void SetType(int numHashBytes) {
            HASH_ARRAY = (numHashBytes > 2);
            if (HASH_ARRAY) {
                kNumHashDirectBytes = 0;
                kMinMatchCheck = 4;
                kFixHashSize = kHash2Size + kHash3Size;
            }
            else {
                kNumHashDirectBytes = 2;
                kMinMatchCheck = 2 + 1;
                kFixHashSize = 0;
            }
        }

        @Override
        public void Init() throws IOException {
            super.Init();
            for (int i = 0; i < _hashSizeSum; i++)
                _hash[i] = kEmptyHashValue;
            _cyclicBufferPos = 0;
            ReduceOffsets(-1);
        }

        @Override
        public void MovePos() throws IOException {
            if (++_cyclicBufferPos >= _cyclicBufferSize)
                _cyclicBufferPos = 0;
            super.MovePos();
            if (_pos == kMaxValForNormalize)
                Normalize();
        }

        public boolean Create(int historySize, int keepAddBufferBefore,
                int matchMaxLen, int keepAddBufferAfter) {
            if (historySize > kMaxValForNormalize - 256)
                return false;
            _cutValue = 16 + (matchMaxLen >> 1);

            int windowReservSize = (historySize + keepAddBufferBefore
                    + matchMaxLen + keepAddBufferAfter) / 2 + 256;

            super.Create(historySize + keepAddBufferBefore, matchMaxLen
                    + keepAddBufferAfter, windowReservSize);

            _matchMaxLen = matchMaxLen;

            int cyclicBufferSize = historySize + 1;
            if (_cyclicBufferSize != cyclicBufferSize)
                _son = new int[(_cyclicBufferSize = cyclicBufferSize) * 2];

            int hs = kBT2HashSize;

            if (HASH_ARRAY) {
                hs = historySize - 1;
                hs |= (hs >> 1);
                hs |= (hs >> 2);
                hs |= (hs >> 4);
                hs |= (hs >> 8);
                hs >>= 1;
                hs |= 0xFFFF;
                if (hs > (1 << 24))
                    hs >>= 1;
                _hashMask = hs;
                hs++;
                hs += kFixHashSize;
            }
            if (hs != _hashSizeSum)
                _hash = new int[_hashSizeSum = hs];
            return true;
        }

        public int GetMatches(int[] distances) throws IOException {
            int lenLimit;
            if (_pos + _matchMaxLen <= _streamPos)
                lenLimit = _matchMaxLen;
            else {
                lenLimit = _streamPos - _pos;
                if (lenLimit < kMinMatchCheck) {
                    MovePos();
                    return 0;
                }
            }

            int offset = 0;
            int matchMinPos = (_pos > _cyclicBufferSize) ? (_pos - _cyclicBufferSize)
                    : 0;
            int cur = _bufferOffset + _pos;
            int maxLen = kStartMaxLen; // to avoid items for len < hashSize;
            int hashValue, hash2Value = 0, hash3Value = 0;

            if (HASH_ARRAY) {
                int temp = CrcTable[_bufferBase[cur] & 0xFF]
                        ^ (_bufferBase[cur + 1] & 0xFF);
                hash2Value = temp & (kHash2Size - 1);
                temp ^= ((int) (_bufferBase[cur + 2] & 0xFF) << 8);
                hash3Value = temp & (kHash3Size - 1);
                hashValue = (temp ^ (CrcTable[_bufferBase[cur + 3] & 0xFF] << 5))
                        & _hashMask;
            }
            else
                hashValue = ((_bufferBase[cur] & 0xFF) ^ ((int) (_bufferBase[cur + 1] & 0xFF) << 8));

            int curMatch = _hash[kFixHashSize + hashValue];
            if (HASH_ARRAY) {
                int curMatch2 = _hash[hash2Value];
                int curMatch3 = _hash[kHash3Offset + hash3Value];
                _hash[hash2Value] = _pos;
                _hash[kHash3Offset + hash3Value] = _pos;
                if (curMatch2 > matchMinPos)
                    if (_bufferBase[_bufferOffset + curMatch2] == _bufferBase[cur]) {
                        distances[offset++] = maxLen = 2;
                        distances[offset++] = _pos - curMatch2 - 1;
                    }
                if (curMatch3 > matchMinPos)
                    if (_bufferBase[_bufferOffset + curMatch3] == _bufferBase[cur]) {
                        if (curMatch3 == curMatch2)
                            offset -= 2;
                        distances[offset++] = maxLen = 3;
                        distances[offset++] = _pos - curMatch3 - 1;
                        curMatch2 = curMatch3;
                    }
                if (offset != 0 && curMatch2 == curMatch) {
                    offset -= 2;
                    maxLen = kStartMaxLen;
                }
            }

            _hash[kFixHashSize + hashValue] = _pos;

            int ptr0 = (_cyclicBufferPos << 1) + 1;
            int ptr1 = (_cyclicBufferPos << 1);

            int len0, len1;
            len0 = len1 = kNumHashDirectBytes;

            if (kNumHashDirectBytes != 0) {
                if (curMatch > matchMinPos) {
                    if (_bufferBase[_bufferOffset + curMatch
                                    + kNumHashDirectBytes] != _bufferBase[cur
                                                                          + kNumHashDirectBytes]) {
                        distances[offset++] = maxLen = kNumHashDirectBytes;
                        distances[offset++] = _pos - curMatch - 1;
                    }
                }
            }

            int count = _cutValue;

            while (true) {
                if (curMatch <= matchMinPos || count-- == 0) {
                    _son[ptr0] = _son[ptr1] = kEmptyHashValue;
                    break;
                }
                int delta = _pos - curMatch;
                int cyclicPos = ((delta <= _cyclicBufferPos) ? (_cyclicBufferPos - delta)
                        : (_cyclicBufferPos - delta + _cyclicBufferSize)) << 1;

                int pby1 = _bufferOffset + curMatch;
                int len = Math.min(len0, len1);
                if (_bufferBase[pby1 + len] == _bufferBase[cur + len]) {
                    while (++len != lenLimit)
                        if (_bufferBase[pby1 + len] != _bufferBase[cur + len])
                            break;
                    if (maxLen < len) {
                        distances[offset++] = maxLen = len;
                        distances[offset++] = delta - 1;
                        if (len == lenLimit) {
                            _son[ptr1] = _son[cyclicPos];
                            _son[ptr0] = _son[cyclicPos + 1];
                            break;
                        }
                    }
                }
                if ((_bufferBase[pby1 + len] & 0xFF) < (_bufferBase[cur + len] & 0xFF)) {
                    _son[ptr1] = curMatch;
                    ptr1 = cyclicPos + 1;
                    curMatch = _son[ptr1];
                    len1 = len;
                }
                else {
                    _son[ptr0] = curMatch;
                    ptr0 = cyclicPos;
                    curMatch = _son[ptr0];
                    len0 = len;
                }
            }
            MovePos();
            return offset;
        }

        public void Skip(int num) throws IOException {
            do {
                int lenLimit;
                if (_pos + _matchMaxLen <= _streamPos)
                    lenLimit = _matchMaxLen;
                else {
                    lenLimit = _streamPos - _pos;
                    if (lenLimit < kMinMatchCheck) {
                        MovePos();
                        continue;
                    }
                }

                int matchMinPos = (_pos > _cyclicBufferSize) ? (_pos - _cyclicBufferSize)
                        : 0;
                int cur = _bufferOffset + _pos;

                int hashValue;

                if (HASH_ARRAY) {
                    int temp = CrcTable[_bufferBase[cur] & 0xFF]
                            ^ (_bufferBase[cur + 1] & 0xFF);
                    int hash2Value = temp & (kHash2Size - 1);
                    _hash[hash2Value] = _pos;
                    temp ^= ((int) (_bufferBase[cur + 2] & 0xFF) << 8);
                    int hash3Value = temp & (kHash3Size - 1);
                    _hash[kHash3Offset + hash3Value] = _pos;
                    hashValue = (temp ^ (CrcTable[_bufferBase[cur + 3] & 0xFF] << 5))
                            & _hashMask;
                }
                else
                    hashValue = ((_bufferBase[cur] & 0xFF) ^ ((int) (_bufferBase[cur + 1] & 0xFF) << 8));

                int curMatch = _hash[kFixHashSize + hashValue];
                _hash[kFixHashSize + hashValue] = _pos;

                int ptr0 = (_cyclicBufferPos << 1) + 1;
                int ptr1 = (_cyclicBufferPos << 1);

                int len0, len1;
                len0 = len1 = kNumHashDirectBytes;

                int count = _cutValue;
                while (true) {
                    if (curMatch <= matchMinPos || count-- == 0) {
                        _son[ptr0] = _son[ptr1] = kEmptyHashValue;
                        break;
                    }

                    int delta = _pos - curMatch;
                    int cyclicPos = ((delta <= _cyclicBufferPos) ? (_cyclicBufferPos - delta)
                            : (_cyclicBufferPos - delta + _cyclicBufferSize)) << 1;

                    int pby1 = _bufferOffset + curMatch;
                    int len = Math.min(len0, len1);
                    if (_bufferBase[pby1 + len] == _bufferBase[cur + len]) {
                        while (++len != lenLimit)
                            if (_bufferBase[pby1 + len] != _bufferBase[cur
                                                                       + len])
                                break;
                        if (len == lenLimit) {
                            _son[ptr1] = _son[cyclicPos];
                            _son[ptr0] = _son[cyclicPos + 1];
                            break;
                        }
                    }
                    if ((_bufferBase[pby1 + len] & 0xFF) < (_bufferBase[cur
                                                                        + len] & 0xFF)) {
                        _son[ptr1] = curMatch;
                        ptr1 = cyclicPos + 1;
                        curMatch = _son[ptr1];
                        len1 = len;
                    }
                    else {
                        _son[ptr0] = curMatch;
                        ptr0 = cyclicPos;
                        curMatch = _son[ptr0];
                        len0 = len;
                    }
                }
                MovePos();
            } while (--num != 0);
        }

        void NormalizeLinks(int[] items, int numItems, int subValue) {
            for (int i = 0; i < numItems; i++) {
                int value = items[i];
                if (value <= subValue)
                    value = kEmptyHashValue;
                else
                    value -= subValue;
                items[i] = value;
            }
        }

        void Normalize() {
            int subValue = _pos - _cyclicBufferSize;
            NormalizeLinks(_son, _cyclicBufferSize * 2, subValue);
            NormalizeLinks(_hash, _hashSizeSum, subValue);
            ReduceOffsets(subValue);
        }

        public void SetCutValue(int cutValue) {
            _cutValue = cutValue;
        }

        private static final int[] CrcTable = new int[256];

        static {
            for (int i = 0; i < 256; i++) {
                int r = i;
                for (int j = 0; j < 8; j++)
                    if ((r & 1) != 0)
                        r = (r >>> 1) ^ 0xEDB88320;
                    else
                        r >>>= 1;
                    CrcTable[i] = r;
            }
        }
    }

    /*package-private*/ static class OutWindow {
        byte[] _buffer;
        int _pos;
        int _windowSize = 0;
        int _streamPos;
        java.io.OutputStream _stream;

        public void Create(int windowSize) {
            if (_buffer == null || _windowSize != windowSize)
                _buffer = new byte[windowSize];
            _windowSize = windowSize;
            _pos = 0;
            _streamPos = 0;
        }

        public void SetStream(java.io.OutputStream stream) throws IOException {
            ReleaseStream();
            _stream = stream;
        }

        public void ReleaseStream() throws IOException {
            Flush();
            _stream = null;
        }

        public void Init(boolean solid) {
            if (!solid) {
                _streamPos = 0;
                _pos = 0;
            }
        }

        public void Flush() throws IOException {
            int size = _pos - _streamPos;
            if (size == 0)
                return;
            _stream.write(_buffer, _streamPos, size);
            if (_pos >= _windowSize)
                _pos = 0;
            _streamPos = _pos;
        }

        public void CopyBlock(int distance, int len) throws IOException {
            int pos = _pos - distance - 1;
            if (pos < 0)
                pos += _windowSize;
            for (; len != 0; len--) {
                if (pos >= _windowSize)
                    pos = 0;
                _buffer[_pos++] = _buffer[pos++];
                if (_pos >= _windowSize)
                    Flush();
            }
        }

        public void PutByte(byte b) throws IOException {
            _buffer[_pos++] = b;
            if (_pos >= _windowSize)
                Flush();
        }

        public byte GetByte(int distance) {
            int pos = _pos - distance - 1;
            if (pos < 0)
                pos += _windowSize;
            return _buffer[pos];
        }
    }
}
