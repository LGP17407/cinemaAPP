package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class UiUtil {
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DATE      = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat VND = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));

    private UiUtil() {}

    public static String money(BigDecimal v) { return VND.format(v) + " đ"; }

    /**
     * Hỗ trợ 3 kiểu poster_url:
     * 1) URL web/file đầy đủ (https://..., http://..., file:/...)
     * 2) Đường dẫn resource trong src/main/resources (vd: /images/avengers.jpg, images/avengers.jpg, hoặc avengers.jpg)
     * 3) Đường dẫn file local trên máy (vd: C:\posters\avengers.jpg)
     */
    public static Image loadImage(String url, double w, double h) {
        String resolved = resolveImageUrl(url);
        if (resolved == null) return null;
        try {
            return new Image(resolved, w, h, true, true, true);   // true cuối = tải nền, không đơ giao diện
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String resolveImageUrl(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String trimmed = raw.trim();

        String lower = trimmed.toLowerCase(Locale.ROOT);
        if (lower.startsWith("http://")
                || lower.startsWith("https://")
                || lower.startsWith("file:/")
                || lower.startsWith("jar:")) {
            return trimmed;
        }

        // Thử tìm trong classpath resources (src/main/resources)
        String[] candidates = trimmed.startsWith("/")
                ? new String[] { trimmed }
                : new String[] { "/" + trimmed, "/images/" + trimmed };
        for (String candidate : candidates) {
            URL res = UiUtil.class.getResource(candidate);
            if (res != null) {
                return res.toExternalForm();
            }
        }

        // Thử tìm file local trên ổ đĩa
        try {
            Path localPath = Path.of(trimmed);
            if (Files.isRegularFile(localPath)) {
                return localPath.toUri().toString();
            }
        } catch (InvalidPathException ignored) {
            // Bỏ qua nếu không phải path hợp lệ
        }

        return null;
    }

    public static void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    public static void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}