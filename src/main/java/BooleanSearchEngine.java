import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<String, List<PageEntry>> map = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы
        for (File pdf : pdfsDir.listFiles()) {
            var document = new PdfDocument(new PdfReader(pdf));
            int pageNumber = document.getNumberOfPages();
            for (int i = 1; i <= pageNumber; i++) {
                var page = document.getPage(i);
                var text = PdfTextExtractor.getTextFromPage(page);
                var words = text.split("\\P{IsAlphabetic}+");

                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }

                for (String word : freqs.keySet()) {
                    PageEntry pageEntry = new PageEntry(pdf.getName(), i, freqs.get(word));
                    if (map.containsKey(word)) {
                        map.get(word).add(pageEntry);

                    } else {
                        map.put(word, new ArrayList<>());
                        map.get(word).add(pageEntry);
                    }
                    map.values().forEach(Collections::sort);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        // тут реализуйте поиск по слову
        List<PageEntry> result = new ArrayList<>();
        String wordToLowerCase = word.toLowerCase();
        if (map.get(wordToLowerCase) != null) {
            for (PageEntry pageEntry : map.get(wordToLowerCase)) {
                result.add(pageEntry);
            }
            return result;
        } else {
            return Collections.emptyList();
        }
    }
}