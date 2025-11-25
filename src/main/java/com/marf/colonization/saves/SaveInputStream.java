package com.marf.colonization.saves;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

public class SaveInputStream {
    private final ImageInputStream imageInputStream;

    public SaveInputStream(ImageInputStream imageInputStream) {
        this.imageInputStream = imageInputStream;
    }

    public String readStringFixed(int numChars) throws IOException {
        byte[] buffer = new byte[numChars];
        imageInputStream.read(buffer);
        return new String(buffer);
    }

}
