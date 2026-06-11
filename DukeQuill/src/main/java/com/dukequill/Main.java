package com.dukequill;

import com.dukequill.gui.MainWindow;

public class Main {
    public static void main (String[]args){
            javax.swing.SwingUtilities.invokeLater(() -> {
            MainWindow gui = new MainWindow();
            gui.setVisible(true);
        });   
    }
}
