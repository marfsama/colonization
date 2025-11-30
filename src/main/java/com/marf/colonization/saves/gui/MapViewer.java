package com.marf.colonization.saves.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.marf.colonization.saves.SaveFileReader;
import com.marf.colonization.saves.SavesReader;
import com.marf.colonization.saves.section.SaveFile;
import com.marf.colonization.saves.section.Tables;

import javax.imageio.stream.FileImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MapViewer extends JFrame {

    public MapViewer() throws HeadlessException {
        super("Map Viewer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 1000);
    }

    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(() -> {

            try (FileImageInputStream stream = new FileImageInputStream(new File("src/main/resources/COLONY01.SAV").getAbsoluteFile())) {
                ObjectMapper tableMapper = new ObjectMapper(new YAMLFactory());
                Tables tables = tableMapper.readValue(SavesReader.class.getResourceAsStream("/tables.yaml"), Tables.class);

                SaveFileReader reader = new SaveFileReader(tables, stream);
                SaveFile saveFile = reader.readAll();


                MapViewer frame = new MapViewer();
                frame.setContentPane(new JScrollPane(new MapPanel(saveFile.getMap())));
                frame.setVisible(true);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
