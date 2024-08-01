
import java.util.EnumSet;

import com.datalogics.PDFL.*;


public class TestAnnotations {
    public static void main(String[] args) throws Throwable {
        System.out.println("TestAnnotations sample:");

        Library lib = new Library();
        System.out.println("Initialized the library.");

        try {
            String sOutput = "TestAnnotations-out.pdf";
            if (args.length > 0)
                sOutput = args[0];
            // Create a new document and blank first page
            Document doc = new Document();
            Rect rect = new Rect(0, 0, 612, 792);
            Page page = doc.createPage(Document.BEFORE_FIRST_PAGE, rect);
            System.out.println("Created new document and first page.");

            freeTextAnno(doc, page);
            // Update the page's content and save the file with clipping
            page.updateContent();
            doc.save(EnumSet.of(SaveFlags.FULL), sOutput);
            System.out.println("Saved " + sOutput);

            // Kill the doc object
            doc.delete();
            System.out.println("Killed document object.");

        } finally {
            lib.delete();
        }
    }

    private static void freeTextAnno(Document doc, Page page){

        Rect frame = new Rect(100, 100, 400, 300);
        String defaultAppearance = "";
        FreeTextAnnotation annot = new FreeTextAnnotation(page, frame, defaultAppearance);
        annot.setAnnotationFeatureLevel(2.0);
        annot.setContents("Test Multiple line free text annotation");
        annot.setTextColor(new Color(1, 0, 0));
        annot.setFontSize(32);
        annot.setFontFace("Artifakt Element");
        annot.setBorderStyleWidth(40);

        PDFName pdfKey = new PDFName("BE", doc, false);
        PDFDict pdfObj = new PDFDict(doc, false);
        pdfObj.put("S", new PDFName("C", doc, false));
        pdfObj.put("I", new PDFReal(1, doc, false));
//        annot.getPDFDict().put(pdfKey, pdfObj);

        // how to set the fill color(with opacity) and border(color, width and style(cloudy, solid, dashed)) and the padding

//        annot.setFillColor(new Color(0, 1, 0, 0.5));
//        annot.setBorderColor(new Color(0, 0, 1));
//        annot.setBorderWidth(10);
//        annot.setBorderStyle(EnumSet.of(BorderStyleFlags.CLOUDY));
//        annot.setPadding(new Rect(10, 10, 10, 10));
        annot.getPDFDict().put(pdfKey, pdfObj);

        annot.setNormalAppearance(annot.generateAppearance());
//        annot.getNormalAppearance().getStream().getDict().put(pdfKey, pdfObj);

    }

}
