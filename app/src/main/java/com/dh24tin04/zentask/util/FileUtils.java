package com.dh24tin04.zentask.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.util.Locale;

public final class FileUtils {

    private FileUtils() {}

    public static class ThongTinFile {
        public String ten = "tai_lieu";
        public long kichThuoc = -1;   // byte, -1 nếu provider không cung cấp
        public String mime;
    }

    // Đọc tên, dung lượng, mime của file từ Uri.
    public static ThongTinFile doc(ContentResolver cr, Uri uri) {
        ThongTinFile t = new ThongTinFile();
        t.mime = cr.getType(uri);

        try (Cursor c = cr.query(uri, null, null, null, null)) {
            if (c != null && c.moveToFirst()) {
                int iTen = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int iSize = c.getColumnIndex(OpenableColumns.SIZE);
                if (iTen >= 0 && !c.isNull(iTen)) t.ten = c.getString(iTen);
                if (iSize >= 0 && !c.isNull(iSize)) t.kichThuoc = c.getLong(iSize);
            }
        }

        // Một số provider trả mime null -> đoán theo đuôi file
        if (t.mime == null) {
            String lower = t.ten.toLowerCase(Locale.ROOT);
            if (lower.endsWith(".pdf")) t.mime = "application/pdf";
            else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) t.mime = "image/jpeg";
            else if (lower.endsWith(".png")) t.mime = "image/png";
        }
        return t;
    }

    public static String dinhDangKichThuoc(long bytes) {
        if (bytes < 0) return "";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format(Locale.US, "%.0f KB", bytes / 1024.0);
        return String.format(Locale.US, "%.1f MB", bytes / (1024.0 * 1024.0));
    }
}