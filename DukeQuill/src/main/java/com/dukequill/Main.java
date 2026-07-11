package com.dukequill;

import javax.swing.UIManager;
import com.dukequill.gui.MainWindow;
import com.formdev.flatlaf.*;

public class Main {
    public static void main (String[]args) throws Exception{
        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("flatlaf.useWindowDecorations", "false");
            
        FlatLightLaf.setup();        
        MainWindow gui = new MainWindow();
        javax.swing.SwingUtilities.invokeLater(() -> {
            gui.setVisible(true);
        });   
    }
}
