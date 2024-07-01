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

        PaddingText(Page page, double fontSize, double borderWidth) {
            this.borderWidth = borderWidth;
            if (page.getRotation() == PageRotation.ROTATE_270) {
                this.originFontsize = fontSize;
                this.appearanceFontSize = fontSize * 3 / 4;

                double textWidth = fontSize * 5 / 18;
                this.padding = textWidth / 2 + this.borderWidth / 2;
                RestApiDatalogicsUtil util = new RestApiDatalogicsUtil();
                Font dLFont1 = util.getFont();
                double measuredWidth = dLFont1.measureTextWidth("A", appearanceFontSize);
                this.measuredWidth = measuredWidth;
                this.textToRectTop = this.borderWidth + measuredWidth;
                this.textToRectLeft = this.borderWidth * 2 + this.padding + this.appearanceFontSize * 5 / 6;
            } else {
                this.appearanceFontSize = fontSize * 5 / 18;
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

        String inputFileNamePDF = "./src/main/resources/test.pdf";
        String outputFileNamePDF = "./src/main/resources/test2.pdf";
        String inputFileName = "./src/main/resources/dwg-b.pdf";
        String outputFileName = "./src/main/resources/dwg-a.pdf";
        String inputFileRotateName = "./src/main/resources/dwg-b.pdf";
        String outputFileRotateName = "./src/main/resources/dwg-a.pdf";

        String dwgRect = "{\"id\":\"25e65016-18b3-40c9-8422-ba47e55386a8\",\"title\":\"rect\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"rectangle\",\"state\":{\"anchors\":[{\"x\":0.5312077352518472,\"y\":9.497643121914173,\"z\":0},{\"x\":13.984413579814586,\"y\":9.497643121914173,\"z\":0},{\"x\":13.984413579814586,\"y\":1.0114315286668067,\"z\":0},{\"x\":0.5312077352518472,\"y\":1.011431528672576,\"z\":-0.000006997537756060298}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String line = "{\"id\":\"e58df102-e6fc-4e7c-a397-7f26a2d437dc\",\"title\":\"line\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"line\",\"state\":{\"anchors\":[{\"x\":0.5008333325386047,\"y\":0.9210034061508581,\"z\":0},{\"x\":33.500831604003906,\"y\":20.921003229779764,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.22440944881889763,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0.07816326131054119,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String pdfLine = "{\"id\":\"3947d470-f042-4cef-9f43-c9edd5fc6b27\",\"title\":\"line\",\"is2d\":true,\"sheetGUID\":\"5ae6cdf7aec11c44a38bb8dacfe377d6/1\",\"sheetName\":\"Page 1\",\"markups\":[{\"type\":\"line\",\"state\":{\"anchors\":[{\"x\":0.04932146867116316,\"y\":0.10179652397536287,\"z\":0},{\"x\":10.922031100591024,\"y\":8.410493787280737,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.2637795275590551,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String dwgText = "{\"id\":\"ece89432-cdd0-4ffe-83ba-a6a7cafeccea\",\"title\":\"text\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":11.164084631456529,\"y\":14.640270253684394,\"z\":-1.6231837338257393e-7},{\"x\":13.40599189304883,\"y\":14.640270253684394,\"z\":-1.6231837338257393e-7},{\"x\":13.40599189304883,\"y\":15.033971041085834,\"z\":1.6231837338257393e-7},{\"x\":11.164084631456529,\"y\":15.033971041085834,\"z\":1.6231837338257393e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":70,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.15748031496062992,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.1859142607174103,\"y\":-0.7545931758530182,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":10.88849408268725,\"y\":12.855493333119169,\"z\":0},{\"x\":14.053410968076577,\"y\":12.855493333119169,\"z\":0},{\"x\":14.053410968076577,\"y\":15.309561574588985,\"z\":0},{\"x\":10.88849408268725,\"y\":15.309561574588985,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.15748031496062992,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String pdfText = "{\"id\":\"\",\"title\":\"\",\"is2d\":true,\"sheetGUID\":\"5ae6cdf7aec11c44a38bb8dacfe377d6/1\",\"sheetName\":\"Page 1\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":3.7615586349978822,\"y\":4.261261484208469,\"z\":-6.950719387107567e-8},{\"x\":4.553168342987843,\"y\":4.261261484208469,\"z\":-6.950719387107567e-8},{\"x\":4.553168342987843,\"y\":4.400275871952978,\"z\":6.950719387107567e-8},{\"x\":3.7615586349978822,\"y\":4.400275871952978,\"z\":6.950719387107567e-8}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":76,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.11811023622047244,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.07723021541365468,\"y\":-0.4479352493991972,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":3.632996322674334,\"y\":3.2368286694449826,\"z\":0},{\"x\":4.836191085456655,\"y\":3.2368286694449826,\"z\":0},{\"x\":4.836191085456655,\"y\":4.528838179953007,\"z\":0},{\"x\":3.632996322674334,\"y\":4.528838179953007,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.11811023622047244,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String dwgNormalText = "{\"id\":\"579c8806-7c58-4fb2-8448-fcc417ebe87a\",\"title\":\"normal text\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":11.724652034649175,\"y\":11.50863472370645,\"z\":-1.9685039369641552e-7},{\"x\":13.966559296241476,\"y\":11.50863472370645,\"z\":-1.9685039369641552e-7},{\"x\":13.966559296241476,\"y\":11.902335511107829,\"z\":1.9685039369641552e-7},{\"x\":11.724652034649175,\"y\":11.902335511107829,\"z\":1.9685039369641552e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":11.50811660157831,\"y\":11.292099290635488,\"z\":0},{\"x\":14.18309472931234,\"y\":11.292099290635488,\"z\":0},{\"x\":14.18309472931234,\"y\":12.118870944178791,\"z\":0},{\"x\":11.50811660157831,\"y\":12.118870944178791,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String dwgLine = "{\"id\": \"25e65016-18b3-40c9-8422-ba47e55386a8\", \"title\": \"rect\", \"is2d\": true, \"sheetGUID\": \"3c9663fa-f808-5c8f-bb86-ca8423d7b731\", \"sheetName\": \"Sheet\", \"markups\": [{\"type\": \"line\", \"state\": {\"anchors\": [{\"x\": -0.010943483884158667, \"y\": 0.0889592884272119, \"z\": 0}, {\"x\": 33.81540543857514, \"y\": 21.816833971798278, \"z\": 0}], \"style\": {\"stroke-color\": \"#FF0000\", \"font-size\": 36, \"font-weight\": \"normal\", \"font-family\": \"Artifakt Element, Arial\", \"font-style\": \"normal\", \"font-color\": \"#FF0000\", \"stroke-width\": 0.39370078740157477, \"fill-opacity\": 0.5, \"fill-color\": \"-1\", \"stroke-opacity\": 1}, \"translation\": {\"x\": 0, \"y\": 0, \"z\": 0}, \"rotation\": 0, \"scale\": {\"x\": 1, \"y\": 1, \"z\": 1}}, \"dataModelVersion\": 6}], \"CreatedByUserName\": \"pancras lu\", \"CreatedByUserId\": 590}";

        String dwgComplexText = "{\"id\":\"\",\"title\":\"complex text\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"WebGL Renderer: ANGLE (NVIDIA, NVIDIA T1000 (0x00001FB0) Direct3D11 vs_5_0 ps_5_0, D3D11)\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":10.400884514837244,\"y\":13.549983881008078,\"z\":-1.6231837338257393e-7},{\"x\":12.642791776429545,\"y\":13.549983881008078,\"z\":-1.6231837338257393e-7},{\"x\":12.642791776429545,\"y\":13.943684668409519,\"z\":1.6231837338257393e-7},{\"x\":10.400884514837244,\"y\":13.943684668409519,\"z\":1.6231837338257393e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":70,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":14.08184287361523,\"y\":2.71473707700819,\"z\":0},\"rotation\":0,\"scale\":{\"x\":3.2162073518462453,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":21.41884630794789,\"y\":13.585368476975766,\"z\":0},{\"x\":29.7885148282896,\"y\":13.585368476975766,\"z\":0},{\"x\":29.7885148282896,\"y\":19.33777442623211,\"z\":0},{\"x\":21.41884630794789,\"y\":19.33777442623211,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String dwgManyLineText = "{\"id\":\"85fb6aae-676f-494d-9f06-52f41434a593\",\"title\":\"many line text\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":12.70352193642178,\"y\":11.651802520187248,\"z\":-1.878838953947568e-7},{\"x\":14.94542919801408,\"y\":11.651802520187248,\"z\":-1.878838953947568e-7},{\"x\":14.94542919801408,\"y\":12.045503307588643,\"z\":1.878838953947568e-7},{\"x\":12.70352193642178,\"y\":12.045503307588643,\"z\":1.878838953947568e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":48,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":9.789398118160758,\"y\":6.395349431803262,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1.7132009386121028,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":21.234137376117314,\"y\":17.522217639548536,\"z\":0},{\"x\":25.993610145037298,\"y\":17.522217639548536,\"z\":0},{\"x\":25.993610145037298,\"y\":18.96578719335431,\"z\":0},{\"x\":21.234137376117314,\"y\":18.96578719335431,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Deprecated API usage.\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":20.024125435488415,\"y\":15.829342109290039,\"z\":-2.505118605263424e-7},{\"x\":23.009689739950357,\"y\":15.829342109290039,\"z\":-2.505118605263424e-7},{\"x\":23.009689739950357,\"y\":16.3542764924919,\"z\":2.505118605263424e-7},{\"x\":20.024125435488415,\"y\":16.3542764924919,\"z\":2.505118605263424e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":48,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":2.115723939578054,\"y\":-0.7729849675140881,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1.3009294357617265,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":21.231309620603472,\"y\":14.334572350850166,\"z\":0},{\"x\":26.033953251521087,\"y\":14.334572350850166,\"z\":0},{\"x\":26.033953251521087,\"y\":16.303076287858037,\"z\":0},{\"x\":21.231309620603472,\"y\":16.303076287858037,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":48,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Net GPU geom memory used: 1145602\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":21.064383146445905,\"y\":11.776624815169878,\"z\":-2.505118605263424e-7},{\"x\":24.049947450907847,\"y\":11.776624815169878,\"z\":-2.505118605263424e-7},{\"x\":24.049947450907847,\"y\":12.30155919837174,\"z\":2.505118605263424e-7},{\"x\":21.064383146445905,\"y\":12.30155919837174,\"z\":2.505118605263424e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":48,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":1.0617267113389017,\"y\":-0.21900161997434964,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1.3077959701366784,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":21.20731987301353,\"y\":10.573371225657901,\"z\":0},{\"x\":26.03046410301759,\"y\":10.573371225657901,\"z\":0},{\"x\":26.03046410301759,\"y\":13.066809545867876,\"z\":0},{\"x\":21.20731987301353,\"y\":13.066809545867876,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":48,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Deprecated API usage: No \\\"GlobalWorker\\\" specified.\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":20.872756726006372,\"y\":8.382099705567459,\"z\":-2.505118605263424e-7},{\"x\":23.858321030468314,\"y\":8.382099705567459,\"z\":-2.505118605263424e-7},{\"x\":23.858321030468314,\"y\":8.90703408876932,\"z\":2.505118605263424e-7},{\"x\":20.872756726006372,\"y\":8.90703408876932,\"z\":2.505118605263424e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":48,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":7.4075672904502525,\"y\":8.91303118058786,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1.304308219348344,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":27.36674082780121,\"y\":16.04841212352632,\"z\":0},{\"x\":32.17947193080971,\"y\":16.04841212352632,\"z\":0},{\"x\":32.17947193080971,\"y\":19.066784826938388,\"z\":0},{\"x\":27.36674082780121,\"y\":19.066784826938388,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":48,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"WebGL Renderer: ANGLE (NVIDIA, NVIDIA T1000 (0x00001FB0) Direct3D11 vs_5_0 ps_5_0, D3D11)\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":28.73181461321682,\"y\":10.658991764854493,\"z\":-2.505118605263424e-7},{\"x\":31.71737891767876,\"y\":10.658991764854493,\"z\":-2.505118605263424e-7},{\"x\":31.71737891767876,\"y\":11.183926148056354,\"z\":2.505118605263424e-7},{\"x\":28.73181461321682,\"y\":11.183926148056354,\"z\":2.505118605263424e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":48,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":-0.4457667669325711,\"y\":1.669887352304416,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1.3248901959349906,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":27.341740053585198,\"y\":9.769823951050169,\"z\":0},{\"x\":32.215919923710075,\"y\":9.769823951050169,\"z\":0},{\"x\":32.215919923710075,\"y\":15.41286857047274,\"z\":0},{\"x\":27.341740053585198,\"y\":15.41286857047274,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":48,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String dwgMultipleText2 = "{\"id\":\"dc8560af-f3ba-47f3-b8c2-f99c8ccdf544\",\"title\":\"multiple text 2\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"Default TextDefault TextDefault TextDefault TextDefault TextDefault Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":18.622514216134164,\"y\":16.237850875725453,\"z\":-1.9685039369641552e-7},{\"x\":20.864421477726466,\"y\":16.237850875725453,\"z\":-1.9685039369641552e-7},{\"x\":20.864421477726466,\"y\":16.63155166312683,\"z\":1.9685039369641552e-7},{\"x\":18.622514216134164,\"y\":16.63155166312683,\"z\":1.9685039369641552e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":9.17093983287138,\"y\":-1.6460807600950123,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1.908977106921955,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":26.55799699092769,\"y\":13.58783313550576,\"z\":0},{\"x\":31.27081759392415,\"y\":13.58783313550576,\"z\":0},{\"x\":31.27081759392415,\"y\":15.989407938655363,\"z\":0},{\"x\":26.55799699092769,\"y\":15.989407938655363,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text Default TextDefault Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":18.674770746551886,\"y\":16.08108127952593,\"z\":-1.9685039369641552e-7},{\"x\":20.91667800814419,\"y\":16.08108127952593,\"z\":-1.9685039369641552e-7},{\"x\":20.91667800814419,\"y\":16.474782066927308,\"z\":1.9685039369641552e-7},{\"x\":18.674770746551886,\"y\":16.474782066927308,\"z\":1.9685039369641552e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":1.1861028308296935,\"y\":-1.4628275384817189,\"z\":7.874015747856622e-8},\"rotation\":0,\"scale\":{\"x\":0.7528176142192118,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":19.921418154867087,\"y\":13.614316708365989,\"z\":0},{\"x\":22.042236334081917,\"y\":13.614316708365989,\"z\":0},{\"x\":22.042236334081917,\"y\":16.015891511515594,\"z\":0},{\"x\":19.921418154867087,\"y\":16.015891511515594,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default TextDefault TextDefault TextDefault Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":22.25434308016572,\"y\":15.480131160761083,\"z\":-1.9685039369641552e-7},{\"x\":24.49625034175802,\"y\":15.480131160761083,\"z\":-1.9685039369641552e-7},{\"x\":24.49625034175802,\"y\":15.873831948162461,\"z\":1.9685039369641552e-7},{\"x\":22.25434308016572,\"y\":15.873831948162461,\"z\":1.9685039369641552e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.9119341121646483,\"y\":-0.8880056857501303,\"z\":7.874015747856622e-8},\"rotation\":0,\"scale\":{\"x\":1.0685984508763637,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":22.872846036893602,\"y\":13.588188479001625,\"z\":0},{\"x\":25.70161565971886,\"y\":13.588188479001625,\"z\":0},{\"x\":25.70161565971886,\"y\":15.989763282151229,\"z\":0},{\"x\":22.872846036893602,\"y\":15.989763282151229,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":19.66764482448857,\"y\":11.74260179348181,\"z\":-1.9685039369641552e-7},{\"x\":21.909552086080872,\"y\":11.74260179348181,\"z\":-1.9685039369641552e-7},{\"x\":21.909552086080872,\"y\":12.13630258088319,\"z\":1.9685039369641552e-7},{\"x\":19.66764482448857,\"y\":12.13630258088319,\"z\":1.9685039369641552e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":19.372369233937388,\"y\":11.447326202930533,\"z\":0},{\"x\":22.204827676632053,\"y\":11.447326202930533,\"z\":0},{\"x\":22.204827676632053,\"y\":12.431578171434467,\"z\":0},{\"x\":19.372369233937388,\"y\":12.431578171434467,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default TextDefault TextDefault Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":19.275720846355668,\"y\":10.018136235287038,\"z\":-1.9685039369641552e-7},{\"x\":21.51762810794797,\"y\":10.018136235287038,\"z\":-1.9685039369641552e-7},{\"x\":21.51762810794797,\"y\":10.411837022688417,\"z\":1.9685039369641552e-7},{\"x\":19.275720846355668,\"y\":10.411837022688417,\"z\":1.9685039369641552e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.3932656243229864,\"y\":-0.31353919239905004,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1.0011968303698402,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":19.372369264596745,\"y\":8.818770256651739,\"z\":0},{\"x\":22.207510959257217,\"y\":8.818770256651739,\"z\":0},{\"x\":22.207510959257217,\"y\":10.984124587360398,\"z\":0},{\"x\":19.372369264596745,\"y\":10.984124587360398,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default TextDefault Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":18.936053398640485,\"y\":7.457566164028131,\"z\":-1.9685039369641552e-7},{\"x\":21.177960660232785,\"y\":7.457566164028131,\"z\":-1.9685039369641552e-7},{\"x\":21.177960660232785,\"y\":7.851266951429509,\"z\":1.9685039369641552e-7},{\"x\":18.936053398640485,\"y\":7.851266951429509,\"z\":1.9685039369641552e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.7333067287775816,\"y\":-0.33966745843230584,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1.0233372519641335,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":19.34792454453949,\"y\":6.4289223168122005,\"z\":0},{\"x\":22.23270292623716,\"y\":6.4289223168122005,\"z\":0},{\"x\":22.23270292623716,\"y\":8.200575860119287,\"z\":0},{\"x\":19.34792454453949,\"y\":8.200575860119287,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default TextDefault\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":23.19020942224035,\"y\":11.66421699538205,\"z\":-1.9685039369641552e-7},{\"x\":25.432116683832653,\"y\":11.66421699538205,\"z\":-1.9685039369641552e-7},{\"x\":25.432116683832653,\"y\":12.057917782783429,\"z\":1.9685039369641552e-7},{\"x\":23.19020942224035,\"y\":12.057917782783429,\"z\":1.9685039369641552e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.05692851247621919,\"y\":-0.11668879869826265,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1.0041679122954574,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":22.947190304629192,\"y\":11.055402215742241,\"z\":0},{\"x\":25.78899282486176,\"y\":11.055402215742241,\"z\":0},{\"x\":25.78899282486176,\"y\":12.433354971647748,\"z\":0},{\"x\":22.947190304629192,\"y\":12.433354971647748,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String dwgDifBorderText = "{\"id\":\"\",\"title\":\"different border text\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":16.869631603072783,\"y\":12.90809217232556,\"z\":-1.9685039370819455e-7},{\"x\":19.111538864665086,\"y\":12.90809217232556,\"z\":-1.9685039370819455e-7},{\"x\":19.111538864665086,\"y\":13.301792959726939,\"z\":1.9685039370819455e-7},{\"x\":16.869631603072783,\"y\":13.301792959726939,\"z\":1.9685039370819455e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":3.6599456226956733,\"y\":4.075492093009091,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":20.31304189639657,\"y\":16.767048644466996,\"z\":0},{\"x\":22.988020024130606,\"y\":16.767048644466996,\"z\":0},{\"x\":22.988020024130606,\"y\":17.593820298010307,\"z\":0},{\"x\":20.31304189639657,\"y\":17.593820298010307,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":19.50376790320954,\"y\":12.799243573672998,\"z\":-1.9685039370819455e-7},{\"x\":21.74567516480184,\"y\":12.799243573672998,\"z\":-1.9685039370819455e-7},{\"x\":21.74567516480184,\"y\":13.192944361074376,\"z\":1.9685039370819455e-7},{\"x\":19.50376790320954,\"y\":13.192944361074376,\"z\":1.9685039370819455e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.07874015748031496,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":1.041478510528357,\"y\":2.906027763072389,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":20.30902594543992,\"y\":15.469050895116231,\"z\":0},{\"x\":23.02337415191411,\"y\":15.469050895116231,\"z\":0},{\"x\":23.02337415191411,\"y\":16.335192627399696,\"z\":0},{\"x\":20.30902594543992,\"y\":16.335192627399696,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.07874015748031496,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":20.439865927225082,\"y\":11.297132912267633,\"z\":-1.9685039370819455e-7},{\"x\":22.68177318881738,\"y\":11.297132912267633,\"z\":-1.9685039370819455e-7},{\"x\":22.68177318881738,\"y\":11.690833699669012,\"z\":1.9685039370819455e-7},{\"x\":20.439865927225082,\"y\":11.690833699669012,\"z\":1.9685039370819455e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.11811023622047244,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.1489199294902832,\"y\":3.092386591938432,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":20.33288034050771,\"y\":14.133613884602632,\"z\":0},{\"x\":23.086598625722054,\"y\":14.133613884602632,\"z\":0},{\"x\":23.086598625722054,\"y\":15.03912569562625,\"z\":0},{\"x\":20.33288034050771,\"y\":15.03912569562625,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.11811023622047244,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":19.203743008094804,\"y\":9.686173652209705,\"z\":-1.9685039370819455e-7},{\"x\":21.445650269687107,\"y\":9.686173652209705,\"z\":-1.9685039370819455e-7},{\"x\":21.445650269687107,\"y\":10.079874439611084,\"z\":1.9685039370819455e-7},{\"x\":19.203743008094804,\"y\":10.079874439611084,\"z\":1.9685039370819455e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.15748031496062992,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":1.4054632423175342,\"y\":3.2508751655629933,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":20.333615675602665,\"y\":12.661458335632387,\"z\":0},{\"x\":23.126704039557175,\"y\":12.661458335632387,\"z\":0},{\"x\":23.126704039557175,\"y\":13.606340225396165,\"z\":0},{\"x\":20.333615675602665,\"y\":13.606340225396165,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.15748031496062992,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":24.363167000924644,\"y\":15.476919100526043,\"z\":-1.9685039370819455e-7},{\"x\":26.605074262516943,\"y\":15.476919100526043,\"z\":-1.9685039370819455e-7},{\"x\":26.605074262516943,\"y\":15.870619887927422,\"z\":1.9685039370819455e-7},{\"x\":24.363167000924644,\"y\":15.870619887927422,\"z\":1.9685039370819455e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":-3.732191029023575,\"y\":-4.045449057356308,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":20.335700324558033,\"y\":11.136194253077793,\"z\":0},{\"x\":23.168158767252695,\"y\":11.136194253077793,\"z\":0},{\"x\":23.168158767252695,\"y\":12.120446221581727,\"z\":0},{\"x\":20.335700324558033,\"y\":12.120446221581727,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":25.25572558196272,\"y\":14.60613031130554,\"z\":-1.9685039370819455e-7},{\"x\":27.497632843555024,\"y\":14.60613031130554,\"z\":-1.9685039370819455e-7},{\"x\":27.497632843555024,\"y\":14.999831098706919,\"z\":1.9685039370819455e-7},{\"x\":25.25572558196272,\"y\":14.999831098706919,\"z\":1.9685039370819455e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.23622047244094488,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":-0.2830063793535338,\"y\":2.176971973051252,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":24.657758581973866,\"y\":16.468141593768216,\"z\":0},{\"x\":27.529587103408687,\"y\":16.468141593768216,\"z\":0},{\"x\":27.529587103408687,\"y\":17.491763641012312,\"z\":0},{\"x\":24.657758581973866,\"y\":17.491763641012312,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.23622047244094488,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":24.90741003814298,\"y\":12.929861892056074,\"z\":-1.9685039370819455e-7},{\"x\":27.14931729973528,\"y\":12.929861892056074,\"z\":-1.9685039370819455e-7},{\"x\":27.14931729973528,\"y\":13.323562679457453,\"z\":1.9685039370819455e-7},{\"x\":24.90741003814298,\"y\":13.323562679457453,\"z\":1.9685039370819455e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.2755905511811024,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.09927995299352688,\"y\":2.2999437238156304,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":24.67204432388113,\"y\":14.895159908296256,\"z\":0},{\"x\":27.583242924056105,\"y\":14.895159908296256,\"z\":0},{\"x\":27.583242924056105,\"y\":15.958152034280506,\"z\":0},{\"x\":24.67204432388113,\"y\":15.958152034280506,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.2755905511811024,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":24.406706443902113,\"y\":11.10120543469302,\"z\":-1.9685039370819455e-7},{\"x\":26.648613705494412,\"y\":11.10120543469302,\"z\":-1.9685039370819455e-7},{\"x\":26.648613705494412,\"y\":11.494906222094398,\"z\":1.9685039370819455e-7},{\"x\":24.406706443902113,\"y\":11.494906222094398,\"z\":1.9685039370819455e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.31496062992125984,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.64352299021186,\"y\":2.4645328329511624,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":24.695898713069674,\"y\":13.211407578204355,\"z\":0},{\"x\":27.64646739198481,\"y\":13.211407578204355,\"z\":0},{\"x\":27.64646739198481,\"y\":14.313769782928766,\"z\":0},{\"x\":24.695898713069674,\"y\":14.313769782928766,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.31496062992125984,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":24.23254867199224,\"y\":9.316088416790992,\"z\":-1.9685039370819455e-7},{\"x\":26.474455933584544,\"y\":9.316088416790992,\"z\":-1.9685039370819455e-7},{\"x\":26.474455933584544,\"y\":9.70978920419237,\"z\":1.9685039370819455e-7},{\"x\":24.23254867199224,\"y\":9.70978920419237,\"z\":1.9685039370819455e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.3543307086614173,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.8673207386184885,\"y\":2.4026917869572237,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":24.725853640341818,\"y\":11.344764509884888,\"z\":0},{\"x\":27.715792397997113,\"y\":11.344764509884888,\"z\":0},{\"x\":27.715792397997113,\"y\":12.48649679334945,\"z\":0},{\"x\":24.725853640341818,\"y\":12.48649679334945,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.3543307086614173,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":28.172868261453008,\"y\":11.427751230650708,\"z\":-1.9685039370819455e-7},{\"x\":30.414775523045307,\"y\":11.427751230650708,\"z\":-1.9685039370819455e-7},{\"x\":30.414775523045307,\"y\":11.821452018052087,\"z\":1.9685039370819455e-7},{\"x\":28.172868261453008,\"y\":11.821452018052087,\"z\":1.9685039370819455e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":-3.051229129353537,\"y\":-1.5654977395070322,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":24.727938235541302,\"y\":9.468552687244701,\"z\":0},{\"x\":27.75724707193675,\"y\":9.468552687244701,\"z\":0},{\"x\":27.75724707193675,\"y\":10.649655049449427,\"z\":0},{\"x\":24.727938235541302,\"y\":10.649655049449427,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String dwgMultipleType = "{\"id\":\"\",\"title\":\"multiple type\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"arrow\",\"state\":{\"anchors\":[{\"x\":9.453664970090808,\"y\":12.456132182032931,\"z\":0},{\"x\":13.783512871046574,\"y\":13.720125094162313,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"freehand\",\"state\":{\"anchors\":[{\"x\":18.220934632895652,\"y\":11.380393533412182,\"z\":0},{\"x\":18.43608235468227,\"y\":11.4072869996277,\"z\":0},{\"x\":18.54365621557558,\"y\":11.461073932058738,\"z\":0},{\"x\":19.51182096361538,\"y\":11.62243472935185,\"z\":0},{\"x\":20.91028115522842,\"y\":11.62243472935185,\"z\":0},{\"x\":21.098535411791715,\"y\":11.595541263136331,\"z\":0},{\"x\":21.475043924918307,\"y\":11.43418046584322,\"z\":0},{\"x\":21.636404716258273,\"y\":11.219032736119068,\"z\":0},{\"x\":21.690191646704925,\"y\":11.030778472610436,\"z\":0},{\"x\":21.690191646704925,\"y\":10.869417675317326,\"z\":0},{\"x\":21.44815045969498,\"y\":10.600483013162137,\"z\":0},{\"x\":21.25989620313168,\"y\":10.439122215869025,\"z\":0},{\"x\":20.587559572548493,\"y\":10.062613688851762,\"z\":0},{\"x\":19.86143601151864,\"y\":9.793679026696575,\"z\":0},{\"x\":19.05463205481881,\"y\":9.65921169561898,\"z\":0},{\"x\":19.027738589595483,\"y\":9.578531296972423,\"z\":0},{\"x\":19.4311405679454,\"y\":9.040661972662049,\"z\":0},{\"x\":19.565607894062037,\"y\":8.691046911860305,\"z\":0},{\"x\":19.75386215062533,\"y\":8.422112249705119,\"z\":0},{\"x\":19.78075561584866,\"y\":8.180071053765449,\"z\":0},{\"x\":19.86143601151864,\"y\":7.91113639161026,\"z\":0},{\"x\":19.915222941965297,\"y\":7.50773439837748,\"z\":0},{\"x\":19.915222941965297,\"y\":7.1850128037912535,\"z\":0},{\"x\":19.807649081071986,\"y\":6.969865074067105,\"z\":0},{\"x\":19.673181754955348,\"y\":6.86229120920503,\"z\":0},{\"x\":19.26977977660543,\"y\":6.7547173443429545,\"z\":0},{\"x\":18.194041167672324,\"y\":6.7278238781274355,\"z\":0},{\"x\":17.817532654545733,\"y\":6.835397742989511,\"z\":0},{\"x\":17.467917606642473,\"y\":6.996758540282624,\"z\":0},{\"x\":17.414130676195818,\"y\":7.0774389389291805,\"z\":0},{\"x\":17.118302558739213,\"y\":7.26569320243781,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"highlight\",\"state\":{\"anchors\":[{\"x\":18.705017006915547,\"y\":15.11858533736929,\"z\":0},{\"x\":18.83948433303219,\"y\":15.199265736015846,\"z\":0},{\"x\":19.08152552004214,\"y\":15.253052668446884,\"z\":0},{\"x\":19.323566707052088,\"y\":15.360626533308958,\"z\":0},{\"x\":19.700075220178675,\"y\":15.441306931955515,\"z\":0},{\"x\":19.88832947674197,\"y\":15.52198733060207,\"z\":0},{\"x\":20.56066610732516,\"y\":15.575774263033107,\"z\":0},{\"x\":21.098535411791715,\"y\":15.683348127895183,\"z\":0},{\"x\":21.313683133578337,\"y\":15.683348127895183,\"z\":0},{\"x\":21.636404716258273,\"y\":15.73713506032622,\"z\":0},{\"x\":22.98107797742466,\"y\":15.790921992757259,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.19685039370078738,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":0.5},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"line\",\"state\":{\"anchors\":[{\"x\":8.135885146698183,\"y\":8.677600188725394,\"z\":0},{\"x\":13.622152107156174,\"y\":7.306033391788238,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.06692913385826771,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":1.479140614732593,\"y\":-0.7664637971151373,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"polyline\",\"state\":{\"closed\":true,\"anchors\":[{\"x\":24.72915321694096,\"y\":13.37051003336057,\"z\":0},{\"x\":27.741221321953667,\"y\":11.86447592529152,\"z\":0},{\"x\":25.21323559096086,\"y\":9.847465959127613,\"z\":0},{\"x\":22.443208672958107,\"y\":12.94021457391227,\"z\":0},{\"x\":24.72915321694096,\"y\":14.338674817119244,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.06692913385826771,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"simple-arrow-callout\",\"state\":{\"anchors\":[],\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"scale\":{\"x\":1,\"y\":1,\"z\":1},\"uuid\":\"B947D4C8-E3F2-4B15-8C92-E8399A349D0E\"},\"dataModelVersion\":6},{\"type\":\"arrow\",\"state\":{\"anchors\":[{\"x\":8.888902200400926,\"y\":17.48521036433494,\"z\":0},{\"x\":7.598015869681195,\"y\":18.50716208052465,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.06692913385826771,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"groupId\":\"B947D4C8-E3F2-4B15-8C92-E8399A349D0E\",\"groupOrder\":0,\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":7.767948569604775,\"y\":17.28835997063425,\"z\":-1.968503937092282e-7},{\"x\":10.009855831197076,\"y\":17.28835997063425,\"z\":-1.968503937092282e-7},{\"x\":10.009855831197076,\"y\":17.68206075803563,\"z\":1.968503937092282e-7},{\"x\":7.767948569604775,\"y\":17.68206075803563,\"z\":1.968503937092282e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.06692913385826771,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"groupId\":\"B947D4C8-E3F2-4B15-8C92-E8399A349D0E\",\"groupOrder\":1,\"translation\":{\"x\":0,\"y\":-0.4271653543307105,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":7.537633608974855,\"y\":16.630879650745577,\"z\":0},{\"x\":10.240170791826998,\"y\":16.630879650745577,\"z\":0},{\"x\":10.240170791826998,\"y\":17.485210359407,\"z\":0},{\"x\":7.537633608974855,\"y\":17.485210359407,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.06692913385826771,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String dwgTest = "{\"id\":\"6e6a4f78-8d46-4764-a729-e5798a8b8ac3\",\"title\":\"123\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"arrow\",\"state\":{\"anchors\":[{\"x\":14.673106857735883,\"y\":9.220633474195752,\"z\":0},{\"x\":18.314008846087106,\"y\":9.576511106654072,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"arrow\",\"state\":{\"anchors\":[{\"x\":21.790659616918727,\"y\":17.104691793272337,\"z\":0},{\"x\":22.47503968991708,\"y\":9.850263131622006,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":3.093397929952541,\"y\":-0.13687601248396852,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":13.305776400660328,\"y\":6.423138843299655,\"z\":-1.878838953947568e-7},{\"x\":15.547683662252629,\"y\":6.423138843299655,\"z\":-1.878838953947568e-7},{\"x\":15.547683662252629,\"y\":6.81683963070105,\"z\":1.878838953947568e-7},{\"x\":13.305776400660328,\"y\":6.81683963070105,\"z\":1.878838953947568e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":5.228663757707393,\"y\":0.4380032399486993,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":18.213574180284247,\"y\":6.54027594358312,\"z\":0},{\"x\":21.097213725341113,\"y\":6.54027594358312,\"z\":0},{\"x\":21.097213725341113,\"y\":7.575709014449261,\"z\":0},{\"x\":18.213574180284247,\"y\":7.575709014449261,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"rectangle\",\"state\":{\"anchors\":[{\"x\":19.08556222648188,\"y\":15.264673803088737,\"z\":0},{\"x\":21.1934528513168,\"y\":15.264673803088737,\"z\":0},{\"x\":21.1934528513168,\"y\":13.704287260771496,\"z\":0},{\"x\":19.08556222648188,\"y\":13.704287260772917,\"z\":-0.0000014893112296112853}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":1.0676329138774285,\"y\":2.682769844685783,\"z\":0},\"rotation\":0.8597290400043295,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"freehand\",\"state\":{\"anchors\":[{\"x\":28.36575601633951,\"y\":13.156783210835624,\"z\":0},{\"x\":28.803759263058456,\"y\":13.156783210835624,\"z\":0},{\"x\":29.269137712697336,\"y\":13.102032805842034,\"z\":0},{\"x\":29.378638524377067,\"y\":13.047282400848449,\"z\":0},{\"x\":29.679765756496344,\"y\":12.965156793358068,\"z\":0},{\"x\":29.844016974015947,\"y\":12.828280780874097,\"z\":0},{\"x\":29.926142582775753,\"y\":12.636654363396543,\"z\":0},{\"x\":29.926142582775753,\"y\":12.116525515957463,\"z\":0},{\"x\":29.844016974015947,\"y\":11.842773490989526,\"z\":0},{\"x\":29.707140959416282,\"y\":11.623771871015176,\"z\":0},{\"x\":29.43338893021694,\"y\":11.514271061028001,\"z\":0},{\"x\":29.022760886417927,\"y\":11.514271061028001,\"z\":0},{\"x\":28.749008857218588,\"y\":11.705897478505557,\"z\":0},{\"x\":28.69425845137872,\"y\":11.705897478505557,\"z\":0},{\"x\":28.666883248458785,\"y\":11.73327268100235,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":-14.098229503766026,\"y\":-0.465378442445493,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"line\",\"state\":{\"anchors\":[{\"x\":28.037253581300302,\"y\":17.482065205329025,\"z\":0},{\"x\":30.446271438254495,\"y\":16.879810750399564,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":-10.9500811679736,\"y\":-4.2431563870030224,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String pdfText2 = "{\"id\":\"555025cc-d4ee-4d60-9ea4-286ff7929bc2\",\"title\":\"more text\",\"is2d\":true,\"sheetGUID\":\"5ae6cdf7aec11c44a38bb8dacfe377d6/1\",\"sheetName\":\"Page 1\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":2.0756911600931747,\"y\":4.856867001718684,\"z\":-6.950719387107567e-8},{\"x\":2.8673008680831353,\"y\":4.856867001718684,\"z\":-6.950719387107567e-8},{\"x\":2.8673008680831353,\"y\":4.995881389463193,\"z\":6.950719387107567e-8},{\"x\":2.0756911600931747,\"y\":4.995881389463193,\"z\":6.950719387107567e-8}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":1.9864989268508066,\"y\":4.76767476847628,\"z\":0},{\"x\":2.956493101325503,\"y\":4.76767476847628,\"z\":0},{\"x\":2.956493101325503,\"y\":5.085073622705596,\"z\":0},{\"x\":1.9864989268508066,\"y\":5.085073622705596,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default TextDefault TextDefault Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":1.9040759426688283,\"y\":3.9886959285939834,\"z\":-6.950719387107567e-8},{\"x\":2.6956856506587887,\"y\":3.9886959285939834,\"z\":-6.950719387107567e-8},{\"x\":2.6956856506587887,\"y\":4.127710316338493,\"z\":6.950719387107567e-8},{\"x\":1.9040759426688283,\"y\":4.127710316338493,\"z\":6.950719387107567e-8}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.25949475551027434,\"y\":0.030285037434582662,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1.2220274339692558,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":1.9864989301674336,\"y\":3.7212671517887035,\"z\":0},{\"x\":3.1322521665438194,\"y\":3.7212671517887035,\"z\":0},{\"x\":3.1322521665438194,\"y\":4.455709169251754,\"z\":0},{\"x\":1.9864989301674336,\"y\":4.455709169251754,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":5.265715201628076,\"y\":4.978007151457015,\"z\":-6.950719387107567e-8},{\"x\":6.057324909618036,\"y\":4.978007151457015,\"z\":-6.950719387107567e-8},{\"x\":6.057324909618036,\"y\":5.117021539201524,\"z\":6.950719387107567e-8},{\"x\":5.265715201628076,\"y\":5.117021539201524,\"z\":6.950719387107567e-8}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":64,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.16141732283464566,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.05406115078955828,\"y\":-0.23169064624096408,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":5.115499347589864,\"y\":4.364410004708508,\"z\":0},{\"x\":6.315663067738165,\"y\":4.364410004708508,\"z\":0},{\"x\":6.315663067738165,\"y\":5.267237395514239,\"z\":0},{\"x\":5.115499347589864,\"y\":5.267237395514239,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.16141732283464566,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":4.777014865144786,\"y\":3.5813093410676555,\"z\":-1.2356834465969008e-7},{\"x\":6.182604785673302,\"y\":3.5813093410676555,\"z\":-1.2356834465969008e-7},{\"x\":6.182604785673302,\"y\":3.828446030391227,\"z\":1.2356834465969008e-7},{\"x\":4.777014865144786,\"y\":3.828446030391227,\"z\":1.2356834465969008e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":64,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.16141732283464566,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":4.572737859065616,\"y\":3.377032334988423,\"z\":0},{\"x\":6.386881791752472,\"y\":3.377032334988423,\"z\":0},{\"x\":6.386881791752472,\"y\":4.0327230364704585,\"z\":0},{\"x\":4.572737859065616,\"y\":4.0327230364704585,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":64,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.16141732283464566,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

        String dwgRect3 = "{\"id\":\"\",\"title\":\"\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"arrow\",\"state\":{\"anchors\":[{\"x\":21.735909220638053,\"y\":13.504852677952787,\"z\":0},{\"x\":23.186794956276167,\"y\":9.535448289900065,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":-1.5877617789153682,\"y\":-1.0265701066385837,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"line\",\"state\":{\"anchors\":[{\"x\":21.68115880360862,\"y\":16.543500145557832,\"z\":0},{\"x\":26.225442491866353,\"y\":12.026591713533533,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":4.57165888925935,\"y\":0.6433172552048845,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"rectangle\",\"state\":{\"anchors\":[{\"x\":13.22222110297938,\"y\":11.93077852137833,\"z\":0},{\"x\":17.109499917610012,\"y\":11.93077852137833,\"z\":0},{\"x\":17.109499917610012,\"y\":6.3188620095356205,\"z\":0},{\"x\":13.22222110297938,\"y\":6.318862009540733,\"z\":-0.000005356294773163395}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0.4717119416630716,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6},{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":18.45231454960792,\"y\":16.442462957126146,\"z\":-1.878838953947568e-7},{\"x\":20.694221811200222,\"y\":16.442462957126146,\"z\":-1.878838953947568e-7},{\"x\":20.694221811200222,\"y\":16.836163744527543,\"z\":1.878838953947568e-7},{\"x\":18.45231454960792,\"y\":16.836163744527543,\"z\":1.878838953947568e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":1.8888890014754516,\"y\":-0.27375202496793527,\"z\":0},\"rotation\":0.9936041031553295,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":20.02033736299191,\"y\":15.847844781683294,\"z\":0},{\"x\":22.903976908048776,\"y\":15.847844781683294,\"z\":0},{\"x\":22.903976908048776,\"y\":16.883277852549437,\"z\":0},{\"x\":20.02033736299191,\"y\":16.883277852549437,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.24803149606299213,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0.9936041031553295,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

//        String sourceLmvRect = "{\"maxPixelPerUnit\":8192,\"width\":6600,\"height\":5100}";
        String sourceLmvRect = "{\"width\":34,\"height\":22}";
        String sourceLmvRectPDF = "{\"width\":11,\"height\":8.5}";
        Library lib = new Library();
        try {
//            DatalogicsUtil.generateLmvMarkupDocument(ComplexHighlight, sourceLmvRect, inputFileName, outputFileName);RestApiDatalogicsUtil util = new RestApiDatalogicsUtil();
            RestApiDatalogicsUtil util = new RestApiDatalogicsUtil();
            util.generateLmvMarkupDocument(dwgRect3, sourceLmvRect, inputFileName, outputFileName);
			util.generateLmvMarkupDocument(pdfText, sourceLmvRectPDF, inputFileNamePDF, outputFileNamePDF);
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


    CircleAnnotation getCircleAnnotation(Page page, Rect transformedRect, Double borderWidth) {
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

        paddingText = new PaddingText(page, fontSize, translateBorderStyleWidth(strokeWidth));
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

    int processContentStackItem(Content currentContent, Deque<ContentStackItem> contentStack, int currentItem) {
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
        return noRectify ? new Point(x, y) :rectifyRotationOfPoint(x, y);
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

    void applyStrokeAndFillColorIfNeeded(FreeTextAnnotation freeTextAnnot, PDFDict pdfDict, EnumSet<PathPaintOpFlags> paintOpFlags, GraphicState gs) {
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

    String generateCurrentSegment(String currentSegment, StringBuilder currentLine, int split) {
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

    String handleCurrentLine(StringBuilder currentLine, String currentSegment, String[] textSegments, int index) {
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

        if (isRectRotated) {
            rectWidth = rect.getHeight();
        }

        EnumSet<PathPaintOpFlags> paintOpFlags = EnumSet.of(PathPaintOpFlags.NEITHER);
        GraphicState gs = getGraphicState();

        GraphicState gsTextRun = getGraphicState();
        gsTextRun.setFillColor(freeTextAnnot.getTextColor());

        PDFDict pdfDict = freeTextAnnot.getPDFDict();

        applyStrokeAndFillColorIfNeeded(freeTextAnnot, pdfDict, paintOpFlags, gs);

        double fontSize = freeTextAnnot.getFontSize();
        TextState textState = getTextState();
        textState.setFontSize(fontSize);

        int align = 0;
        HorizontalAlignment q = freeTextAnnot.getQuadding();
        if (q == HorizontalAlignment.CENTER) align = 2;
        else if (q == HorizontalAlignment.RIGHT) align = 1;

        Text text = getText();
        int i = 0;
        if (annotationText != null) {
            String[] textSegments = annotationText.split("\\s");
            double totalWidth = rectWidth - 2 * padding + fontSize / 2;

            if (isRectRotated) {
                totalWidth = rectWidth - (2 * (paddingText.borderWidth + paddingText.measuredWidth)) + fontSize / 3;
            }

            double remainingWidth = totalWidth;
            Matrix matrix = new Matrix();
            TextRun space = getTextRun(" ", dLFont, gsTextRun, textState, matrix);
            double spaceAdv = space.getAdvance();
            matrix.delete();

            double horizPos = rectLeft + paddingText.textToRectLeft;
            double vertPos = rectTop - paddingText.textToRectTop;
            if (isRectRotated) {
                horizPos = rectLeft + paddingText.borderWidth * 2 + paddingText.borderWidth * 4 / 5;
                vertPos = rectTop - paddingText.textToRectTop;
            }

            String currentSegment = textSegments[0];
            StringBuilder currentLine = new StringBuilder();
            matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);
            while (vertPos > rect.getBottom() && currentSegment != null && !currentSegment.isEmpty()) {
                TextRun dummy = getTextRun(currentSegment, dLFont, gsTextRun, textState, matrix);
                double dummyAdvance = getAdvance(dummy);
                boolean shouldHandleText = false;

                if (dummyAdvance > totalWidth) {
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
                    if (!isRectRotated) horizPos = rect.getLeft() + padding;
                    matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);
                    remainingWidth = totalWidth;
                }
            }

            if (currentLine.length() > 0) {
                TextAttributes textAttributes = new TextAttributes(dLFont, gsTextRun, textState, text);
                handleText(matrix, currentLine, totalWidth, align, horizPos, vertPos, textAttributes);
            }
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

    void handleText(Matrix matrix, StringBuilder currentLine, double totalWidth, int align, double horizPos, double vertPos, TextAttributes textAttributes) {
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
