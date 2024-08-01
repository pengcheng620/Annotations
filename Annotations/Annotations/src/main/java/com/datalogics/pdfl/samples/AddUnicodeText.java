package com.datalogics.pdfl.samples;

/*
 *
 * This sample program adds six lines of Unicode text to a PDF file, in six different languages.
 *
 * Copyright (c) 2007-2023, Datalogics, Inc. All rights reserved.
 *
 */

import com.datalogics.PDFL.*;
import com.datalogics.PDFL.Color;
import com.datalogics.PDFL.Font;
import com.datalogics.PDFL.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class AddUnicodeText {

    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable {
        System.out.println("AddUnicodeText sample:");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        // 获取并打印所有已安装字体家族名称
//        String[] fontFamilies = ge.getAvailableFontFamilyNames();
//        System.out.println("已安装的字体家族：");
//        for (String fontFamily : fontFamilies) {
//            System.out.println(fontFamily);
//        }

        Library lib = new Library();
        String sOutput = "AddUnicodeText-out.pdf";
        try {
            System.out.println("Initialized the library.");
            if (args.length != 0)
                sOutput = args[0];
            System.out.println("Output file: " + sOutput);

            Document doc = new Document();
            doc.setMajorVersion((short) 2.0);
            doc.setMinorVersion((short) 5);
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


            Matrix mm = new Matrix(10, 0, 0, 10, x, y);
            TextRun tr1 = new TextRun(" ", new Font("Microsoft YaHei"), gs, ts, mm);
            TextRun tr12 = new TextRun("x", new Font("Microsoft YaHei"), gs, ts, mm);
            TextRun tr13 = new TextRun("我", new Font("Microsoft YaHei"), gs, ts, mm);
            TextRun tr2 = new TextRun(" ", new Font("KozGoPr6N-Medium"), gs, ts, mm);
            TextRun tr22 = new TextRun("x", new Font("KozGoPr6N-Medium"), gs, ts, mm);
            TextRun tr23 = new TextRun("我", new Font("KozGoPr6N-Medium"), gs, ts, mm);
            System.out.println("tr1: " + tr1.getAdvance());
            System.out.println("tr12: " + tr12.getAdvance());
            System.out.println("tr13: " + tr13.getAdvance());
            System.out.println("tr2: " + tr2.getAdvance());
            System.out.println("tr22: " + tr22.getAdvance());
            System.out.println("tr23: " + tr23.getAdvance());


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

            Rect textRect = new Rect(0, 0, 400, 200);
            FreeTextAnnotation annotation = new FreeTextAnnotation(docpage, textRect, "");
            annotation.setFontSize(14);
            annotation.setContents("Chinese (Mandarin)111111111111 - Chinese (Mandarin) - 世界人权宣言");
            annotation.setTextColor(new Color(0.5, 0.5, 0.5));
//            annotation.setFontFace("KozGoPr6N-Medium");
            annotation.setFontFace("Microsoft YaHei");

            Form newAppearance = generateAppearance(annotation);
            annotation.setNormalAppearance(newAppearance);
//            annotation.setNormalAppearance(annotation.generateAppearance());

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

    static Form generateAppearance(FreeTextAnnotation freeTextAnnot) {


        Rect rect = freeTextAnnot.getRect();

        EnumSet<PathPaintOpFlags> paintOpFlags = EnumSet.of(PathPaintOpFlags.NEITHER);
        GraphicState gs = new GraphicState();

        GraphicState gsTextRun = new GraphicState();
        gsTextRun.setFillColor(freeTextAnnot.getTextColor());

        double DAfontSize = freeTextAnnot.getFontSize(); // PCG, this should properly be read from the DA string, the argument preceding the 'tf' command.


        double fontSize = freeTextAnnot.getFontSize();
        TextState textState = new TextState();
        textState.setFontSize(fontSize);
        textState.setWordSpacing(0);

        int align = 0;
        Text cnmText = new Text();
        int i = 0;
        /*if (annotationText != null) {
            String[] textSegments = annotationText.split("\\s");

            final double totalWidth = rect.getWidth() - 2 * padding;
            double remainingWidth = totalWidth;
            Matrix matrix = new Matrix();
            TextRun space = new TextRun(" ", dLFont, gsTextRun, textState, matrix);
            double spaceAdv = space.getAdvance();
            matrix.delete();

            double horizPos = rect.getLeft() + padding;
            double vertPos = rect.getTop() - DAfontSize;
            String currentSegment = textSegments[0];
            StringBuilder currentLine = new StringBuilder();
            matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);
            while (vertPos > rect.getBottom() && currentSegment != null && currentSegment.length() > 0) {
                TextRun dummy = new TextRun(currentSegment, dLFont, gsTextRun, textState, matrix);
                double dummyAdvance = getAdvance(dummy);
                if (dummyAdvance > totalWidth) {
                    // find a breakpoint in the segment if there is room for at least 1 char in the remainingWidth (if notstart a new line and continue),
                    // add first half as a new line, advance vertically and
                    // set up the second half as the currentsegment. And continue.
                    int split = findBreakPoint(currentSegment, remainingWidth - spaceAdv, dLFont, gsTextRun, textState);
                    if (split == -1) {
                        if (align != 0) {
                            matrix.delete();
                            double shift = calcQuadShift(currentLine.toString(), totalWidth, align, dLFont, gsTextRun, textState);
                            matrix = new Matrix(1, 0, 0, 1, horizPos + shift, vertPos);
                        }
                        textRun = new TextRun(currentLine.toString(), dLFont, gsTextRun, textState, matrix);
                        cnmText.addRun(textRun);
                        textRun.delete();
                        matrix.delete();
                        currentLine = new StringBuilder();
                        vertPos -= DAfontSize;
                        horizPos = rect.getLeft() + padding;
                        matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);
                        remainingWidth = totalWidth;
                    } else {
                        String firstHalf = currentSegment.substring(0, split);
                        if (currentLine.length() > 0) {
                            currentLine.append(' ');
                        }
                        currentLine.append(firstHalf);
                        if (align != 0) {
                            matrix.delete();
                            double shift = calcQuadShift(currentLine.toString(), totalWidth, align, dLFont, gsTextRun, textState);
                            matrix = new Matrix(1, 0, 0, 1, horizPos + shift, vertPos);
                        }
                        textRun = new TextRun(currentLine.toString(), dLFont, gsTextRun, textState, matrix);
                        cnmText.addRun(textRun);
                        textRun.delete();
                        matrix.delete();
                        currentLine = new StringBuilder();
                        vertPos -= DAfontSize;
                        horizPos = rect.getLeft() + padding;
                        matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);
                        currentSegment = currentSegment.substring(split);
                        remainingWidth = totalWidth;
                    }

                } else if ((dummyAdvance + spaceAdv) > remainingWidth) {
                    // finalize the currentLine, put the current segment on the next line,
                    // return to the top to try again on the next line.
                    if (align != 0) {
                        matrix.delete();
                        double shift = calcQuadShift(currentLine.toString(), totalWidth, align, dLFont, gsTextRun, textState);
                        matrix = new Matrix(1, 0, 0, 1, horizPos + shift, vertPos);
                    }
                    textRun = new TextRun(currentLine.toString(), dLFont, gsTextRun, textState, matrix);
                    cnmText.addRun(textRun);
                    textRun.delete();
                    matrix.delete();
                    currentLine = new StringBuilder();
                    vertPos -= DAfontSize;
                    horizPos = rect.getLeft() + padding;
                    matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);

                    remainingWidth = totalWidth;
                } else {
                    // add the current segment to the current line,
                    // calculate the remainingWidth,
                    // and get the next text segment as the current segment and continue.
                    if (currentLine.length() > 0) {
                        currentLine.append(' ');
                    }
                    currentLine.append(currentSegment);
                    if (i + 1 >= textSegments.length)
                        currentSegment = null;
                    else
                        currentSegment = textSegments[++i];

                    remainingWidth -= spaceAdv + dummyAdvance;
                }
            }

            if (currentLine.length() > 0) {
                if (align != 0) {
                    matrix.delete();
                    double shift = calcQuadShift(currentLine.toString(), totalWidth, align, dLFont, gsTextRun, textState);
                    matrix = new Matrix(1, 0, 0, 1, horizPos + shift, vertPos);
                }
                textRun = new TextRun(currentLine.toString(), dLFont, gsTextRun, textState, matrix);
                cnmText.addRun(textRun);
                textRun.delete();
                matrix.delete();
            }
        }*/


        Font dLFont = new Font("Microsoft YaHei", EnumSet.of(FontCreateFlags.TO_UNICODE));
        Matrix m1 = new Matrix(1, 0, 0, 1, rect.getLeft(), rect.getTop() - DAfontSize);
        TextRun textRun1 = new TextRun("a god in room 世界人权宣言 zxxxxxxxxxxxxxxxxx", dLFont, gsTextRun, textState, m1);

        cnmText.addRun(textRun1);

        Font dLFon2 = new Font("AdobeMyungjoStd-Medium", EnumSet.of(FontCreateFlags.DO_NOT_EMBED));
        Matrix m = new Matrix(1, 0, 0, 1, rect.getLeft(), rect.getTop() - 3 * DAfontSize);
        TextRun textRun2 = new TextRun("annotationText", dLFon2, gsTextRun, textState, m);
        cnmText.addRun(textRun2);

        Content content = new Content();

        Path path = new Path(gs);
        path.setPaintOp(paintOpFlags);
        path.addRect(new Point(rect.getLeft(), rect.getBottom()), rect.getWidth(), rect.getHeight());

        content.addElement(path);
        content.addElement(cnmText);
        Form form = new Form(content);

//        content.delete();
//        path.delete();
//        cnmText.delete();
//        gs.delete();

        return form;
    }


    double calcQuadShift(String str, double totalWidth, int align, Font f, GraphicState gs, TextState ts) {
        Matrix dummyMatrix = new Matrix();
        TextRun dummy = new TextRun(str, f, gs, ts, dummyMatrix);
        double adv = getAdvance(dummy);
        dummyMatrix.delete();
        dummy.delete();
        return (totalWidth - adv) / align;
    }

    public double getAdvance(TextRun tr) {
        Text dummy = new Text();
        dummy.addRun(tr);
        Point adv = dummy.getAdvanceForTextRun(0);
        dummy.delete();
        double value = adv.getH();
        adv.delete();
        return value;
    }

    public int findBreakPoint(String str, double availWidth, Font f, GraphicState gs, TextState ts) {
        String baseCase = str.substring(0, 0);
        Matrix dummyMatrix = new Matrix();
        TextRun dummy = new TextRun(baseCase, f, gs, ts, dummyMatrix);
        if (getAdvance(dummy) > availWidth) {
            dummy.delete();
            dummyMatrix.delete();
            return -1;
        }
        dummy.delete();

        int rngL = 0;
        int rngR = str.length() - 1;
        int n;
        while (rngR >= rngL) {
            n = (rngL + rngR) / 2;
            String str1 = str.substring(0, n);
            String str2 = str.substring(0, n + 1);
            TextRun dummy1 = new TextRun(str1, f, gs, ts, dummyMatrix);
            TextRun dummy2 = new TextRun(str2, f, gs, ts, dummyMatrix);
            double adv1 = getAdvance(dummy1);
            double adv2 = getAdvance(dummy2);
            dummy1.delete();
            dummy2.delete();
            if (adv2 > availWidth && adv1 <= availWidth) {
                dummyMatrix.delete();
                return n;
            } else {
                if (adv1 < availWidth) {
                    rngL = n + 1;
                } else {
                    rngR = n - 1;
                }
            }
        }

        return -2;
    }
}
