import com.datalogics.PDFL.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class RestApiDatalogicsUtil {
    public static class DestDocumentRect {
        private final Double width;
        private final Double height;

        public DestDocumentRect(Double width, Double height) {
            this.width = width;
            this.height = height;
        }
    }

    public static class SourceLmvRect {
        private final Double width;
        private final Double height;

        public SourceLmvRect(Double width, Double height) {
            this.width = width;
            this.height = height;
        }
    }

    public static class ContentStackItem {
        private final Content content;
        private final int prevQueueNum;

        private ContentStackItem(Content content, int prevQueueNum) {
            this.content = content;
            this.prevQueueNum = prevQueueNum;
        }
    }

    public static class TextAttributes {
        private Font font;
        private GraphicState graphicState;
        private TextState textState;
        private Text text;

        private TextAttributes(Font font, GraphicState graphicState, TextState textState, Text text) {
            this.font = font;
            this.graphicState = graphicState;
            this.textState = textState;
            this.text = text;
        }
    }

    private static class PaddingText {
        double padding;
        double borderWidth;
        double originFontsize;
        double appearanceFontSize;
        double textToRectTop;
        double textToRectLeft;
        double measuredWidth;

        PaddingText(Page page, double fontSize, double borderWidth, SourceLmvRect sourceLmvRect) {
            this.borderWidth = borderWidth;
            this.appearanceFontSize = fontSize * (sourceLmvRect.width + sourceLmvRect.height) / 72;
            if (page.getRotation() == PageRotation.ROTATE_270) {
                this.originFontsize = fontSize;

                double textWidth = fontSize * 5 / 18;
                this.padding = textWidth / 2 + this.borderWidth / 2;
                this.measuredWidth = this.appearanceFontSize * 0.56;
                this.textToRectTop = this.borderWidth + this.measuredWidth;
                this.textToRectLeft = this.borderWidth * 3 - this.appearanceFontSize * 0.15;
            } else {
                this.padding = this.appearanceFontSize / 2 + this.borderWidth;
                this.textToRectTop = this.appearanceFontSize * 5 / 6 + this.padding;
                this.textToRectLeft = this.padding;
            }
        }
    }

    SourceLmvRect sourceLmvRect;
    DestDocumentRect destDocumentRect;
    Page curPage;
    static PaddingText paddingText;

    private static final String NEW_LINE_FLAG = "<NL>";

    private static final String PDF_EXTENSION = ".pdf";
    private static final String LMV_MARKUP_TYPE = "type";
    private static final String LMV_MARKUP_TYPE_CLOUD = "cloud";
    private static final String LMV_MARKUP_TYPE_ELLIPSE = "ellipse";
    private static final String LMV_MARKUP_TYPE_RECTANGLE = "rectangle";
    private static final String LMV_MARKUP_TYPE_POLYCLOUD = "polycloud";
    private static final String LMV_MARKUP_TYPE_FREEHAND = "freehand";
    private static final String LMV_MARKUP_TYPE_HIGHLIGHT = "highlight";
    private static final String LMV_MARKUP_TYPE_POLYLINE = "polyline";
    private static final String LMV_MARKUP_TYPE_LINE = "line";
    private static final String LMV_MARKUP_TYPE_ARROW = "arrow";
    private static final String LMV_MARKUP_TYPE_CALLOUT = "callout";

    private static final String LMV_MARKUP_FRAMESTATE = "frameState";
    private static final String LMV_MARKUP_STATE = "state";
    private static final String LMV_MARKUP_STATE_TEXT = "text";
    private static final String LMV_MARKUP_STATE_TRANSLATION = "translation";
    private static final String LMV_MARKUP_STATE_ROTATION = "rotation";
    private static final String LMV_MARKUP_STATE_SCALE = "scale";
    private static final String LMV_MARKUP_STATE_ANCHORS = "anchors";
    private static final String LMV_MARKUP_STATE_STYLE = "style";
    private static final String LMV_MARKUP_STATE_STYLE_STROKE_COLOR = "stroke-color";
    private static final String LMV_MARKUP_STATE_STYLE_FILL_COLOR = "fill-color";
    private static final String LMV_MARKUP_STATE_STYLE_FILL_OPACITY = "fill-opacity";
    private static final String LMV_MARKUP_STATE_STYLE_STROKE_WIDTH = "stroke-width";
    private static final String LMV_MARKUP_STATE_STYLE_STROKE_OPACITY = "stroke-opacity";
    private static final String LMV_MARKUP_STATE_STYLE_FONT_SIZE = "font-size";
    private static final String LMV_MARKUP_STATE_STYLE_FONT_COLOR = "font-color";

    public String licenseKey;

    public static void main(String[] args) throws Exception {
        System.out.println("Add Annotation to PDF:");

        Library lib = new Library();
        try {
            RestApiDatalogicsUtil util = new RestApiDatalogicsUtil();

//            MarkupsData.Pdf5x5 x5 = new MarkupsData.Pdf5x5();
//            util.generateLmvMarkupDocument(x5.mp, x5.sourceLmvRect, x5.inputFile, x5.outputFile);
//            MarkupsData.Pdf5x7_5 x5_7_5 = new MarkupsData.Pdf5x7_5();
//            util.generateLmvMarkupDocument(x5_7_5.mp, x5_7_5.sourceLmvRect, x5_7_5.inputFile, x5_7_5.outputFile);
//            MarkupsData.Pdf5x10 x5_10 = new MarkupsData.Pdf5x10();
//            util.generateLmvMarkupDocument(x5_10.mp, x5_10.sourceLmvRect, x5_10.inputFile, x5_10.outputFile);
//
//            MarkupsData.Pdf10x10 x10 = new MarkupsData.Pdf10x10();
//            util.generateLmvMarkupDocument(x10.mp, x10.sourceLmvRect, x10.inputFile, x10.outputFile);
//            MarkupsData.Pdf10x5 x10_5 = new MarkupsData.Pdf10x5();
//            util.generateLmvMarkupDocument(x10_5.mp, x10_5.sourceLmvRect, x10_5.inputFile, x10_5.outputFile);
//
//            MarkupsData.Pdf15x15 x15 = new MarkupsData.Pdf15x15();
//            util.generateLmvMarkupDocument(x15.mp, x15.sourceLmvRect, x15.inputFile, x15.outputFile);
//
            MarkupsData.Dwg22x17 dwg22x17 = new MarkupsData.Dwg22x17();
            util.generateLmvMarkupDocument(dwg22x17.difFS2, dwg22x17.sourceLmvRect, dwg22x17.inputFile, dwg22x17.outputFile);

//            util.generateLmvMarkupDocument(MarkupsData.lxData, sourcePDF1, inputFile, outputFile);
//            util.generateLmvMarkupDocument(lxData, sourceLmvRect, inputFileName, outputFileName);
//			util.generateLmvMarkupDocument(pdfText, sourceLmvRectPDF, inputFileNamePDF, outputFileNamePDF);
//            DatalogicsUtil.generateLmvMarkupDocument(text4, sourceLmvRect, inputFileName, outputFileName);
        } finally {
            lib.delete();
        }

    }

    public void generateLmvMarkupDocument(String lmvMarkupData, String sourceLmvRectData, String inputFileName, String outputFileName) {
        // Library.setLicenseKey("xxxx-xxxx-xxxx-xxxx");
        Library lib = new Library();
        try {
            JSONObject sourceLmvRectObject = new JSONObject(sourceLmvRectData);
            JSONObject data = new JSONObject(lmvMarkupData);
            String createdByUserName = data.getString("CreatedByUserName");
            JSONArray markups = data.getJSONArray("markups");
            String sheetName = data.getString("sheetName");
            int pageNumber = 0;
            try {
                pageNumber = Integer.parseInt(sheetName.split(" ")[1]) - 1;
            } catch (Exception ignored) {
            }

            System.out.println("pageNumber: " + pageNumber);
            Document document = new Document(inputFileName);
            Page page = document.getPage(pageNumber);
            curPage = page;
            double sourceWidth = sourceLmvRectObject.getDouble("width");
            double sourceHeight = sourceLmvRectObject.getDouble("height");
            double cropBoxWidth = page.getCropBox().getWidth();
            double cropBoxHeight = page.getCropBox().getHeight();
            double width, height;
            if (sourceWidth > sourceHeight) {
                width = Math.max(cropBoxWidth, cropBoxHeight);
                height = Math.min(cropBoxWidth, cropBoxHeight);
            } else {
                width = Math.min(cropBoxWidth, cropBoxHeight);
                height = Math.max(cropBoxWidth, cropBoxHeight);
            }
            destDocumentRect = new RestApiDatalogicsUtil.DestDocumentRect(width, height);
            System.out.println("getWidth: " + page.getCropBox().getWidth());
            System.out.println("getHeight: " + page.getCropBox().getHeight());
            System.out.println("realWidth: " + width);
            System.out.println("realHeight: " + height);
            sourceLmvRect = new RestApiDatalogicsUtil.SourceLmvRect(sourceWidth, sourceHeight);

            for (int i = 0; i < markups.length(); i++) {
                JSONObject markup = markups.getJSONObject(i);
                String type = markup.getString("type");
                switch (type) {
                    case "cloud":
                    case "rectangle":
                    case "ellipse":
                        System.out.println("Supported markup type: " + type);
                        translateEllipseOrRectangle(document, page, markup, createdByUserName);
                        break;
                    case "polyline":
                    case "polycloud":
                    case "freehand":
                    case "highlight":
                    case "line":
                    case "arrow":
                        System.out.println("Supported markup type: " + type);
                        translatePolyline(page, markup, createdByUserName);
                        break;
                    case "callout":
                        System.out.println("Supported markup type: " + type);
                        translateCallout(document, page, markup, createdByUserName);
//                        translateText(document, page, markup, createdByUserName);
                        break;
                    default:
                        System.out.println("Unsupported markup type: " + type);
                }
            }

            page.updateContent();
            document.save(EnumSet.of(SaveFlags.FULL), outputFileName);
        } finally {
            lib.delete();
        }
    }


    /**
     * Translates and applies an ellipse or rectangle markup to a PDF page.
     *
     * @param doc               The PDF document.
     * @param page              The page to which the markup will be applied.
     * @param markup            The markup data in JSONObject format.
     * @param createdByUserName The name of the user who created the markup.
     */

    void translateEllipseOrRectangle(Document doc, Page page, JSONObject markup, String createdByUserName) {
        String type = markup.getString(LMV_MARKUP_TYPE);
        JSONObject state = markup.getJSONObject(LMV_MARKUP_STATE);
        JSONObject translation = state.getJSONObject(LMV_MARKUP_STATE_TRANSLATION);
        Double rotation = state.getDouble(LMV_MARKUP_STATE_ROTATION);
        JSONObject scale = state.getJSONObject(LMV_MARKUP_STATE_SCALE);
        JSONArray anchors = state.getJSONArray(LMV_MARKUP_STATE_ANCHORS);
        JSONObject style = state.getJSONObject(LMV_MARKUP_STATE_STYLE);
        String strokeColor = style.getString(LMV_MARKUP_STATE_STYLE_STROKE_COLOR);
        String fillColor = style.getString(LMV_MARKUP_STATE_STYLE_FILL_COLOR);
        Double fillOpacity = style.getDouble(LMV_MARKUP_STATE_STYLE_FILL_OPACITY);
        Double strokeWidth = style.getDouble(LMV_MARKUP_STATE_STYLE_STROKE_WIDTH);

        Double borderWidth = translateBorderStyleWidth(strokeWidth);
        Rect rect = translateRect(anchors);
        Rect transformedRect = transformAnnotationRect(rect, borderWidth, translateTranslation(translation), translateScale(scale));
        Annotation annot = (type.equals(LMV_MARKUP_TYPE_ELLIPSE)) ? getCircleAnnotation(page, transformedRect, borderWidth) : getSquareAnnotation(page, transformedRect, borderWidth);
        annot.setTitle(createdByUserName);
        if (strokeColor.startsWith("#")) {
            annot.setColor(translateColor(strokeColor));
        }
        if (fillColor.startsWith("#")) {
            annot.setInteriorColor(translateColor(fillColor));
        }
        annot.setOpacity(fillOpacity);
        annot.setNormalAppearance(annot.generateAppearance());

        setNormalAppearance(annot, markup, type);
        rotateAnnotationRect(annot, doc, -Math.toDegrees(rotation));
    }

    private CircleAnnotation getCircleAnnotation(Page page, Rect transformedRect, Double borderWidth) {
        CircleAnnotation circleAnnotation = new CircleAnnotation(page, transformedRect);
        circleAnnotation.setBorderStyleWidth(borderWidth);
        return circleAnnotation;
    }


    SquareAnnotation getSquareAnnotation(Page page, Rect transformedRect, Double borderWidth) {
        SquareAnnotation squareAnnotation = new SquareAnnotation(page, transformedRect);
        squareAnnotation.setBorderStyleWidth(borderWidth);
        return squareAnnotation;
    }

    /**
     * Translates and applies a callout markup to a PDF page.
     *
     * @param doc               The PDF document.
     * @param page              The page to which the markup will be applied.
     * @param markup            The markup data in JSONObject format.
     * @param createdByUserName The name of the user who created the markup.
     */

    void translateCallout(Document doc, Page page, JSONObject markup, String createdByUserName) {
        JSONObject state = markup.getJSONObject(LMV_MARKUP_STATE);
        Double rotation = state.getDouble(LMV_MARKUP_STATE_ROTATION);
        String text = state.getString(LMV_MARKUP_STATE_TEXT);
        JSONObject style = state.getJSONObject(LMV_MARKUP_STATE_STYLE);
        Double fontSize = style.getDouble(LMV_MARKUP_STATE_STYLE_FONT_SIZE);
        String fontColor = style.getString(LMV_MARKUP_STATE_STYLE_FONT_COLOR);
        Double strokeWidth = style.getDouble(LMV_MARKUP_STATE_STYLE_STROKE_WIDTH);

        paddingText = new PaddingText(page, fontSize, translateBorderStyleWidth(strokeWidth), sourceLmvRect);
        double fontSizeInPoints = paddingText.appearanceFontSize;
        double borderWidth = paddingText.borderWidth;

        JSONObject frameState = markup.getJSONObject(LMV_MARKUP_FRAMESTATE);
        JSONArray frameAnchors = frameState.getJSONArray(LMV_MARKUP_STATE_ANCHORS);
        Rect frame = translateTextRect(frameAnchors, borderWidth);

        Double[] fontColorAsArray = hexToRgb(fontColor);
        String fontColorString = fontColorAsArray[0] + " " + fontColorAsArray[1] + " " + fontColorAsArray[2] + " rg";
        String defaultAppearance = fontColorString + " rg /Arial " + fontSizeInPoints + " Tf";

        FreeTextAnnotation annot = getFreeTextAnnotation(page, frame, defaultAppearance);
        annot.setTitle(createdByUserName);
        annot.setContents(text);
        annot.setTextColor(translateColor(fontColor));
        annot.setFontSize(fontSizeInPoints);
        try {
            annot.setFontFace("Artifakt Element");
        } catch (Exception e) {
            annot.setFontFace("Arial");
        }
        annot.setBorderStyleWidth(borderWidth * 2);

        setNormalAppearance(annot, markup, LMV_MARKUP_TYPE_CALLOUT);
        rotateAnnotationRect(annot, doc, -Math.toDegrees(rotation));
    }

    /**
     * Translates and applies a polyline markup (such as a line, arrow, or highlight) to a PDF page.
     *
     * @param page              The page to which the markup will be applied.
     * @param markup            The markup data in JSONObject format.
     * @param createdByUserName The name of the user who created the markup.
     */

    void translatePolyline(Page page, JSONObject markup, String createdByUserName) {
        String type = markup.getString(LMV_MARKUP_TYPE);
        JSONObject state = markup.getJSONObject(LMV_MARKUP_STATE);
        JSONObject translation = state.getJSONObject(LMV_MARKUP_STATE_TRANSLATION);
        Double rotation = state.getDouble(LMV_MARKUP_STATE_ROTATION);
        JSONObject scale = state.getJSONObject(LMV_MARKUP_STATE_SCALE);
        JSONArray anchors = state.getJSONArray(LMV_MARKUP_STATE_ANCHORS);
        JSONObject style = state.getJSONObject(LMV_MARKUP_STATE_STYLE);
        String strokeColor = style.getString(LMV_MARKUP_STATE_STYLE_STROKE_COLOR);
        Double strokeOpacity = style.getDouble(LMV_MARKUP_STATE_STYLE_STROKE_OPACITY);
        Double strokeWidth = style.getDouble(LMV_MARKUP_STATE_STYLE_STROKE_WIDTH);

        Rect rect = new Rect(0, 0, destDocumentRect.width, destDocumentRect.height);
        ArrayList<Point> vertices = translateVertices(anchors);
        if ((type.equals(LMV_MARKUP_TYPE_POLYLINE) || type.equals(LMV_MARKUP_TYPE_POLYCLOUD)) && state.getBoolean("closed")) {
            vertices.add(vertices.get(0));
        }

        PolyLineAnnotation annot = getPolyLineAnnotation(page, rect, vertices);
        annot.setTitle(createdByUserName);
        annot.setColor(translateColor(strokeColor));
        annot.setOpacity(strokeOpacity);
        annot.setBorderStyleWidth(translateBorderStyleWidth(strokeWidth));
        if (type.equals(LMV_MARKUP_TYPE_ARROW)) {
            annot.setEndPointEndingStyle(LineEndingStyle.OPEN_ARROW);
        }

        transformLineAppearance(annot, translateTranslation(translation), translateScale(scale), -Math.toDegrees(rotation));
        setNormalAppearance(annot, markup, LMV_MARKUP_TYPE_LINE);
    }

    /**
     * Sets the normal appearance for a markup.
     *
     * @param annot  The annotation for which the appearance will be set.
     * @param markup The markup data in JSONObject format.
     * @param type   The type of markup.
     */

    void setNormalAppearance(Annotation annot, JSONObject markup, String type) {
        Form normApForm = annot.generateAppearance();

        if (annot instanceof FreeTextAnnotation) {
            normApForm = generateAppearance((FreeTextAnnotation) annot);
        }

        Deque<ContentStackItem> contentStack = new ArrayDeque<>();
        int currentItem = 0;
        Content currentContent = normApForm.getContent();

        do {
            currentItem = processContentStackItem(currentContent, contentStack, currentItem);
            if (currentItem < currentContent.getNumElements()) {
                Element elem = currentContent.getElement(currentItem);
                if (elem instanceof Form || elem instanceof Container) {
                    ContentStackItem si = new ContentStackItem(currentContent, currentItem);
                    contentStack.push(si);
                    currentItem = 0;
                    currentContent = (elem instanceof Form) ? ((Form) elem).getContent() : ((Container) elem).getContent();
                } else {
                    processNormalAppearance(annot, elem, currentItem, currentContent, markup, type);
                    ++currentItem;
                }
            }
        } while (currentItem < currentContent.getNumElements() || !contentStack.isEmpty());

        Form newFrm = getForm(currentContent);
        annot.setNormalAppearance(newFrm);
        currentContent.delete();
    }

    private void processNormalAppearance(Annotation annot, Element elem, int currentItem, Content currentContent, JSONObject markup, String type) {
        switch (type) {
            case LMV_MARKUP_TYPE_LINE:
                lineNormalAppearance(annot, elem, currentItem, currentContent, markup);
                break;
            case LMV_MARKUP_TYPE_CALLOUT:
                calloutNormalAppearance(annot, elem, currentItem, currentContent, markup);
                break;
            case LMV_MARKUP_TYPE_ELLIPSE:
            case LMV_MARKUP_TYPE_RECTANGLE:
            case LMV_MARKUP_TYPE_CLOUD:
                squareNormalAppearance(annot, elem, currentItem, currentContent);
                break;
            default:
                break;
        }
    }

    private int processContentStackItem(Content currentContent, Deque<ContentStackItem> contentStack, int currentItem) {
        if (currentItem == currentContent.getNumElements() && !contentStack.isEmpty()) {
            ContentStackItem si = contentStack.pop();
            Content modContent = currentContent;
            currentContent = si.content;
            Element olditem = currentContent.getElement(si.prevQueueNum);
            if (olditem instanceof Form) {
                Form moditem = (Form) olditem;
                moditem.setContent(modContent);
                currentContent.addElement(moditem, si.prevQueueNum);
                currentContent.removeElement(si.prevQueueNum);
            } else if (olditem instanceof Container) {
                Container moditem = (Container) olditem;
                moditem.setContent(modContent);
                currentContent.addElement(moditem, si.prevQueueNum);
                currentContent.removeElement(si.prevQueueNum);
            }

            currentItem = si.prevQueueNum + 1;
        }
        return currentItem;
    }


    void calloutNormalAppearance(Annotation annot, Element elem, int currentItem, Content currentContent, JSONObject markup) {
        JSONObject state = markup.getJSONObject(LMV_MARKUP_STATE);
        JSONObject style = state.getJSONObject(LMV_MARKUP_STATE_STYLE);
        String strokeColor = style.getString(LMV_MARKUP_STATE_STYLE_STROKE_COLOR);
        Double fillOpacity = style.getDouble(LMV_MARKUP_STATE_STYLE_FILL_OPACITY);
        String fillColor = style.getString(LMV_MARKUP_STATE_STYLE_FILL_COLOR);
        Double strokeOpacity = style.getDouble(LMV_MARKUP_STATE_STYLE_STROKE_OPACITY);

        if (elem instanceof Path) {
            Path path = (Path) elem;
            ExtendedGraphicState egs = getExtendedGraphicState();
            if (fillColor.equals("-1")) {
                egs.setOpacityForOtherThanStroking(0);
            } else {
                egs.setOpacityForOtherThanStroking(fillOpacity);
            }
            egs.setOpacityForStroking(strokeOpacity);

            GraphicState gs = getGraphicState();
            gs.setWidth(((FreeTextAnnotation) annot).getBorderStyleWidth());

            gs.setStrokeColor(translateColor(strokeColor));
            if (!fillColor.equals("-1")) gs.setFillColor(translateColor(fillColor));
            else gs.setFillColor(new Color(1, 1, 1));
            gs.setExtendedGraphicState(egs);
            path.setGraphicState(gs);

            currentContent.addElement(path, currentItem);
            currentContent.removeElement(currentItem);
        } else if (elem instanceof Text) {
            Text text = (Text) elem;
            rectifyRotationOfPage(curPage, text, (FreeTextAnnotation) annot);
        }
    }

    private void rectifyRotationOfPage(Page page, Text text, FreeTextAnnotation annot) {
        PageRotation originPageRotation = page.getRotation();

        double w = annot.getRect().getWidth();
        double h = annot.getRect().getHeight();
        w = w - 2 * annot.getBorderStyleWidth() - 2 * paddingText.measuredWidth;

        if (originPageRotation == PageRotation.ROTATE_90) {
            text.rotate(90);
            text.translate(h, 0);
        } else if (originPageRotation == PageRotation.ROTATE_180) {
            text.rotate(180);
            text.translate(h, w);
        } else if (originPageRotation == PageRotation.ROTATE_270) {
            text.rotate(270);
            text.translate(0, w);
        }
    }


    void lineNormalAppearance(Annotation annot, Element elem, int currentItem, Content currentContent, JSONObject markup) {
        String type = markup.getString(LMV_MARKUP_TYPE);
        boolean isRoundCap = type.equals(LMV_MARKUP_TYPE_FREEHAND) || type.equals(LMV_MARKUP_TYPE_HIGHLIGHT);
        boolean isRoundJoin = type.equals(LMV_MARKUP_TYPE_POLYLINE) || type.equals(LMV_MARKUP_TYPE_FREEHAND) || type.equals(LMV_MARKUP_TYPE_HIGHLIGHT);

        if (elem instanceof Path) {
            Path path = (Path) elem;
            ExtendedGraphicState egs = getExtendedGraphicState();
            int l = currentContent.getNumElements();
            if (annot.getOpacity() != 1) egs.setOpacityForStroking(annot.getOpacity() / l);

            GraphicState gs = getGraphicState();
            gs.setWidth(path.getGraphicState().getWidth());
            gs.setStrokeColor(path.getGraphicState().getStrokeColor());
            gs.setFillColor(path.getGraphicState().getFillColor());
            gs.setExtendedGraphicState(egs);
            if (isRoundCap) gs.setLineCap(LineCap.ROUND_CAP);
            if (isRoundJoin) gs.setLineJoin(LineJoin.ROUND_JOIN);
            gs.setLineFlatness(path.getGraphicState().getLineFlatness());
            gs.setMiterLimit(path.getGraphicState().getMiterLimit());

            path.setGraphicState(gs);
            currentContent.addElement(path, currentItem);
            currentContent.removeElement(currentItem);
        }
    }


    void squareNormalAppearance(Annotation annot, Element elem, int currentItem, Content currentContent) {
        if (elem instanceof Path) {
            Path path = (Path) elem;
            if (path.getGraphicState().getExtendedGraphicState() != null) {
                path.getGraphicState().getExtendedGraphicState().setOpacityForOtherThanStroking(annot.getOpacity());
            } else {
                ExtendedGraphicState egs = getExtendedGraphicState();
                egs.setOpacityForOtherThanStroking(annot.getOpacity());
                GraphicState gs = getGraphicState();
                gs.setWidth(path.getGraphicState().getWidth());
                gs.setStrokeColor(path.getGraphicState().getStrokeColor());
                gs.setFillColor(path.getGraphicState().getFillColor());
                gs.setExtendedGraphicState(egs);
                gs.setLineCap(path.getGraphicState().getLineCap());
                gs.setLineJoin(path.getGraphicState().getLineJoin());
                gs.setLineFlatness(path.getGraphicState().getLineFlatness());
                gs.setMiterLimit(path.getGraphicState().getMiterLimit());
                path.setGraphicState(gs);
            }
            currentContent.addElement(path, currentItem);
            currentContent.removeElement(currentItem);

        }
    }

    /**
     * Transforms an annotation rectangle.
     *
     * @param source      The original rectangle.
     * @param borderWidth The border width of the rectangle.
     * @param translation The translation to be applied.
     * @param scale       The scale to be applied.
     * @return The transformed rectangle.
     */

    Rect transformAnnotationRect(Rect source, Double borderWidth, Point translation, Point scale) {
        Matrix transformMatrix = new Matrix()
                .translate(translation.getH(), translation.getV())
                .translate((source.getRight() + source.getLeft()) / 2, (source.getTop() + source.getBottom()) / 2)
                .rotate(0.0)
                .scale(scale.getH(), scale.getV())
                .translate(-(source.getRight() + source.getLeft()) / 2, -(source.getTop() + source.getBottom()) / 2);

        Point bl = (new Point(source.getLLx(), source.getLLy())).transform(transformMatrix);
        Point tl = (new Point(source.getLLx(), source.getURy())).transform(transformMatrix);
        Point br = (new Point(source.getURx(), source.getLLy())).transform(transformMatrix);
        Point tr = (new Point(source.getURx(), source.getURy())).transform(transformMatrix);

        Double minX = Math.min(Math.min(bl.getH(), br.getH()), Math.min(tl.getH(), tr.getH()));
        Double minY = Math.min(Math.min(bl.getV(), br.getV()), Math.min(tl.getV(), tr.getV()));
        Double maxX = Math.max(Math.max(bl.getH(), br.getH()), Math.max(tl.getH(), tr.getH()));
        Double maxY = Math.max(Math.max(bl.getV(), br.getV()), Math.max(tl.getV(), tr.getV()));

        Double nLLx = minX - borderWidth / 2;
        Double nLLy = minY - borderWidth / 2;
        Double nURx = maxX + borderWidth / 2;
        Double nURy = maxY + borderWidth / 2;
        return new Rect(nLLx, nLLy, nURx, nURy);
    }

    /**
     * Rotates an annotation rectangle.
     *
     * @param annot  The annotation to be rotated.
     * @param doc    The PDF document.
     * @param degree The degree of rotation to be applied.
     */

    void rotateAnnotationRect(Annotation annot, Document doc, Double degree) {
        Matrix rotateMatrix = new Matrix().rotate(degree);
        Rect oldRect = annot.getRect();

        Matrix transformMatrix = new Matrix()
                .translate((oldRect.getRight() + oldRect.getLeft()) / 2, (oldRect.getTop() + oldRect.getBottom()) / 2)
                .rotate(degree)
                .translate(-(oldRect.getRight() + oldRect.getLeft()) / 2, -(oldRect.getTop() + oldRect.getBottom()) / 2);

        Point bl = (new Point(annot.getRect().getLLx(), annot.getRect().getLLy())).transform(transformMatrix);
        Point tl = (new Point(annot.getRect().getLLx(), annot.getRect().getURy())).transform(transformMatrix);
        Point br = (new Point(annot.getRect().getURx(), annot.getRect().getLLy())).transform(transformMatrix);
        Point tr = (new Point(annot.getRect().getURx(), annot.getRect().getURy())).transform(transformMatrix);

        Double minX = Math.min(Math.min(bl.getH(), br.getH()), Math.min(tl.getH(), tr.getH()));
        Double minY = Math.min(Math.min(bl.getV(), br.getV()), Math.min(tl.getV(), tr.getV()));
        Double maxX = Math.max(Math.max(bl.getH(), br.getH()), Math.max(tl.getH(), tr.getH()));
        Double maxY = Math.max(Math.max(bl.getV(), br.getV()), Math.max(tl.getV(), tr.getV()));
        annot.setRect(new Rect(minX, minY, maxX, maxY));

        PDFArray pdfArray = getPDFArray(doc, false, rotateMatrix);
        annot.getNormalAppearance().getStream().getDict().put("Matrix", pdfArray);
    }


    PDFArray getPDFArray(Document doc, boolean indirect, Matrix rotateMatrix) {
        PDFArray pdfArray = new PDFArray(doc, indirect);
        pdfArray.put(0, new PDFReal(rotateMatrix.getA(), doc, false));
        pdfArray.put(1, new PDFReal(rotateMatrix.getB(), doc, false));
        pdfArray.put(2, new PDFReal(rotateMatrix.getC(), doc, false));
        pdfArray.put(3, new PDFReal(rotateMatrix.getD(), doc, false));
        pdfArray.put(4, new PDFReal(rotateMatrix.getH(), doc, false));
        pdfArray.put(5, new PDFReal(rotateMatrix.getV(), doc, false));
        return pdfArray;
    }


    Point translateTranslation(JSONObject translation) {
        Double x = translation.getDouble("x") * (destDocumentRect.width / sourceLmvRect.width);
        Double y = translation.getDouble("y") * (destDocumentRect.height / sourceLmvRect.height);
        return rectifyRotationOfPoint(x, y);
    }


    Point translateScale(JSONObject scale) {
        Double x = scale.getDouble("x");
        Double y = scale.getDouble("y");
        boolean noRectify = x == 1 && y == 1;
        return noRectify ? new Point(x, y) : rectifyRotationOfPoint(x, y);
    }

    private Point rectifyRotationOfPoint(double x, double y) {
        if (curPage.getRotation() == PageRotation.ROTATE_90) {
            return new Point(-y, x);
        } else if (curPage.getRotation() == PageRotation.ROTATE_180) {
            return new Point(-x, -y);
        } else if (curPage.getRotation() == PageRotation.ROTATE_270) {
            return new Point(y, -x);
        }
        return new Point(x, y);
    }


    Double translateBorderStyleWidth(Double strokeWidth) {
        return strokeWidth * destDocumentRect.width / sourceLmvRect.width;
    }

    private Double[] hexToRgb(String hexColor) {
        hexColor = hexColor.replace("#", "");
        Double r = Integer.parseInt(hexColor.substring(0, 2), 16) / 255.0;
        Double g = Integer.parseInt(hexColor.substring(2, 4), 16) / 255.0;
        Double b = Integer.parseInt(hexColor.substring(4, 6), 16) / 255.0;
        return new Double[]{r, g, b};
    }


    Color translateColor(String hexColor) {
        Double[] rgb = hexToRgb(hexColor);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }


    ArrayList<Point> translateVertices(JSONArray anchors) {
        ArrayList<Point> points = new ArrayList<>();
        for (int i = 0; i < anchors.length(); i++) {
            JSONObject anchor = anchors.getJSONObject(i);
            Double x = translateX(anchor.getDouble("x"));
            Double y = translateY(anchor.getDouble("y"));
            Point newPoint = rectifyRotationOfPage(curPage, new Point(x, y));
            points.add(newPoint);
        }
        return points;
    }

    private Matrix getRotationMatrix(Page page) {
        PageRotation originPageRotation = page.getRotation();

        switch (originPageRotation) {
            case ROTATE_90:
                Matrix rotateMatrix90 = new Matrix();
                return rotateMatrix90.rotate(90).translate(0, page.getCropBox().getWidth());
            case ROTATE_180:
                Matrix rotateMatrix180 = new Matrix();
                return rotateMatrix180.rotate(180).translate(-page.getCropBox().getHeight(), page.getCropBox().getWidth());
            case ROTATE_270:
                Matrix rotateMatrix270 = new Matrix();
                return rotateMatrix270.rotate(270).translate(-page.getCropBox().getHeight(), 0);
            default:
                return new Matrix();
        }
    }

    private Point rectifyRotationOfPage(Page page, Point point) {
        Matrix rotateMatrix = getRotationMatrix(page);
        return point.transform(rotateMatrix);
    }

    Rect rectifyRotationOfPage(Page page, Rect rect) {
        Matrix rotateMatrix = getRotationMatrix(page);
        return rect.transform(rotateMatrix);
    }

    Double translateX(Double x) {
        return x * (destDocumentRect.width / sourceLmvRect.width);
    }

    private Double translateY(Double y) {
        return y * (destDocumentRect.height / sourceLmvRect.height);
    }


    Rect translateRect(JSONArray anchors) {
        Double nURx = translateX(anchors.getJSONObject(1).getDouble("x"));
        Double nURy = translateY(anchors.getJSONObject(1).getDouble("y"));
        Double nLLx = translateX(anchors.getJSONObject(3).getDouble("x"));
        Double nLLy = translateY(anchors.getJSONObject(3).getDouble("y"));
        return rectifyRotationOfPage(curPage, new Rect(nLLx, nLLy, nURx, nURy));
    }


    Rect translateTextRect(JSONArray anchors, Double borderWidth) {
        Double nURx = translateX(anchors.getJSONObject(1).getDouble("x")) + borderWidth / 2;
        Double nURy = translateY(anchors.getJSONObject(1).getDouble("y")) - borderWidth / 2;
        Double nLLx = translateX(anchors.getJSONObject(3).getDouble("x")) - borderWidth / 2;
        Double nLLy = translateY(anchors.getJSONObject(3).getDouble("y")) + borderWidth / 2;
        return rectifyRotationOfPage(curPage, new Rect(nLLx, nLLy, nURx, nURy));
    }


    void transformLineAppearance(Annotation annot, Point translation, Point scale, Double rotation) {
        annot.setNormalAppearance(annot.generateAppearance());
        Rect oldRect = annot.getRect();

        Matrix transformMatrix = new Matrix()
                .translate(translation.getH(), translation.getV())
                .translate((oldRect.getRight() + oldRect.getLeft()) / 2, (oldRect.getTop() + oldRect.getBottom()) / 2)
                .rotate(rotation)
                .scale(scale.getH(), scale.getV())
                .translate(-(oldRect.getRight() + oldRect.getLeft()) / 2, -(oldRect.getTop() + oldRect.getBottom()) / 2);

        List<Point> annotPoints = ((PolyLineAnnotation) annot).getVertices();
        ArrayList<Point> transPoints = new ArrayList<>();
        for (Point point : annotPoints) {
            transPoints.add(point.transform(transformMatrix));
        }
        ((PolyLineAnnotation) annot).setVertices(transPoints);
    }


    double getAdvance(TextRun tr) {
        Text dummy = new Text();
        dummy.addRun(tr);
        Point adv = dummy.getAdvanceForTextRun(0);
        dummy.delete();
        double value = adv.getH();
        adv.delete();
        return value;
    }


    int findBreakPoint(String str, double availWidth, Font f, GraphicState gs, TextState ts) {
        String baseCase = str.substring(0, 0);
        Matrix dummyMatrix = new Matrix();
        TextRun dummy = getTextRun(baseCase, f, gs, ts, dummyMatrix);
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
            TextRun dummy1 = getTextRun(str1, f, gs, ts, dummyMatrix);
            TextRun dummy2 = getTextRun(str2, f, gs, ts, dummyMatrix);
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


    double calcQuadShift(String str, double totalWidth, int align, Font f, GraphicState gs, TextState ts) {
        Matrix dummyMatrix = new Matrix();
        TextRun dummy = getTextRun(str, f, gs, ts, dummyMatrix);
        double adv = getAdvance(dummy);
        dummyMatrix.delete();
        dummy.delete();
        return (totalWidth - adv) / align;
    }

    private void applyStrokeAndFillColorIfNeeded(FreeTextAnnotation freeTextAnnot, PDFDict pdfDict, EnumSet<PathPaintOpFlags> paintOpFlags, GraphicState gs) {
        if (pdfDict.contains("BS")) {
            paintOpFlags.add(PathPaintOpFlags.STROKE);
            gs.setStrokeColor(freeTextAnnot.getTextColor());
            gs.setDashPattern(freeTextAnnot.getBorderStyleDashPattern());
            gs.setWidth(freeTextAnnot.getBorderStyleWidth());
        }
        if (pdfDict.contains("C")) {
            paintOpFlags.add(PathPaintOpFlags.FILL);
            gs.setFillColor(freeTextAnnot.getInteriorColor());
        }
    }

    private String generateCurrentSegment(String currentSegment, StringBuilder currentLine, int split) {
        if (split != -1) {
            String firstHalf = currentSegment.substring(0, split);
            if (currentLine.length() > 0) {
                currentLine.append(' ');
            }
            currentLine.append(firstHalf);
            return currentSegment.substring(split);
        }
        return currentSegment;
    }

    private String handleCurrentLine(StringBuilder currentLine, String currentSegment, String[] textSegments, int index) {
        if (currentLine.length() > 0) {
            currentLine.append(' ');
        }
        currentLine.append(currentSegment);
        return (index >= textSegments.length) ? null : textSegments[index];
    }


    Form generateAppearance(FreeTextAnnotation freeTextAnnot) {
        Font dLFont = getFont();
        double padding = paddingText.padding;
        boolean isRectRotated = curPage.getRotation() == PageRotation.ROTATE_90 || curPage.getRotation() == PageRotation.ROTATE_270;

        String annotationText = freeTextAnnot.getContents();

        Rect rect = freeTextAnnot.getRect();
        double rectWidth = rect.getWidth();
        double rectLeft = rect.getLeft();
        double rectTop = rect.getTop();
        rectWidth = calcRectWidth(rectWidth, isRectRotated, rect);

        EnumSet<PathPaintOpFlags> paintOpFlags = EnumSet.of(PathPaintOpFlags.NEITHER);
        GraphicState gs = getGraphicState();

        GraphicState gsTextRun = getGraphicState();
        gsTextRun.setFillColor(freeTextAnnot.getTextColor());

        PDFDict pdfDict = freeTextAnnot.getPDFDict();

        applyStrokeAndFillColorIfNeeded(freeTextAnnot, pdfDict, paintOpFlags, gs);

        double fontSize = freeTextAnnot.getFontSize();
        TextState textState = getTextState();
        textState.setFontSize(fontSize);

        int align = calculateAlign(freeTextAnnot);
        Text text = getText();
        int i = 0;
        if (annotationText != null) {
            String annotationTextWithFlags = annotationText.replace("\n", " " + NEW_LINE_FLAG + " ");
            String[] textSegments = annotationTextWithFlags.split("\\s+");
            double totalWidth = rectWidth - 2 * padding + fontSize / 2;
            totalWidth = calculateTotalWidth(isRectRotated, totalWidth, fontSize, rectWidth);
            double remainingWidth = totalWidth;
            Matrix matrix = new Matrix();
            TextRun space = getTextRun(" ", dLFont, gsTextRun, textState, matrix);
            double spaceAdv = space.getAdvance();
            matrix.delete();

            double horizPos = rectLeft + paddingText.textToRectLeft;
            double vertPos = rectTop - paddingText.textToRectTop;

            String currentSegment = textSegments[0];
            StringBuilder currentLine = new StringBuilder();
            matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);
            while (vertPos > rect.getBottom() && currentSegment != null && !currentSegment.isEmpty()) {
                TextRun dummy = getTextRun(currentSegment, dLFont, gsTextRun, textState, matrix);
                double dummyAdvance = getAdvance(dummy);
                boolean shouldHandleText = false;

                if (NEW_LINE_FLAG.equals(currentSegment)) {
                    String replacedString = currentLine.toString().replace(NEW_LINE_FLAG, "");
                    currentLine.setLength(0);
                    currentLine.append(replacedString);
                    currentSegment = handleCurrentLine(currentLine, "", textSegments, ++i);
                    shouldHandleText = true;
                } else if (dummyAdvance > totalWidth) {
                    int split = findBreakPoint(currentSegment, remainingWidth - spaceAdv, dLFont, gsTextRun, textState);
                    currentSegment = generateCurrentSegment(currentSegment, currentLine, split);
                    shouldHandleText = true;
                } else if ((dummyAdvance + spaceAdv) > remainingWidth) {
                    shouldHandleText = true;
                } else {
                    currentSegment = handleCurrentLine(currentLine, currentSegment, textSegments, ++i);
                    remainingWidth -= spaceAdv + dummyAdvance;
                }

                if (shouldHandleText) {
                    TextAttributes textAttributes = new TextAttributes(dLFont, gsTextRun, textState, text);
                    handleText(matrix, currentLine, totalWidth, align, horizPos, vertPos, textAttributes);
                    currentLine = new StringBuilder();
                    vertPos -= fontSize;
                    horizPos = calculateHorizontalPosition(isRectRotated, horizPos, rect, padding);
                    matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);
                    remainingWidth = totalWidth;
                }
            }

            TextAttributes textAttributes = new TextAttributes(dLFont, gsTextRun, textState, text);
            processTextAttributes(textAttributes, currentLine, matrix, totalWidth, align, horizPos, vertPos);
        }
        Content content = getContent();

        Path path = getPath(gs);
        path.setPaintOp(paintOpFlags);
        path.addRect(new Point(rect.getLeft(), rect.getBottom()), rect.getWidth(), rect.getHeight());

        content.addElement(path);
        content.addElement(text);
        Form form = getForm(content);

        content.delete();
        path.delete();
        text.delete();
        gs.delete();

        return form;
    }

    private void processTextAttributes(TextAttributes textAttributes, StringBuilder currentLine, Matrix matrix, double totalWidth, int align, double horizPos, double vertPos) {
        if (currentLine.length() > 0) {
            handleText(matrix, currentLine, totalWidth, align, horizPos, vertPos, textAttributes);
        }
    }

    private double calcRectWidth(double rectWidth, boolean isRectRotated, Rect rect) {
        if (isRectRotated) {
            rectWidth = rect.getHeight();
        }
        return rectWidth;
    }

    private double calculateHorizontalPosition(boolean isRectRotated, double horizPos, Rect rect, double padding) {
        if (!isRectRotated) {
            horizPos = rect.getLeft() + padding;
        }
        return horizPos;
    }

    private double calculateTotalWidth(boolean isRectRotated, double totalWidth, double fontSize, double rectWidth) {
        if (isRectRotated) {
            totalWidth = rectWidth - (2 * (paddingText.borderWidth + paddingText.measuredWidth)) + fontSize * 4 / 9;
        }
        return totalWidth;
    }

    private int calculateAlign(FreeTextAnnotation freeTextAnnot) {
        int align = 0;
        HorizontalAlignment q = freeTextAnnot.getQuadding();
        if (q == HorizontalAlignment.CENTER) {
            align = 2;
        } else if (q == HorizontalAlignment.RIGHT) {
            align = 1;
        }
        return align;
    }

    private void handleText(Matrix matrix, StringBuilder currentLine, double totalWidth, int align, double horizPos, double vertPos, TextAttributes textAttributes) {
        if (align != 0) {
            matrix.delete();
            double shift = calcQuadShift(currentLine.toString(), totalWidth, align, textAttributes.font, textAttributes.graphicState, textAttributes.textState);
            matrix = new Matrix(1, 0, 0, 1, horizPos + shift, vertPos);
        }
        TextRun textRun = getTextRun(currentLine.toString(), textAttributes.font, textAttributes.graphicState, textAttributes.textState, matrix);
        textAttributes.text.addRun(textRun);
        textRun.delete();
        matrix.delete();
    }

    /**
     * The below methods are primarily designed for facilitating unit testing.
     */

    Document getDocument(java.nio.file.Path tempFile) {
        return new Document(tempFile.toString());
    }

    Library initPdfLibrary() {
//		Library.setLicenseKey("7524-5023-3866-2061");
        Library.setLicenseKey("7905-5023-7336-0131");
        return new Library();
    }


    Path getPath(GraphicState gs) {
        return new Path(gs);
    }


    Font getFont() {
        return new Font("Artifakt Element", EnumSet.of(FontCreateFlags.DO_NOT_EMBED));
    }


    GraphicState getGraphicState() {
        return new GraphicState();
    }

    ExtendedGraphicState getExtendedGraphicState() {
        return new ExtendedGraphicState();
    }

    TextState getTextState() {
        return new TextState();
    }

    Text getText() {
        return new Text();
    }

    Content getContent() {
        return new Content();
    }

    FreeTextAnnotation getFreeTextAnnotation(Page page, Rect frame, String defaultAppearance) {
        return new FreeTextAnnotation(page, frame, defaultAppearance);
    }

    PolyLineAnnotation getPolyLineAnnotation(Page page, Rect rect, ArrayList<Point> vertices) {
        return new PolyLineAnnotation(page, rect, vertices);
    }

    Form getForm(Content currentContent) {
        return new Form(currentContent);
    }

    TextRun getTextRun(String baseCase, Font f, GraphicState gs, TextState ts, Matrix dummyMatrix) {
        return new TextRun(baseCase, f, gs, ts, dummyMatrix);
    }

}

// 875.0948486328125 + 28.346456690913385*x + 5.41666666666667*y = 958.8586281603715
// 545.8817138671875 + 28.346456690913385*x + 54.1666666666667*y = 625.2517926273049
// 749.0017700195312 + 28.346456690913385*x + 28.346456692913385*y = 831.20649442898