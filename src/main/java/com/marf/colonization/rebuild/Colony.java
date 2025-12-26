package com.marf.colonization.rebuild;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.util.List;

@Data
public class Colony {
    /** 0x0 0x1 */
    private int x;
    /** 0x1 0x1 */
    private int y;
    /**0x2	0x18*/
    private String name;
    /** 0x1a 0x1 */
    private int nation;
    /** 0x1b 0x1 */
    private byte field_0x1b;
    /** 0x1c 0x4 */
    private byte sonsOfLibertyLevel;
    /** 0x1d 0x2 */
    private short field_0x1d;
    /** 0x1f 0x1 */
    private int numColonists;
    /** 0x20 0x20 */
    private List<Integer> colonistOccupation;
    /** 0x40 0x20 */
    private List<Integer> colonistSpecialization;
    /** 0x60 0x10 */
    private List<Integer> colonistTime;
    /** 0x70 0x8 */
    private List<Integer> tileUsage;
    /** 0x78 0xc */
    private List<Integer> field0x78;
    /** 0x84 0x6 */
    private List<Integer> buildings;
    /** 0x8a 0x2 */
    private int customsHouse;
    /** 0x8c 0x6 */
    private List<Integer> field0x8c;
    /** 0x92 0x2 */
    private int hammers;
    /** 0x94 0x1 */
    private int	currentProduction;
    /** 0x95 0x5 */
    private List<Integer> field0x95;
    /** 0x9a 0x20 */
    private List<Integer> storage;
    /** 0xba 0x4
     * number of colonists the player has seen in the colony (so this is a snapshot)
     * */
    private List<Integer> colonistsSeenInColony;
    /** 0xbe 0x4 */
    private List<Integer> seenFortificationLevel;
    /** 0xc2 0x8 */
    private long bells;
    // total size: 0xca


    public Colony(com.marf.colonization.saves.section.Colony other) {
        this.x = other.getX();
        this.y = other.getY();
        this.name = other.getName();
        this.nation = other.getNation();
        this.field_0x1b = other.getField_0x1b();
        this.sonsOfLibertyLevel = other.getSonsOfLibertyLevel();
        this.field_0x1d = other.getField_0x1d();
        this.numColonists = other.getNumColonists();
        this.colonistOccupation = other.getColonistOccupation();
        this.colonistSpecialization = other.getColonistSpecialization();
        this.colonistTime = other.getColonistTime();
        this.tileUsage = other.getTileUsage();
        this.buildings = other.getBuildings();
        this.customsHouse = other.getCustomsHouse();
        this.field0x8c = other.getField0x8c();
        this.hammers = other.getHammers();
        this.currentProduction = other.getCurrentProduction();
        this.field0x95 = other.getField0x95();
        this.storage = other.getStorage();
        this.colonistsSeenInColony = other.getColonistsSeenInColony();
        this.seenFortificationLevel = other.getSeenFortificationLevel();
        this.bells = other.getBells();
    }

    /** @see com.marf.colonization.decompile.cmodules.Code15#FUN_15d9_0368_is_building_in_colony */
    public boolean hasBuilding(int buildingIndex) {
        if (buildingIndex < 0) {
            return false;
        }

        // building / 8
        int building = buildingIndex >> 3;
        int mask = 0x1 << (buildingIndex & 7);

        return (buildings.get(building) & mask) > 0;
    }

    public int getColonistsSeenInColony(int power) {
        return colonistsSeenInColony.get(power);
    }

    public void setColonistsSeenInColony(int power, int population) {
        colonistsSeenInColony.set(power, population);
    }

    public int getSeenFortificationLevel(int power) {
        return seenFortificationLevel.get(power);
    }

    /** @see com.marf.colonization.decompile.cmodules.Module02#FUN_4af1_1b76_module_2_is_colony_visible */
    public boolean isColonyVisible(GameData gameData, int playerIndex) {
        // TODO: check this. this doesn't seem to be right
        if (getNation() == 0) {
            return true;
        }

        if (gameData.savegameHeader.field_0x22_maybe_current_turn != 0) {
            return true;
        }

        if (getColonistsSeenInColony(playerIndex) > 0) {
            return true;
        }

        return false;
    }


    /** @see com.marf.colonization.decompile.cmodules.Code11#FUN_112b_0c64_draw_colony */
    public void drawColony(Canvas canvas, GameData gameData, Resources resources, BufferedImage destination, int zoom_level_percent, int screenX, int screenY, boolean displayColonyName, boolean displayPopulation) {
        int local_2c_param_x_in_pixels = screenX;
        int local_2a_param_y_in_pixels = screenY;

        int nation = this.getNation();
        int numColonists = this.getNumColonists();

        int fortificationLevel = 0;
        // check for stockade
        if (this.hasBuilding(0)) {
            fortificationLevel++;
        }
        // check for fort
        if (this.hasBuilding(1)) {
            fortificationLevel++;
        }
        // check for fortress
        if (this.hasBuilding(2)) {
            fortificationLevel++;
        }

        if (nation != gameData.savegameHeader.maybe_player_controlled_power) {
            int seenColonists = this.getColonistsSeenInColony(gameData.savegameHeader.maybe_player_controlled_power);
            if (seenColonists == 0) {
                seenColonists = 1;
                this.setColonistsSeenInColony(gameData.savegameHeader.maybe_player_controlled_power, 1);
            }
            numColonists = seenColonists;
            fortificationLevel = this.getSeenFortificationLevel(gameData.savegameHeader.maybe_player_controlled_power);
        }

        // note: 0 wraps around to 0xffff and therefor to 0x3
        int colonySprite = (fortificationLevel - 1) & 0x3;

        if (zoom_level_percent < 100 ) {
            screenX -= 2 >> com.marf.colonization.decompile.cmodules.Data.DAT_017a_zoom_level;
        }


        int local_10_x = screenX + (gameData.tileSize / 2);
        int local_12_y = screenY + gameData.tileSize;

        int local_a_another_x;
        int local_c_another_y;

        // this looks like some rotation
        if (zoom_level_percent == 100) {
            local_a_another_x = screenX + 6;
            local_c_another_y = screenY + gameData.tileSize;
        } else {
            local_a_another_x = screenX + 2;
            local_c_another_y = screenY + 3;
        }

        // draw base icon
        canvas.drawSpriteFlippableCenteredZoomed(destination, local_10_x, local_12_y, zoom_level_percent, colonySprite+1, resources.getIcons());
        // draw flag
        int flagOwner = nation;
        int flagSprite = 0x77 + flagOwner;
        if ((gameData.savegameHeader.field1_0x2_independence_flag & 1) != 0 &&
                gameData.savegameHeader.rebels_nation_maybe == gameData.savegameHeader.maybe_current_player) {
            flagSprite = 0x83;
        }
        canvas.drawSpriteFlippableCenteredZoomed(destination, local_a_another_x, local_c_another_y, zoom_level_percent, flagSprite, resources.getIcons());

        // draw population and colony name only in max zoom
        if (zoom_level_percent == 100) {
            int populationColor = 0xf;
            if ((this.getSonsOfLibertyLevel() & 0x4) != 0) {
                populationColor = 0xa;
                if ((this.getSonsOfLibertyLevel() & 0x2) != 0) {
                    populationColor = 0xb;
                }
            }

            canvas.setTextColors(0xff, populationColor, populationColor, populationColor);
            if (displayPopulation) {
                canvas.drawString(destination, resources.getFontTiny(), "" + numColonists, local_2c_param_x_in_pixels + 7, local_2a_param_y_in_pixels + resources.getFontTiny().getHeight(), 0);
            }
            if (displayColonyName) {
                canvas.setTextColors(0xff, 0xf, 0x0, 0x0);
                canvas.drawString(destination, resources.getFontIntr(), this.getName(), screenX + 2, screenY+8-1 + resources.getFontIntr().getHeight(), 0);
            }

        }
        if (zoom_level_percent <= 25) {
            // draw colored rectangle in smaller zoom modes
            int color = gameData.fractionsColorsTable[this.getNation()];
            canvas.fillRect(destination, screenX, screenY, gameData.tileSize, gameData.tileSize, color);
        }


    }
}
