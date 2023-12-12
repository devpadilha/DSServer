package augustopadilha.serverdistributedsystems.views;

import augustopadilha.serverdistributedsystems.controllers.UI.ClientCellController;
import augustopadilha.serverdistributedsystems.models.Session;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class ClientCellFactory extends ListCell<Session> {
    @Override
    protected void updateItem(Session session, boolean empty) {
        super.updateItem(session, empty);
        Platform.runLater(() -> {
            if(empty || session == null) {
                setText(null);
                setGraphic(null);
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/augustopadilha/serverdistributedsystems/fxmlfiles/usercell.fxml"));
                ClientCellController controller = new ClientCellController(session);
                fxmlLoader.setController(controller);
                setText(null);
                try {
                    setGraphic(fxmlLoader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}