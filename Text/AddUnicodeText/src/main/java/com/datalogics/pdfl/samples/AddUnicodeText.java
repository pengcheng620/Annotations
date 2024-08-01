package com.datalogics.pdfl.samples;

/*
 *
 * This sample program adds six lines of Unicode text to a PDF file, in six different languages.
 *
 * Copyright (c) 2007-2023, Datalogics, Inc. All rights reserved.
 *
 */

import com.datalogics.PDFL.*;

import java.util.ArrayList;
import java.util.List;
import java.util.EnumSet;

public class AddUnicodeText {

    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable {
        System.out.println("AddUnicodeText sample:");

        Library lib = new Library();
        String sOutput = "AddUnicodeText-out.pdf";
        try {
            System.out.println("Initialized the library.");
            if (args.length != 0)
                sOutput = args[0];
            System.out.println("Output file: " + sOutput);
            Document doc = new Document();
            System.out.println("version:::::" + doc.getVersionString());

            Rect pageRect = new Rect(0, 0, 612, 792);
            Page docpage = doc.createPage(Document.BEFORE_FIRST_PAGE, pageRect);

            Text unicodeText = new Text();
            GraphicState gs = new GraphicState();
            TextState ts = new TextState();

            List<String> strings = new ArrayList<String>();

            strings.add("Chinese (Mandarin)111111111111 - Chinese (Mandarin) - 世界人权宣言");
            strings.add("Japanese - \u300e\u4e16\u754c\u4eba\u6a29\u5ba3\u8a00\u300f");
            strings.add("French - \u0044\u00e9\u0063\u006c\u0061\u0072\u0061\u0074\u0069\u006f\u006e\u0020\u0075\u006e\u0069\u0076\u0065\u0072\u0073\u0065\u006c\u006c\u0065\u0020\u0064\u0065\u0073\u0020\u0064\u0072\u006f\u0069\u0074\u0073\u0020\u0064\u0065\u0020\u006c\u2019\u0068\u006f\u006d\u006d\u0065");
            strings.add("Korean - \uc138\u0020\uacc4\u0020\uc778\u0020\uad8c\u0020\uc120\u0020\uc5b8");
            strings.add("English - \u0055\u006e\u0069\u0076\u0065\u0072\u0073\u0061\u006c\u0020\u0044\u0065\u0063\u006c\u0061\u0072\u0061\u0074\u0069\u006f\u006e\u0020\u006f\u0066\u0020\u0048\u0075\u006d\u0061\u006e\u0020\u0052\u0069\u0067\u0068\u0074\u0073");
            strings.add("Greek - \u039f\u0399\u039a\u039f\u03a5\u039c\u0395\u039d\u0399\u039a\u0397\u0020\u0394\u0399\u0391\u039a\u0397\u03a1\u03a5\u039e\u0397\u0020\u0393\u0399\u0391\u0020\u03a4\u0391\u0020\u0391\u039d\u0398\u03a1\u03a9\u03a0\u0399\u039d\u0391\u0020\u0394\u0399\u039a\u0391\u0399\u03a9\u039c\u0391\u03a4\u0391");
            strings.add("Russian - \u0412\u0441\u0435\u043e\u0431\u0449\u0430\u044f\u0020\u0434\u0435\u043a\u043b\u0430\u0440\u0430\u0446\u0438\u044f\u0020\u043f\u0440\u0430\u0432\u0020\u0447\u0435\u043b\u043e\u0432\u0435\u043a\u0430");

            List<Font> fonts = new ArrayList<Font>();
            fonts.add(new Font("Microsoft YaHei"));
            fonts.add(new Font("KozGoPr6N-Medium"));
            fonts.add(new Font("AdobeMyungjoStd-Medium"));

            // These will be used to place the strings into position on the page.
            int x = 1 * 72;
            int y = 10 * 72;

            for (String str : strings) {
                // Find a font that can represent all characters in the string, if there is one.
                Font font = GetRepresentableFont(fonts, str);
                if (font == null) {
                    System.out.println("Couldn't find a font that can represent all characters in the string: " + str);
                } else {
                    // From this point, the string is handled the same way as non-Unicode text.
                    Matrix m = new Matrix(10, 0, 0, 10, x, y);
                    TextRun tr = new TextRun(font.getName() + " - " + str, font, gs, ts, m);
                    unicodeText.addRun(tr);
                }

                // Start the next string lower down the page.
                y -= 18;
            }
            docpage.getContent().addElement(unicodeText);
            docpage.updateContent();


            Rect textRect = new Rect(0, 0, 100, 200);
            FreeTextAnnotation annotation = new FreeTextAnnotation(docpage, textRect, "");

            annotation.setFontSize(14);
            annotation.setContents("Chinese (Mandarin)111111111111 - Chinese (Mandarin) - 世界人权宣言");
            annotation.setTextColor(new Color(0.5, 0.5, 0.5));
//            annotation.setFontFace("KozGoPr6N-Medium");
            annotation.setFontFace("Microsoft YaHei");
            annotation.setNormalAppearance(annotation.generateAppearance());

            // Save the document.
            System.out.println("Embedding fonts.");
            doc.embedFonts(EnumSet.of(EmbedFlags.NONE));
            System.out.println("Saving to " + sOutput);
            doc.save(EnumSet.of(SaveFlags.FULL), sOutput);

        } finally {
            lib.delete();
        }
    }


    public static Font GetRepresentableFont(List<Font> fonts, String str) {
        for (Font font : fonts) {
            if (font.isTextRepresentable(str)) {
                return font;
            }
        }
        return null;
    }
}
