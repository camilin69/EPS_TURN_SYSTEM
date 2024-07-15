package co.edu.uptc;

import java.io.FileWriter;
import java.io.Reader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.time.Duration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import co.edu.uptc.model.Module;
import co.edu.uptc.model.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class EpsView {

    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private TextField duration;
    @FXML
    private ComboBox<String> procedure;


    @FXML
    private AnchorPane m1;

    @FXML
    private AnchorPane m2;

    @FXML
    private AnchorPane m3;

    @FXML
    private AnchorPane turnPanel;

    @FXML
    private Label errorMsg1;
    @FXML
    private Label errorMsg2;

    @FXML
    private ComboBox<String> indeque;

    @FXML
    private CheckBox random;


    private static List<User> users;
    private ObservableList<User> turns;

    private ArrayList<Module> modules;
    private Module mod1;
    private Module mod2;
    private Module mod3;

    public static final int boundDuration = 10;
   

    public void initialize(){
        duration.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    duration.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        users = new ArrayList<>();
        modules = new ArrayList<>();
        turns = FXCollections.observableArrayList();

        mod1 = new Module(m1, indeque);
        mod2 = new Module(m2, indeque);
        mod3 = new Module(m3, indeque);
        modules.addAll(List.of(mod1, mod2, mod3));

        
        loadUsers();
        ObservableList<String> p = FXCollections.observableArrayList("See Appointments", "Add Appointment", "Delete Appointment");
        procedure.setItems(p);

        Thread t = new Thread(() -> {
            while(true){
                if(!turns.isEmpty()){
                    User cu = turns.getFirst();
                    if(cu.isRequest()){
                        for (Module m : modules) {
                            if(m.isAvailable()){    
                                m.setCurrentUser(cu);
                                m.setProcedure(cu.getProcedure());
                                m.setAvailable(false);
                                m.setSeconds(cu.getDuration());
                                new Thread(m).start();
                                turns.remove(cu);
                                break;
                            }
                        }
                    }
                }
            
                try{
                    Thread.sleep(Duration.ofSeconds(1)); 
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();

        turns.addListener((ListChangeListener<User>) change -> {
            Platform.runLater(() -> {
                ObservableList<String> userList = FXCollections.observableArrayList(
                    turns.stream().map(u -> u.getFirstName() + " " + u.getLastName()).collect(Collectors.toList())
                );
                indeque.setItems(userList);
            });
        });
    }


    @FXML
    void turn(ActionEvent event) {
        User cu = findUser();
        if(cu != null){
            if(!cu.isRequest()){
                if(checkProcedure()){
                    if(checkDuration(cu)){
                        errorMsg1.setOpacity(0);
                        errorMsg2.setOpacity(0);
                        cu.setProcedure((short) procedure.getSelectionModel().getSelectedIndex());
                        cu.setRequest(true);
                        turns.add(cu);
                    }else{
                        errorView(3);
                    }
                }else{
                    errorView(2);
                }
            }else{
                errorView(1);
            }
        }else{
            errorView(0);
        }

        
        
    }

    @FXML
    void randomDuration(ActionEvent e){
        if(random.isSelected()){
            random.setSelected(true);
            duration.setDisable(true);
        }else{
            random.setSelected(false);
            duration.setDisable(false);
        
        }
    }

    public static void saveUsers(){
        try (FileWriter writer = new FileWriter("eps/src/main/resources/co/edu/uptc/users.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadUsers() {
        try (Reader reader = new FileReader("eps/src/main/resources/co/edu/uptc/users.json")) {
            
            Type listType = new TypeToken<List<User>>(){}.getType();
            users = new Gson().fromJson(reader, listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    private User findUser(){
        Optional<User> user = users.stream().filter(u -> u.getEmail().equals(email.getText()) && u.getPassword().equals(password.getText())).findAny();
        return user.isPresent() ? user.get(): null;
    }

    private boolean checkDuration(User cu){
        if(duration.isDisable() && random.isSelected()){
            cu.setDuration(new Random().nextInt(boundDuration) + 1);
            return true;
        }else if(!duration.getText().isBlank()){
            cu.setDuration(Integer.parseInt(duration.getText()));
            return true;
        }else{
            errorView(3);
            return false;
        }
    }

    private boolean checkProcedure(){
        return procedure.getSelectionModel().getSelectedItem() != null ? true : false;
    }

    public void errorView(int error){
        if(error == 0){
            errorMsg1.setText("USER NOT FOUND OR PASSWORD INCORRECT");
        }else if(error == 1){
            errorMsg1.setText("USER ALREADY IN DEQUE OR MODULE");
        }else if(error == 2){
            errorMsg2.setText("SELECT A PROCEDURE");
        }else if(error == 3){
            errorMsg2.setText("ENTER A DURATION OF PROCEDURE");
        }
        if(error == 0 || error ==1){
            errorMsg1.setOpacity(1);
            Timeline t = new Timeline(new KeyFrame(javafx.util.Duration.millis(300), f -> {errorMsg1.setOpacity(errorMsg1.getOpacity() - 0.1d);}));
            t.setCycleCount(10);
            t.play();
        }else if(error == 2 || error == 3){
            errorMsg2.setOpacity(1);
            Timeline t = new Timeline(new KeyFrame(javafx.util.Duration.millis(300), f -> {errorMsg2.setOpacity(errorMsg2.getOpacity() - 0.1d);}));
            t.setCycleCount(10);
            t.play();
        }
        
    }



}
