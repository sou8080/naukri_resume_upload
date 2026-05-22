package utilities;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SessionManager {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ==========================================
    // GET SESSIONS DIRECTORY
    // ==========================================
    private static File getSessionsDir() {

        File sessionsDir = new File(
                "src/test/resources/sessions");

        if (!sessionsDir.exists()) {

            boolean created = sessionsDir.mkdirs();

            if (created) {

                System.out.println(
                        "Sessions directory created : "
                                + sessionsDir.getAbsolutePath());
            }
        }

        return sessionsDir;
    }

    // ==========================================
    // SAVE SESSION
    // ==========================================
    public static void saveSession(WebDriver driver) {

        try {

            File sessionsDir = getSessionsDir();

            ZonedDateTime nowIST = ZonedDateTime.now(
                    ZoneId.of("Asia/Kolkata"));

            String timestamp = nowIST.format(
                    DateTimeFormatter.ofPattern(
                            "yyyyMMdd_HHmmss"));

            File sessionFile = new File(
                    sessionsDir,
                    "session_" + timestamp + ".json");

            Set<Cookie> cookies = driver.manage().getCookies();

            List<Map<String, Object>> cookieList = new ArrayList<>();

            for (Cookie cookie : cookies) {

                Map<String, Object> map = new HashMap<>();

                map.put("name", cookie.getName());

                map.put("value", cookie.getValue());

                map.put("domain", cookie.getDomain());

                map.put("path", cookie.getPath());

                map.put("secure", cookie.isSecure());

                map.put("httpOnly", cookie.isHttpOnly());

                map.put("sameSite", cookie.getSameSite());

                if (cookie.getExpiry() != null) {

                    map.put(
                            "expiry",
                            cookie.getExpiry().getTime());
                }

                cookieList.add(map);
            }

            Map<String, Object> sessionData = new HashMap<>();

            sessionData.put(
                    "createdAt",
                    nowIST.toString());

            sessionData.put(
                    "cookies",
                    cookieList);

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(sessionFile, sessionData);

            System.out.println(
                    "Session saved successfully : "
                            + sessionFile.getAbsolutePath());

            cleanOldSessions(sessionFile);

        } catch (Exception e) {

            System.out.println(
                    "Failed to save session.");

            e.printStackTrace();
        }
    }

    // ==========================================
    // LOAD SESSION
    // ==========================================
    @SuppressWarnings("unchecked")
    public static boolean loadSession(WebDriver driver) {

        try {

            File sessionsDir = getSessionsDir();

            File[] files = sessionsDir.listFiles(
                    (dir, name) -> name.startsWith("session_")
                            && name.endsWith(".json"));

            if (files == null || files.length == 0) {

                System.out.println(
                        "No session files found.");

                return false;
            }

            // ==========================================
            // GET NEWEST FILE
            // ==========================================
            Arrays.sort(files,
                    Comparator.comparingLong(File::lastModified)
                            .reversed());

            File newestFile = files[0];

            // ==========================================
            // CHECK TTL
            // ==========================================
            long age = System.currentTimeMillis()
                    - newestFile.lastModified();

            long ttl = 24L * 60 * 60 * 1000;

            if (age > ttl) {

                System.out.println(
                        "Session expired.");

                newestFile.delete();

                return false;
            }

            Map<String, Object> sessionData = objectMapper.readValue(
                    newestFile,
                    new TypeReference<Map<String, Object>>() {
                    });

            List<Map<String, Object>> cookies = (List<Map<String, Object>>) sessionData.get("cookies");

            if (cookies == null || cookies.isEmpty()) {

                return false;
            }

            for (Map<String, Object> cookieMap : cookies) {

                try {

                    String name = (String) cookieMap.get("name");

                    String value = (String) cookieMap.get("value");

                    String domain = (String) cookieMap.get("domain");

                    String path = (String) cookieMap.get("path");

                    Boolean secure = (Boolean) cookieMap.get("secure");

                    Boolean httpOnly = (Boolean) cookieMap.get("httpOnly");

                    String sameSite = (String) cookieMap.get("sameSite");

                    Date expiry = null;

                    Object expiryObj = cookieMap.get("expiry");

                    if (expiryObj != null) {

                        long expiryValue = ((Number) expiryObj)
                                .longValue();

                        // seconds to millis safety
                        if (expiryValue < 9999999999L) {

                            expiryValue *= 1000;
                        }

                        expiry = new Date(expiryValue);

                        if (expiry.before(new Date())) {

                            continue;
                        }
                    }

                    Cookie.Builder builder = new Cookie.Builder(
                            name,
                            value)
                            .domain(domain)
                            .path(path);

                    if (expiry != null) {

                        builder.expiresOn(expiry);
                    }

                    if (secure != null && secure) {

                        builder.isSecure(true);
                    }

                    if (httpOnly != null && httpOnly) {

                        builder.isHttpOnly(true);
                    }

                    if (sameSite != null) {

                        builder.sameSite(sameSite);
                    }

                    driver.manage()
                            .addCookie(
                                    builder.build());

                } catch (Exception ignored) {
                }
            }

            System.out.println(
                    "Session loaded successfully from : "
                            + newestFile.getName());

            return true;

        } catch (Exception e) {

            System.out.println(
                    "Failed to load session.");

            e.printStackTrace();

            return false;
        }
    }

    // ==========================================
    // CLEAN OLD SESSIONS
    // ==========================================
    private static void cleanOldSessions(
            File latestFile) {

        try {

            File sessionsDir = getSessionsDir();

            File[] files = sessionsDir.listFiles(
                    (dir, name) -> name.startsWith("session_")
                            && name.endsWith(".json"));

            if (files == null) {
                return;
            }

            for (File file : files) {

                if (!file.getAbsolutePath()
                        .equals(latestFile.getAbsolutePath())) {

                    file.delete();

                    System.out.println(
                            "Deleted old session : "
                                    + file.getName());
                }
            }

        } catch (Exception ignored) {
        }
    }

    // ==========================================
    // CLEAR ALL SESSIONS
    // ==========================================
    public static void clearSession() {

        File sessionsDir = getSessionsDir();

        File[] files = sessionsDir.listFiles(
                (dir, name) -> name.startsWith("session_")
                        && name.endsWith(".json"));

        if (files != null) {

            for (File file : files) {

                file.delete();
            }
        }

        System.out.println(
                "All sessions cleared.");
    }
}