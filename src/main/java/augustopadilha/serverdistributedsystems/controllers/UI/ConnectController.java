package augustopadilha.serverdistributedsystems.controllers.UI;

import augustopadilha.serverdistributedsystems.App;
import augustopadilha.serverdistributedsystems.models.ConnectionModel;
import augustopadilha.serverdistributedsystems.views.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class ConnectController implements Initializable {
    private App app = new App();
    public Label server_ip_lbl;
    public Label error_text;
    @FXML
    private Button button_connect;
    @FXML
    private TextField tf_port;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tf_port.setText("23000");
        try {
            server_ip_lbl.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        button_connect.setOnAction(event -> {
            if (tf_port.getText().isEmpty()) {
                error_text.setText("O número da porta não pode ser vazio.");
            }
            else {
                ViewFactory.getInstance().closeStage((Stage) tf_port.getScene().getWindow());
                ViewFactory.getInstance().showUsersListView();
                app.startServer(tf_port.getText());
            }
        });
    }
}
