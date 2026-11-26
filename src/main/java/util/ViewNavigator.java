package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.function.Consumer;

/** Mở popup, chuyển bước trong popup, chuyển màn hình chính. */
public final class ViewNavigator {
    /** Thư mục chứa các file .fxml trong resources. */
    private static final String BASE = "/com/Cinema/cinemaProject/";

    private ViewNavigator() {}

    private static <T> T load(String fxml, Consumer<T> init, Scene[] sceneOut) {
        try {
            URL resource = ViewNavigator.class.getResource(BASE + fxml);
            if (resource == null) {
                throw new IllegalStateException("Không tìm thấy file FXML: " + BASE + fxml);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            T controller = loader.getController();
            if (init != null) init.accept(controller);   // truyền dữ liệu TRƯỚC khi hiển thị
            sceneOut[0] = new Scene(root);
            return controller;
        } catch (IOException e) {
            throw new UncheckedIOException("Không nạp được " + fxml, e);
        }
    }

    /** Mở popup modal (chặn cửa sổ phía sau), chờ đến khi popup đóng. */
    public static <T> void showDialog(String fxml, Window owner, String title, Consumer<T> init) {
        Scene[] holder = new Scene[1];
        load(fxml, init, holder);
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(holder[0]);
        stage.setResizable(false);
        stage.showAndWait();
    }

    /** Đổi nội dung của CÙNG một popup (popup 1 -> 2 -> 3), không mở cửa sổ mới. */
    public static <T> void replaceScene(Window current, String fxml, Consumer<T> init) {
        Scene[] holder = new Scene[1];
        load(fxml, init, holder);
        Stage stage = (Stage) current;
        stage.setScene(holder[0]);
        stage.sizeToScene();
        stage.centerOnScreen();
    }

    /** Đổi màn hình chính (Home <-> Login/Account/EditProfile). */
    public static <T> void switchMain(Window current, String fxml, Consumer<T> init) {
        replaceScene(current, fxml, init);
    }
}
