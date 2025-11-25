package com.marf.colonization.saves;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.marf.colonization.reader.GameDataSection;
import com.marf.colonization.saves.section.SaveFile;
import com.marf.colonization.saves.section.Tables;

import javax.imageio.stream.FileImageInputStream;
import java.io.File;
import java.io.IOException;

public class SavesReader {
    public static void main(String[] args) throws IOException {

        ObjectMapper tableMapper = new ObjectMapper(new YAMLFactory());
        Tables tables = tableMapper.readValue(SavesReader.class.getResourceAsStream("/tables.yaml"), Tables.class);

        try (FileImageInputStream stream = new FileImageInputStream(new File("src/main/resources/COLONY01.SAV").getAbsoluteFile())) {
            SaveFileReader reader = new SaveFileReader(tables, stream);
            SaveFile saveFile = reader.readAll();


            // Check for errors
            if (saveFile.isHasErrors()) {
                System.out.println("\n=== ERRORS ===");
                checkAndPrintError(saveFile.getHeader(), "Header");

                for (String error : saveFile.getGlobalErrors()) {
                    System.out.println("Global: " + error);
                }
            }

            var mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
            System.out.println(mapper.writeValueAsString(saveFile));


        } catch (Exception e) {
            System.err.println("Failed to open file: " + e.getMessage());
        }
    }

    private static void checkAndPrintError(GameDataSection section, String sectionName) {
        if (section != null && section.hasError()) {
            System.out.println(sectionName + ": " + section.getErrorMessage());
        }
    }
}
