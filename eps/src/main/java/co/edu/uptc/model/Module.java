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
            ((Label) panel.getChildren().get(1)).setText("Turn: " + currentUser.getTurn());
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
        try{
            Thread.sleep(Duration.ofSeconds(seconds)); 
        }catch(Exception e){
            e.printStackTrace();
        }
        

        

        Platform.runLater(() -> {
            indeque.getItems().removeIf(i -> i.equals(currentUser.getFirstName() + " " + currentUser.getLastName()));
            currentUser.setRequest(false);
            moduleViewReset();
            panel.getChildren().remove(2);
            ((Label) panel.getChildren().get(0)).setStyle("-fx-background-color: #a9ffab; -fx-border-color: black;");
        });
        available = true;

        
        
    
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
        Label l = new Label("ADDING THE APPOINTMENT: " + currentUser.getProcedureAppo() + " \nDURATION: " + seconds + " seconds");
     
        moduleViewAD(l);
        modify(0, currentUser.getProcedureAppo());
    }

    public void deleteAppointment(){
        moduleView();
        Label l = new Label("DELETING THE APPOINTMENT: " + currentUser.getProcedureAppo() + " \nDURATION: " + seconds + " seconds");
        
        moduleViewAD(l);
        modify(1, currentUser.getProcedureAppo());

    }

    public String appointmentName(String service){
        long count = currentUser.getAppointments().stream()
        .filter(a -> a.getName().split(" ")[0].equals(service)).map(s -> s.getName().split(" ")[0])
        .count();
        
        return count > 0 ? service + " " + (count + 1) : service;
    }

    public void moduleView(){
        p = new AnchorPane();
        p.setPrefSize(195, 280);
        Label l = new Label("In service with: " + currentUser.getFirstName() + " " + currentUser.getLastName());
        l.setWrapText(true);
        l.setPrefSize(184, 150);
        l.setFont(new Font(30));
        p.getChildren().add(l);
        sp = new ScrollPane(p);
        sp.getStylesheets().add(getClass().getResource("/co/edu/uptc/style.css").toExternalForm());

        sp.setLayoutY(70);
        sp.setPrefSize(200, 280);
        p.setStyle("-fx-background-color: #d77373;");
        panel.getChildren().add(sp);
    }

    public void moduleViewAD(Label l){
        l.setFont(new Font(18));
        l.setLayoutX(0);
        l.setLayoutY(150);
        l.setPrefSize(195, 130);
        l.setWrapText(true);
        l.setStyle("-fx-border-color: BLACK;");

        p.getChildren().addAll(l);

    }

    private void modify(int op, String s){
        if(s != null){
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
