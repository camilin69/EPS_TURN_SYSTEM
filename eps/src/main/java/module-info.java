module co.edu.uptc {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive com.google.gson;
    requires javafx.graphics;
    requires javafx.base;

    opens co.edu.uptc to javafx.fxml;
    opens co.edu.uptc.model;
    
    exports co.edu.uptc;
}
