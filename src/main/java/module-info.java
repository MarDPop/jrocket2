module com.mardpop.jrocket {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.logging;

    opens com.mardpop.jrocket to javafx.fxml;
    exports com.mardpop.jrocket;
}
