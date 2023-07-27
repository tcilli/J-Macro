package macro.win32.callbacks;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import macro.Main;

public class MouseCallback {

    public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.MSLLHOOKSTRUCT lParam) {
        if (nCode >= 0) {
            switch (wParam.intValue()) {
                case 0x0201 -> Main.getScriptContainer().handleKey((short) 2000);
                case 0x0202 -> Main.getScriptContainer().handleKey((short) -2000);
                case 0x0204 -> Main.getScriptContainer().handleKey((short) 2001);
                case 0x0205 -> Main.getScriptContainer().handleKey((short) -2001);
                case 0x0207 -> Main.getScriptContainer().handleKey((short) 2002);
                case 0x0208 -> Main.getScriptContainer().handleKey((short) -2002);

                case 0x020B, 0x020C -> {
                    int whichButton = (wParam.intValue() >> 16) & 0xFFFF;
                    int type = (wParam.intValue() == 0x020B) ? 1 : 2;
                    int button = (whichButton == 0x0001) ? (type == 1 ? 2003 : -2003) : (type == 1 ? 2004 : -2004);
                    Main.getScriptContainer().handleKey((short) button);
                }
            }
        }
        return User32.INSTANCE.CallNextHookEx(null, nCode, wParam, new WinDef.LPARAM(Pointer.nativeValue(lParam.getPointer())));
    }
}