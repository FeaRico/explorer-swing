package ru.mahach.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UI extends JFrame {

    private final int ROOT_LEVEL = 1;
    private final int MOUSE_DOUBLE_CLICK = 2;

    private JList filesList = new JList();

    private JPanel catalogPanel = new JPanel();
    private final JPanel controlPanel = new JPanel();
    private final JPanel createNewDirectoryPanel = new JPanel();

    private final JDialog createNewDirectoryDialog = new JDialog(UI.this, "Создание папки", true);

    private final JScrollPane filesScroll = new JScrollPane(filesList);

    private final JButton createFolder = new JButton("Создать папку");
    private final JButton previousFolder = new JButton("Вернуться");
    private final JButton deleteFolder = new JButton("Удалить");
    private final JButton renameFolder = new JButton("Переименовать");

    private final List<String> currentDirectory = new ArrayList<>();
    private final DefaultListModel listModel = new DefaultListModel();

    private final Logger logger = Logger.getLogger(UI.class.getName());

    public UI(){
        super("Проводник");
        configureControlPanel();
        configureFilesScroll();
        configureCatalogPanel();
        configureCreateNewDirectoryDialog();
        initializeFileList();
        fileListListeners();
        previousFolderListeners();
        configureWindow();
    }

    private void configureWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        getContentPane().add(this.catalogPanel);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        logger.info("Configure main frame successful");
    }

    private void configureCatalogPanel(){
        this.catalogPanel.setLayout(new BorderLayout(5,5));
        this.catalogPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        this.catalogPanel.add(this.filesScroll, BorderLayout.CENTER);
        this.catalogPanel.add(this.controlPanel, BorderLayout.SOUTH);
        logger.info("Configure catalog panel successful");
    }

    private void configureControlPanel(){
        this.controlPanel.add(this.createFolder);
        this.controlPanel.add(this.previousFolder);
        this.controlPanel.add(this.deleteFolder);
        this.controlPanel.add(this.renameFolder);
        this.controlPanel.setLayout(new GridLayout(1, 4, 5,5));
        this.controlPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        logger.info("Configure control panel successful");
    }

    private void configureCreateNewDirectoryDialog(){
        this.createNewDirectoryDialog.add(this.createNewDirectoryPanel);
        logger.info("Configure dialog \"Create New Directory\" successful");

    }

    private void configureFilesScroll(){
        this.filesScroll.setPreferredSize(new Dimension(400,500));
        logger.info("Configure files scroll successful");
    }


    private void initializeFileList(){
        File[] disks = File.listRoots();
        this.filesList.setListData(disks);
        configureFilesList();
    }

    private void configureFilesList(){
        this.filesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        logger.info("Configure files list successful");
    }

    private void previousFolderListeners(){
        this.previousFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int lastIndexDirectory = currentDirectory.size() - 1;
                currentDirectory.remove(lastIndexDirectory);
                String fullPath = provideFullPath(currentDirectory);
                updateFileListModelIfSelectedDirectory(fullPath);
            }
        });
    }

    private void fileListListeners(){
        this.filesList.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == MOUSE_DOUBLE_CLICK)
                    updateFileList();
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });
    }

    private void updateFileList(){
        String selected = filesList.getSelectedValue().toString(); //Выбраный элемент

        clearListModelIfContainsData();
        updateFileListModelIfSelectedDirectory(selected);

        logger.info("Update file list successful");
    }


    private void updateFileListModelIfSelectedDirectory(String selected){
        File currentFile = provideRootOrCatalog(selected);

        if(currentFile.isDirectory()){
            String[] rootElements = currentFile.list();

            assert rootElements != null;
            for(String element : rootElements){
                File currentElementOfRoot = new File(currentFile.getPath(), element);
                addElementToModelIfNotHidden(currentElementOfRoot);
            }

            addFolderToCurrentDirectory(selected);
            filesList.setModel(this.listModel);

        }

    }

    @SuppressWarnings("unchecked")
    private void addElementToModelIfNotHidden(File currentElementOfRoot){
        if(!currentElementOfRoot.isHidden()){

            if(currentElementOfRoot.isDirectory()){
                this.listModel.addElement(currentElementOfRoot);
            }
            else
                this.listModel.addElement("Файл: " + currentElementOfRoot);

        }

    }

    private File provideRootOrCatalog(String selectedFile){
        String fullPath = provideFullPath(this.currentDirectory);

        if(this.currentDirectory.size() > ROOT_LEVEL)
            return new File(fullPath, selectedFile);
        else
            return new File(fullPath + selectedFile);
    }

    private void addFolderToCurrentDirectory(File directory){
        if(directory != null){
            if(directory.isDirectory())
                this.currentDirectory.add(directory.toString());
        }
    }

    private void clearListModelIfContainsData(){
        Boolean isEmpty = this.listModel.getSize() == 0;
        if(!isEmpty)
            this.listModel.clear();
        logger.info("clear list model");
    }

    private void addFolderToCurrentDirectory(String directory){
        if(directory != null){
            if(!"".equals(directory))
                this.currentDirectory.add(directory);
        }
        logger.info("Current directory: " + provideFullPath(this.currentDirectory));
    }

    private String provideFullPath(List<String> foldersCurrentDirectory) {
        StringBuilder fullPath = new StringBuilder();
        for(String folder : foldersCurrentDirectory){
            fullPath.append(folder);
        }
        return fullPath.toString();
    }

}
