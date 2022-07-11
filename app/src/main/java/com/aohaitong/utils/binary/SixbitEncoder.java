/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aohaitong.utils.binary;

/**
 * Class to encode into a six bit string
 */
public class SixbitEncoder {

    /**
     * The internal representation as binary arrya
     */
    private final BinArray binArray = new BinArray();

    /**
     * The number of padding bits
     */
    private int padBits;

    /**
     * Add a value using bits number of bits
     *
     * @param value
     * @param bits
     */
    public void addVal(long value, int bits) {
        binArray.append(value, bits);
    }

    /**
     * Add string
     *
     * @param str
     */
    public void addString(String str) {
        for (int i = 0; i < str.length(); i++) {
            int c = str.charAt(i);
            if (c >= 64) {
                c -= 64;
            }
            addVal(c, 6);
        }
    }

    /**
     * Add string
     *
     * @param str
     */
    public void addString(String str, int length, char fillBit) {
        int i = 0;
        for (; i < str.length() && i < length; i++) {
            int c = str.charAt(i);
            if (c >= 64) {
                c -= 64;
            }
            addVal(c, 6);
        }
        for (; i < length; i++) {
            int c = fillBit;
            if (c >= 64) {
                c -= 64;
            }
            addVal(c, 6);
        }
    }

    /**
     * Add string
     *
     * @param str
     */
    public void addDataString(String str) {
        for (int i = 0; i < str.length(); i++) {
            int chr = str.charAt(i);
            addVal(chr, 8);
        }
    }


    /**
     * Add string
     *
     * @param str
     */
    public void addUTF8DataString(String str) {
        for (int i = 0; i < str.length(); i++) {
            int chr = str.charAt(i);
            addVal(chr, 16);
        }
    }

    /**
     * @param str    原始字符串
     * @param length 需要补充到的最大的长度
     */
    public void addUTF8DataStringForLength(String str, int length) {
        BinArray binArray2 = new BinArray();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        if (str.length() < length / 16) {
            for (int r = 0; r < length / 16 - str.length(); r++) {
                stringBuilder.append(" ");
            }
        }
        for (int i = 0; i < stringBuilder.toString().length(); i++) {
            int chr = stringBuilder.toString().charAt(i);
            addVal(chr, 16);
            binArray2.append(chr, 16);
        }
    }


    /**
     * Add string
     *
     * @param str
     */
    public void add16String(String str) {
        for (int i = 0; i < str.length() / 2; i++) {
            int parseInt = Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            addVal(parseInt, 8);
        }
    }

    /**
     * Add string
     *
     * @param str
     */
    public void add2String(String str) {
        for (int i = 0; i < str.length(); i++) {
            int parseInt = Integer.parseInt(str.substring(i, i + 1), 2);
            addVal(parseInt, 1);
        }
    }

    /**
     * Add string and a number of spaces to fill length characters
     *
     * @param str
     * @param length
     */
    public void addString(String str, int length) {
        int i = 0;
        for (; i < str.length() && i < length; i++) {
            int c = str.charAt(i);
            if (c >= 64) {
                c -= 64;
            }
            addVal(c, 6);
        }
        for (; i < length; i++) {
            addVal(' ', 6);
        }
    }

    /**
     * Append another encoder
     *
     * @param encoder
     */
    public void append(SixbitEncoder encoder) {
        binArray.append(encoder.binArray);
    }

    /**
     * Append a binary array
     *
     * @param ba
     */
    public void append(BinArray ba) {
        binArray.append(ba);
    }

    /**
     * Get encoded six bit string
     *
     * @return string
     * @throws SixbitException
     */
    public String encode() throws SixbitException {
        StringBuilder buf = new StringBuilder();
        int start = 0;
        int stop = 0;
        while (start < binArray.getLength()) {
            stop = start + 6 - 1;
            if (stop >= binArray.getLength()) {
                padBits = stop - binArray.getLength() + 1;
                stop = binArray.getLength() - 1;
            }
            int data = (int) binArray.getVal(start, stop);
            data = data << padBits;
            int value = BinArray.intToSixbit(data);
            buf.append((char) value);
            start = stop + 1;
        }
        return buf.toString();
    }

    /**
     * Get encoded eight bit string
     *
     * @return string
     * @throws SixbitException
     */
    public String encode16bit(int start) throws SixbitException {
        StringBuilder buf = new StringBuilder();
//        int start = 0;
        int stop = 0;
        while (start < binArray.getLength()) {
            stop = start + 16 - 1;
            if (stop >= binArray.getLength()) {
                padBits = stop - binArray.getLength() + 1;
                stop = binArray.getLength() - 1;
            }
            int data = (int) binArray.getVal(start, stop);
            //     data = data << padBits;
            //     int value = BinArray.intToSixbit(data);
            buf.append((char) data);
            start = stop + 1;
        }
        return buf.toString();
    }

    /**
     * Get encoded eight bit string
     *
     * @return string
     * @throws SixbitException
     */
    public String encode16bit(int start, int len) throws SixbitException {
        StringBuilder buf = new StringBuilder();
        int endLength = start + len * 16;
//        int start = 0;
        int stop = 0;
        while (start < endLength) {
            stop = start + 16 - 1;
            if (stop >= binArray.getLength()) {
                padBits = stop - binArray.getLength() + 1;
                stop = binArray.getLength() - 1;
            }
            int data = (int) binArray.getVal(start, stop);
            //     data = data << padBits;
            //     int value = BinArray.intToSixbit(data);
            buf.append((char) data);
            start = stop + 1;
        }
        return buf.toString();
    }

    /**
     * The number of padding bits
     *
     * @return
     */
    public int getPadBits() {
        return padBits;
    }

    /**
     * Get bit length
     *
     * @return
     */
    public int getLength() {
        return binArray.getLength();
    }

    /**
     * Get the underlying binary array
     *
     * @return
     */
    public BinArray getBinArray() {
        return binArray;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SixbitEncoder [binArray=");
        builder.append(binArray);
        builder.append(", padBits=");
        builder.append(padBits);
        builder.append("]");
        return builder.toString();
    }


    /**
     * Get encoded eight bit string
     *
     * @return string
     * @throws SixbitException
     */
    public String encode8bit() throws SixbitException {
        StringBuilder buf = new StringBuilder();
        int start = 0;
        int stop = 0;
        while (start < binArray.getLength()) {
            stop = start + 8 - 1;
            if (stop >= binArray.getLength()) {
                padBits = stop - binArray.getLength() + 1;
                stop = binArray.getLength() - 1;
            }
            int data = (int) binArray.getVal(start, stop);
            //     data = data << padBits;
            //     int value = BinArray.intToSixbit(data);
            buf.append((char) data);
            start = stop + 1;
        }
        return buf.toString();
    }


    /**
     * Get encoded eight bit string
     *
     * @return string
     * @throws SixbitException
     */
    public String encode8bit(int start) throws SixbitException {
        StringBuilder buf = new StringBuilder();
//        int start = 0;
//        int start = 12;
        int stop = 0;
        while (start < binArray.getLength()) {
            stop = start + 8 - 1;
            if (stop >= binArray.getLength()) {
                padBits = stop - binArray.getLength() + 1;
                stop = binArray.getLength() - 1;
                break;
            }
            int data = (int) binArray.getVal(start, stop);
            buf.append((char) data);
            start = stop + 1;
        }
        return buf.toString();
    }


    /**
     * Get encoded four bit string
     *
     * @return string
     * @throws SixbitException
     */
    public String encode4bit() throws SixbitException {
        StringBuilder buf = new StringBuilder();
        int start = 0;
        int stop = 0;
        while (start < binArray.getLength()) {
            stop = start + 4 - 1;
            if (stop >= binArray.getLength()) {
                padBits = stop - binArray.getLength() + 1;
                stop = binArray.getLength() - 1;
            }
            int data = (int) binArray.getVal(start, stop);
            buf.append(Long.toHexString(data).toUpperCase());
            start = stop + 1;
        }
        return buf.toString();
    }

    /**
     * Get encoded four bit string
     *
     * @return string
     * @throws SixbitException
     */
    public String encodeBit() throws SixbitException {
        StringBuilder buf = new StringBuilder();
        int start = 0;
        int stop = 0;
        while (start < binArray.getLength()) {
            stop = start;
            if (stop >= binArray.getLength()) {
                padBits = stop - binArray.getLength() + 1;
                stop = binArray.getLength() - 1;
            }
            int data = (int) binArray.getVal(1);
            buf.append(Long.toHexString(data).toUpperCase());
            start = stop + 1;
        }
        return buf.toString();
    }

}
