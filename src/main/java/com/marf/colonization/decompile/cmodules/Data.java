package com.marf.colonization.decompile.cmodules;


import java.util.ArrayList;
import java.util.List;

public class Data {

    int TRUE = 1;
    int FALSE = 0;

    static String DAT_0042 = "$STRING";

    /**
     * pointer to the allocated terrain type layer
     */
    static byte[] DAT_0152_game_map_terrain = new byte[0];

    /**
     * pointer to the allocated surface layer
     */
    static byte[] DAT_0156_game_map_surface = new byte[0];
    /**
     * pointer to the allocated visitor layer
     */
    static byte[] DAT_015a_game_map_visitor = new byte[0];
    /**
     * pointer to the allocated visibility layer
     */
    static byte[] DAT_015e_game_map_visibility = new byte[0];

    /**
     * TODO: sprite type
     */
    static int DAT_0162_terrain_sprites = 0;
    /**
     * TODO: sprite type
     */
    static int DAT_016a_phys0_sprite_sheet = 0;

    static byte DAT_0184_show_hidden_terrain_state = 0;

    /**
     * something which is saved to the savegame
     */
    static byte DAT_0186;
    static int[] DAT_0188_maybe_prime_resource_per_terrain_type = {6, 1, 2, 3, 4, 5, 6, 6, 9, 1, 8, 9, 0xA, 0xA, 6, 6, 9, 1, 8, 9, 0xA, 0xA, 6, 6, -1, 7, -1, 0xC, 0xD};

    static int DAT_033e_some_flag;
    static int DAT_0342_some_flag;

    static int DAT_07dc;

    static Sprite DAT_14a0_some_address_of_sprite;

    static int DAT_2360_available_sprite_sheet_memory;
    static int DAT_2364_sprite_sheets_loaded_counter;
    static int DAT_238c;


    static int DAT_2c8e_width;
    static int DAT_2c90_height;
    static int DAT_2c92_x;
    static int DAT_2c94_y;

    static Sprite DAT_2638_backscreen = new Sprite();
    static Sprite DAT_2640_2nd_backscreen = new Sprite();

    /** Memory Block structure pointing to a list of null terminated strings. */
    static List<String> DAT_2d00_string_table = new ArrayList<>();
    /** number of strings in the {@link #DAT_2d00_string_table} list*/
    static int DAT_2d12_string_count = 0;

    static int DAT_2d64_max_timeout;

    static int DAT_2dfc; // 396 (0x18c): Rebel
    static int DAT_2dfe; // 397 (0x18d): Tory

    static int DAT_2e1e; // 413 (0x19d): Rebels
    static int DAT_2e20; // 414 (0x19e): Tories

    static Unit[] DAT_30fc_units_list = new Unit[0];
    static SavegameHeader DAT_5338_savegame_header;


    static Player[] DAT_53c6_player_list = new Player[4];
    static List<Colony> DAT_5cfe_colonies_list = new ArrayList<>();


    static int DAT_82e2_game_window_x_min = 0;
    static int DAT_82e6_game_window_y_min = 0;

    // map width and height in tiles
    static int DAT_84e6_map_width = 0;
    static int DAT_84e8_map_height = 0;


    static int DAT_87aa_game_window_x_max = 0;
    static int DAT_87ac_game_window_y_max = 0;

    /** shorts, text index into power names: 46 (0x2e): English, 47 (0x2f): French, 48 (0x30): Spanish, 49 (0x31): Dutch */
    static int[] DAT_8cb0_power_names = new int[4];
    /** ie: 268 (0x10c): Incas, 269 (0x10d): Inca, 270 (0x10e): Jewelled Relics */
    static TribesNames[] DAT_8cb8_tribes_names = new TribesNames[8];

    static Europe[] DAT_87e2_europe = new Europe[4];

    static Colony DAT_8d6c_current_colony_ptr;


    static Building[] DAT_8f2c_buildings_table = new Building[42];

    static int[] DAT_9c60_int_placeholder;
    /** Array of preallocated string buffer slots. Each slot is 0x40 (64) bytes in size. There are 5 slots. */
    static String[] DAT_9c82_string_placeholder_array = new String[5];

    static int DAT_a5d6_sprite_sheet_next_free;
    static int DAT_a5da_sprite_sheet_size;
    static int DAT_a5de;


    static byte[] DAT_8d08_building_type_stuff = new byte[16];

    static byte DAT_a85b_colony_valid_flag;


}

