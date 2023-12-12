package augustopadilha.serverdistributedsystems.controllers.UI;

import augustopadilha.serverdistributedsystems.models.Session;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientCellController implements Initializable {
    private final Session session;
    public Label ip_lbl;
    public Label name_lbl;
    public Label type_lbl;

    public ClientCellController(Session session) {
        this.session = session;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ip_lbl.setText("IP: " + session.getIp());
        name_lbl.setText("Usuário: " + session.getUser().getName());
        String type = session.getUser().getType().equals("Admin") ? "Administrador" : "Usuário";
        type_lbl.setText("Tipo: " + type);
    }
}
