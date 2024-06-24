import com.datalogics.PDFL.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

		PaddingText(Page page, double fontSize, double borderWidth) {
			this.borderWidth = borderWidth;
			if (page.getRotation() == PageRotation.ROTATE_270) {
				this.originFontsize = fontSize;
				this.appearanceFontSize = fontSize * 3 / 4;

				double textWidth = fontSize * 5 / 18;
//                double textWidth = fontSize * 3 / 4;
				this.padding = textWidth / 2 + this.borderWidth;
//                this.textToRectTop = textWidth + this.padding;
//                this.textToRectLeft = this.padding - 20;
//                this.textToRectLeft = 0;
//                this.textToRectTop = this.padding;
//                this.textToRectLeft = textWidth + this.padding;

//                this.appearanceFontSize = fontSize * 5 / 18;
//
//                this.padding = this.appearanceFontSize / 2 + this.borderWidth;
				this.textToRectTop = textWidth * 5 / 6 + this.padding;
				this.textToRectLeft = this.padding;
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
	private static final String LMV_MARKUP_STATE_STYLE_STROKE_OPACITY= "stroke-opacity";
	private static final String LMV_MARKUP_STATE_STYLE_FONT_SIZE= "font-size";
	private static final String LMV_MARKUP_STATE_STYLE_FONT_COLOR= "font-color";

	public String licenseKey;

	public static void main(String[] args) throws Exception {
		System.out.println("Add Annotation to PDF:");

		String inputFileNamePDF = "./src/main/resources/test.pdf";
		String outputFileNamePDF = "./src/main/resources/test2.pdf";
		String inputFileName = "./src/main/resources/dwg-b.pdf";
		String outputFileName = "./src/main/resources/dwg-a.pdf";

		String dwgRect = "{\"id\":\"25e65016-18b3-40c9-8422-ba47e55386a8\",\"title\":\"rect\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"rectangle\",\"state\":{\"anchors\":[{\"x\":0.5312077352518472,\"y\":9.497643121914173,\"z\":0},{\"x\":13.984413579814586,\"y\":9.497643121914173,\"z\":0},{\"x\":13.984413579814586,\"y\":1.0114315286668067,\"z\":0},{\"x\":0.5312077352518472,\"y\":1.011431528672576,\"z\":-0.000006997537756060298}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.39370078740157477,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

		String line = "{\"id\":\"e58df102-e6fc-4e7c-a397-7f26a2d437dc\",\"title\":\"line\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"line\",\"state\":{\"anchors\":[{\"x\":0.5008333325386047,\"y\":0.9210034061508581,\"z\":0},{\"x\":33.500831604003906,\"y\":20.921003229779764,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.22440944881889763,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0.07816326131054119,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

		String pdfLine = "{\"id\":\"3947d470-f042-4cef-9f43-c9edd5fc6b27\",\"title\":\"line\",\"is2d\":true,\"sheetGUID\":\"5ae6cdf7aec11c44a38bb8dacfe377d6/1\",\"sheetName\":\"Page 1\",\"markups\":[{\"type\":\"line\",\"state\":{\"anchors\":[{\"x\":0.04932146867116316,\"y\":0.10179652397536287,\"z\":0},{\"x\":10.922031100591024,\"y\":8.410493787280737,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.2637795275590551,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

		String dwgText = "{\"id\":\"ece89432-cdd0-4ffe-83ba-a6a7cafeccea\",\"title\":\"text\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":11.164084631456529,\"y\":14.640270253684394,\"z\":-1.6231837338257393e-7},{\"x\":13.40599189304883,\"y\":14.640270253684394,\"z\":-1.6231837338257393e-7},{\"x\":13.40599189304883,\"y\":15.033971041085834,\"z\":1.6231837338257393e-7},{\"x\":11.164084631456529,\"y\":15.033971041085834,\"z\":1.6231837338257393e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":70,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.15748031496062992,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.1859142607174103,\"y\":-0.7545931758530182,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":10.88849408268725,\"y\":12.855493333119169,\"z\":0},{\"x\":14.053410968076577,\"y\":12.855493333119169,\"z\":0},{\"x\":14.053410968076577,\"y\":15.309561574588985,\"z\":0},{\"x\":10.88849408268725,\"y\":15.309561574588985,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.15748031496062992,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

		String pdfText = "{\"id\":\"\",\"title\":\"\",\"is2d\":true,\"sheetGUID\":\"5ae6cdf7aec11c44a38bb8dacfe377d6/1\",\"sheetName\":\"Page 1\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":3.7615586349978822,\"y\":4.261261484208469,\"z\":-6.950719387107567e-8},{\"x\":4.553168342987843,\"y\":4.261261484208469,\"z\":-6.950719387107567e-8},{\"x\":4.553168342987843,\"y\":4.400275871952978,\"z\":6.950719387107567e-8},{\"x\":3.7615586349978822,\"y\":4.400275871952978,\"z\":6.950719387107567e-8}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":76,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.11811023622047244,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0.07723021541365468,\"y\":-0.4479352493991972,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":3.632996322674334,\"y\":3.2368286694449826,\"z\":0},{\"x\":4.836191085456655,\"y\":3.2368286694449826,\"z\":0},{\"x\":4.836191085456655,\"y\":4.528838179953007,\"z\":0},{\"x\":3.632996322674334,\"y\":4.528838179953007,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.11811023622047244,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

		String dwgNormalText = "{\"id\":\"579c8806-7c58-4fb2-8448-fcc417ebe87a\",\"title\":\"normal text\",\"is2d\":true,\"sheetGUID\":\"3c9663fa-f808-5c8f-bb86-ca8423d7b731\",\"sheetName\":\"Sheet\",\"markups\":[{\"type\":\"callout\",\"state\":{\"text\":\"Default Text\",\"isFrameUsed\":true,\"frameType\":\"rectangle\",\"anchors\":[{\"x\":11.724652034649175,\"y\":11.50863472370645,\"z\":-1.9685039369641552e-7},{\"x\":13.966559296241476,\"y\":11.50863472370645,\"z\":-1.9685039369641552e-7},{\"x\":13.966559296241476,\"y\":11.902335511107829,\"z\":1.9685039369641552e-7},{\"x\":11.724652034649175,\"y\":11.902335511107829,\"z\":1.9685039369641552e-7}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"frameState\":{\"anchors\":[{\"x\":11.50811660157831,\"y\":11.292099290635488,\"z\":0},{\"x\":14.18309472931234,\"y\":11.292099290635488,\"z\":0},{\"x\":14.18309472931234,\"y\":12.118870944178791,\"z\":0},{\"x\":11.50811660157831,\"y\":12.118870944178791,\"z\":0}],\"style\":{\"stroke-color\":\"#FF0000\",\"font-size\":36,\"font-weight\":\"normal\",\"font-family\":\"Artifakt Element, Arial\",\"font-style\":\"normal\",\"font-color\":\"#FF0000\",\"stroke-width\":0.03937007874015748,\"fill-opacity\":0.5,\"fill-color\":\"-1\",\"stroke-opacity\":1},\"translation\":{\"x\":0,\"y\":0,\"z\":0},\"rotation\":0,\"scale\":{\"x\":1,\"y\":1,\"z\":1}},\"dataModelVersion\":6}],\"CreatedByUserName\":\"pancras lu\",\"CreatedByUserId\":590}";

		String dwgLine = "{\"id\": \"25e65016-18b3-40c9-8422-ba47e55386a8\", \"title\": \"rect\", \"is2d\": true, \"sheetGUID\": \"3c9663fa-f808-5c8f-bb86-ca8423d7b731\", \"sheetName\": \"Sheet\", \"markups\": [{\"type\": \"line\", \"state\": {\"anchors\": [{\"x\": -0.010943483884158667, \"y\": 0.0889592884272119, \"z\": 0}, {\"x\": 33.81540543857514, \"y\": 21.816833971798278, \"z\": 0}], \"style\": {\"stroke-color\": \"#FF0000\", \"font-size\": 36, \"font-weight\": \"normal\", \"font-family\": \"Artifakt Element, Arial\", \"font-style\": \"normal\", \"font-color\": \"#FF0000\", \"stroke-width\": 0.39370078740157477, \"fill-opacity\": 0.5, \"fill-color\": \"-1\", \"stroke-opacity\": 1}, \"translation\": {\"x\": 0, \"y\": 0, \"z\": 0}, \"rotation\": 0, \"scale\": {\"x\": 1, \"y\": 1, \"z\": 1}}, \"dataModelVersion\": 6}], \"CreatedByUserName\": \"pancras lu\", \"CreatedByUserId\": 590}";

//        String sourceLmvRect = "{\"maxPixelPerUnit\":8192,\"width\":6600,\"height\":5100}";
		String sourceLmvRect = "{\"width\":34,\"height\":22}";
		String sourceLmvRectPDF = "{\"width\":11,\"height\":8.5}";
		Library lib = new Library();
		try {
//            DatalogicsUtil.generateLmvMarkupDocument(ComplexHighlight, sourceLmvRect, inputFileName, outputFileName);RestApiDatalogicsUtil util = new RestApiDatalogicsUtil();
			RestApiDatalogicsUtil util = new RestApiDatalogicsUtil();
			util.generateLmvMarkupDocument(dwgText, sourceLmvRect, inputFileName, outputFileName);
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
	 * @param doc The PDF document.
	 * @param page The page to which the markup will be applied.
	 * @param markup The markup data in JSONObject format.
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
	 * @param doc The PDF document.
	 * @param page The page to which the markup will be applied.
	 * @param markup The markup data in JSONObject format.
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
//        Double fontSizeInPoints = fontSize * 5 / 18;
//        Double fontSizeInPoints = translateBorderStyleWidth(fontSize);
		double fontSizeInPoints = paddingText.appearanceFontSize;
		double borderWidth = paddingText.borderWidth;


		/* square */
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
	 * @param page The page to which the markup will be applied.
	 * @param markup The markup data in JSONObject format.
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
		Double strokeWidth = style.getDouble(LMV_MARKUP_STATE_STYLE_FILL_OPACITY);

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
	 * @param annot The annotation for which the appearance will be set.
	 * @param markup The markup data in JSONObject format.
	 * @param type The type of markup.
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
//
//            currentContent.addElement(text, currentItem);
//            currentContent.removeElement(currentItem);
		}
	}

	private static void rectifyRotationOfPage(Page page, Text text, FreeTextAnnotation annot) {
		PageRotation originPageRotation = page.getRotation();

		double w = annot.getRect().getWidth();
		double h = annot.getRect().getHeight();
		double padding = paddingText.padding;
		w = w - 2 * annot.getBorderStyleWidth() - 2 * padding;

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
	 * @param source The original rectangle.
	 * @param borderWidth The border width of the rectangle.
	 * @param translation The translation to be applied.
	 * @param scale The scale to be applied.
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
	 * @param annot The annotation to be rotated.
	 * @param doc The PDF document.
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
		return new Point(x, y);
	}

	
	Point translateScale(JSONObject scale) {
		Double x = scale.getDouble("x");
		Double y = scale.getDouble("y");
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

	private Rect rectifyRotationOfPage(Page page, Rect rect) {
		Matrix rotateMatrix = getRotationMatrix(page);
		return rect.transform(rotateMatrix);
	}

	private Double translateX(Double x) {
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
//        double padding = freeTextAnnot.getFontSize() / 2 + freeTextAnnot.getBorderStyleWidth() / 2;
		double padding = paddingText.padding;

		String annotationText = freeTextAnnot.getContents();

		Rect rect = freeTextAnnot.getRect();


		double rectWidth = rect.getWidth();
		double rectHeight = rect.getHeight();
		double rectLeft = rect.getLeft();
		double rectRight = rect.getRight();
		double rectTop = rect.getTop();
		double rectBottom = rect.getBottom();

		if (curPage.getRotation() == PageRotation.ROTATE_270) {
			rectWidth = rect.getHeight();
			rectHeight = rect.getWidth();
//            rectLeft = rect.getTop();
//            rectRight = rect.getBottom();
//            rectTop = rect.getLeft();
//            rectBottom = rect.getRight();
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
			final double totalWidth = rectWidth - 2 * padding + fontSize / 2;
//			final double totalWidth = rect.getWidth() - 2 * padding + fontSize / 2;

			double remainingWidth = totalWidth;
			Matrix matrix = new Matrix();
			TextRun space = getTextRun(" ", dLFont, gsTextRun, textState, matrix);
			double spaceAdv = space.getAdvance();
			matrix.delete();

//			double horizPos = rect.getLeft() + padding;
//			double vertPos = rect.getTop() - fontSize * 5 / 6 - padding;
			double horizPos = rectLeft + paddingText.textToRectLeft;
			double vertPos = rectTop - paddingText.textToRectTop;

			String currentSegment = textSegments[0];
			StringBuilder currentLine = new StringBuilder();
			matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);
			while (vertPos > rect.getBottom() && currentSegment != null && !currentSegment.isEmpty()) {
				TextRun dummy = getTextRun(currentSegment, dLFont, gsTextRun, textState, matrix);
				double dummyAdvance = getAdvance(dummy);
				if (dummyAdvance > totalWidth) {
					int split = findBreakPoint(currentSegment, remainingWidth - spaceAdv, dLFont, gsTextRun, textState);
					currentSegment = generateCurrentSegment(currentSegment, currentLine, split);
					TextAttributes textAttributes = new TextAttributes(dLFont, gsTextRun, textState, text);
					handleText(matrix, currentLine, totalWidth, align, horizPos, vertPos, textAttributes);
					currentLine = new StringBuilder();
					vertPos -= fontSize;
					horizPos = rect.getLeft() + padding;
					matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);
					remainingWidth = totalWidth;

				} else if ((dummyAdvance + spaceAdv) > remainingWidth) {
					TextAttributes textAttributes = new TextAttributes(dLFont, gsTextRun, textState, text);
					handleText(matrix, currentLine, totalWidth, align, horizPos, vertPos, textAttributes);
					currentLine = new StringBuilder();
					vertPos -= fontSize;
					horizPos = rect.getLeft() + padding;
					matrix = new Matrix(1, 0, 0, 1, horizPos, vertPos);
					remainingWidth = totalWidth;
				} else {
					currentSegment = handleCurrentLine(currentLine, currentSegment, textSegments, ++i);
					remainingWidth -= spaceAdv + dummyAdvance;
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

	/** The below methods are primarily designed for facilitating unit testing. */

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
