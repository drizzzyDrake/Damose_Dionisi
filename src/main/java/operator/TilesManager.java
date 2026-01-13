package operator;

// Classi.
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * TilesManager
 * ------------
 * Gestisce il recupero e la cache delle tile OpenStreetMap.
 *
 * Funzionalità:
 * - conversione lat/lon ↔ tile
 * - cache LRU in RAM
 * - cache persistente su disco
 * - download asincrono delle tile mancanti
 */
public class TilesManager {

    // SERVER DI TILES -------------------------------------------------------------------------------------------------
    private static final String TILE_SERVER = "https://cartodb-basemaps-a.global.ssl.fastly.net/rastertiles/voyager";

    // CARTELLA DI TILES IN CACHE LOCALE -------------------------------------------------------------------------------
    private static final Path TILES_DIR =
            Paths.get(System.getProperty("user.home"),
                    ".damose_dionisi", "cache", "tiles");

    // DIMENSIONE DELLE TILES ------------------------------------------------------------------------------------------
    private static final int TILE_SIZE = 512;

    // LIMITI DI ZOOM --------------------------------------------------------------------------------------------------
    public static final int MIN_ZOOM = 11;              // Livello minimo di zoom.
    public static final int MAX_ZOOM = 17;              // Livello massimo di zoom.

    // CONCORRENZA E CACHE ---------------------------------------------------------------------------------------------
    private final ExecutorService downloadExecutor;

    // CACHE LRU IN RAM ------------------------------------------------------------------------------------------------
    private final Map<String, BufferedImage> cache = new LinkedHashMap<>(
            500,
            0.75f,
            true) {
        protected boolean removeEldestEntry(Map.Entry<String, BufferedImage> eldest) {
            return size() > 500;
        }
    };

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    public TilesManager() {
        this.downloadExecutor = Executors.newFixedThreadPool(4);
        try {
            Files.createDirectories(TILES_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // API PUBBLICA DI ACCESSO AI TILES --------------------------------------------------------------------------------
    /**
     * Restituisce la tile corrispondente alle coordinate e zoom richiesti.
     * <p>
     * Se la tile è già presente in cache RAM o su disco, viene restituita subito.
     * Altrimenti ne programma il download asincrono e ritorna {@code null}.
     * </p>
     *
     * @param zoom livello di zoom richiesto.
     * @param x    coordinata X della tile.
     * @param y    coordinata Y della tile.
     * @return     immagine della tile se disponibile, altrimenti {@code null}.
     */
    public synchronized BufferedImage getTile(int zoom, int x, int y) {

        zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom));

        if (!isValidTile(zoom, x, y)) return null;

        String key = getTileKey(zoom, x, y);

        BufferedImage cached = cache.get(key);
        if (cached != null) return cached;

        File tileFile = getTileFile(zoom, x, y);
        if (tileFile.exists()) {
            try {
                BufferedImage img = ImageIO.read(tileFile);
                if (img != null) {
                    cache.put(key, img);
                    return img;
                } else {
                    tileFile.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
                tileFile.delete();
            }
        }

        downloadTileAsync(zoom, x, y);
        return null;
    }

    // PROGRAMMA IL DOWNLOAD DEI TILES ---------------------------------------------------------------------------------
    private void downloadTileAsync(int zoom, int x, int y) {
        downloadExecutor.submit(() -> downloadTile(zoom, x, y));
    }

    // EFFETTUA IL DOWNLOAD DEI TILES ----------------------------------------------------------------------------------
    private void downloadTile(int zoom, int x, int y) {

        if (!isValidTile(zoom, x, y)) return;

        String key = getTileKey(zoom, x, y);
        if (cache.containsKey(key)) return;

        try {
            File tileFile = getTileFile(zoom, x, y);
            tileFile.getParentFile().mkdirs();

            String urlString = String.format("%s/%d/%d/%d@2x.png", TILE_SERVER, zoom, x, y);
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestProperty("User-Agent", "JavaMapViewer/1.0");

            int responseCode = conn.getResponseCode();
            String contentType = conn.getContentType();

            if (responseCode == 200 && contentType != null && contentType.contains("image/png")) {
                try (InputStream in = conn.getInputStream()) {
                    BufferedImage img = ImageIO.read(in);
                    if (img != null) {
                        ImageIO.write(img, "png", tileFile); // persistenza
                        synchronized (this) {
                            cache.put(key, img);              // cache RAM
                        }
                    }
                }
            } else {
                System.err.println("Tile non disponibile: " + zoom + "/" + x + "/" + y +
                        " (HTTP " + responseCode + ", content-type=" + contentType + ")");
            }

        } catch (Exception e) {
            System.err.println("Errore scaricamento tile " + zoom + "/" + x + "/" + y +
                    ": " + e.getMessage());
        }
    }

    // CONVERTE LAT/LON IN (Z,Y) ---------------------------------------------------------------------------------------
    /**
     * Converte una coppia latitudine/longitudine in coordinate tile (x,y) al livello di zoom dato.
     *
     * @param lat  latitudine in gradi.
     * @param lon  longitudine in gradi.
     * @param zoom livello di zoom.
     * @return     array con coordinate tile [x, y].
     */
    public static double[] latLonToTile(double lat, double lon, int zoom) {
        double latRad = Math.toRadians(lat);
        double n = (double) (1 << zoom);

        double x = (lon + 180.0) / 360.0 * n;
        double y = (1.0 - Math.log(Math.tan(latRad) + 1.0 / Math.cos(latRad)) / Math.PI)
                / 2.0 * n;

        return new double[]{x, y};
    }

    // CONVERTE (X,Y) IN LAT/LON ---------------------------------------------------------------------------------------
    /**
     * Converte coordinate tile (x,y) in latitudine/longitudine.
     *
     * @param x    coordinata X della tile.
     * @param y    coordinata Y della tile.
     * @param zoom livello di zoom.
     * @return     array con latitudine e longitudine [lat, lon].
     */
    public static double[] tileToLatLon(double x, double y, int zoom) {
        double n = (double) (1 << zoom);

        double lon = x / n * 360.0 - 180.0;
        double latRad = Math.atan(Math.sinh(Math.PI * (1 - 2.0 * y / n)));
        double lat = Math.toDegrees(latRad);

        return new double[]{lat, lon};
    }

    // VERIFICA CHE IL TILE SIA VALIDO ---------------------------------------------------------------------------------
    private boolean isValidTile(int zoom, int x, int y) {
        int max = (1 << zoom) - 1;
        return x >= 0 && x <= max && y >= 0 && y <= max;
    }

    // RESTITUISCE LA CHIAVE DEL TILE ----------------------------------------------------------------------------------
    private String getTileKey(int zoom, int x, int y) {
        return zoom + "_" + x + "_" + y;
    }

    // RESTITUISCE L'IMMAGINE PNG DEL TILE -----------------------------------------------------------------------------
    private File getTileFile(int zoom, int x, int y) {
        return TILES_DIR
                .resolve(String.valueOf(zoom))
                .resolve(String.valueOf(x))
                .resolve(y + ".png")
                .toFile();
    }

    // RESTITUISCE LA DIMENSIONE DEI TILES -----------------------------------------------------------------------------
    /**
     * Restituisce la dimensione (in pixel) delle tile.
     *
     * @return dimensione delle tile.
     */
    public static int getTileSize() {
        return TILE_SIZE;
    }

    // TERMINA IL THREAD POOL DI DOWNLOAD DEI TILES --------------------------------------------------------------------
    /**
     * Termina il thread pool di download delle tile.
     * <p>
     * Attende la chiusura dei thread per un massimo di 5 secondi.
     * </p>
     */
    public void shutdown() {
        downloadExecutor.shutdown();
        try {
            downloadExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
