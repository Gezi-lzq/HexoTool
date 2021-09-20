package sample;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Controller {
    public JFXTextField blogName;
    public TextArea categories;
    public TextArea tags;
    public JFXTextField postDate;
    public JFXTextField blogpath;
    public AnchorPane dragPane;
    public ImageView image;
    public ProgressBar pbar;
    private File blogDirectory;
    private File blogFile;
    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");

    public void EventHandler(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();
        if (dragboard.hasFiles()){
            try {
                File file = dragboard.getFiles().get(0);
                if (file != null) {
                    blogFile=file;

                    blogName.setText(file.getName());
                    postDate.setText(df.format(new Date()));
                    image.setVisible(true);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    Service<Integer> service = new Service<Integer>() {
        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    executeHexo();
                    clear();
                    return null;
                };
            };
        }
    };
    public void Post(ActionEvent actionEvent) {
        if(blogDirectory==null)blogDirectory=new File(blogpath.getText());

        if(categories.getText()!=""&&tags.getText()!=""){
            String head="---\n" +
                    "title: "+blogName.getText()+"\n" +
                    "date: "+postDate.getText()+"\n";
            head+="categories: \n";
            for(String s:categories.getText().split("\n")){
                head+=("- "+s+"\n");
            }
            head+="tags:\n";
            for(String s:tags.getText().split("\n")){
                head+=("- "+s+"\n");
            }
            head+="---\n";
            EditAndCopyBlog(head);
            pbar.progressProperty().bind(service.progressProperty());
            service.restart();
        }
    }

    private void clear(){
        categories.clear();
        tags.clear();
        blogName.clear();
        postDate.clear();
        image.setVisible(false);
        pbar.setProgress(0.0);
    }

    private void executeHexo(){
        PowerShell session = PowerShell.openSession();
        Map<String,String> configMap = new HashMap<>();
        configMap.put("maxWait","30000");
        session.configuration(configMap);
        session.executeCommand("cd "+blogDirectory.getAbsolutePath());
        PowerShellResponse response=session.executeCommand("hexo g");
        PowerShellResponse response1=session.executeCommand("hexo d");
    }

    private void EditAndCopyBlog(String head){
        try (
                BufferedReader in = new BufferedReader(
                        new StringReader(
                                read(blogFile)));
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new FileWriter(blogDirectory.getAbsoluteFile()+
                                "\\source\\_posts\\"+blogFile.getName())))
        ) {
            out.write(head);
            in.lines().forEach(out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String read(File filename) {
        //`Reader` 和 `Writer` 则提供兼容 Unicode 和面向字符 I/O 的功能
        try (BufferedReader in = new BufferedReader(
                new FileReader(filename))) {
            return in.lines().collect(Collectors.joining("\n"));
            //`Collectors.joining()` 在其内部使用了一个 `StringBuilder` 来累加其运行结果
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void DragOverEvent(DragEvent dragEvent) {
        if (dragEvent.getGestureSource() != dragPane){
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void Editpath(ActionEvent actionEvent) {
        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setTitle("Open Folder");
        if (blogDirectory != null) folderChooser.setInitialDirectory(blogDirectory);
        File selectedfolder = folderChooser.showDialog(Main.getPrimaryStage());
        if (selectedfolder != null) {
            blogDirectory=selectedfolder;
            blogpath.setText(blogDirectory.getAbsolutePath());
        }
    }
}
