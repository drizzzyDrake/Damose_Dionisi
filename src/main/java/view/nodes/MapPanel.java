package view.nodes;

// Model.
import model.Route;

// Operator.
import operator.TilesManager;

// Swing.
import org.kordamp.ikonli.swing.FontIcon;
import javax.swing.*;

// Altro.
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Pannello Swing che visualizza una mappa a tiles con supporto a:
 * - pan e zoom continui
 * - disegno di linee di percorso (shapes)
 * - fermate
 * - veicoli
 *
 * Tutta la logica di rendering è contenuta qui.
 */
public class MapPanel extends JPanel {

    // OPERATOR --------------------------------------------------------------------------------------------------------
    private final TilesManager tilesManager;

    // ELEMENTI MAPPA --------------------------------------------------------------------------------------------------
    private final double latMin, latMax, lonMin, lonMax;            // Bounding box geografico.
    private double zoom;                                            // Zoom.
    private double centerX, centerY;                                // Centro in coordinate tile.
    private Point lastDrag;                                         // Ultimo punto del drag per il pan.

    // ALTRO -----------------------------------------------------------------------------------------------------------
    private Route currentRoute;

    // OVERLAY ---------------------------------------------------------------------------------------------------------
    private List<double[]> stopsOverlay    = new ArrayList<>();
    private List<double[]> shapesOverlay   = new ArrayList<>();
    private List<double[]> vehiclesOverlay = new ArrayList<>();

    // COSTRUTTORE -----------------------------------------------------------------------------------------------------
    /**
     * Costruttore.
     *
     * @param tilesManager controller della mappa.
     * @param latMin       latitudine minima della mappa.
     * @param latMax       latitudine massima della mappa.
     * @param lonMin       longitudine minima della mappa.
     * @param lonMax       longitudine massima della mappa.
     * @param zoom         livello di zoom della mappa.
     */
    public MapPanel(TilesManager tilesManager,
                    double latMin,
                    double latMax,
                    double lonMin,
                    double lonMax,
                    int zoom) {

        this.tilesManager = tilesManager;
        this.latMin = latMin;
        this.latMax = latMax;
        this.lonMin = lonMin;
        this.lonMax = lonMax;
        this.zoom   = zoom;

        double[] tl = TilesManager.latLonToTile(latMax, lonMin, zoom);
        double[] br = TilesManager.latLonToTile(latMin, lonMax, zoom);
        this.centerX = (tl[0] + br[0]) / 2.0;
        this.centerY = (tl[1] + br[1]) / 2.0;

        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        initMouseHandlers();
    }

    // GESTIONE DELLE AZIONI COL MOUSE ---------------------------------------------------------------------------------
    private void initMouseHandlers() {

        // --- Mouse press: inizio pan ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDrag = e.getPoint();
            }
        });

        // --- Mouse drag: pan ---
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDrag == null) return;

                int dx = e.getX() - lastDrag.x;
                int dy = e.getY() - lastDrag.y;
                lastDrag = e.getPoint();

                int tileSize = TilesManager.getTileSize();
                centerX -= (dx / (double) tileSize) * 0.8;
                centerY -= (dy / (double) tileSize) * 0.8;

                clampCenterToBBox();
                repaint();
            }
        });

        // --- Mouse wheel: zoom continuo centrato sul cursore ---
        addMouseWheelListener(e -> {
            double step = 0.1;
            double prevZoom = zoom;

            double newZoom = Math.max(
                    TilesManager.MIN_ZOOM,
                    Math.min(TilesManager.MAX_ZOOM,
                            prevZoom + (e.getWheelRotation() < 0 ? step : -step))
            );

            if (newZoom == prevZoom) return;

            int tileSize = TilesManager.getTileSize();

            int prevZoomLevel = (int) Math.floor(prevZoom);
            double prevScale  = Math.pow(2, prevZoom - prevZoomLevel);

            double mx = e.getX();
            double my = e.getY();

            double mouseTileX = centerX + (mx - getWidth()  / 2.0) / (tileSize * prevScale);
            double mouseTileY = centerY + (my - getHeight() / 2.0) / (tileSize * prevScale);

            double[] latlon = TilesManager.tileToLatLon(mouseTileX, mouseTileY, prevZoomLevel);

            zoom = newZoom;

            int newZoomLevel = (int) Math.floor(newZoom);
            double newScale  = Math.pow(2, newZoom - newZoomLevel);

            double[] newMouseTile = TilesManager.latLonToTile(latlon[0], latlon[1], newZoomLevel);

            centerX = newMouseTile[0] - (mx - getWidth()  / 2.0) / (tileSize * newScale);
            centerY = newMouseTile[1] - (my - getHeight() / 2.0) / (tileSize * newScale);

            clampCenterToBBox();
            repaint();
        });
    }

    // CLAMP NELLA BOUNDING BOX ----------------------------------------------------------------------------------------
    private void clampCenterToBBox() {

        int zoomLevel = (int) Math.floor(zoom);

        double[] tl = TilesManager.latLonToTile(latMax, lonMin, zoomLevel);
        double[] br = TilesManager.latLonToTile(latMin, lonMax, zoomLevel);

        double minX = Math.min(tl[0], br[0]);
        double maxX = Math.max(tl[0], br[0]);
        double minY = Math.min(tl[1], br[1]);
        double maxY = Math.max(tl[1], br[1]);

        int tileSize = TilesManager.getTileSize();

        double halfTilesX = getWidth()  / (2.0 * tileSize);
        double halfTilesY = getHeight() / (2.0 * tileSize);

        int zoomRange = TilesManager.MAX_ZOOM - TilesManager.MIN_ZOOM;
        double factor = (zoom - TilesManager.MIN_ZOOM) / zoomRange;
        double marginTiles = (1.0 - factor) * 4.0;

        double clampMinX = minX + halfTilesX - marginTiles;
        double clampMaxX = maxX - halfTilesX + marginTiles;
        double clampMinY = minY + halfTilesY - marginTiles;
        double clampMaxY = maxY - halfTilesY + marginTiles;

        boolean tooWide = clampMinX > clampMaxX;
        boolean tooTall = clampMinY > clampMaxY;

        centerX = tooWide ? (minX + maxX) / 2.0 : Math.max(clampMinX, Math.min(centerX, clampMaxX));
        centerY = tooTall ? (minY + maxY) / 2.0 : Math.max(clampMinY, Math.min(centerY, clampMaxY));
    }

    // RENDERING DELLA MAPPA E DEGLI OVERLAY ---------------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        int tileSize = TilesManager.getTileSize();

        int zoomLevel = (int) Math.floor(zoom);
        double scale  = Math.pow(2, zoom - zoomLevel);

        int baseTileX = (int) Math.floor(centerX);
        int baseTileY = (int) Math.floor(centerY);

        double fracX = centerX - baseTileX;
        double fracY = centerY - baseTileY;

        int offsetX = (int) Math.round(getWidth()  / 2.0 - fracX * tileSize * scale);
        int offsetY = (int) Math.round(getHeight() / 2.0 - fracY * tileSize * scale);

        drawTiles(g, zoomLevel, scale, baseTileX, baseTileY, offsetX, offsetY);
        drawShapes(g, zoomLevel, scale, baseTileX, baseTileY, offsetX, offsetY);
        drawStops(g, zoomLevel, scale, baseTileX, baseTileY, offsetX, offsetY);
        drawVehicles(g, zoomLevel, scale, baseTileX, baseTileY, offsetX, offsetY);

        g.dispose();
    }

    // TILES -----------------------------------------------------------------------------------------------------------
    private void drawTiles(Graphics2D g,
                           int zoomLevel,
                           double scale,
                           int baseTileX,
                           int baseTileY,
                           int offsetX,
                           int offsetY) {

        int tileSize = TilesManager.getTileSize();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int tilesX = getWidth()  / (int) (tileSize * scale) + 2;
        int tilesY = getHeight() / (int) (tileSize * scale) + 2;

        double[] tl = TilesManager.latLonToTile(latMax, lonMin, zoomLevel);
        double[] br = TilesManager.latLonToTile(latMin, lonMax, zoomLevel);

        int extra = 6;
        int xMin = (int) Math.floor(Math.min(tl[0], br[0])) - extra;
        int xMax = (int) Math.floor(Math.max(tl[0], br[0])) + extra;
        int yMin = (int) Math.floor(Math.min(tl[1], br[1])) - extra;
        int yMax = (int) Math.floor(Math.max(tl[1], br[1])) + extra;

        for (int dx = -tilesX / 2; dx <= tilesX / 2; dx++) {
            for (int dy = -tilesY / 2; dy <= tilesY / 2; dy++) {

                int tx = baseTileX + dx;
                int ty = baseTileY + dy;

                if (tx < xMin || tx > xMax || ty < yMin || ty > yMax) continue;

                BufferedImage tile = tilesManager.getTile(zoomLevel, tx, ty);

                int w = (int) (tileSize * scale);
                int h = (int) (tileSize * scale);

                int px = offsetX + dx * w;
                int py = offsetY + dy * h;

                if (tile != null) {
                    g.drawImage(tile, px, py, w, h, null);
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(px, py, w, h);
                }
            }
        }
    }

    // SHAPES ----------------------------------------------------------------------------------------------------------
    private void drawShapes(Graphics2D g,
                            int zoomLevel,
                            double scale,
                            int baseTileX,
                            int baseTileY,
                            int offsetX,
                            int offsetY) {

        if (shapesOverlay.isEmpty() || currentRoute == null) return;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Stroke borderStroke = new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        Stroke mainStroke   = new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

        g.setStroke(borderStroke);
        g.setColor(Color.BLACK);
        drawShapePath(g, zoomLevel, scale, baseTileX, baseTileY, offsetX, offsetY);

        g.setStroke(mainStroke);
        g.setColor(getColorForRouteType(currentRoute.getRouteType()));
        drawShapePath(g, zoomLevel, scale, baseTileX, baseTileY, offsetX, offsetY);

        drawTerminals(g, zoomLevel, scale, baseTileX, baseTileY, offsetX, offsetY);
    }

    private void drawShapePath(Graphics2D g,
                               int zoomLevel,
                               double scale,
                               int baseTileX,
                               int baseTileY,
                               int offsetX,
                               int offsetY) {

        int tileSize = TilesManager.getTileSize();
        if (shapesOverlay.size() < 2) return;

        Path2D path = new Path2D.Double();

        double[] first = TilesManager.latLonToTile(shapesOverlay.get(0)[0], shapesOverlay.get(0)[1], zoomLevel);
        double x0 = offsetX + (first[0] - baseTileX) * tileSize * scale;
        double y0 = offsetY + (first[1] - baseTileY) * tileSize * scale;
        path.moveTo(x0, y0);

        for (int i = 1; i < shapesOverlay.size(); i++) {
            double[] p = TilesManager.latLonToTile(shapesOverlay.get(i)[0], shapesOverlay.get(i)[1], zoomLevel);
            double x = offsetX + (p[0] - baseTileX) * tileSize * scale;
            double y = offsetY + (p[1] - baseTileY) * tileSize * scale;
            path.lineTo(x, y);
        }

        g.draw(path);
    }

    // CAPOLINEA -------------------------------------------------------------------------------------------------------
    private void drawTerminals(Graphics2D g,
                               int zoomLevel,
                               double scale,
                               int baseTileX,
                               int baseTileY,
                               int offsetX,
                               int offsetY) {

        if (shapesOverlay.size() < 2 || currentRoute == null) return;

        drawTerminalIcon(g, shapesOverlay.get(0),
                zoomLevel, scale, baseTileX, baseTileY, offsetX, offsetY, true);

        drawTerminalIcon(g, shapesOverlay.get(shapesOverlay.size() - 1),
                zoomLevel, scale, baseTileX, baseTileY, offsetX, offsetY, false);
    }

    private void drawTerminalIcon(Graphics2D g,
                                  double[] latlon,
                                  int zoomLevel,
                                  double scale,
                                  int baseTileX,
                                  int baseTileY,
                                  int offsetX,
                                  int offsetY,
                                  boolean isStart) {

        int tileSize = TilesManager.getTileSize();
        double[] tile = TilesManager.latLonToTile(latlon[0], latlon[1], zoomLevel);

        double px = offsetX + (tile[0] - baseTileX) * tileSize * scale;
        double py = offsetY + (tile[1] - baseTileY) * tileSize * scale;

        Color routeColor = getColorForRouteType(currentRoute.getRouteType());

        FontAwesomeSolid innerIcon = isStart
                ? FontAwesomeSolid.PLAY
                : FontAwesomeSolid.FLAG_CHECKERED;

        Icon borderTerminal = FontIcon.of(
                FontAwesomeSolid.CIRCLE,
                32,
                Color.BLACK
        );

        Icon backgroundTerminal = FontIcon.of(
                FontAwesomeSolid.CIRCLE,
                26,
                routeColor
        );

        Icon iconTerminal = FontIcon.of(
                innerIcon,
                14,
                Color.WHITE
        );

        int x = (int) Math.round(px - backgroundTerminal.getIconWidth() / 2.0);
        int y = (int) Math.round(py - backgroundTerminal.getIconHeight() / 2.0);

        borderTerminal.paintIcon(this, g, x - 3, y - 3);
        backgroundTerminal.paintIcon(this, g, x, y);

        int sx = (int) Math.round(px - iconTerminal.getIconWidth() / 2.0);
        int sy = (int) Math.round(py - iconTerminal.getIconHeight() / 2.0);

        iconTerminal.paintIcon(this, g, sx, sy);
    }

    // FERMATE ---------------------------------------------------------------------------------------------------------
    private void drawStops(Graphics2D g,
                           int zoomLevel,
                           double scale,
                           int baseTileX,
                           int baseTileY,
                           int offsetX,
                           int offsetY) {

        Color stopColor = currentRoute != null
                ? getColorForRouteType(currentRoute.getRouteType())
                : Color.DARK_GRAY;

        Icon iconStop = FontIcon.of(FontAwesomeSolid.MAP_MARKER_ALT, 29, stopColor);
        Icon backgroungStop = FontIcon.of(FontAwesomeSolid.CIRCLE, 17, Color.WHITE);
        Icon borderStop = FontIcon.of(FontAwesomeSolid.MAP_MARKER_ALT, 32, Color.BLACK);

        int tileSize = TilesManager.getTileSize();

        for (double[] latlon : stopsOverlay) {

            double[] tile = TilesManager.latLonToTile(latlon[0], latlon[1], zoomLevel);

            double px = offsetX + (tile[0] - baseTileX) * tileSize * scale;
            double py = offsetY + (tile[1] - baseTileY) * tileSize * scale;

            int x = (int) Math.round(px - iconStop.getIconWidth()  / 2.0);
            int y = (int) Math.round(py - iconStop.getIconHeight());

            borderStop.paintIcon(this, g, x, y);
            backgroungStop.paintIcon(this, g, x + 2, y + 2);
            iconStop.paintIcon(this, g, x, y);
        }
    }

    // VEICOLI ---------------------------------------------------------------------------------------------------------
    private void drawVehicles(Graphics2D g,
                              int zoomLevel,
                              double scale,
                              int baseTileX,
                              int baseTileY,
                              int offsetX,
                              int offsetY) {

        if (currentRoute == null || vehiclesOverlay.isEmpty()) return;

        Icon iconVehicle = FontIcon.of(
                getIconForRouteType(currentRoute.getRouteType()),
                29,
                getColorForRouteType(currentRoute.getRouteType())
        );

        Icon borderVehicle = FontIcon.of(
                getIconForRouteType(currentRoute.getRouteType()),
                32,
                Color.BLACK
        );

        Icon backgroundVehicle = FontIcon.of(
                FontAwesomeSolid.SQUARE_FULL, 22, Color.WHITE
        );

        int tileSize = TilesManager.getTileSize();

        for (double[] latlon : vehiclesOverlay) {

            double[] tile = TilesManager.latLonToTile(latlon[0], latlon[1], zoomLevel);

            double px = offsetX + (tile[0] - baseTileX) * tileSize * scale;
            double py = offsetY + (tile[1] - baseTileY) * tileSize * scale;

            int x = (int) Math.round(px - backgroundVehicle.getIconWidth()  / 2.0);
            int y = (int) Math.round(py - borderVehicle.getIconHeight() / 2.0);

            borderVehicle.paintIcon(this, g, x, y);
            backgroundVehicle.paintIcon(this, g, x + 2, y + 2);
            iconVehicle.paintIcon(this, g, x, y);
        }
    }

    // RITORNA IL COLORE IN BASE AL TIPO DI LINEA ----------------------------------------------------------------------
    private Color getColorForRouteType(Integer type) {
        return switch (type) {
            case 0 -> new Color(255, 0, 0);         // Tram.
            case 1 -> new Color(0, 191, 255);       // Metro.
            case 3 -> new Color(255, 20, 147);      // Bus.
            default -> new Color(169, 169, 169);    // Fallback.
        };
    }

    // RITORNA L'ICONA IN BASE AL TIPO DI LINEA ------------------------------------------------------------------------
    private FontAwesomeSolid getIconForRouteType(Integer type) {
        return switch (type) {
            case 0 -> FontAwesomeSolid.TRAIN;               // Tram.
            case 1 -> FontAwesomeSolid.SUBWAY;              // Metro.
            case 3 -> FontAwesomeSolid.BUS;                 // Bus.
            default -> FontAwesomeSolid.QUESTION_CIRCLE;    // Fallback.
        };
    }

    // API PUBBLICA PER DISEGNARE LE FERMATE ---------------------------------------------------------------------------
    /**
     * Disegna le fermate sulla mappa.
     *
     * @param stops lista di coordinate lat/lon delle fermate.
     * @param route route associata alle fermate.
     */
    public void drawStops(List<double[]> stops, Route route) {
        if (currentRoute != null && route != null &&
                currentRoute.getRouteId().equals(route.getRouteId())) {
            stopsOverlay = new ArrayList<>(stops);
        } else {
            shapesOverlay.clear();
            stopsOverlay = new ArrayList<>(stops);
            currentRoute = route;
        }
        repaint();
    }

    // API PUBBLICA PER DISEGNARE LE SHAPES ----------------------------------------------------------------------------
    /**
     * Disegna le shapes (linee di percorso) sulla mappa.
     *
     * @param shapes lista di coordinate lat/lon delle shapes.
     * @param route  route associata alle shapes.
     */
    public void drawShapes(List<double[]> shapes, Route route) {
        shapesOverlay = new ArrayList<>(shapes);
        currentRoute = route;
        stopsOverlay.clear();
        repaint();
    }

    // API PUBBLICA PER DISEGNARE I VEICOLI ----------------------------------------------------------------------------
    /**
     * Disegna i veicoli sulla mappa.
     *
     * @param vehicles lista di coordinate lat/lon dei veicoli.
     */
    public void drawVehicles(List<double[]> vehicles) {
        vehiclesOverlay = new ArrayList<>(vehicles);
        repaint();
    }

    // CANCELLA I VEICOLI ----------------------------------------------------------------------------------------------
    /**
     * Cancella tutti i veicoli dalla mappa.
     */
    public void clearVehicles() {
        vehiclesOverlay.clear();
        repaint();
    }

    // RESTITUISCE IL CENTRO DELLA LATITUDINE ATTUALE ------------------------------------------------------------------
    /**
     * Restituisce la latitudine del centro della mappa.
     *
     * @return latitudine del centro.
     */
    public double getCenterLat() {
        return TilesManager.tileToLatLon(centerX, centerY, (int) Math.floor(zoom))[0];
    }

    // RESTITUISCE IL CENTRO DELLA LONGITUIDINE ATTUALE ----------------------------------------------------------------
    /**
     * Restituisce la longitudine del centro della mappa.
     *
     * @return longitudine del centro.
     */
    public double getCenterLon() {
        return TilesManager.tileToLatLon(centerX, centerY, (int) Math.floor(zoom))[1];
    }

    // RESTITUISCE IL LIVELLO DI ZOOM ATTUALE --------------------------------------------------------------------------
    /**
     * Restituisce il livello di zoom attuale.
     *
     * @return livello di zoom.
     */
    public double getZoomLevel() {
        return zoom;
    }

    // ANIMAZIONE ZOOM + PAN -------------------------------------------------------------------------------------------
    /**
     * Esegue un'animazione di zoom e pan tra due punti geografici.
     *
     * @param fromLat     latitudine di partenza.
     * @param fromLon     longitudine di partenza.
     * @param toLat       latitudine di arrivo.
     * @param toLon       longitudine di arrivo.
     * @param fromZoom    livello di zoom iniziale.
     * @param toZoom      livello di zoom finale.
     * @param durationMs  durata dell'animazione in millisecondi.
     * @param onFinished  callback eseguita al termine dell'animazione.
     */
    public void animateZoomAndPan(double fromLat, double fromLon,
                                  double toLat,   double toLon,
                                  double fromZoom, double toZoom,
                                  int durationMs,
                                  Runnable onFinished) {

        final long start = System.nanoTime();
        final int delay = 8;

        Timer timer = new Timer(delay, null);
        timer.addActionListener(e -> {

            double elapsed = (System.nanoTime() - start) / 1e6;
            double t = Math.min(elapsed / durationMs, 1.0);

            double te = (t < 0.5) ? 2 * t * t : -1 + (4 - 2 * t) * t;

            double lat = fromLat  + te * (toLat  - fromLat);
            double lon = fromLon  + te * (toLon  - fromLon);
            double z   = fromZoom + te * (toZoom - fromZoom);

            zoom = z;

            double[] tile = TilesManager.latLonToTile(lat, lon, (int) Math.floor(z));
            centerX = tile[0];
            centerY = tile[1];

            clampCenterToBBox();
            repaint();

            if (t >= 1.0) {
                timer.stop();
                if (onFinished != null) onFinished.run();
            }
        });

        timer.setInitialDelay(0);
        timer.start();
    }
}

