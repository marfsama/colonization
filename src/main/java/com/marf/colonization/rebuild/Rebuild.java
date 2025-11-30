package com.marf.colonization.rebuild;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.Caching;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Rebuild extends JPanel {

    private Canvas canvas;

    public Rebuild(PicoContainer container) {
        this.canvas = container.getComponent(Canvas.class);
        System.out.println("panel with canvas "+this.canvas);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(4.0, 4.0);
        g2d.drawImage(canvas.getBackscreen(), 1, 1, this);
        g2d.setColor(Color.GREEN);
        g2d.drawRect(0,0, 320+2, 200+2);
    }

    public static void main(String[] args) throws IOException {
        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching());
        pico.addComponent(Resources.load());
        pico.addComponent(Canvas.class);
        pico.addComponent(Minimap.class);
        pico.addComponent(GameData.class);
        pico.addComponent(Savegame.class);


        Rebuild rebuild = new Rebuild(pico);
        rebuild.canvas.clear();

        Savegame savegame = pico.getComponent(Savegame.class);
        savegame.loadSavegame("COLONY01.SAV");
        Minimap minimap = pico.getComponent(Minimap.class);
        minimap.init();
        minimap.calculateViewport();
        minimap.renderMinimapPanel(0);


        JFrame frame = new JFrame();
        frame.setSize(328*4, 215*4);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(rebuild);
        frame.setVisible(true);

    }
}
