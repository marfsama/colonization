package com.marf.colonization.mpskit;

import com.marf.colonization.reader.GameDataSection;

public class Section extends GameDataSection {
    public int flags;
    public int uncompressedSize;
    public int compressedSize;
    private byte[] data;


    @Override
    public String toString() {
        return "Section{" +
                "flags=" + flags +
                ", uncompressedSize=" + uncompressedSize +
                ", compressedSize=" + compressedSize +
                ", data=" + getData().length +
                '}';
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
