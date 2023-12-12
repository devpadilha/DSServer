package augustopadilha.serverdistributedsystems.controllers.UI;

import augustopadilha.serverdistributedsystems.models.Session;
import augustopadilha.serverdistributedsystems.system.connection.SessionManager;
import augustopadilha.serverdistributedsystems.views.ClientCellFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class UIController implements Initializable {
    public ListView<Session> segment_list_view;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        segment_list_view.setItems(SessionManager.getInstance().getLoginSessions());
        segment_list_view.setCellFactory(studentListView -> new ClientCellFactory());

        SessionManager.getInstance().getSessions().addListener((javafx.collections.ListChangeListener.Change<? extends Session> c) -> {
            SessionManager.getInstance().updateLoginSessions();
            segment_list_view.refresh();
        });
    }
}
