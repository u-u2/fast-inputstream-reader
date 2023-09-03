package com.uu2.io;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * https://github.com/u-u2/fast-inputstream-reader
 */
public class FastInputStreamReader implements Closeable {

    private static final int MAX_BUFFER_SIZE = 1 << 16;
    private static final byte LF = 10;
    private static final byte CR = 13;
    private static final byte DASH = 45;
    private static final int[] intCache = new int[58];
    static {
        var v = 0;
        for (int i = 48; i < 58; i++) {
            intCache[i] = v++;
        }
    }

    private final InputStream in;

    private byte[] buf;
    private int availableBytes;
    private int off;
    private int pos;

    /**
     * @param in
     */
    public FastInputStreamReader(InputStream in) {
        this(in, 8192);
    }

    /**
     * @param in
     * @param bufferSize
     */
    public FastInputStreamReader(InputStream in, int bufferSize) {
        this.in = in;
        this.buf = new byte[bufferSize];
        this.availableBytes = 0;
        this.off = 0;
        this.pos = 0;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    /**
    *
    **/
    public boolean hasNext() {
        if (pos < availableBytes) {
            return true;
        }
        if (availableBytes == 0 || pos >= availableBytes) {
            try {
                fill(buf, 0, buf.length);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return availableBytes > -1;
    }

    /**
     * @throws IOException
     */
    public int read() throws IOException {
        if (!hasNext()) {
            return -1;
        }
        return buf[pos++];
    }

    /**
     * @throws IOException
    **/
    public int readInt() throws IOException {
        if (!hasNext()) {
            throw new EOFException();
        }
        var sign = 1;
        var v = 0;
        if (buf[pos] == DASH) {
            sign = -1;
            pos++;
        }
        for (;;) {
            while (pos < availableBytes) {
                if (48 <= buf[pos] && buf[pos] <= 57) {
                    v = (v << 3) + (v << 1);
                    v += intCache[buf[pos++]];
                } else {
                    if (buf[pos] == CR
                            && pos < availableBytes
                            && buf[pos + 1] == LF) {
                        pos += 2;
                    } else {
                        pos++;
                    }
                    off = pos;
                    return v * sign;
                }
            }
            if (!hasNext()) {
                return v * sign;
            }
        }
    }

    /**
     * @return
     * @throws IOException
     */
    public long readLong() throws IOException {
        if (!hasNext()) {
            throw new EOFException();
        }
        var sign = 1;
        var v = 0l;
        if (buf[pos] == DASH) {
            sign = -1;
            pos++;
        }
        for (;;) {
            while (pos < availableBytes) {
                if (48 <= buf[pos] && buf[pos] <= 57) {
                    v = (v << 3) + (v << 1);
                    v += intCache[buf[pos++]];
                } else {
                    if (buf[pos] == CR
                            && pos < availableBytes
                            && buf[pos + 1] == LF) {
                        pos += 2;
                    } else {
                        pos++;
                    }
                    off = pos;
                    return v * sign;
                }
            }
            if (!hasNext()) {
                return v * sign;
            }
        }
    }

    /**
     * @return
     * @throws IOException
     */
    public String readLine() throws IOException {
        if (!hasNext()) {
            throw new EOFException();
        }
        var reloaded = false;
        String s;
        for (;;) {
            while (pos < availableBytes) {
                if (buf[pos] != CR && buf[pos] != LF) {
                    pos++;
                } else {
                    if (buf[pos] == LF) {
                        s = new String(buf, off, pos - off);
                        pos++;
                    } else if (buf[pos] == CR
                            && pos < availableBytes
                            && buf[pos + 1] == LF) {
                        s = new String(buf, off, pos - off);
                        pos += 2;
                    } else {
                        s = new String(buf, off, pos - off);
                        pos++;
                    }
                    off = pos;
                    return s;
                }
            }
            if (!reloaded) {
                var len = availableBytes - off;
                System.arraycopy(buf, off, buf, 0, len);
                fill(buf, len, buf.length - len);
                if (availableBytes == -1) {
                    return new String(buf, 0, len);
                }
                reloaded = true;
            } else {
                extendBuffer();
                reloaded = false;
            }
        }
    }

    /**
     * @throws IOException
     */
    private void fill(byte[] buf, int off, int len) throws IOException {
        if ((availableBytes = in.read(buf, off, len)) > -1) {
            availableBytes += off;
            this.pos = 0;
            this.off = 0;
        }
    }

    /**
     * @throws IOException
     */
    private void extendBuffer() throws IOException {
        if ((buf.length << 1) > MAX_BUFFER_SIZE) {
            throw new UnsupportedOperationException();
        }
        var newBuffer = new byte[buf.length << 1];
        var copyLength = availableBytes - off;
        System.arraycopy(buf, off, newBuffer, 0, copyLength);
        fill(newBuffer, copyLength, newBuffer.length - copyLength);
        buf = newBuffer;
    }

}
