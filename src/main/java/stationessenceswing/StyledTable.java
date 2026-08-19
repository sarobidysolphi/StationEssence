package stationessenceswing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;

public class StyledTable extends JTable {

    public StyledTable(TableModel model) {
        super(model);
        setFont(Theme.POLICE_NORMALE);
        setRowHeight(40);
        setBackground(Theme.FOND_CARTE);
        setForeground(Theme.TEXTE_FONCE);
        setGridColor(Theme.BORDURE_CLAIRE);
        setSelectionBackground(Theme.TABLE_SELECTION);
        setSelectionForeground(Theme.TEXTE_FONCE);
        setShowVerticalLines(false);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        JTableHeader header = getTableHeader();
        header.setBackground(Theme.TABLE_HEADER_FOND);
        header.setForeground(Theme.TABLE_HEADER_TEXTE);
        header.setFont(Theme.POLICE_GRAS);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(new javax.swing.border.MatteBorder(0, 0, 1, 0, Theme.BORDURE_CLAIRE));

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Theme.FOND_CARTE : Theme.TABLE_LIGNE_ALTERNE);
                    setForeground(Theme.TEXTE_FONCE);
                }
                return this;
            }
        });
    }
}
