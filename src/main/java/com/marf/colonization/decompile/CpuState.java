package com.marf.colonization.decompile;

import lombok.Data;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class CpuState {
    private Address cs;
    private Address ip;
    private Address ax;
    private Address bx;
    private Address cx;
    private Address dx;
    private Address si;
    private Address di;
    private Address bp;
    private Address sp;
    private Address ds;
    private Address es;
    private Address fs;
    private Address gs;
    private Address ss;
    // flags
    private Address flags;

    public static CpuState fromString(String cpuStateString) {
        String[] parts = cpuStateString.split(" +");
        Map<String, Address> map = Arrays.stream(parts)
                // ony use stuff which looks like a register
                .filter(part -> part.contains(":"))
                // split register and value
                .map(part -> part.split(":"))
                // create a map with [register] -> [value as Address]
                .collect(Collectors.toMap(
                        regValue -> regValue[0],
                        regValue -> new Address(Integer.parseInt(regValue[1], 16))
                ));


        CpuState cpuState = new CpuState();
        cpuState.ax = map.get("EAX");
        cpuState.bx = map.get("EBX");
        cpuState.cx = map.get("ECX");
        cpuState.dx = map.get("EDX");
        cpuState.si = map.get("ESI");
        cpuState.di = map.get("EDI");
        cpuState.bp = map.get("EBP");
        cpuState.sp = map.get("ESP");
        cpuState.ds = map.get("DS");
        cpuState.es = map.get("ES");
        cpuState.ss = map.get("SS");
        cpuState.flags = map.get("FLG");

        // CS:IP is the first part
        String[] csIp = parts[0].split(":");
        cpuState.cs = new Address(Integer.parseInt(csIp[0], 16));
        cpuState.ip = new Address(Integer.parseInt(csIp[1], 16));

        return cpuState;
    }


    public static void main(String[] args) {
        CpuState cpustate = fromString("0BFA:0000000A  push ax                                                 50                    EAX:00008000 EBX:00000016 ECX:00008000 EDX:0000BE5A ESI:00008000 EDI:0000BEDE EBP:0000BE16 ESP:0000BE0C DS:1D3A ES:1D3A FS:0000 GS:0000 SS:1D3A CF:0 ZF:1 SF:0 OF:0 AF:0 PF:1 IF:0 TF:0 VM:0 FLG:00007046 CR0:00000000");
        System.out.println(cpustate);
    }
}
