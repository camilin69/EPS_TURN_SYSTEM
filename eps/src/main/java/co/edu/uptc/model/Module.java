package co.edu.uptc.model;

import java.time.Duration;
import java.util.stream.Collectors;

import co.edu.uptc.EpsView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Module implements Runnable{

    private boolean available;
    private User currentUser;
    private AnchorPane panel;
    private int seconds;
    private int procedure;

    private AnchorPane p;
    private ScrollPane sp;

    private ComboBox<String> indeque;

    public Module(AnchorPane panel, ComboBox<String> indeque) {
        available = true;
        this.panel = panel;
        this.indeque = indeque;
    }

    @Override
    public void run() {
        Platform.runLater(() ->{
            ((Label) panel.getChildren().get(0)).setStyle("-fx-background-color: #d77373; -fx-border-color: black;");
            switch (procedure) {
                case 0:
                    seeAppointments();
                    break;
                case 1:
                    addAppointment();
                    break;
                case 2:
                    deleteAppointment();
                    break;
            }
        });
        while (!available) {
            try{
                Thread.sleep(Duration.ofSeconds(seconds)); 
             }catch(Exception e){
                 e.printStackTrace();
             }
            
            if(procedure == 0){available = true;}
        }
        
        Platform.runLater(() -> {
            indeque.getItems().removeIf(i -> i.equals(currentUser.getFirstName() + " " + currentUser.getLastName()));
            currentUser.setRequest(false);
            moduleViewReset();
            panel.getChildren().remove(1);
            ((Label) panel.getChildren().get(0)).setStyle("-fx-background-color: #a9ffab; -fx-border-color: black;");

        });
    }

    public void seeAppointments(){
        moduleView();
        for(int i = 0; i < currentUser.getAppointments().size(); i++){
            Label la = new Label((i + 1) + ": " + currentUser.getAppointments().get(i).getName());
            la.setFont(new Font(14));
            la.setLayoutX(5);
            la.setLayoutY(50 * i + 30);
            p.getChildren().add(la);
        }
    }

    public void addAppointment(){
        moduleView();
        Label l = new Label("Select a service:");
        ComboBox<String> services = new ComboBox<>(FXCollections.observableArrayList(
            "MedicineGeneral", "Ondontology", "Psichology", "Nutriology"));
        Button select = new Button("Select");

        moduleViewAD(l, services, select);
        select.setOnAction(e -> modify(0, services.getSelectionModel().getSelectedItem()));
        
    }

    public void deleteAppointment(){
        moduleView();
        Label l = new Label("Select an appointment to delete:");
        ComboBox<String> apps = new ComboBox<>(FXCollections.observableArrayList(
            currentUser.getAppointments().stream()
                .map(Appointment::getName)
                .collect(Collectors.toList())
        ));
        Button select = new Button("Select");

        moduleViewAD(l, apps, select);

        select.setOnAction(e -> modify(1, apps.getSelectionModel().getSelectedItem()));

    }

    public String appointmentName(String service){
        long count = currentUser.getAppointments().stream()
        .filter(a -> a.getName().split(" ")[0].equals(service)).map(s -> s.getName().split(" ")[0])
        .count();
        
        return count > 0 ? service + " " + (count + 1) : service;
    }

    public void moduleView(){
        p = new AnchorPane();
        p.setPrefSize(184, 194);
        p.getChildren().add(new Label("In service with: " + currentUser.getFirstName() + " " + currentUser.getLastName()));
        sp = new ScrollPane(p);
        sp.getStylesheets().add(getClass().getResource("/co/edu/uptc/style.css").toExternalForm());

        sp.setLayoutY(20);
        sp.setPrefSize(200, 194);
        p.setStyle("-fx-background-color: #d77373;");
        panel.getChildren().add(sp);
    }

    public void moduleViewAD(Label l, ComboBox<String> b, Button select){
        
        l.setLayoutX(5);
        l.setLayoutY(30);

        b.setLayoutX(5);
        b.setLayoutY(50);
        b.setPrefSize(150, 20);

        select.setPrefSize(80, 20);
        select.setStyle("-fx-background-color: blue;");
        select.setTextFill(Color.WHITE);
        select.setLayoutX(65);
        select.setLayoutY(100);
        select.setCursor(Cursor.HAND);

        p.getChildren().addAll(l, b, select);

    }

    private void modify(int op, String s){
        if(s != null){
            available = true;
            EpsView.saveUsers();
            if(op == 0){currentUser.getAppointments().add(new Appointment(appointmentName(s)));}
            else if(op == 1){currentUser.getAppointments().removeIf(a -> a.getName().equals(s));}
        }
    }



    public void moduleViewReset(){
        p = null;
        sp = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public AnchorPane getPanel() {
        return panel;
    }

    public void setPanel(AnchorPane panel) {
        this.panel = panel;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getProcedure() {
        return procedure;
    }

    public void setProcedure(int procedure) {
        this.procedure = procedure;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    
}
