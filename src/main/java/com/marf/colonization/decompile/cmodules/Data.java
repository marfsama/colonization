package com.marf.colonization.decompile.cmodules;


import java.util.ArrayList;
import java.util.List;

public class Data {

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    public static String DAT_0042 = "$STRING";

    public static int[] DAT_00a8_x_directions = new int[] {0, 1, 0, -1, 0, 0};
    public static int[] DAT_00ae_y_directions = new int[] {-1, 0, 1, 0, 0, 0};

    public static int[] DAT_00b4_directions_x = new int[] {0, 1, 1, 1, 0, -1, -1, -1, 0, 0};
    public static int[] DAT_00be_directions_y = new int[] {-1, -1, 0, 1, 1, 1, 0, -1, 0, 0};

    /** maybe "map loaded" flag or so */
    public static int DAT_0150_some_flag;

    /**
     * pointer to the allocated terrain type layer
     */
    public static byte[] DAT_0152_game_map_terrain = new byte[0];

    /**
     * pointer to the allocated surface layer
     */
    public static byte[] DAT_0156_game_map_surface = new byte[0];
    /**
     * pointer to the allocated visitor layer
     */
    public static byte[] DAT_015a_game_map_visitor = new byte[0];
    /**
     * pointer to the allocated visibility layer
     */
    public static byte[] DAT_015e_game_map_visibility = new byte[0];

    /**
     * TODO: sprite type
     */
    public static Module1a.SpriteSheetSomeStructure DAT_0162_terrain_sprites;
    public static Module1a.SpriteSheetSomeStructure DAT_016a_phys0_sprite_sheet;

    public static int DAT_0172_viewport_center_x;
    public static int DAT_0174_viewport_center_y;
    public static int DAT_0176_map_size_lower;
    public static int DAT_0178_map_size_upper;
    public static int DAT_017a_zoom_level;
    public static int DAT_017c_zoom_level_percent;
    public static int DAT_017e_maybe_scroll_amount;

    /** 0 - map view, 1 = colony view */
    public static int DAT_0180_maybe_colony_vs_map_view;
    public static int DAT_0184_show_hidden_terrain_state = 0;

    /**
     * something which is saved to the savegame
     */
    public static byte DAT_0186_map_seed;
    public static int[] DAT_0188_maybe_prime_resource_per_terrain_type = {6, 1, 2, 3, 4, 5, 6, 6, 9, 1, 8, 9, 0xA, 0xA, 6, 6, 9, 1, 8, 9, 0xA, 0xA, 6, 6, -1, 7, -1, 0xC, 0xD};

    public static int DAT_1e72_sub_tile_x;
    public static int DAT_1e73_sub_tile_y;

    public static int DAT_033e_some_flag;
    public static int DAT_0342_some_flag;

    public static int DAT_07dc;

    public static int DAT_0816;
    public static int DAT_0818;
    public static Sprite DAT_081c_address_of_woodtile_sprite_maybe;
    public static Module1a.SpriteSheetSomeStructure DAT_082e_icons_sprite_sheet;
    public static Module1a.SpriteSheetSomeStructure DAT_0832_buildings_sprite_sheet;

    public static int[] DAT_0838_minimap_fractions_colors_table = new int[] {
               0xC, 0x9, 0xE, 0xD,
               0xF, 0x95, 0x36, 0xB,
               0x43, 0x6F, 0x75, 0x47,
               0x7, 0xB, 0x9, 0xA
    };

    public static Sprite DAT_14a0_address_of_woodtile_sprite_maybe;

    public static int DAT_235c;
    public static int DAT_2360_available_sprite_sheet_memory;
    public static int DAT_2364_sprite_sheets_loaded_counter;
    public static int DAT_2386_load_sprite_sheet_last_error;
    public static int DAT_2388_palette_destination;
    public static int DAT_238a;
    public static Module1a.SpriteSheetSomeStructure DAT_238c;


    public static Sprite DAT_2638_backscreen = new Sprite();
    public static Sprite DAT_2640_2nd_backscreen = new Sprite();

    public static int DAT_26b6_current_random_slot;

    // 32 bits unsigned
    public static long DAT_273a_random_seed;

    public static int[] DAT_27b9_maybe_key_modifier_state = new int[0];

    public static int DAT_2c8e_width;
    public static int DAT_2c90_height;
    public static int DAT_2c92_x;
    public static int DAT_2c94_y;

    public static int[] DAT_2cec_adjection_land_stuff = new int[] {};


    /** Memory Block structure pointing to a list of null terminated strings. */
    public static List<String> DAT_2d00_string_table = new ArrayList<>();
    /** number of strings in the {@link #DAT_2d00_string_table} list*/
    public static int DAT_2d12_string_count = 0;

    public static int DAT_2d64_max_timeout;

    public static int DAT_2dfc; // 396 (0x18c): Rebel
    public static int DAT_2dfe; // 397 (0x18d): Tory

    public static int DAT_2e1e; // 413 (0x19d): Rebels
    public static int DAT_2e20; // 414 (0x19e): Tories

    public static Unit[] DAT_30fc_units_list = new Unit[0];
    public static UnitTypeConfig[] DAT_51e8_unit_config_array = new UnitTypeConfig[24];
    public static SavegameHeader DAT_5338_savegame_header;


    public static Player[] DAT_53c6_player_list = new Player[4];

    public static IndianVillage[] DAT_54a4_indian_village_list = new IndianVillage[0];

    public static int DAT_5a8c_tile_pixel_size;

    // Tribe[]
    public static Tribe[] DAT_2b4d_5a8e_tribes_list;

    public static List<Colony> DAT_5cfe_colonies_list = new ArrayList<>();


    public static int DAT_82de_tile_pixel_size;
    public static int DAT_82e0_viewport_x_offset;
    public static int DAT_82e2_viewport_x_min = 0;
    public static int DAT_82e4_viewport_y_offset;
    public static int DAT_82e6_viewport_y_min = 0;

    // map width and height in tiles
    public static int DAT_84e6_map_width = 0;
    public static int DAT_84e8_map_height = 0;
    public static int DAT_84ea_number_of_x_tiles_in_viewport;
    public static int DAT_84ec_number_of_y_tiles_in_viewport;
    public static int DAT_84ee_some_width;
    public static int DAT_84f0_some_height;

    public static int DAT_84f2_some_x;
    public static int DAT_84f4_some_y;


    public static int DAT_87aa_viewport_x_max = 0;
    public static int DAT_87ac_viewport_y_max = 0;
    public static Europe[] DAT_87e2_europe = new Europe[4];

    public static int DAT_a862_power_mask;

    /** shorts, text index into power names: 46 (0x2e): English, 47 (0x2f): French, 48 (0x30): Spanish, 49 (0x31): Dutch */
    public static int[] DAT_8cb0_power_names = new int[4];
    /** ie: 268 (0x10c): Incas, 269 (0x10d): Inca, 270 (0x10e): Jewelled Relics */
    public static TribesNames[] DAT_8cb8_tribes_names = new TribesNames[8];

    public static byte[] DAT_8d08_building_type_stuff = new byte[16];
    public static Colony DAT_8d6c_current_colony_ptr;


    public static Building[] DAT_8f2c_buildings_table = new Building[42];

    public static int[] DAT_9c60_int_placeholder;
    public static int DAT_9c7a_minimap_min_y;
    public static int DAT_9c7c_minimap_min_x;
    /** Array of preallocated string buffer slots. Each slot is 0x40 (64) bytes in size. There are 5 slots. */
    public static String[] DAT_9c82_string_placeholder_array = new String[5];

    public static int[] DAT_a526_terrain_minimap_colors = new int[] {

    };

    public static int DAT_a544_surface_map_pointer;
    public static int DAT_a548_terrain_map_pointer_to_current_position;
    public static int DAT_a54c_visibility_map_pointer;



    public static int DAT_84f6_viewport_width = 0xf0;
    public static int DAT_84f8_viewport_height = 0xc0;

    public static int DAT_a550_draw_map_x_in_tiles;
    public static int DAT_a552_draw_map_y_in_tiles;
    public static int DAT_a554_draw_map_x_in_pixels;
    public static int DAT_a556_draw_map_y_in_pixels;

    public static int DAT_a558;

    public static long[] DAT_a5c6_random_slot_seeds = new long[4];

    public static int DAT_a5d6_sprite_sheet_next_free;
    public static int DAT_a5da_sprite_sheet_size;
    public static int DAT_a5de;

    public static int[] DAT_a62a_random_slot_availability = new int[4];

    // stuff for calculating adjected land tiles
    public static int DAT_a863_value_from_terrain_map;
    public static int DAT_a864_value_from_visibility_map;
    public static int DAT_a865_current_terrain; // byte
    public static int DAT_a866_adjusted_current_terrain_type; // byte
    public static int DAT_a867_adjected_land_tiles_count; // byte
    public static int DAT_a868_lower_bound; //
    public static int DAT_a869_upper_bound; //
    public static int DAT_a86a_adjected_land_bitmask; // byte


    public static byte DAT_a85b_colony_valid_flag;



}

