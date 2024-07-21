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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

public class EpsView {



    @FXML
    private AnchorPane m1;

    @FXML
    private AnchorPane m2;

    @FXML
    private AnchorPane m3;

    @FXML
    private AnchorPane turnPanel;

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
        labels();
        users = new ArrayList<>();
        modules = new ArrayList<>();
        turns = FXCollections.observableArrayList();

        mod1 = new Module(m1, indeque);
        mod2 = new Module(m2, indeque);
        mod3 = new Module(m3, indeque);
        modules.addAll(List.of(mod1, mod2, mod3));

        
        loadUsers();

        Thread t = new Thread(() -> {
            while(true){
                if(!turns.isEmpty()){
                    User cu = turns.getFirst();
                    if(cu.isRequest()){
                        for (Module m : modules) {
                            if(m.isAvailable()){    
                                m.setCurrentUser(cu);
                                m.setProcedure(cu.getProcedureNum());
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
                    turns.stream().map(u -> u.getFirstName() + " " + u.getLastName() + " | TURN: " + u.getTurn()).collect(Collectors.toList())
                );
                indeque.setItems(userList);
            });
        });
    }


    @FXML   
    void turn(ActionEvent event) {
        users.forEach(u -> {
            u.setRequest(true);
            u.setDuration(new Random().nextInt(10) + 1);
            turns.add(u);
            u.setTurn(turns.size());
        });
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

    public void labels(){
        try {
            Font digitalFont = Font.loadFont(getClass().getResourceAsStream("/co/edu/uptc/ds_digital/DS-DIGIT.TTF"),46);
            if (digitalFont == null) {
                throw new RuntimeException("Font not found");
            }
            ((Label) m1.getChildren().get(1)).setFont(digitalFont);
            ((Label) m2.getChildren().get(1)).setFont(digitalFont);
            ((Label) m3.getChildren().get(1)).setFont(digitalFont);
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }



    

        
    



}
