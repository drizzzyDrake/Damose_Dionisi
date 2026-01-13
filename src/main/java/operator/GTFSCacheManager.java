package operator;

// Operator.
import static service.NetworkService.isOnline;

// Altre classi.
import java.net.URL;
import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Classe responsabile della gestione della cache dei file GTFS statici.
 * <p>
 * Scarica e aggiorna i file dal sito ATAC al primo avvio se online,
 * li salva in una directory locale (cache),
 * permette di accedere ai dati anche in modalità offline.
 * </p>
 */
public class GTFSCacheManager {

    // URL UFFICIALE DEL FEED STATICO ATAC (GTFS) ----------------------------------------------------------------------
    private static final String GTFS_URL = "https://romamobilita.it/sites/default/files/rome_static_gtfs.zip";

    // DIRECTORY PER MEMORIZZAZIONE DELLA CACHE IN LOCALE (HOME UTENTE) ------------------------------------------------
    private static final Path CACHE_DIR = Paths.get(System.getProperty("user.home"), ".damose_dionisi", "cache", "staticGTFS");

    // VERIFICA CHE ESISTA UNA CACHE VALIDA IN LOCALE ------------------------------------------------------------------
    /**
     * Controlla se la cache locale dei file GTFS esiste e contiene almeno il file stops.txt.
     *
     * @return true se la cache esiste ed è valida, false altrimenti
     */
    public static boolean isCacheAvailable() {
        return Files.exists(CACHE_DIR) && Files.isDirectory(CACHE_DIR)        // Controlla se il percorso esiste, se punta a una directory e...
                && Files.exists(CACHE_DIR.resolve("stops.txt"));        // ...Se contiene almeno il file stops.txt (presente in ogni feed GTFS).
    }

    // RESTITUISCE IL PATH DELLA DIRECTORY DI CACHE --------------------------------------------------------------------
    /**
     * Restituisce il percorso della directory locale dove vengono memorizzati i file GTFS.
     *
     * @return Path della directory di cache
     */
    public static Path getCacheDirectory() {
        return CACHE_DIR;
    }

    // SE ONLINE SCARICA I FILE DAL FEED E AGGIORNA LA CACHE -----------------------------------------------------------
    /**
     * Se il dispositivo è online, scarica il feed GTFS dal sito ATAC,
     * aggiorna la cache locale e rimuove il file ZIP dopo l'estrazione.
     *
     * @throws IOException in caso di errori di I/O durante il download o l'estrazione
     */
    public static void updateCacheIfOnline() throws IOException {

        if (!isOnline()) return;                                                // Se non online esce subito, altrimenti...

        Files.createDirectories(CACHE_DIR);                                     // ...Crea la cartella della cache (e tutte le sottocartelle necessarie).
        Path zipFile = CACHE_DIR.resolve("rome_gtfs.zip");                // Definisce dove salvare il file ZIP scaricato.
        try (InputStream in = new URL(GTFS_URL).openStream()) {                 // Apre uno stream dall’URL e...
            Files.copy(in, zipFile, StandardCopyOption.REPLACE_EXISTING);       // ...Copia il contenuto nel file rome_gtfs.zip sovrascrivendolo se gia esiste.
        }
        unZip(zipFile, CACHE_DIR);                                              // Estrae i file nella cartella CACHE_DIR.
        Files.deleteIfExists(zipFile);                                          // Elimina lo ZIP dopo l'estrazione.
    }

    // ESTRAE I FILE .TXT DAL FILE ZIP NELLA CARTELLA DI CACHE ---------------------------------------------------------
    /**
     * Estrae i file .txt dal file ZIP e li salva nella directory di cache locale.
     *
     * @param zipFile      Path del file ZIP da estrarre
     * @param cachetDir    Directory di destinazione della cache
     * @throws IOException in caso di errori durante l'estrazione
     */
    private static void unZip(Path zipFile, Path cachetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry entry;                                                                     // Apre uno stream ZIP per leggere i file compressi.
            while ((entry = zis.getNextEntry()) != null) {                                      // Legge ogni voce (file o cartella) nel file ZIP.

                if (entry.isDirectory() || !entry.getName().endsWith(".txt")) {                 // Ignora directory o file non .txt
                    zis.closeEntry();
                    continue;
                }

                String fileName = Paths.get(entry.getName()).getFileName().toString();          // Rimuove eventuale prefisso di cartella (es. "google_transit/").

                Path newFile = cachetDir.resolve(fileName);                                     // Costruisce il percorso finale e si assicura che la cartella esista.
                Files.createDirectories(newFile.getParent());

                Files.copy(zis, newFile, StandardCopyOption.REPLACE_EXISTING);                  // Copia il contenuto del file dal ZIP nella cache, sovrascrivendo se esiste.
                zis.closeEntry();
            }
        }
    }
}


