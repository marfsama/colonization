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
        g2d.drawImage(canvas.getBackscreen(), 0, 0, this);
    }

    public static void main(String[] args) throws IOException {
        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching());
        pico.addComponent(Resources.load());
        pico.addComponent(Canvas.class);
        pico.addComponent(Minimap.class);
        pico.addComponent(GameData.class);
        pico.addComponent(Savegame.class);


        Rebuild rebuild = new Rebuild(pico);
        Canvas canvas = pico.getComponent(Canvas.class);
        canvas.clear(canvas.getBackscreen());

        Savegame savegame = pico.getComponent(Savegame.class);
        savegame.loadSavegame("COLONY02.SAV");

        GameData gameData = pico.getComponent(GameData.class);
//        gameData.viewportCenter = new Point(31,11);

        Minimap minimap = pico.getComponent(Minimap.class);
        minimap.init();
        minimap.calculateViewport();
        minimap.renderMinimapPanel(0);
        minimap.drawMapForType(1, 0);
        canvas.drawSprite(canvas.getBackscreen(), canvas.getScratch(), 15*16, 12*16, 0,8,0,0);


        JFrame frame = new JFrame();
        frame.setSize(328*4, 215*4);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(rebuild);
        frame.setVisible(true);

    }
}
