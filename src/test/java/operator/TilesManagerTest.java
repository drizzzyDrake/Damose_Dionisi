package operator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per TilesManager.
 *
 * La classe testa SOLO le funzioni matematiche:
 *  - latLonToTile
 *  - tileToLatLon
 */
public class TilesManagerTest {

    // LATLON-TILE REVERSIBLE TEST -------------------------------------------------------------------------------------
    /**
     * Verifica che la conversione lat/lon -> tile -> lat/lon
     * sia approssimativamente reversibile.
     */
    @Test
    void testLatLonConversionIsReversible() {
        double lat = 43.7696;
        double lon = 11.2558;
        int zoom = 14;

        // Converto lat/lon in tile.
        double[] tile = TilesManager.latLonToTile(lat, lon, zoom);

        // Riconverto tile in lat/lon.
        double[] latlon = TilesManager.tileToLatLon(tile[0], tile[1], zoom);

        // Controllo che la differenza sia minima.
        assertEquals(lat, latlon[0], 0.0001, "Latitudine non coerente");
        assertEquals(lon, latlon[1], 0.0001, "Longitudine non coerente");
    }

    // TILESIZE TEST ---------------------------------------------------------------------------------------------------
    /**
     * Verifica che la dimensione delle tile sia quella attesa.
     */
    @Test
    void testTileSizeConstant() {
        assertEquals(512, TilesManager.getTileSize());
    }

    // LATLONTOTILE TEST -----------------------------------------------------------------------------------------------
    /**
     * Verifica che latLonToTile produca valori nel range corretto.
     */
    @Test
    void testTileRange() {
        double lat = 43.7696;
        double lon = 11.2558;
        int zoom = 14;

        double[] tile = TilesManager.latLonToTile(lat, lon, zoom);

        int max = (1 << zoom);

        assertTrue(tile[0] >= 0 && tile[0] <= max, "Tile X fuori range");
        assertTrue(tile[1] >= 0 && tile[1] <= max, "Tile Y fuori range");
    }
}
