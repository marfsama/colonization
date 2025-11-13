# 21.03.2025: FUN_20fe_2982_add_call_to_stack

Input:

* SS:SI - return address for caller
* BX - moduleIndex

Result:

* void (at the moment)

## Template:

```
int MAIN_MODULE_CS = 0x20fe;
// offset of the instruction right after the dynamic call. The call places this instruction as "return address" on the stack.
int FUN_20fe_154f_dynamic_call_return_offset = 0x1554

// list of module configurations
ModuleConfig[] DAT_20fe_399b_module_configs_paragraph = new ModuleConfig[31]; 

void FUN_20fe_2982_add_call_to_stack(SegmentOffset* callerReturnAddress, int moduleIndex) {
  // break here: bp 12EB:2982
  [...]
}
```


## Step 1: Check if caller is `FUN_20fe_154f_dynamic_io_load_file`

       20fe:154f 9a 00 00 00 00      CALLF      SUB_0000_0000  <- check if this is the caller
       20fe:1554 2e c6 06 e1 39      MOV        byte ptr CS:[DAT_20fe_39e1_maybe_busy_flag],0xff

```
  // skip method then the caller is the function FUN_20fe_154f_dynamic_call
  if (callerReturnAddress->segment == MAIN_MODULE_CS && callerReturnAddress->offset == FUN_20fe_154f_dynamic_call_return_offset) {
    return;
  }
  DAT_20fe_39e0 = -1
```

Note: SI = 0xC042 and SP = 0xC02E for a diff of 0x14


## Step 2: check if module id is set and get module code segment


```
  int moduleSegment = 0;
  
  if (moduleIndex != 0) {
    // make table index into module configs list
    index = (moduleIndex & 0x3fff) - 1;
    // get module config for the module
    moduleConfig = DAT_20fe_399b_module_configs_paragraph[index];
    // get original bit of flags
    bool bit15Set = (moduleConfig->flags & 0x8000) != 0; 
    // set flag regardless of previous value
    moduleConfig->flags &= 0x8000;
    // if the flag was *not* set, mark the module index 
    if (!bit15Set) {
      moduleIndex = moduleIndex & 0x8000; 
    }
    moduleSegment = moduleConfig.code_segment;   
  }
```



## Step 3: Prepare StackEntry

Note: caller return address on stack is 1D01:2013. The code at this position looks like:

    1d01:0202 ff 36 9f 27         PUSH       word ptr [0x279f]
    1d01:0206 ff 36 9d 27         PUSH       word ptr [0x279d]
    1d01:020a ff 36 9b 27         PUSH       word ptr [0x279b]
    1d01:020e 9a 00 00 10 28      CALLF      FUN_2810_0000
    1d01:0213 50                  PUSH       AX     <- caller return address

FUN_2810_0000 is:

    19FD:0000  9A520EEB12          call 12EB:0E52  <-  FUN_20fe_0e52_module_alternative3
    19FD:0005  EA5E020000          jmp  0000:025E

The call might already be changed, as the original call in Ghidra is 

    2810:0000 9a ab 0d fe 20      CALLF      FUN_20fe_0dab_module_loader_resolve_function_call 
    2810:0005 ea 5e 02 00 00      JMPF       LAB_0000_025e

Pseudo code:

```
  // break here: bp 12EB:29c0
  // ES:DI = 0x190b:0000, everything is zero
  ModuleStackEntry* stackEntry = DAT_20fe_3952;
  stackEntry.moduleIndex = moduleIndex;
  stackEntry.codeSegment = moduleSegment;
  
  // swap return address on stack with CS:FUN_20fe_154f_dynamic_call (just after the call opcode)  
  originalCaller = copyOf(callerReturnAddress);
  callerReturnAddress.offset = FUN_20fe_154f_dynamic_call_return_offset;
  callerReturnAddress.segment = MAIN_MODULE_CS;
  stackEntry.caller = originalCaller;
  
  // save caller address to stack entry and to global variable
  DAT_20fe_395c_sp_to_last_caller_return_address = pointerOf(callerReturnAddress) 
  stackEntry.stackPointerToCallerReturnAddress = pointerOf(callerReturnAddress)  
```

## Step 4: update stack head

Update the stack head to point to a new (unused) entry.
Check if the stack head overflows and exit if it does.

```
  DAT_20fe_3952_stackentry_list_head++;
  if (DAT_20fe_3952_stackentry_list_head > DAT_20fe_3956_stackentry_list_max_size) {
    exit("evm0004: ", "VM Manager Internal Stack Overflow.\r\n");
  }
}
```

# 22.10.2025 - FUN_20fe_0e52_module_alternative3

### Summary

This method is called from a call table structure in segment 0x2810. Initially these table entries looks like this:

    CALLF      FUN_20fe_0dab_module_loader_resolve_function_call      ; check which alternative to use and replace this call
    JMPF       LAB_0000_025e                                          ; jump to destination module. Jmp is replaced in FUN_20fe_0e52_module_alternative3 
    dw         19h                                                    ; module id (1 based)
    dw         E5h                                                    ; intra module segment

The call to `FUN_20fe_0dab_module_loader_resolve_function_call` checks which "module_alternative" variant is needed and 
replaces the call to `FUN_20fe_0dab_module_loader_resolve_function_call` with the correct alternative call.

`FUN_20fe_0e52_module_alternative3` is used when the call table contains the module id and a segment for relocation of 
the call.

The process in `FUN_20fe_0e52_module_alternative3` is multiple steps:

1. preamble: only used after the call is rewritten
2. check if module is loaded. if not, load the module, process relocations
3. add stack entry
4. rewrite jmp with loaded module segment (20fe:111d)
   rewrite call in FUN_20fe_154f_dynamic_call with loaded module segment
5. some housekeeping (remember previous module id and caller return address)
6. return
7. the relocated jmp in the call table structure will jump to the module function


Whole process is:

1. first call of the call table function (ie.e 0x2810:0000):
   * call points to `FUN_20fe_0dab_module_loader_resolve_function_call`
   * jmp points to 0000:<offset in module segment>
   * `FUN_20fe_0dab_module_loader_resolve_function_call` replaces the call with `FUN_20fe_0e52_module_alternative3`
   * `FUN_20fe_0dab_module_loader_resolve_function_call` calls `FUN_20fe_0e52_module_alternative3` without preamble
   * `FUN_20fe_0e52_module_alternative3` resolves (loads and relocates) the module,
      * relocates the jmp in the call table and
      * the call in `FUN_20fe_154f_dynamic_call`
2.  second call of the call table function (ie.e 0x2810:0000):
   * the call in the call table is replaces with `FUN_20fe_0e52_module_alternative3` with preamble
   * `FUN_20fe_0e52_module_alternative3` checks if the module is (still) loaded and possibly reloads the module and 
     relocates the jmp 
   * in the previous step the jmp target is refreshed with a possible reloaded module. so the jmp has a valid target 

### Analysis

Resolves the module and function to call via the function call table in segment 0x2810.

First time a module function is called looks like this:

* client calls a forwarding method in 0x2810
  ie: ` call 0x2810:0000` 
* The entry in 0x2810:0000 looks like this:
  ```
  2810:0000 9a ab 0d fe 20      CALLF      FUN_20fe_0dab_module_loader_resolve_function_call 
  2810:0005 ea 5e 02 00 00      JMPF       LAB_0000_025e
  2810:000a 19 00               dw         19h
  2810:000c e5 00               dw         E5h
  ```
* FUN_20fe_0dab_module_loader_resolve_function_call replaces the call at 2810:0000 with a call to 
  FUN_20fe_0e52_module_alternative3. Then it calls FUN_20fe_0e52_module_alternative3 while skipping 
  the preamble.

The second time a client calls the method, the call is already patched:

* client calls a forwarding method in 0x2810
  ie: ` call 0x2810:0000`
* The entry in 0x2810:0000 looks like this:
  ```
  2810:0000 9a 52 0e fe 20      CALLF      FUN_20fe_0e52_module_alternative3  <- patched 
  2810:0005 ea 5e 02 00 00      JMPF       LAB_0000_025e
  2810:000a 19 00               dw         19h
  2810:000c e5 00               dw         E5h
  ```
* FUN_20fe_0e52_module_alternative3 first executes the preamble to prepare the stack and then proceeds normally.

## Part 1 - Preamble

Difficult to write this in C. Record Stack postion of caller return address and the return address itself.
Push a lot of registers on stack.

```
// save position in stack where the return address is placed
// SP - 2: flags (via PUSHf)
// SP - 4: return address segment (via CALLF)
// SP - 6: return address offset (via CALLF) 
DAT_20fe_3983_SP_to__caller_return_address = SP - 6

// advance the SP by 12
// PUSH       AX, BX, DX, SI, DS, ES

DAT_20fe_397d_caller_return_offset = [SP - 6]
DAT_20fe_397d_caller_return_segment = [SP - 4]
```

## Part 2 - check if module is loaded. if not, load the module, process relocations 

`bp 12EB:0e93` to break at the start of part 1

## Part 3 - add stack entry

## Part 4: relocate jmp and call in "dynamic call" function

## Part 5: housekeeping

## Part 6: return

## Part 7: the relocated jmp in the call table structure will jump to the module function

## TODO

There is a lot of stuff not analyzed in the function. The execution flow is kinda complicated and jumps all over the place.

# 22.10.2025 - FUN_20fe_0dab_module_loader_resolve_function_call

Who decides which resolver function is used? 

It depends on the loader function called:

* `FUN_20fe_0d91_module_loader_no_params`: The call is from a call table and has no parameters (module id and module segment).
  This means that a) the call is into the main module and no module needs to be loaded and b) that the jmp after the call
  is already correctly located and does not need relocation rewrites. Note that this function just sets a global variable
  and jumps into the next function. The resolver is the alternative 4: `FUN_20fe_1341_module_resolve_call_table_main_module`  
* `FUN_20fe_0dab_module_loader_resolve_function_call`: The call is into a module, therefore after the jump is a module id and
  a module segment. The resolver is alternative 3 (described above): `FUN_20fe_0e52_module_resolve_call_table_module_call`


`FUN_20fe_0dab_module_loader_resolve_function_call` contains some code to rewrite calls from "not call tables". In this the rewriting
of the caller is much more complicated and the resolver functions are `FUN_20fe_1216_module_alternative1` and `FUN_20fe_147c_module_alternative2`.
I've not seen this kind of call, so maybe it is unused and all calls are through call tables. 

# 23.10.2025 - what role does the `FUN_20fe_154f_dynamic_call` play 

BP 12EB:1554

Seems like the call at 20fe:154f is rewritten, but never executed. Every return goes directly to 20fe:1554.  

    20fe:154f CALLF      SUB_0000_0000                                     ; never executed
    20fe:1554 MOV        byte ptr CS:[DAT_20fe_39e1_maybe_busy_flag],0xff
    20fe:155a CALL       FUN_20fe_1563_module_pop_module_stack             ; cleans up the module call stack
    20fe:155d RETF

The function `FUN_20fe_1563_module_pop_module_stack` cleans up the module stack and restores the correct return address
on the stack.

To correctly unwind the stack I have to pop one entry from the model stack each time the "dynamic call" is encountered 
and a stack cleanup is performed:


| flags | moduleIndex | caller    | stackPointerToCallerReturnAddress | codeSegment | stackStuff |
| ----- | ----------- | --------- | --------------------------------- | ----------- | ---------- |
| 0     | 0           | 0eee:0213 | ffff                              | 0000        | null       |
| 80    | 19          | 9f7c:0274 | c042                              | 9e97        | 12eb:1554  |

Caller is the correct return address. This may or may not be in a module. 

# 23.10.2025 - check FUN_8d90_0000_module_1b

create breakpoint in call table: `BP 1BFD:0E8F`

params (back to front):
 - 1D3A:BFA8 // DS    - fontintr.FF - filename      - SI
 - 1D3A:BEDE // DS    - nothing. maybe destination? - DI

# 24.10.2025 - check `FUN_8d7f_0008_module_1a_79_madspack_get_entry`

create breakpoint in call table: `BP 1BFD:0a7d`

    1D3A:C01E     54 15 EB 12 FA 2A 2E C0 13 00 FA 84 82 72 23 0C  T....*.......r#.
    1D3A:C02E     40 C0 54 15 EB 12 FA 2A FA 2A 03 00 00 00 01 00  @.T....*.*......

* process: copy filename, add '.FF' if it does not exist
* skip leading '*' in filename
* open archive
* get basename (first 8 chars of filename)
* call FUN_8f08_0138_module_1e

## FUN_8f08_0138_module_1e_get_named_memory_block

Looks like this gets some named memory block from the system. The memory block can be in conventional memory or EMS memory.

Input:
* memblock_name - far pointer to a 8 char string with the name for the memory block
* DX:AX - Size of the block?

Return
* DX:AX pointer to new block


# 25.10.2025 - Check what the stack trace looks like with other game resources

    bp 0eee:271E - int 21 open

## names.txt


    FUN_1d01_26d4_file_open_create_truncate(filename_ptr = "NAMES.TXT" (DS:bf00), attribute_and_flags = 4000, open_mode = 01a4, unknown = 01a4)
    FUN_1d01_168a_sth_io_with_filename(filename_ptr = "NAMES.TXT" (DS:bf00), unknown1 = 1fea, unknown2 = 0000, unknown3 = 28fe)
    FUN_1d01_04b4_io_with_filename_trunk(filename_ptr = "NAMES.TXT" (DS:bf00), open_mode = "rt" (DS:1fe8), unknown2 = 0000)
    FUN_1d01_04e0_stdio_fopen(filename_ptr = "NAMES.TXT" (DS:bf00), open_mode = "rt" (DS:1fe8))
    FUN_19da_0100_io_create_filename_and_open_it(something_on_stack1 = bf5e, data_segment_2b4d = 1d3a, unknown3 = 2afa, something_on_stack_copy = c02e)
    FUN_8778_001a_module_17(param_1 = 0872, param_2 = 2158)
    FUN_8a1f_14bc_module_19()
    FUN_8a1f_2a58_module_19_load_game_resources()
    FUN_8883_025e_module_18(param1 = 0001, param2 = c04c, param3 = c0a8)
    FUN_1d01_0156_main_entry_and_setup_stack()

## pedia.txt

    FUN_1d01_26d4_file_open_create_truncate(filename_ptr = "PEDIA.TXT" (DS:bf00), attribute_and_flags = 4000, open_mode = 01a4, unknown = 01a4)
    FUN_1d01_168a_sth_io_with_filename(filename_ptr = "PEDIA.TXT" (DS:bf00), unknown1 = 1fea, unknown2 = 0000, unknown3 = 28fe)
    FUN_1d01_04b4_io_with_filename_trunk(filename_ptr = "PEDIA.TXT" (DS:bf00), open_mode = "rt" (DS:1fe8), unknown2 = 0000)
    FUN_1d01_04e0_stdio_fopen(filename_ptr = "PEDIA.TXT" (DS:bf00), open_mode = "rt" (DS:1fe8))
    FUN_19da_0100_io_create_filename_and_open_it(something_on_stack1 = bf5e, data_segment_2b4d = 1d3a, unknown3 = 0090, something_on_stack_copy = 30ac)
    FUN_8778_001a_module_17(param_1 = 2298, param_2 = 228a)
    FUN_8a1f_14bc_module_19()
    FUN_8a1f_2a58_module_19_load_game_resources()
    FUN_8883_025e_module_18(param1 = 0001, param2 = c04c, param3 = c0a8)
    FUN_1d01_0156_main_entry_and_setup_stack()

## CURSOR.SS

    FUN_1d01_26d4_file_open_create_truncate(filename_ptr = "CURSOR.SS" (DS:bd88), attribute_and_flags = 8000, open_mode = 01a4, unknown = 01a4)
    FUN_1d01_168a_sth_io_with_filename(filename_ptr = "CURSOR.SS" (DS:bd88), unknown1 = 2385, unknown2 = 0000, unknown3 = 2906)
    FUN_1d01_04b4_io_with_filename_trunk(filename_ptr = "CURSOR.SS" (DS:bd88), open_mode = "rb" (DS:2383), unknown2 = 0000)
    FUN_1d01_04e0_stdio_fopen(filename_ptr = "CURSOR.SS" (DS:bd88), open_mode = "rb" (DS:2383))
    FUN_19da_0100_io_create_filename_and_open_it(something_on_stack1 = bfca, data_segment_2b4d = 1d3a, unknown3 = 2afa, something_on_stack_copy = c02e)
    FUN_8d90_0000_module_1b_open_madspack_archive(filename = "CURSOR.SS" (1d3a:bfca), buffer_offset = be36, buffer_segment = 1d3a)
    FUN_8d1c_000a_module_1a_16()
    FUN_8a1f_2a58_module_19_load_game_resources()
    FUN_8883_025e_module_18(param1 = 0001, param2 = c04c, param3 = c0a8)
    FUN_1d01_0156_main_entry_and_setup_stack()

## TERRAIN.SS - dump 10

    FUN_1d01_26d4_file_open_create_truncate(filename_ptr = "TERRAIN.SS" (DS:bd52), attribute_and_flags = 8000, open_mode = 01a4, unknown = 01a4)
    FUN_1d01_168a_sth_io_with_filename(filename_ptr = "TERRAIN.SS" (DS:bd52), unknown1 = 2385, unknown2 = 0000, unknown3 = 2906)
    FUN_1d01_04b4_io_with_filename_trunk(filename_ptr = "TERRAIN.SS" (DS:bd52), open_mode = "rb" (DS:2383), unknown2 = 0000)
    FUN_1d01_04e0_stdio_fopen(filename_ptr = "TERRAIN.SS" (DS:bd52), open_mode = "rb" (DS:2383))
    FUN_19da_0100_io_create_filename_and_open_it(something_on_stack1 = bf94, data_segment_2b4d = 1d3a, unknown3 = 0000, something_on_stack_copy = c02e)
    FUN_8d90_0000_module_1b_open_madspack_archive(filename = "TERRAIN.SS" (1d3a:bf94), buffer_offset = be00, buffer_segment = 1d3a)
    FUN_8d1c_000a_module_1a_16_load_ss_sprite_sheet()
    FUN_8906_0b0a_module_19_read_sprite_sheet(filename = "terrain" (DS:20aa))
    FUN_8906_0bbe_module_19_read_terrain_sprites()
    FUN_8a1f_2a58_module_19_119_load_game_resources()
    FUN_8883_025e_module_18(param1 = 0001, param2 = c04c, param3 = c0a8)
    FUN_1d01_0156_main_entry_and_setup_stack()



# 26.10.2025 - FUN_8a1f_073c_module_19_load_savegame - create structures in memorry

    bp 1BFD:0cfd (2a10:0cfd)


# 28.10.2025 - FUN_8000_1594_module_15  

    bp 19FD:1409 (2810:1409)

# 30.10.2025 - FUN_8007_0938 - doing something with terrain tiles

FUN_8007_0938
  calls FUN_8007_06e0 ( 1, isSeaTile, 0)
  FUN_8007_0558()


# 01.11.2025 - FUN_8117_0eac_module_15 

    bp 19FD:1409 (2810:1409)

this function

* draws a pix (woodpanel background)
* build some strings together and draws these string


looks like the function is never called.

# 02.11.2025 -  FUN_43d0_0842_module_1_draw_tile_tooltip_in_colony 

bp 19fd:160c (2810:160c)

looks like the function is not called when the tooltip is displayed

0x2391c  0x23882

# 02.11.2025 -  FUN_13e0_0002

The function updates the visibility (fog of war) bitfield and the "last visitor".

Note: it seems the "last visitor" is only updated when there is not yet one. So this might be the "first visitor" and not the last. 

       13e0:002a 9a 04 02 73 13      CALLF      FUN_1373_0204_visitor_get_last_visitor                                 get last visitor
       13e0:002f 83 c4 04            ADD        SP,0x4
       13e0:0032 0a c0               OR         AL,AL                                                                  is there a last visitor?
       13e0:0034 7d 1b               JGE        LAB_13e0_0051                                                          yes, skip update last visitor
             [..] 
       13e0:0049 9a 2c 02 73 13      CALLF      FUN_1373_022c_visitor_set_last_visited                                 undefined FUN_1373_022c_visitor_set_last_visited(undefined2 x, undefine
       13e0:004e 83 c4 06            ADD        SP,0x6
             ; this is the target if there already was a last visitor
                             LAB_13e0_0051                                   XREF[2]:     13e0:0034(j), 13e0:0042(j)  
       13e0:0051 8b c6               MOV        AX,SI


# 04.11.2025 - FUN_83d9_03ec - module 16

    bp 19FD:0cfd (2810:043d)

The function copies some strings.

# 07.11.2025 - FUN_1009_00b4

    bp 01F6:00b4 (1009:00b4)

draw rectangle
x = 0
y = 0
width = 0x140 // 320
height = 0x7

draw line
offset: 0x936A
sprite:
    width:   0018
    height:  0020
    offset:  0000
    segment: 608D

18 00

# 09.11.2025: analyze of pik and ss files

* terrain - base terrain tiles
* phys0 - everything on the surface

# Introduction to AI 

I'm reverse engineering the old MS DOS Game Colonization from 1994. The game resulting artifacts is X86 Real Mode Assembly. Parts of the game is hand written assembly and parts are c.

I want to rewrite the functions in java to ease the cross referencing of functions and fields, as far as it is possible.
I've moved all global data (DAT_2b4d_*) to its own "Data" class with static fields for each data item. The data class im imported with a wildcard import.
The naming for the global data fields is DAT_<offset>_<purpose>, so I removed the segment part (2b4d) from the name.   


I send you some assembly functions and you should:
a) analyze the purpose of the function
b) if the function is small enough, do a deep analyze

# TODO

* FUN_20fe_1ebd_load_module_entry: check which flags are set in moduleConfig 
* FUN_8117_03bc_module_15 - uses the back buffers and the font address
* FUN_8f53_0004_module_1e - memory stuff
* FUN_8a1f_24f8_module_19_119_main_menu

DS = 2B4D

Module 0    4000
Module 1    43d0
Module 9    5fea
Module f    7478
Module a    6158
Module b    61e8
Module 12	7adf
Module 16 	83d9
Module 17   8778
Module 1c   8e33


4da1:1633
4ccd

4da1:6300