package model.repository;

// Model.
import model.ShapePoint;

// Classi per parsing.
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

// Altre classi.
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Repository per la gestione delle entità {@link ShapePoint}
 * <p>
 * Questa classe si occupa di leggere i dati dei punti geografici da un file CSV e di
 * caricarli in memoria come oggetti {@code ShapePoint}. I dati vengono memorizzati
 * in una lista interna, accessibile tramite appositi metodi getter.
 * </p>
 * <p>
 * Utilizza Apache Commons CSV per il parsing dei file.
 * </p>
 */
public class ShapePointRepository {

    // Lista che contiene tutti gli oggetti ShapePoint parsati dal file.
    private List<ShapePoint> shapePoints = new ArrayList<>();

    /**
     * Carica il file {@code shapes.txt} e costruisce la lista dei {@link ShapePoint}.
     * <p>
     * Ogni riga del file viene convertita in un oggetto {@link ShapePoint},
     * con i valori letti e assegnati ai relativi campi tramite i metodi setter.
     * </p>
     * <p>
     * Utilizza un blocco try-with-resources per garantire la chiusura automatica
     * dello {@link InputStream} e del {@link CSVParser}.
     * </p>
     *
     * @param input lo {@link InputStream} contenente il file {@code shapes.txt}
     * @throws IOException se si verifica un errore durante la lettura o il parsing del file
     */
    public void loadShapePointsFromStream(InputStream input) throws IOException {   // Eccezione propagata al chiamante (controller).
        try (CSVParser parser = CSVFormat.DEFAULT                                   // try-with-resources : chiude automaticamente InputStream e CSVParser.
                .withFirstRecordAsHeader()                                          // Dice al parser che la prima riga contiene i nomi delle colonne.
                .parse(new InputStreamReader(input))){                              // Carica il file CSV da uno stream specificato.

            for (CSVRecord record : parser) {   // Per ogni riga del file...
                ShapePoint shapePoint = new ShapePoint();       // ...Crea un nuovo oggetto ShapePoint...
                shapePoint.setShapeId(record.get("shape_id"));   // ...Imposta ogni campo usando i setter...
                shapePoint.setShapePtLat(parseDouble(record.get("shape_pt_lat")));
                shapePoint.setShapePtLon(parseDouble(record.get("shape_pt_lon")));
                shapePoint.setShapePtSequence(parseInt(record.get("shape_pt_sequence")));
                shapePoint.setShapeDistTraveled(parseDouble(record.get("shape_dist_traveled")));
                shapePoints.add(shapePoint);                                 // ...Aggiunge la shapePoint alla lista.
            }
        }
    }

    /**
     * Converte in modo sicuro una stringa in un intero, gestendo i casi di campi vuoti o mancanti.
     * <p>
     * Questo metodo evita eccezioni {@link NumberFormatException} restituendo {@code null}
     * se il valore fornito è nullo o vuoto.
     * </p>
     *
     * @param value la stringa da convertire
     * @return il valore intero corrispondente o {@code null} se il campo è vuoto o mancante
     */
    private Integer parseInt(String value) {
        return (value == null || value.isEmpty()) ? null : Integer.parseInt(value);
    }

    /**
     * Converte in modo sicuro una stringa in un numero decimale, gestendo i casi di campi vuoti o mancanti.
     * <p>
     * Questo metodo evita eccezioni {@link NumberFormatException} restituendo {@code null}
     * se il valore fornito è nullo o vuoto.
     * </p>
     *
     * @param value la stringa da convertire
     * @return il valore double corrispondente o {@code null} se il campo è vuoto o mancante
     */
    private Double parseDouble(String value) {
        return (value == null || value.isEmpty()) ? null : Double.parseDouble(value);
    }

    /**
     * Restituisce tutte le shape point caricate dal file CSV.
     *
     * @return lista completa di {@link ShapePoint}
     */
    public List<ShapePoint> getAllShapePoints() {
        return shapePoints;
    }
}
