package com.marf.colonization.rebuild;

import com.marf.colonization.mpskit.Ff;
import com.marf.colonization.saves.section.IndianTribe;
import com.marf.colonization.saves.section.TableValue;
import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class IndianVillage {
    private int x;
    private int y;
    private TableValue nation; // Will use Lookup enum later
    private int state;
    private int population;
    private int mission; // Will use Lookup enum later
    private byte[] padding1;
    private int[] panic;

    public IndianVillage(com.marf.colonization.saves.section.IndianVillage other) {
        this.x = other.getX();
        this.y = other.getY();
        this.nation = other.getNation();
        this.state = other.getState();
        this.population = other.getPopulation();
        this.mission = other.getMission();
        this.padding1 = other.getPadding1();
        this.panic = other.getPanic();
    }

    /** @see com.marf.colonization.decompile.cmodules.Modulea#FUN_6158_03fa_module_a_maybe_get_max_european_threat */
    public int[] FUN_6158_03fa_module_a_maybe_get_max_european_threat(IndianVillage village) {
        // TODO: implement
        // Note: this might be moved to an "ai" module or so
        return new int[] {0, 10};
    }

    /** @see com.marf.colonization.decompile.cmodules.Code11#FUN_112b_0790_draw_indian_village */
    public void drawIndianVillage(GameData gameData, Canvas canvas, Resources resources, BufferedImage destination, int zoomLevelPercent, int screenX, int screenY) {
        IndianVillage village = this;
        int tribeIndex = village.getNation().getId() - 4;
        IndianTribe tribe = gameData.gameMap.indianTribes.get(tribeIndex);
        int tribeLevel = tribe.getLevel();

        int tileSize = 16;
        if (zoomLevelPercent < 100) {
            screenX -= (2 >> gameData.tileSize) & 0x3;
            tileSize = gameData.tileSize;
        }

        // draw base icon
        int screen_x = screenX + (tileSize >> 1);
        int screen_y = screenY + tileSize;
        canvas.drawSpriteFlippableCenteredZoomed(destination, screen_x, screen_y, zoomLevelPercent, Math.min(tribeLevel, 3) + 0xb, resources.getIcons());

        // draw tribe colors on villages of size 0 and 1
        int color = gameData.fractionsColorsTable[tribeIndex + 4];
        if (zoomLevelPercent == 100) {
            if (tribeLevel == 0) {
                canvas.fillRect(destination, screenX + 3, screenY + 4, 1, 1, color);
                canvas.fillRect(destination, screenX + 12, screenY + 4, 1, 1, color);
                canvas.fillRect(destination, screenX + 9, screenY + 6, 1, 1, color);
            } else if (tribeLevel == 1) {
                canvas.fillRect(destination, screenX + 4, screenY + 9, 2, 1, color);
                canvas.fillRect(destination, screenX + 9, screenY + 11, 3, 1, color);
            }
            // no tribe colors for level 2 and 3
        }
        if (zoomLevelPercent == 50) {
            if (tribeLevel == 0) {
                canvas.fillRect(destination, screenX + 2, screenY + 2, 1, 1, color);
                canvas.fillRect(destination, screenX + 6, screenY + 2, 1, 1, color);
                canvas.fillRect(destination, screenX + 5, screenY + 3, 1, 1, color);
            } else if (tribeLevel == 1) {
                canvas.fillRect(destination, screenX + 2, screenY + 4, 1, 1, color);
                canvas.fillRect(destination, screenX + 5, screenY + 5, 3, 1, color);
            }
            // no tribe colors for level 2 and 3
        }

        // is the village a capital?
        if ((village.getState() & 4) != 0) {
            //  draw the capital icon
            canvas.drawSpriteFlippableCenteredZoomed(destination, screen_x, screen_y, zoomLevelPercent, 0x12, resources.getIcons());
        }

        if (zoomLevelPercent == 100) {
            screen_x = screenX + 6;
            screen_y = screenY;

            // draw threat level
            int[] out_thread_level = FUN_6158_03fa_module_a_maybe_get_max_european_threat(this);;
            int aggressor = out_thread_level[0];
            int threatLevel = out_thread_level[1];

            if (aggressor >= 0) {
                int panicBackgroundColor;
                int panicColor;
                if (gameData.savegameHeader.maybe_current_player == aggressor) {
                    int panic = village.getPanic()[aggressor];
                    if (panic < 0) {
                        village.getPanic()[aggressor] = 0;
                    }
                    int panicLevel = panic >> 5;
                    if (panicLevel > 3) {
                        panicLevel = 3;
                    }
                    int tribeAggression = gameData.gameMap.getTribeAggressionForPower(tribeIndex, aggressor);
                    if (tribeAggression >= 0x4b) {
                        panicLevel = 3;
                    }

                    panicColor = switch (panicLevel) {
                        case 0 -> 0xa;
                        case 1 -> 0xb;
                        case 2 -> 0xe;
                        default -> 0xc;
                    };
                    panicBackgroundColor = 0;
                } else {
                    threatLevel = 1;
                    panicColor = gameData.fractionsColorsTable[aggressor];
                    // TODO: check this. the panic color cannot be the same as the background color
                    panicBackgroundColor = panicColor;
                }

                if (threatLevel > 0) {
                    screen_y += 4;
                    do {
                        // for half an aggression level show darker color
                        if (threatLevel < 3) {
                            panicColor -= 8;
                        }
                        // draw background: for the player black, for other powers the color of the power
                        canvas.fillRect(destination, screen_x, screen_y, 3, 7, panicBackgroundColor);
                        // long part of the exclamation mark
                        canvas.fillRect(destination, screen_x+1, screen_y+1, 1, 3, panicColor);
                        // dot of the exclamation mark
                        canvas.fillRect(destination, screen_x+1, screen_y+5, 1, 1, panicColor);

                        screen_x += 2;
                        threatLevel -= 4;
                    } while (threatLevel > 0);
                }
                screen_x += 2;
            }
            // draw mission
            byte mission = (byte) village.getMission();
            if (mission > -1) {
                int missionPower = mission & 0x7;
                int upperNibble = mission & 0xf8;
                screen_y = screenY;

                int missionColor = gameData.fractionsColorsTable[missionPower] + (upperNibble != 0 ? 0xf8 : 0);
                // draw background
                canvas.fillRect(destination, screen_x, screen_y+5, 5, 6, 0);
                // draw cross
                canvas.fillRect(destination, screen_x+2, screen_y+6, 1, 4, missionColor);
                canvas.fillRect(destination, screen_x+1, screen_y+7, 3, 1, missionColor);
            }

            if ((gameData.debugInfoFlags & 1) > 0) {
                int panic = village.getPanic()[aggressor];
                // draw panic value to screen
                String panicString = String.valueOf(panic);
                Ff.Font font = resources.getFontTiny();
                int stringWidth = font.getStringWidth(panicString);
                canvas.fillRect(destination, screenX+2, screenY+2+font.getHeight(), stringWidth+1, font.getHeight()+1, 0);
                canvas.setTextColors( 0xff, 0xf, 0xf, 0xf);
                canvas.drawString(destination, font, panicString, screenX+3, screenY+3 + resources.getFontTiny().getHeight(), 0);
            }
        }

        if (zoomLevelPercent <= 25) {
            // draw colored rectangles for the two smallest zoom levels
            canvas.fillRect(destination,screenX, screenY, tileSize, tileSize, color);
        }
    }
}
