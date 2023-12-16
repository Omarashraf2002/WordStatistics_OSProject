
package wordstatisticsapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class WordStatisticsApp extends JFrame {
    private JTextField directoryField;
    private JCheckBox subdirectoryCheckBox;
    private JButton browseButton; // Added browse button
    private JButton processButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public WordStatisticsApp() {
        setTitle("Word Statistics App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel directoryLabel = new JLabel("Directory:");
        directoryField = new JTextField(20);
        inputPanel.add(directoryLabel);
        inputPanel.add(directoryField);
        
        browseButton = new JButton("Browse"); // Added browse button
        inputPanel.add(browseButton); // Added browse button

        subdirectoryCheckBox = new JCheckBox("Include Subdirectories");
        inputPanel.add(subdirectoryCheckBox);
        processButton = new JButton("Start Processing");
        inputPanel.add(processButton);
        
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        tableModel.addColumn("File Name");
        tableModel.addColumn("Word Count");
        tableModel.addColumn("is Count");
        tableModel.addColumn("are Count");
        tableModel.addColumn("you Count");
        tableModel.addColumn("Longest Word");
        tableModel.addColumn("Shortest Word");

        JScrollPane tableScrollPane = new JScrollPane(resultTable);

        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        browseButton.addActionListener(e -> browseDirectory()); // Added browse button action listener
        processButton.addActionListener(e -> processDirectory());

        setVisible(true);
    }

    private void browseDirectory() { // Added browseDirectory method
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            directoryField.setText(selectedFile.getAbsolutePath());
        }
    }
    private void processDirectory() {
        String directoryPath = directoryField.getText();
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Invalid directory");
            return;
        }

        processFiles(directory, subdirectoryCheckBox.isSelected());
        findLongestAndShortestWords(directory);
    }

    private void processFiles(File directory, boolean includeSubdirectories) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && includeSubdirectories) {
                    processFiles(file, true);
                } else if (file.isFile() && file.getName().endsWith(".txt")) {
                    processFile(file, file.getName());
                }
            }
        }
    }

    private void processFile(File file, String filename) {
        int wordCount = 0;
        String longestWord = "";
        String shortestWord = "";
        int isCount = 0;
        int areCount = 0;
        int youCount = 0;

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] words = line.split("\\s+");
                for (String word : words) {
                    wordCount++;
                    if (word.length() > longestWord.length()) {
                        longestWord = word;
                    }
                    if (shortestWord.isEmpty() || word.length() < shortestWord.length()) {
                       shortestWord = word;
                    }
                    if (word.equalsIgnoreCase("is")) {
                        isCount++;
                    } else if (word.equalsIgnoreCase("are")) {
                        areCount++;
                    } else if (word.equalsIgnoreCase("you")) {
                        youCount++;
                    }
                }
            }

            tableModel.addRow(new String[]{filename, String.valueOf(wordCount), String.valueOf(isCount), String.valueOf(areCount), String.valueOf(youCount), longestWord, shortestWord});

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while reading files");
        }
    }

    private void findLongestAndShortestWords(File directory) {
        String longestWordInDirectory = "";
        String shortestWordInDirectory = "";

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    try {
                        List<String> lines = Files.readAllLines(file.toPath());
                        for (String line : lines) {
                            String[] words = line.split("\\s+");
                            for (String word : words) {
                                if (longestWordInDirectory.isEmpty() || word.length() > longestWordInDirectory.length()) {
                                    longestWordInDirectory = word;
                                }
                                if (shortestWordInDirectory.isEmpty() || word.length() < shortestWordInDirectory.length()) {
                                    shortestWordInDirectory = word;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (file.isDirectory()) {
                    findLongestAndShortestWords(file);
                }
            }
        }

        System.out.println("Longest word in directory: " + longestWordInDirectory);
        System.out.println("Shortest word in directory: " + shortestWordInDirectory);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WordStatisticsApp::new);
    }
}


