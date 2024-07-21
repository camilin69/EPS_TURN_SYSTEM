package co.edu.uptc.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private boolean request;
    private short procedureNum;
    private String procedureAppo;
    private int duration;
    private int turn;
    

   
    private List<Appointment> appointments;

    

    public User(long id, String firstName, String lastName, String userName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        appointments = new ArrayList<>();
    }
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public boolean isRequest() {
        return request;
    }

    public void setRequest(boolean request) {
        this.request = request;
    }

    

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
   
    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public short getProcedureNum() {
        return procedureNum;
    }

    public String getProcedureAppo() {
        return procedureAppo;
    }

    public void setProcedureNum(short procedureNum) {
        this.procedureNum = procedureNum;
    }

    public void setProcedureAppo(String procedureAppo) {
        this.procedureAppo = procedureAppo;
    }

    
    

    
}
