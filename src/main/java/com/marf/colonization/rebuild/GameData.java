package com.marf.colonization.rebuild;

import com.marf.colonization.decompile.cmodules.SavegameHeader;

/** Data parts of the game. Note that the order of the properties do not need to be the same as in the original file. */
public class GameData {

    /**
     * Tile coordinate of the top left corner of the viewport. This is the smallest tile visible.
     *
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_82e2_viewport_x_min
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_82e6_viewport_y_min
     */
    public Point viewportMin = new Point();
    /**
     * Tile coordinate of the bottom right corner of the viewport.
     *
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_87aa_viewport_x_max
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_87ac_viewport_y_max
     */
    public Point viewportMax = new Point();
    /**
     * Tile coordinate of the center of the viewport
     *
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_0172_viewport_center_x
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_0174_viewport_center_y
     */
    public Point viewportCenter = new Point();

    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_82e0_viewport_x_offset
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_82e4_viewport_y_offset
     */
    public Point viewportOffset = new Point();

    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_017a_zoom_level
     * */
    public int zoomLevel;
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_017c_zoom_level_percent
     * */
    public int zoomLevelPercent;
    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_017e_maybe_scroll_amount
     * */
    public int scrollAmount;

    /**
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_84e6_map_width
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_84e8_map_height
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_0152_game_map_terrain
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_015a_game_map_visitor
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_0156_game_map_surface
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_015e_game_map_visibility
     * */
    public GameMap gameMap = new GameMap();

    /**
     * false - map view
     * true - colony view
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_0180_maybe_colony_vs_map_view */
    public boolean colonyView;

    /**
     * There are 2 tile size variables, but it seems they are always the same.
     *
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_5a8c_tile_pixel_size
     * @see com.marf.colonization.decompile.cmodules.Data#DAT_82de_tile_pixel_size
     */
    public int tileSize;

    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_0150_some_flag */
    public boolean DAT_0150_some_flag;
    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_0186 */
    public int DAT_0186 = 1;

    /** @see com.marf.colonization.decompile.cmodules.Data#DAT_5338_savegame_header */
    public SavegameHeader savegameHeader;
}
