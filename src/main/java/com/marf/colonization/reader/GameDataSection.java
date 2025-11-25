package com.marf.colonization.reader;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class GameDataSection {
    private String errorMessage;
    private long startPosition;
    private long endPosition;
    private boolean readSuccessfully;

    public boolean hasError() {
        return errorMessage != null;
    }
}