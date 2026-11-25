package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class UiUtil {
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DATE      = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat VND = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    private UiUtil() {}

    public static String money(BigDecimal v) { return VND.format(v) + " đ"; }

    /** poster_url phải là URL đầy đủ (https://..., file:/...). Sai/rỗng -> trả về null (không có ảnh). */
    public static Image loadImage(String url, double w, double h) {
        if (url == null || url.isBlank()) return null;
        try {
            return new Image(url, w, h, true, true, true);   // true cuối = tải nền, không đơ giao diện
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    public static void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}