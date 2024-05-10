package pcd.ass02.rx;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WebWordFinderGUIRx extends JFrame {
    private JTextField urlField;
    private JTextField wordField;
    private JTextField depthField;
    private JTable outputTable;
    private JButton analyzeButton;
    private JButton clearButton;
    private WebWordFinderRx webAnalyzer;
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WebWordFinderGUIRx().setVisible(true);
            }
        });
    }


    public WebWordFinderGUIRx() {
        setTitle("Web Word Finder");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 3));
        inputPanel.add(new JLabel("URL:"));
        urlField = new JTextField();
        inputPanel.add(urlField);

        inputPanel.add(new JLabel("Word:"));
        wordField = new JTextField();
        inputPanel.add(wordField);

        inputPanel.add(new JLabel("Depth:"));
        depthField = new JTextField();
        inputPanel.add(depthField);

        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Depth", "URL", "Occurrences"}, 0);
        outputTable = new JTable(tableModel);
        outputTable.getColumnModel().getColumn(0).setPreferredWidth((int)(this.getWidth() * 0.10));
        outputTable.getColumnModel().getColumn(1).setPreferredWidth((int)(this.getWidth() * 0.70));
        outputTable.getColumnModel().getColumn(2).setPreferredWidth((int)(this.getWidth() * 0.20));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        outputTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        outputTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        add(new JScrollPane(outputTable), BorderLayout.CENTER);

        analyzeButton = new JButton("Analyze");
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = urlField.getText();
                String word = wordField.getText();
                int depth = Integer.parseInt(depthField.getText());

                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        webAnalyzer = new WebWordFinderRx(word, depth, WebWordFinderGUIRx.this);
                        webAnalyzer.analyze(url);
                        return null;
                    }
                }.execute();
            }
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(analyzeButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0);
            }
        });
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void print(WebWordFinderRx.AnalyzeResult result) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tableModel.addRow(new Object[]{result.depth(), result.url(), result.occurences()});
            }
        });
    }
}
