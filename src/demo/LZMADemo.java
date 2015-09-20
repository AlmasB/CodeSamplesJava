package com.almasb.java.framework.demo;

public class LZMADemo {

    /*public class LzmaAlone
    {
        static public class CommandLine
        {
            public static final int kEncode = 0;
            public static final int kDecode = 1;
            public static final int kBenchmak = 2;

            public int Command = -1;
            public int NumBenchmarkPasses = 10;

            public int DictionarySize = 1 << 23;
            public boolean DictionarySizeIsDefined = false;

            public int Lc = 3;
            public int Lp = 0;
            public int Pb = 2;

            public int Fb = 128;
            public boolean FbIsDefined = false;

            public boolean Eos = false;

            public int Algorithm = 2;
            public int MatchFinder = 1;

            public String InFile;
            public String OutFile;

            boolean ParseSwitch(String s)
            {
                if (s.startsWith("d"))
                {
                    DictionarySize = 1 << Integer.parseInt(s.substring(1));
                    DictionarySizeIsDefined = true;
                }
                else if (s.startsWith("fb"))
                {
                    Fb = Integer.parseInt(s.substring(2));
                    FbIsDefined = true;
                }
                else if (s.startsWith("a"))
                    Algorithm = Integer.parseInt(s.substring(1));
                else if (s.startsWith("lc"))
                    Lc = Integer.parseInt(s.substring(2));
                else if (s.startsWith("lp"))
                    Lp = Integer.parseInt(s.substring(2));
                else if (s.startsWith("pb"))
                    Pb = Integer.parseInt(s.substring(2));
                else if (s.startsWith("eos"))
                    Eos = true;
                else if (s.startsWith("mf"))
                {
                    String mfs = s.substring(2);
                    if (mfs.equals("bt2"))
                        MatchFinder = 0;
                    else if (mfs.equals("bt4"))
                        MatchFinder = 1;
                    else if (mfs.equals("bt4b"))
                        MatchFinder = 2;
                    else
                        return false;
                }
                else
                    return false;
                return true;
            }

            public boolean Parse(String[] args) throws Exception
            {
                int pos = 0;
                boolean switchMode = true;
                for (int i = 0; i < args.length; i++)
                {
                    String s = args[i];
                    if (s.length() == 0)
                        return false;
                    if (switchMode)
                    {
                        if (s.compareTo("--") == 0)
                        {
                            switchMode = false;
                            continue;
                        }
                        if (s.charAt(0) == '-')
                        {
                            String sw = s.substring(1).toLowerCase();
                            if (sw.length() == 0)
                                return false;
                            try
                            {
                                if (!ParseSwitch(sw))
                                    return false;
                            }
                            catch (NumberFormatException e)
                            {
                                return false;
                            }
                            continue;
                        }
                    }
                    if (pos == 0)
                    {
                        if (s.equalsIgnoreCase("e"))
                            Command = kEncode;
                        else if (s.equalsIgnoreCase("d"))
                            Command = kDecode;
                        else if (s.equalsIgnoreCase("b"))
                            Command = kBenchmak;
                        else
                            return false;
                    }
                    else if(pos == 1)
                    {
                        if (Command == kBenchmak)
                        {
                            try
                            {
                                NumBenchmarkPasses = Integer.parseInt(s);
                                if (NumBenchmarkPasses < 1)
                                    return false;
                            }
                            catch (NumberFormatException e)
                            {
                                return false;
                            }
                        }
                        else
                            InFile = s;
                    }
                    else if(pos == 2)
                        OutFile = s;
                    else
                        return false;
                    pos++;
                    continue;
                }
                return true;
            }
        }


        static void PrintHelp()
        {
            System.out.println(
                    "\nUsage:  LZMA <e|d> [<switches>...] inputFile outputFile\n" +
                    "  e: encode file\n" +
                    "  d: decode file\n" +
                    "  b: Benchmark\n" +
                    "<Switches>\n" +
                    // "  -a{N}:  set compression mode - [0, 1], default: 1 (max)\n" +
                    "  -d{N}:  set dictionary - [0,28], default: 23 (8MB)\n" +
                    "  -fb{N}: set number of fast bytes - [5, 273], default: 128\n" +
                    "  -lc{N}: set number of literal context bits - [0, 8], default: 3\n" +
                    "  -lp{N}: set number of literal pos bits - [0, 4], default: 0\n" +
                    "  -pb{N}: set number of pos bits - [0, 4], default: 2\n" +
                    "  -mf{MF_ID}: set Match Finder: [bt2, bt4], default: bt4\n" +
                    "  -eos:   write End Of Stream marker\n"
                    );
        }

        public static void main(String[] args) throws Exception
        {
            System.out.println("\nLZMA (Java) 4.61  2008-11-23\n");

            if (args.length < 1)
            {
                PrintHelp();
                return;
            }

            CommandLine params = new CommandLine();
            if (!params.Parse(args))
            {
                System.out.println("\nIncorrect command");
                return;
            }

            if (params.Command == CommandLine.kBenchmak)
            {
                int dictionary = (1 << 21);
                if (params.DictionarySizeIsDefined)
                    dictionary = params.DictionarySize;
                if (params.MatchFinder > 1)
                    throw new Exception("Unsupported match finder");
                SevenZip.LzmaBench.LzmaBenchmark(params.NumBenchmarkPasses, dictionary);
            }
            else if (params.Command == CommandLine.kEncode || params.Command == CommandLine.kDecode)
            {
                java.io.File inFile = new java.io.File(params.InFile);
                java.io.File outFile = new java.io.File(params.OutFile);

                java.io.BufferedInputStream inStream  = new java.io.BufferedInputStream(new java.io.FileInputStream(inFile));
                java.io.BufferedOutputStream outStream = new java.io.BufferedOutputStream(new java.io.FileOutputStream(outFile));

                boolean eos = false;
                if (params.Eos)
                    eos = true;
                if (params.Command == CommandLine.kEncode)
                {
                    SevenZip.Compression.LZMA.Encoder encoder = new SevenZip.Compression.LZMA.Encoder();
                    if (!encoder.SetAlgorithm(params.Algorithm))
                        throw new Exception("Incorrect compression mode");
                    if (!encoder.SetDictionarySize(params.DictionarySize))
                        throw new Exception("Incorrect dictionary size");
                    if (!encoder.SetNumFastBytes(params.Fb))
                        throw new Exception("Incorrect -fb value");
                    if (!encoder.SetMatchFinder(params.MatchFinder))
                        throw new Exception("Incorrect -mf value");
                    if (!encoder.SetLcLpPb(params.Lc, params.Lp, params.Pb))
                        throw new Exception("Incorrect -lc or -lp or -pb value");
                    encoder.SetEndMarkerMode(eos);
                    encoder.WriteCoderProperties(outStream);
                    long fileSize;
                    if (eos)
                        fileSize = -1;
                    else
                        fileSize = inFile.length();
                    for (int i = 0; i < 8; i++)
                        outStream.write((int)(fileSize >>> (8 * i)) & 0xFF);
                    encoder.Code(inStream, outStream, -1, -1, null);
                }
                else
                {
                    int propertiesSize = 5;
                    byte[] properties = new byte[propertiesSize];
                    if (inStream.read(properties, 0, propertiesSize) != propertiesSize)
                        throw new Exception("input .lzma file is too short");
                    SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
                    if (!decoder.SetDecoderProperties(properties))
                        throw new Exception("Incorrect stream properties");
                    long outSize = 0;
                    for (int i = 0; i < 8; i++)
                    {
                        int v = inStream.read();
                        if (v < 0)
                            throw new Exception("Can't read stream size");
                        outSize |= ((long)v) << (8 * i);
                    }
                    if (!decoder.Code(inStream, outStream, outSize))
                        throw new Exception("Error in data stream");
                }
                outStream.flush();
                outStream.close();
                inStream.close();
            }
            else
                throw new Exception("Incorrect command");
            return;
        }
    }*/

    /*

public class LzmaBench
{
    static final int kAdditionalSize = (1 << 21);
    static final int kCompressedAdditionalSize = (1 << 10);

    static class CRandomGenerator
    {
        int A1;
        int A2;
        public CRandomGenerator() { Init(); }
        public void Init() { A1 = 362436069; A2 = 521288629; }
        public int GetRnd()
        {
            return
                ((A1 = 36969 * (A1 & 0xffff) + (A1 >>> 16)) << 16) ^
                ((A2 = 18000 * (A2 & 0xffff) + (A2 >>> 16)));
        }
    };

    static class CBitRandomGenerator
    {
        CRandomGenerator RG = new CRandomGenerator();
        int Value;
        int NumBits;
        public void Init()
        {
            Value = 0;
            NumBits = 0;
        }
        public int GetRnd(int numBits)
        {
            int result;
            if (NumBits > numBits)
            {
                result = Value & ((1 << numBits) - 1);
                Value >>>= numBits;
                NumBits -= numBits;
                return result;
            }
            numBits -= NumBits;
            result = (Value << numBits);
            Value = RG.GetRnd();
            result |= Value & (((int)1 << numBits) - 1);
            Value >>>= numBits;
            NumBits = 32 - numBits;
            return result;
        }
    };

    static class CBenchRandomGenerator
    {
        CBitRandomGenerator RG = new CBitRandomGenerator();
        int Pos;
        int Rep0;

        public int BufferSize;
        public byte[] Buffer = null;

        public CBenchRandomGenerator() { }
        public void Set(int bufferSize)
        {
            Buffer = new byte[bufferSize];
            Pos = 0;
            BufferSize = bufferSize;
        }
        int GetRndBit() { return RG.GetRnd(1); }
        int GetLogRandBits(int numBits)
        {
            int len = RG.GetRnd(numBits);
            return RG.GetRnd((int)len);
        }
        int GetOffset()
        {
            if (GetRndBit() == 0)
                return GetLogRandBits(4);
            return (GetLogRandBits(4) << 10) | RG.GetRnd(10);
        }
        int GetLen1() { return RG.GetRnd(1 + (int)RG.GetRnd(2)); }
        int GetLen2() { return RG.GetRnd(2 + (int)RG.GetRnd(2)); }
        public void Generate()
        {
            RG.Init();
            Rep0 = 1;
            while (Pos < BufferSize)
            {
                if (GetRndBit() == 0 || Pos < 1)
                    Buffer[Pos++] = (byte)(RG.GetRnd(8));
                else
                {
                    int len;
                    if (RG.GetRnd(3) == 0)
                        len = 1 + GetLen1();
                    else
                    {
                        do
                            Rep0 = GetOffset();
                        while (Rep0 >= Pos);
                        Rep0++;
                        len = 2 + GetLen2();
                    }
                    for (int i = 0; i < len && Pos < BufferSize; i++, Pos++)
                        Buffer[Pos] = Buffer[Pos - Rep0];
                }
            }
        }
    };

    static class CrcOutStream extends java.io.OutputStream
    {
        public CRC CRC = new CRC();

        public void Init()
        {
            CRC.Init();
        }
        public int GetDigest()
        {
            return CRC.GetDigest();
        }
        public void write(byte[] b)
        {
            CRC.Update(b);
        }
        public void write(byte[] b, int off, int len)
        {
            CRC.Update(b, off, len);
        }
        public void write(int b)
        {
            CRC.UpdateByte(b);
        }
    };

    static class MyOutputStream extends java.io.OutputStream
    {
        byte[] _buffer;
        int _size;
        int _pos;

        public MyOutputStream(byte[] buffer)
        {
            _buffer = buffer;
            _size = _buffer.length;
        }

        public void reset()
        {
            _pos = 0;
        }

        public void write(int b) throws IOException
        {
            if (_pos >= _size)
                throw new IOException("Error");
            _buffer[_pos++] = (byte)b;
        }

        public int size()
        {
            return _pos;
        }
    };

    static class MyInputStream extends java.io.InputStream
    {
        byte[] _buffer;
        int _size;
        int _pos;

        public MyInputStream(byte[] buffer, int size)
        {
            _buffer = buffer;
            _size = size;
        }

        public void reset()
        {
            _pos = 0;
        }

        public int read()
        {
            if (_pos >= _size)
                return -1;
            return _buffer[_pos++] & 0xFF;
        }
    };

    static class CProgressInfo implements ICodeProgress
    {
        public long ApprovedStart;
        public long InSize;
        public long Time;
        public void Init()
        { InSize = 0; }
        public void SetProgress(long inSize, long outSize)
        {
            if (inSize >= ApprovedStart && InSize == 0)
            {
                Time = System.currentTimeMillis();
                InSize = inSize;
            }
        }
    }
    static final int kSubBits = 8;

    static int GetLogSize(int size)
    {
        for (int i = kSubBits; i < 32; i++)
            for (int j = 0; j < (1 << kSubBits); j++)
                if (size <= ((1) << i) + (j << (i - kSubBits)))
                    return (i << kSubBits) + j;
        return (32 << kSubBits);
    }

    static long MyMultDiv64(long value, long elapsedTime)
    {
        long freq = 1000; // ms
        long elTime = elapsedTime;
        while (freq > 1000000)
        {
            freq >>>= 1;
            elTime >>>= 1;
        }
        if (elTime == 0)
            elTime = 1;
        return value * freq / elTime;
    }

    static long GetCompressRating(int dictionarySize, long elapsedTime, long size)
    {
        long t = GetLogSize(dictionarySize) - (18 << kSubBits);
        long numCommandsForOne = 1060 + ((t * t * 10) >> (2 * kSubBits));
        long numCommands = (long)(size) * numCommandsForOne;
        return MyMultDiv64(numCommands, elapsedTime);
    }

    static long GetDecompressRating(long elapsedTime, long outSize, long inSize)
    {
        long numCommands = inSize * 220 + outSize * 20;
        return MyMultDiv64(numCommands, elapsedTime);
    }

    static long GetTotalRating(
            int dictionarySize,
            long elapsedTimeEn, long sizeEn,
            long elapsedTimeDe,
            long inSizeDe, long outSizeDe)
    {
        return (GetCompressRating(dictionarySize, elapsedTimeEn, sizeEn) +
                GetDecompressRating(elapsedTimeDe, inSizeDe, outSizeDe)) / 2;
    }

    static void PrintValue(long v)
    {
        String s = "";
        s += v;
        for (int i = 0; i + s.length() < 6; i++)
            System.out.print(" ");
        System.out.print(s);
    }

    static void PrintRating(long rating)
    {
        PrintValue(rating / 1000000);
        System.out.print(" MIPS");
    }

    static void PrintResults(
            int dictionarySize,
            long elapsedTime,
            long size,
            boolean decompressMode, long secondSize)
    {
        long speed = MyMultDiv64(size, elapsedTime);
        PrintValue(speed / 1024);
        System.out.print(" KB/s  ");
        long rating;
        if (decompressMode)
            rating = GetDecompressRating(elapsedTime, size, secondSize);
        else
            rating = GetCompressRating(dictionarySize, elapsedTime, size);
        PrintRating(rating);
    }

    static public int LzmaBenchmark(int numIterations, int dictionarySize) throws Exception
    {
        if (numIterations <= 0)
            return 0;
        if (dictionarySize < (1 << 18))
        {
            System.out.println("\nError: dictionary size for benchmark must be >= 18 (256 KB)");
            return 1;
        }
        System.out.print("\n       Compressing                Decompressing\n\n");

        SevenZip.Compression.LZMA.Encoder encoder = new SevenZip.Compression.LZMA.Encoder();
        SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();

        if (!encoder.SetDictionarySize(dictionarySize))
            throw new Exception("Incorrect dictionary size");

        int kBufferSize = dictionarySize + kAdditionalSize;
        int kCompressedBufferSize = (kBufferSize / 2) + kCompressedAdditionalSize;

        ByteArrayOutputStream propStream = new ByteArrayOutputStream();
        encoder.WriteCoderProperties(propStream);
        byte[] propArray = propStream.toByteArray();
        decoder.SetDecoderProperties(propArray);

        CBenchRandomGenerator rg = new CBenchRandomGenerator();

        rg.Set(kBufferSize);
        rg.Generate();
        CRC crc = new CRC();
        crc.Init();
        crc.Update(rg.Buffer, 0, rg.BufferSize);

        CProgressInfo progressInfo = new CProgressInfo();
        progressInfo.ApprovedStart = dictionarySize;

        long totalBenchSize = 0;
        long totalEncodeTime = 0;
        long totalDecodeTime = 0;
        long totalCompressedSize = 0;

        MyInputStream inStream = new MyInputStream(rg.Buffer, rg.BufferSize);

        byte[] compressedBuffer = new byte[kCompressedBufferSize];
        MyOutputStream compressedStream = new MyOutputStream(compressedBuffer);
        CrcOutStream crcOutStream = new CrcOutStream();
        MyInputStream inputCompressedStream = null;
        int compressedSize = 0;
        for (int i = 0; i < numIterations; i++)
        {
            progressInfo.Init();
            inStream.reset();
            compressedStream.reset();
            encoder.Code(inStream, compressedStream, -1, -1, progressInfo);
            long encodeTime = System.currentTimeMillis() - progressInfo.Time;

            if (i == 0)
            {
                compressedSize = compressedStream.size();
                inputCompressedStream = new MyInputStream(compressedBuffer, compressedSize);
            }
            else if (compressedSize != compressedStream.size())
                throw (new Exception("Encoding error"));

            if (progressInfo.InSize == 0)
                throw (new Exception("Internal ERROR 1282"));

            long decodeTime = 0;
            for (int j = 0; j < 2; j++)
            {
                inputCompressedStream.reset();
                crcOutStream.Init();

                long outSize = kBufferSize;
                long startTime = System.currentTimeMillis();
                if (!decoder.Code(inputCompressedStream, crcOutStream, outSize))
                    throw (new Exception("Decoding Error"));;
                decodeTime = System.currentTimeMillis() - startTime;
                if (crcOutStream.GetDigest() != crc.GetDigest())
                    throw (new Exception("CRC Error"));
            }
            long benchSize = kBufferSize - (long)progressInfo.InSize;
            PrintResults(dictionarySize, encodeTime, benchSize, false, 0);
            System.out.print("     ");
            PrintResults(dictionarySize, decodeTime, kBufferSize, true, compressedSize);
            System.out.println();

            totalBenchSize += benchSize;
            totalEncodeTime += encodeTime;
            totalDecodeTime += decodeTime;
            totalCompressedSize += compressedSize;
        }
        System.out.println("---------------------------------------------------");
        PrintResults(dictionarySize, totalEncodeTime, totalBenchSize, false, 0);
        System.out.print("     ");
        PrintResults(dictionarySize, totalDecodeTime,
                kBufferSize * (long)numIterations, true, totalCompressedSize);
        System.out.println("    Average");
        return 0;
    }
}*/


}
