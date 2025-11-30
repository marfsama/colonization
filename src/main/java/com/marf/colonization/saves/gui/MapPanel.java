package com.marf.colonization.saves.gui;

import com.marf.colonization.saves.section.GameMap;
import com.marf.colonization.saves.section.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapPanel extends JPanel {
    public static final int SPILL_NORTH = 0;
    public static final int SPILL_SOUTH = 2;
    public static final int SPILL_WEST = 3;
    public static final int SPILL_EAST = 1;
    private final GameMap gameMap;
    private final BufferedImage terrain;
    private final BufferedImage forest;
    private final BufferedImage minorRiver;
    private final BufferedImage majorRiver;
    private final BufferedImage terrainMask;
    private final List<BufferedImage> terrainSpill = new ArrayList<>();
    private final BufferedImage shoresSharpCorner;
    private final BufferedImage shoresBigCorner;
    private final BufferedImage shoresSmallCorner;
    private int scale = 3;

    public MapPanel(GameMap gameMap) {
        setBackground(Color.BLACK);
        this.gameMap = gameMap;
        try {
            this.terrain = readImage("terrain.png");
            this.terrainMask = readImage("terrain-mask.png");
            this.forest = readImage("forest.png");
            this.minorRiver = readImage("minor-river.png");
            this.majorRiver = readImage("major-river.png");
            this.shoresSharpCorner = readImage("shore-b.png");
            this.shoresSmallCorner = readImage("shore-small-corner.png");
            this.shoresBigCorner = readImage("shore-z.png");
            createSpillTerrain();
            updateSeaTiles();
        } catch (IOException e) {
            throw  new IllegalStateException(e);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tileX = e.getX() / (16 * scale);
                int tileY = e.getY() / (16 * scale);

                if (tileX < gameMap.getWidth() && tileY < gameMap.getHeight()) {
                    Tile tile = gameMap.getTile(tileX, tileY);

                    int seaMask = tile.getSeaMask();
                    // 7 6  5  4 3 2  1 0
                    // N NE E SE S SW W NW
                    // reverse:
                    // S SW W NW N NE E SE
                    int reverse = seaMask | (seaMask << 8);
                    reverse = (reverse >> 4) & 0xff;
                    int and = seaMask & reverse;
                    System.out.printf("%d x %d: Sea Mask: %n%8s%n%8s%n%8s%n", tileX, tileY,
                            toBinaryString(seaMask), toBinaryString(reverse), toBinaryString(and) );
                }

            }
        });
    }

    /** reads the image from the classpath and makes sure the image is in the argb color space*/
    private BufferedImage readImage(String filename) throws IOException {
        BufferedImage image = ImageIO.read(getClass().getResource("/" + filename));

        if (image.getType() == BufferedImage.TYPE_INT_ARGB) {
            return image;
        }
        
        // convert image
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = newImage.createGraphics();
        graphics.drawImage(image,0,0,null);
        graphics.dispose();

        return newImage;
    }

    private void updateSeaTiles() {
        // sets all black pixels in the 8x8 corner tiles to the sea pixels
        BufferedImage seaTile = getSubImage(terrain, 9, 4);
        int[] offsets = new int[]{0, 0, 8, 0, 8, 8, 0, 8};
        for (int tileIndex = 0; tileIndex < 4; tileIndex++) {
            for (BufferedImage tiles : Arrays.asList(shoresSharpCorner, shoresSmallCorner)) {
                BufferedImage tile = getSubImage8(tiles, tileIndex);
                for (int x = 0; x < 8; x++) {
                    for (int y = 0; y < 8; y++) {
                        if ((tile.getRGB(x, y) & 0x00FFFFFF) == 0) {
                            // replace pixel with sea
                            int value = seaTile.getRGB(x + offsets[tileIndex * 2], y + offsets[tileIndex * 2 + 1]);
                            tile.setRGB(x, y, value);
                        }
                    }
                }
            }
        }
    }

    public String toBinaryString(int value) {
        String bin = Integer.toBinaryString(value);
        return "0".repeat(Math.max(0, 8-bin.length())) + bin;
    }

    /** creates the masked terrain tiles which spills into neighbouring tiles */
    private void createSpillTerrain() {
        for (int direction = 0; direction < 4; direction++) {
            BufferedImage mask = this.terrainMask.getSubimage(direction * 16, 0, 16, 16);
            BufferedImage maskedTerrain = new BufferedImage(terrain.getWidth(), terrain.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < terrain.getWidth(); x++) {
                for (int y = 0; y < terrain.getHeight(); y++) {
                    int maskX = x % 16;
                    int maskY = y % 16;
                    int maskColor = mask.getRGB(maskX, maskY);
                    if (maskColor != 0) {
                        // copy color
                        maskedTerrain.setRGB(x,y,terrain.getRGB(x,y));
                    }
                }
            }
            terrainSpill.add(maskedTerrain);
        }

    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(gameMap.getWidth() * 16 * scale, gameMap.getHeight()*16*scale);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                Tile tile = gameMap.getTile(x,  y);

                drawTile(g, terrain, tile, x, y);
                if (!tile.isSea()) {
                    if (y > 0 && y < gameMap.getHeight() && x > 0 && x < gameMap.getWidth()) {
                        drawSpillTile(g, x, y, 0, -1, SPILL_NORTH);
                        drawSpillTile(g, x, y, 0, +1, SPILL_SOUTH);
                        drawSpillTile(g, x, y, -1, 0, SPILL_WEST);
                        drawSpillTile(g, x, y, +1, 0, SPILL_EAST);
                    }
                }

                if (tile.isSea() && tile.getSeaMask() > 0) {
                    drawShore(g, tile, x, y);
                }

                if (tile.isForest() && !tile.isSea()) {
                    int mask = tile.getForestMask();
                    BufferedImage forestImage = forest.getSubimage((mask % 4) * 16, (mask / 4) * 16, 16, 16);
                    g.drawImage(forestImage, x * 16*scale, y * 16*scale,16*scale, 16*scale,  null);
                }

                if (tile.isRiver() && !tile.isSea()) {
                    int mask = tile.getRiverMask();
                    BufferedImage riverMask = majorRiver.getSubimage((mask % 4) * 16, (mask / 4) * 16, 16, 16);
                    g.drawImage(riverMask, x * 16*scale, y * 16*scale,16*scale, 16*scale,  null);
                }
            }
        }
    }

    private void drawShore(Graphics g, Tile tile, int x, int y) {
        // first: draw tile to the west as a base for the shore
        if (x > 0) {
            //drawTile(g, terrain, gameMap.getTile(x-1,y), x, y);
        }

        int seaMask = tile.getSeaMask();

        // calculate mask of opposite directions
        int widenedSeaMask = seaMask | (seaMask << 8);
        int opposite = (widenedSeaMask >> 4) & 0xff;

        // calculate directions in which land is opposite to each other
        int oppositeLand = seaMask & opposite;
        // when there are land tiles opposite to each other, the sea are is small
        boolean smallSea = oppositeLand > 0;


//        g.setColor(smallSea ? Color.GREEN : Color.WHITE);
//        g.drawRect(x * 16 * scale, y * 16 * scale, 16 * scale-1, 16 * scale-1);


        int[] offsets = new int[]{0, 0, 8, 0, 8, 8, 0, 8};
        for (int i = 0; i < 4; i++) {
            // move interesting corner from upper 3 bits to the lower 3 bits and separate them
            int cornerMask = (widenedSeaMask >> 5+8) & 0x7;
            // there is a corner when the diagonal (north-east) is land and the others (north and east) are not
            if (cornerMask == 2) {
                // corner is land, both adjected tiles are sea
                BufferedImage subImage = getSubImage8(this.shoresSharpCorner, i);
                g.drawImage(subImage, (x * 16 + offsets[i * 2]) * scale, (y * 16 + offsets[i * 2 + 1]) * scale, 8 * scale, 8 * scale, null);
            } else if (cornerMask == 7 && !smallSea) {
                // corner is land, both adjected tiles are land too. The tiles opposite the corner are sea
                BufferedImage subImage = getSubImage(this.shoresBigCorner, i,2);
                g.drawImage(subImage, x * 16 * scale, y * 16 * scale, 16 * scale, 16 * scale, null);
            } else if (cornerMask == 7 && smallSea) {
                // corner is land, both adjected tiles are land too. At least one tiles opposite to the corner is land
                BufferedImage subImage = getSubImage8(this.shoresSmallCorner, i);
                g.drawImage(subImage, (x * 16 + offsets[i * 2]) * scale, (y * 16 + offsets[i * 2 + 1]) * scale, 8 * scale, 8 * scale, null);
            }
            widenedSeaMask = widenedSeaMask << 2;
        }
    }

    private void drawSpillTile(Graphics g, int x, int y, int deltaX, int deltaY, int spillIndex) {
        Tile neighbour = gameMap.getTile(x + deltaX, y + deltaY);
        if (!neighbour.isSea()) {
            drawTile(g, terrainSpill.get(spillIndex), neighbour, x, y);
        }
    }

    private void drawTile(Graphics g, BufferedImage terrainTiles, Tile tile, int x, int y) {
        BufferedImage tileImage = getSubImage(terrainTiles, tile.getTileId() + (tile.isSea() ? 8 : 0), 4);
        if (tileImage != null) {
            g.drawImage(tileImage, x * 16*scale, y * 16*scale,16*scale, 16*scale,  null);
        }
    }

    // 0x00 0000 0000 - Tundra
    // 0x01 0000 0001 - Desert
    // 0x02 0000 0010 - Plains
    // 0x03 0000 0011 - Prairie
    // 0x04 0000 0100 - Grassland
    // 0x05 0000 0101 - Savanna
    // 0x06 0000 0110 - Marsh
    // 0x07 0000 0111 - Swamp

    // 0x08 0000 1000 - Boreal Forest (Tundra)
    // 0x09 0000 1001 - Scrub Forest (Desert)
    // 0x0a 0000 1010 - Mixed Forest (Plains)
    // 0x0b 0000 1011 - Broadleaf Forest (Prairie)
    // 0x0c 0000 1100 - Conifer Forest (Grassland)
    // 0x0d 0000 1101 - Tropical Forest (Savanna)
    // 0x0e 0000 1110 - Wetland Forest (Marsh)
    // 0x0f 0000 1111 - Rain Forest (Swamp)

    // 0x18 0001 1000 - Artic
    // 0x19 0001 1001 - Sea
    // 0x1a 0001 1010 - Sea Lane

    // 0xa? 1010 0??? - mountains
    // 0x2? 0010 0??? - hills

    // 0x59 0101 1001 - minor river
    // 0xe8 1100 1000 - major river
    // 0xe9 1101 1001 - inland lake

    private BufferedImage getSubImage(BufferedImage image, int index, int columns) {
        return image.getSubimage((index % columns) * 16, (index / columns) * 16, 16, 16);
    }

    private BufferedImage getSubImage8(BufferedImage image, int index) {
        int columns = image.getWidth() / 8;
        return image.getSubimage((index % columns) * 8, (index / columns) * 8, 8, 8);
    }

}
