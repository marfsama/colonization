package com.marf.colonization.reader;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;

@FunctionalInterface
public interface ReadDataSectionFunction<E extends GameDataSection> {
    /**
     * Reads the game data section in the supplied object
     */
    E readGameDataSection(E section, ImageInputStream stream) throws IOException;

}
