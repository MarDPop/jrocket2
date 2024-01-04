module com.mardpop.jrocket {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mardpop.jrocket to javafx.fxml;
    exports com.mardpop.jrocket;
}
