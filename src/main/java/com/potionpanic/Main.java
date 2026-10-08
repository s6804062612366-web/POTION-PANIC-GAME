package com.potionpanic;

import com.potionpanic.ui.GameWindow;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * คลาสเริ่มต้นของโปรแกรม (Main Entry Point)
 * โครงงานวิชาการเขียนโปรแกรมเชิงวัตถุ (OOP Project)
 * นาย ปภาวิน โกการัตน์ 6804062612366 Section 3
 */
public class Main {
    public static void main(String[] args) {
        // ตั้งค่า Look and Feel ให้เข้ากับระบบปฏิบัติการ
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // รันหน้าต่างเกมบน Event Dispatch Thread (EDT) เพื่อความปลอดภัยของ Swing UI
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}
