package augustopadilha.serverdistributedsystems.controllers.UI;

import augustopadilha.serverdistributedsystems.App;
import augustopadilha.serverdistributedsystems.models.ConnectionModel;
import augustopadilha.serverdistributedsystems.views.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConnectController implements Initializable {
    @FXML
    private Button button_connect;
    @FXML
    private TextField tf_ip;
    @FXML
    private TextField tf_port;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tf_ip.setText("localhost");
        tf_port.setText("23000");
        button_connect.setOnAction(event -> openDialog(App.openConnection(tf_ip.getText(), tf_port.getText())));
    }

    public void openDialog(ConnectionModel result) {
        Platform.runLater(() -> {
            try {
                if (result != null && result.validate()) {
                    String ip = result.getIp();
                    String port = result.getPort();
                    try {
                        App.getConnection().connect(ip, port);

                        ViewFactory.getInstance().closeStage((Stage) button_connect.getScene().getWindow());
                        ViewFactory.getInstance().showUsersListView();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    App.openConnectWindow();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
