package com.dukequill;

import com.dukequill.gui.MainWindow;

public class Main {
    public static void main (String[]args) throws Exception{
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
            System.setProperty("file.encoding", "UTF-8");
            
            MainWindow gui = new MainWindow();
            javax.swing.SwingUtilities.invokeLater(() -> {
            gui.setVisible(true);
        });   
    }
}
