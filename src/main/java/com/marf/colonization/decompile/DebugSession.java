package com.marf.colonization.decompile;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class DebugSession {
    private Address ghidraMainCodeSegment = new Address(0x20fe);


    private Scanner scanner;
    private Path userHome = Path.of(System.getProperty("user.home"));
    private byte[] codeSegmentDump;

    public static void main(String[] args) throws IOException {
        new DebugSession().run();
    }

    private void run() throws IOException {
        this.scanner = new Scanner(System.in);
        /*
        // mostly: 12eb
        System.out.print("Main CS: ");
        Address dosboxMainCodeSegment = new Address(scanner.next());
        Address diff = ghidraMainCodeSegment.sub(dosboxMainCodeSegment);
        System.out.println("diff: "+diff); // mostly 0xE13

        Address segmentOfOpenFileFunction = new Address(0x1d01).sub(diff);
        System.out.println("open function: bp "+segmentOfOpenFileFunction+":271e");
        System.out.println("skip until the desired file is opened (press enter)");
        scanner.next();
        System.out.println("export memory: memdumpbin 0:0 100000");
        System.out.println("export cpu state: log 1   (press enter when done)");
        scanner.next();

        // rename code segment dump
        Files.move(userHome.resolve("MEMDUMP.BIN"), userHome.resolve("MEMDUMP.BIN.cs"));

        System.out.println("export main data segment: memdumpbin ds:0 ffff   (press enter when done)");
        scanner.next();

        // rename data segment dump
        Files.move(userHome.resolve("MEMDUMP.BIN"), userHome.resolve("MEMDUMP.BIN.ds"));
        */

        // bp 0eee:271E - int 21 open
        // bp 0eee:04f1 - fopen
        // bp 12eb:1563 - module loader - FUN_20fe_1563_module_execution_dispatcher
        // bp 0BC7:0100 - file read
        // bp 12eb:0dab - FUN_20fe_0dab_module_loader_resolve_function_call

        // open memory dump
        this.codeSegmentDump = Files.readAllBytes(userHome.resolve("MEMDUMP.BIN.cs"));


        System.out.println("Module Loader");
        addValue(0x155e, "saved stack pointer SP");

        addSegmentOffset(0x3952, "segment:offset of current module queue pointer");


        addValue(0x3958, "stackelement[4], current module index");
        addValue(0x395a, "segment(paragraph) to current module config");
        addValue(0x395c, "stackelement[6], is a stack offset");
        addValue(0x3973, "ptr zum pfad von viceroy");
        addValue(0x3975, "handle zum viceroy");
        addValue(0x3977, "unbekannt");
        addValue(0x3979, "ModulLoader: segment wo der erste paragraph hingeladen wird");
        addValue(0x397b, "ModulLoader: code segment des moduls");

        addValue(0x397d, "FUN_20fe_0dab_module_loader_resolve_function_call: caller return offset");
        addValue(0x397f, "FUN_20fe_0dab_module_loader_resolve_function_call: caller return segment");
        addValue(0x3981, "FUN_20fe_2fd2_some_stuff: offset of the call (caller return address - size of the call instuction)");
        addValue(0x3983, "FUN_20fe_0dab_module_loader_resolve_function_call: SP pointing to the caller return address");


        addValue(0x399b, "segment (paragraph) to list of module headers?");
        addValue(0x399d, "stackelement[8]");


        System.out.println("Module Loader");
        addValue(0x39e1, "set to -1 after a relocated module function is called, maybe busy flag");
        addValue(0x39e2, "Module Loader Flag, maybe global error");


        addValue(0x3a07, "offset where the file position is standing");

        addValue(0x5d91, "default size for memory allocation");
        addValue(0x5d93, "default size for memory allocation");

        addValue(0x5dab, "global memory usage in paragraphs");

        addValue(0x5db3, "global memory pool pointer");
        addValue(0x5db7, "some memory management pool linked list head");
        addValue(0x5db9, "some memory management pool linked list tail");

        addValue(0x5e24, "start of the module table");
        // dword: size of code block
        // dword: offset in viceroy.exe
        // dword: number of the module

        // ds stuff (1d3a):
        // [1fe2] - FILE* von fontintr.FF (oder dem gerade geöffneten File)
        // [1fe4] - "TXT" file extension
        // [28d6] - [0x2a16] - array of file entries, 8 bytes each
        //    [SI + 0x0] - Unknown (pointer/handle?) - set to 0 when freeing
        //    [SI + 0x2] - Unknown - set to 0 when freeing
        //    [SI + 0x4] - Unknown - set to 0 when freeing
        //    [SI + 0x6] - Status flags byte
        //          0x0c - buffer flags
        //          0x10 - End of file reached
        //          0x20 - Read error occurred
        //    [SI + 0x7] - int21 file handle - set to 0xFF when freeing
        // [2A8A] - increased when a file is successfully opened
        // [82f0] - line buffer for text file processing
        // [a5b8] - first free byte after line buffer 0x82f0


        readModuleStack();
        readModuleConfig();
    }

    public void addValue(int address, String description) {
        System.out.println("0x04x: %x (%s)".formatted(address, readShort(address), description));
    }

    public void addSegmentOffset(int address, String description) {
        int offset = readShort(address);
        int segment = readShort(address);
        System.out.println("0x04x: %04x:%04x (%s)".formatted(address, segment, offset, description));
    }

    private void readModuleConfig() {
        var segment = new Address(readShort(0x399b));
        System.out.println("-------- Module Config --------");
        System.out.println("Segment: "+segment);
    }


    private void readModuleStack() {
        var segment = new Address(readShort(0x3954));
        var head = new Address(readShort(0x3952));

        System.out.println("-------- Module Stack --------");
        System.out.println("Segment: "+segment);
        System.out.println("Head: "+head);
    }

    private int readShort(int offset) {
        return (codeSegmentDump[offset] & 0xff) + ((codeSegmentDump[offset+1] & 0xff) << 8);
    }
}
