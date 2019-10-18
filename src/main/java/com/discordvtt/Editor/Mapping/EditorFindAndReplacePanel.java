package com.discordvtt.Editor.Mapping;

import com.discordvtt.Data.GameMap;
import com.discordvtt.Engine.SpecialText;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * Created by Jared on 3/25/2018.
 */
public class EditorFindAndReplacePanel extends JPanel {

    /**
     * EditorFindAndReplacePanel:
     *
     * The Editor Window that allows the user to find and replace SpecialText in the Level backdrop.
     * Also has a randomize feature, so randomized, textured environments aren't too hard to generate.
     */

    private JSpinner replaceChanceSpinner;
    
    public EditorFindAndReplacePanel(GameMap gamemap, UndoManager undoManager){

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createTitledBorder("Find and Replace"));

        int fieldWidth = 3;
        int hgap = 5;

        SingleCharacterField findField = new SingleCharacterField(fieldWidth);
        add(findField);

        add(new JLabel(" --> "));

        SingleCharacterField replaceField = new SingleCharacterField(fieldWidth);
        add(replaceField);

        add(Box.createHorizontalStrut(hgap));

        JPanel randomPanel = new JPanel();
        randomPanel.setBorder(BorderFactory.createEtchedBorder());
        randomPanel.setLayout(new BoxLayout(randomPanel, BoxLayout.LINE_AXIS));

        replaceChanceSpinner = new JSpinner(new SpinnerNumberModel(100, 0, 100, 1));
        randomPanel.add(new JLabel("Chance (%): "));
        randomPanel.add(replaceChanceSpinner);

        add(randomPanel);
        add(Box.createHorizontalStrut(hgap));

        JButton replaceButton = new JButton("Replace");
        replaceButton.addActionListener(e -> {
            int chance = ((SpinnerNumberModel)replaceChanceSpinner.getModel()).getNumber().intValue();
            SpecialText findText = getSpecTextFromField(findField);
            SpecialText replaceText = getSpecTextFromField(replaceField);
            gamemap.getBackdrop().findAndReplace(findText, replaceText, chance);
            undoManager.recordGameMap();
        });
        add(replaceButton);

        validate();
    }

    private SpecialText getSpecTextFromField(JTextField textField){
        if (textField.getText().length() > 0)
            return new SpecialText(textField.getText().charAt(0), MappingTheme.BACKDROP_FONT_COLOR, new Color(0, 0, 0, 0));
        return null;
    }

    private class SingleCharacterField extends JTextField {

        private SingleCharacterField(int columns){
            super(columns);
            setHorizontalAlignment(JTextField.CENTER);
            getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    trimField();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    //trimField();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    //trimField();
                }
            });
        }

        private void trimField(){
            String fieldText = getText();
            if (fieldText.length() > 1){
                Timer timer = new Timer(20, e -> {
                    int strLength = Math.max(0, fieldText.length());
                    setText(fieldText.substring(strLength - 1));
                });
                timer.setRepeats(false);
                timer.start();
            }
        }

    }


    /*
    //Copied from StackOverflow, url: https://stackoverflow.com/questions/3679886/how-can-i-let-jtoolbars-wrap-to-the-next-line-flowlayout-without-them-being-hi
    //
    //It is not used here, but in case I need it I'm not going to fish for it again from the Internet.
    //So here it will stay
    //
    private class ModifiedFlowLayout extends FlowLayout {
        public ModifiedFlowLayout() {
            super();
        }

        public ModifiedFlowLayout(int align) {
            super(align);
        }
        public ModifiedFlowLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        public Dimension minimumLayoutSize(Container target) {
            // Size of largest component, so we can resize it in
            // either direction with something like a split-pane.
            return computeMinSize(target);
        }

        public Dimension preferredLayoutSize(Container target) {
            return computeSize(target);
        }

        private Dimension computeSize(Container target) {
            synchronized (target.getTreeLock()) {
                int hgap = getHgap();
                int vgap = getVgap();
                int w = target.getWidth();

                // Let this behave like a regular FlowLayout (single row)
                // if the container hasn't been assigned any size yet
                if (w == 0) {
                    w = Integer.MAX_VALUE;
                }

                Insets insets = target.getInsets();
                if (insets == null){
                    insets = new Insets(0, 0, 0, 0);
                }
                int reqdWidth = 0;

                int maxwidth = w - (insets.left + insets.right + hgap * 2);
                int n = target.getComponentCount();
                int x = 0;
                int y = insets.top + vgap; // FlowLayout starts by adding vgap, so do that here too.
                int rowHeight = 0;

                for (int i = 0; i < n; i++) {
                    Component c = target.getComponent(i);
                    if (c.isVisible()) {
                        Dimension d = c.getPreferredSize();
                        if ((x == 0) || ((x + d.width) <= maxwidth)) {
                            // fits in current row.
                            if (x > 0) {
                                x += hgap;
                            }
                            x += d.width;
                            rowHeight = Math.max(rowHeight, d.height);
                        }
                        else {
                            // Start of new row
                            x = d.width;
                            y += vgap + rowHeight;
                            rowHeight = d.height;
                        }
                        reqdWidth = Math.max(reqdWidth, x);
                    }
                }
                y += rowHeight;
                y += insets.bottom;
                return new Dimension(reqdWidth+insets.left+insets.right, y);
            }
        }

        private Dimension computeMinSize(Container target) {
            synchronized (target.getTreeLock()) {
                int minx = Integer.MAX_VALUE;
                int miny = Integer.MIN_VALUE;
                boolean found_one = false;
                int n = target.getComponentCount();

                for (int i = 0; i < n; i++) {
                    Component c = target.getComponent(i);
                    if (c.isVisible()) {
                        found_one = true;
                        Dimension d = c.getPreferredSize();
                        minx = Math.min(minx, d.width);
                        miny = Math.min(miny, d.height);
                    }
                }
                if (found_one) {
                    return new Dimension(minx, miny);
                }
                return new Dimension(0, 0);
            }
        }

    }
    */
}
