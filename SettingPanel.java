import javax.swing.*;
// import java.awt.*;
// import java.awt.event.*;
// import javax.swing.border.LineBorder;
// import java.util.*;

class SettingPanel extends JPanel {
  JButton returnButton;
  SettingPanel() {
    setLayout(null);
    returnButton = new JButton("Return Title");
    returnButton.setBounds(400, 425, 250, 100);
    add(returnButton);
  }
}