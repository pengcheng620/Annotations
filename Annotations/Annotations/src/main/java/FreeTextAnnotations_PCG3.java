import java.util.EnumSet;

import com.datalogics.PDFL.AnnotationFlags;
import com.datalogics.PDFL.Color;
import com.datalogics.PDFL.Content;
import com.datalogics.PDFL.Document;
import com.datalogics.PDFL.ExtendedGraphicState;
import com.datalogics.PDFL.Font;
import com.datalogics.PDFL.FontCreateFlags;
import com.datalogics.PDFL.Form;
import com.datalogics.PDFL.FreeTextAnnotation;
import com.datalogics.PDFL.GraphicState;
import com.datalogics.PDFL.HorizontalAlignment;
import com.datalogics.PDFL.Library;
import com.datalogics.PDFL.Matrix;
import com.datalogics.PDFL.PDFDict;
import com.datalogics.PDFL.PDFName;
import com.datalogics.PDFL.Page;
import com.datalogics.PDFL.Path;
import com.datalogics.PDFL.PathPaintOpFlags;
import com.datalogics.PDFL.Point;
import com.datalogics.PDFL.Rect;
import com.datalogics.PDFL.SaveFlags;
import com.datalogics.PDFL.Text;
import com.datalogics.PDFL.TextRun;
import com.datalogics.PDFL.TextState;

import java.lang.StringBuilder;

/*
 *
 * A sample which demonstrates the use of the DLE API to work with
 * PolygonAnnotation objects
 *
 * Copyright (c) 2007-2017, Datalogics, Inc. All rights reserved.
 *
 * The information and code in this sample is for the exclusive use of Datalogics
 * customers and evaluation users only.  Datalogics permits you to use, modify and
 * distribute this file in accordance with the terms of your license agreement.
 * Sample code is for demonstrative purposes only and is not intended for production use.
 *
 */

public class FreeTextAnnotations_PCG3 {
    static String FONT_NAME = "Microsoft YaHei";

    public static double getAdvance(TextRun tr) {
        Text dummy = new Text();
        dummy.addRun(tr);
        Point adv = dummy.getAdvanceForTextRun(0);
        dummy.delete();
        double value = adv.getH();
        adv.delete();
        return value;
    }

    public static int findBreakPoint(String str, double availWidth, Font f, GraphicState gs, TextState ts) {
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

    public static double calcQuadShift(String str, double totalWidth, int align, Font f, GraphicState gs, TextState ts) {
        Matrix dummyMatrix = new Matrix();
        TextRun dummy = new TextRun(str, f, gs, ts, dummyMatrix);
        double adv = getAdvance(dummy);
        dummyMatrix.delete();
        dummy.delete();
        return (totalWidth - adv) / align;
    }

    public static Form generateAppearance(FreeTextAnnotation freeTextAnnot, Font dLFont) {
        String annotationText = freeTextAnnot.getContents();

        Rect rect = freeTextAnnot.getRect();
        EnumSet<PathPaintOpFlags> paintOpFlags = EnumSet.of(PathPaintOpFlags.NEITHER);
        GraphicState gs = new GraphicState();

        GraphicState gsTextRun = new GraphicState();
        gsTextRun.setFillColor(freeTextAnnot.getTextColor());

        PDFDict pdfDict = freeTextAnnot.getPDFDict();

        System.out.println("pdfDict: " + pdfDict);
        // NOTE: This code does not handle "BS","Border", and "BE" entries in the annotation dictionary, so it is not strictly complete.
        if (pdfDict.contains("BS")) {
            paintOpFlags.add(PathPaintOpFlags.STROKE);
            gs.setStrokeColor(freeTextAnnot.getTextColor());
//        	gs.setDashPattern(freeTextAnnot.getDashPattern());
//        	gs.setWidth(freeTextAnnot.getWidth());
            gs.setDashPattern(freeTextAnnot.getBorderStyleDashPattern());
            gs.setWidth(freeTextAnnot.getBorderStyleWidth());
        }
        if (pdfDict.contains("C")) {
            paintOpFlags.add(PathPaintOpFlags.FILL);
            gs.setFillColor(freeTextAnnot.getInteriorColor());
        }

        double DAfontSize = freeTextAnnot.getFontSize(); // PCG, this should properly be read from the DA string, the argument preceding the 'tf' command.
        double padding = 1;
        //gs.setWidth(1);
        TextState textState = new TextState();
        textState.setFontSize(DAfontSize);

        int align = 0;
        HorizontalAlignment q = freeTextAnnot.getQuadding();
        if (q == HorizontalAlignment.CENTER) align = 2;
        else if (q == HorizontalAlignment.RIGHT) align = 1;

        TextRun textRun = null;
        Text text = new Text();

        int i = 0;

        if (annotationText != null) {
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
                        text.addRun(textRun);
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
                        text.addRun(textRun);
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
                    text.addRun(textRun);
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
                text.addRun(textRun);
                textRun.delete();
                matrix.delete();
            }
        }

        // create content to save the new annotation to
        Content content = new Content();

        Path path = new Path(gs);
        path.setPaintOp(paintOpFlags);
        path.addRect(new Point(rect.getLeft(), rect.getBottom()), rect.getWidth(), rect.getHeight());

        content.addElement(path);
        content.addElement(text);
        Form form = new Form(content);

        content.delete();
        path.delete();
        text.delete();
        gs.delete();
        double opacity = freeTextAnnot.getOpacity();
        if (opacity < 1.0) {
            Content transContent = new Content(form);
            Form transForm = new Form(transContent);
            PDFDict formDict = transForm.getStream().getDict();
            PDFDict transDict = new PDFDict(formDict, false);
            transDict.put("S", new PDFName("Transparency", transDict, false));
            formDict.put("Group", transDict);
            GraphicState transGS = new GraphicState();
            ExtendedGraphicState extgs = new ExtendedGraphicState();

            extgs.setOpacityForStroking(0.2);
            extgs.setOpacityForOtherThanStroking(.5);

            transGS.setExtendedGraphicState(extgs);
            transForm.setGraphicState(transGS);
            Content normAPContent = new Content(transForm);
            Form normAPForm = new Form(normAPContent);

            normAPContent.delete();
            extgs.delete();
            transGS.delete();
            transDict.delete();
            transForm.delete();
            transContent.delete();

            return normAPForm;
        }

        return form;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable {
        System.out.println("FreeTextAnnotation sample:");

        Library lib = new Library();
        System.out.println("Initialized the library.");

        try {
//			Reader reader = new InputStreamReader( new FileInputStream(args[0]),"UTF-16");
//			Reader reader = new InputStreamReader(Files.newInputStream(Paths.get("./src/main/resources/test.pdf")), StandardCharsets.UTF_16);
//			BufferedReader fin = new BufferedReader(reader);
//			StringBuilder sb = new StringBuilder();
//			String s;
//		    while ((s=fin.readLine())!=null) {
//				sb.append(s);
//   			}
//
//   			fin.close();
            // Create a new document and blank first page
            Document doc = new Document();
            Rect rect = new Rect(0, 0, 612, 792);
            Page page = doc.createPage(Document.BEFORE_FIRST_PAGE, rect);
            System.out.println("Created new document and first page.");

            Rect annotRect = new Rect(38.5391, 37.2218, 303.155, 299.293);

            // Create and add a new FreeTextAnnotation to the 0th element of first page's annotation array
            String DA = "0 G 1 0 0 rg 0 Tc 0 Tw 100 Tz 0 TL 0 Ts 0 Tr /" + FONT_NAME + " 11 Tf";
//            Font dLFont = new Font("MS-Mincho", EnumSet.of(FontCreateFlags.DO_NOT_EMBED /*,FontCreateFlags.ALL_WIDTHS*/, FontCreateFlags.DEFER_WIDTHS));
            Font dLFont = new Font(FONT_NAME, EnumSet.of(FontCreateFlags.DO_NOT_EMBED /*,FontCreateFlags.ALL_WIDTHS*/, FontCreateFlags.DEFER_WIDTHS));
            FreeTextAnnotation freetextAnnot = new FreeTextAnnotation(page, annotRect, DA);
//			freetextAnnot.setContents(sb.toString());
//            freetextAnnot.setContents("Built with the same core technology used in Adobe Acrobat, Adobe PDF Library provides a reliable and consistent experience whether you are deploying across Windows, Linux or Mac platforms. Embed Adobe PDF functionality within client and/or server solutions easily with our comprehensive SDK.");
            freetextAnnot.setContents("湾湾会在 2024-12-24 回归");
            System.out.println("Created new FreetextAnnotation as 0th element of annotation array.");


            Color color = new Color(0.5, 0.3, 0.8);
            freetextAnnot.setInteriorColor(color);
            Color gold = new Color(0.9, 0.7, 0.1);
            freetextAnnot.setTextColor(gold);
            System.out.println("Set the stroke and fill colors.");
            //freetextAnnot.setQuadding(HorizontalAlignment.RIGHT);


            freetextAnnot.setNormalAppearance(generateAppearance(freetextAnnot, dLFont));
            System.out.println("Generated the appearance stream.");

            //PCG: Add another annotation with same text but with a narrow rect to test text-wrapping.
            FreeTextAnnotation freetextAnnot2 = new FreeTextAnnotation(page, new Rect(40, 350, 240, 650), DA);
//            freetextAnnot2.setContents(sb.toString());
//            freetextAnnot2.setContents("With the same core technology used in Adobe Acrobat, Adobe PDF Library provides a reliable and consistent experience whether you are deploying across Windows, Linux or Mac platforms. Embed Adobe PDF functionality within client and/or server solutions easily with our comprehensive SDK.");
            freetextAnnot2.setContents("湾湾会在 2024-12-24 回归");
            freetextAnnot2.setQuadding(HorizontalAlignment.CENTER);
            freetextAnnot2.setInteriorColor(gold);
//            freetextAnnot2.setWidth(1.5);
            freetextAnnot2.setBorderStyleWidth(1);
            freetextAnnot2.setOpacity(0.5);
            freetextAnnot2.setNormalAppearance(generateAppearance(freetextAnnot2, dLFont));

            //Set "/F 4" Entry - Add by Maruyama start
            EnumSet<AnnotationFlags> flag = freetextAnnot.getFlags();
            flag = EnumSet.of(AnnotationFlags.PRINT);
            freetextAnnot.setFlags(flag);
            //Add by Maruyama end

            freetextAnnot2.setFlags(flag);

            // Update the page's content and save the file with clipping
            page.updateContent();
            doc.save(EnumSet.of(SaveFlags.FULL), "FreeTextAnnotations_PCG3-out.pdf");
            System.out.println("Saved FreeTextAnnotations_PCG3-out.pdf");

            // Kill the doc object
            doc.delete();
            System.out.println("Killed document object.");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lib.delete();
        }
    }
}
