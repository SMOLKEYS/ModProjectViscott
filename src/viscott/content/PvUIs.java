package viscott.content;

import viscott.world.ui.PvUI;

public class PvUIs {
    public static PvUI
        extraUI;
    public static void load()
    {
        extraUI = new PvUI();
    }
    public static void init()
    {
        extraUI.init();
    }
}
