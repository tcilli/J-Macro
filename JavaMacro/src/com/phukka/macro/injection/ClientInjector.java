package com.phukka.macro.injection;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class ClientInjector {

    public static void main(String[] args) {
        listModulesFor("Runescape");
    }

    public static WinNT.HANDLE openProcess(int dwDesiredAccess, int dwProcessId) {
        return Kernel32.INSTANCE.OpenProcess(dwDesiredAccess, false, dwProcessId);
    }

    public static int getProcessId(String window) {
        User32 user32 = User32.INSTANCE;
        IntByReference pid = new IntByReference(0);
        user32.GetWindowThreadProcessId(user32.FindWindow(null, window), pid);
        return pid.getValue();
    }



    public static void listModulesFor(String window) {
        int pid = getProcessId(window);
        WinNT.HANDLE process = openProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, pid);

        if (process != null) {
            WinDef.HMODULE[] hMods = new WinDef.HMODULE[1024];
            Psapi.INSTANCE.EnumProcessModules(process, hMods, hMods.length, new IntByReference(hMods.length * Native.POINTER_SIZE));

            int moduleCount = hMods.length > 1024 ? 1024 : hMods.length;

            for (int i = 0; i < moduleCount; i++) {
                if (hMods[i] != null) {
                    char[] moduleName = new char[1024];
                    Psapi.INSTANCE.GetModuleFileNameExW(process, hMods[i], moduleName, moduleName.length);
                    String moduleNameStr = Native.toString(moduleName);
                    if (!moduleNameStr.isEmpty()) {
                        System.out.println("Module " + i + ": " + moduleNameStr);

                        if (moduleNameStr.contains("rs2client")) {
                            System.out.println("Found window: " + window);
                            WinDef.LPVOID baseAddress = new WinDef.LPVOID(hMods[i].getPointer());
                            System.out.println(baseAddress);
                            PointerByReference buffer = new PointerByReference();
                            int sizeToRead = 1; // Specify the number of bytes to read

                            // Check if ReadMemory is successful
                            if (MemoryManipulation.INSTANCE.ReadMemory(process, baseAddress, buffer, sizeToRead)) {
                                // Access the read memory through the PointerByReference
                                byte[] readMemory = buffer.getValue().getByteArray(0, sizeToRead);
                                System.out.println(readMemory.length);
                            } else {
                                System.out.println("Failed to read the memory.");
                            }
                        }
                    }
                }
            }
            Kernel32.INSTANCE.CloseHandle(process);

        } else {
            System.out.println("Failed to open the process.");
        }
    }
}
