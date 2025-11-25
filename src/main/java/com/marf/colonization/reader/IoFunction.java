package com.marf.colonization.reader;

import java.io.IOException;

public interface IoFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws IOException;

}
