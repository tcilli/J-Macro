package com.phukka.macro.injection;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public interface MemoryManipulation extends Library {

    MemoryManipulation INSTANCE = (MemoryManipulation) Native.load("memhook.dll", MemoryManipulation.class);

    boolean ReadMemory(WinNT.HANDLE hProcess, WinDef.LPVOID lpBaseAddress, PointerByReference lpBuffer, int nSize);
    boolean WriteMemory(WinNT.HANDLE hProcess, WinDef.LPVOID lpBaseAddress, Pointer lpBuffer, int nSize);
}