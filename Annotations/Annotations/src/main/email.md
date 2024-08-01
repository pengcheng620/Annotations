## 2024-07-09

Ticket Bug fix:

Title: [Backend] Garbled characters appear when merge markup into PDF files.

Problem Description:
- The text will appear as garbled char when the markup contains Chinese, Japanese, or Korean characters in the merge markup workflow.

Background:
- We use the datalogics.PDFL library (version 18.35.0) to merge markups into PDF files, and the text annotation can only set one font-face.
- The font-face come from frontend just support English. When the text contains Chinese, Japanese, or Korean characters, the text will appear as garbled char.

Action Plan:
- Handle the content text one by one, and set the font-face according to the text content.



## 2024-06-14

Dear Patrick,

Thank you very much for your prompt response. I have carefully reviewed the sample code you provided and found it quite helpful. However, I still have some questions that I hope you can assist me with.

From your example, it is evident that the cloudy border is constructed using points to form semicircles, which seems quite complex. I am wondering if there is a simpler way to achieve a cloudy border effect. For instance, is there a direct way to set a cloudy border style, or perhaps another method that could simplify this process?

Regarding this issue, I found some information in the PDF standard (ISO 32000-2:2020(E)) under section 12.5.4, which mentions using the `BE` entry to set the `S` and `I` attribute.

Additionally, I am using the Datalogics.PDFL library (version 18.35.0) to add an appearance stream style. My code is as follows:

```java
private static void translateRectangle(Document doc) {
// ... other code
    PDFName pdfKey = new PDFName("BE", doc, false);
    PDFDict pdfObj = new PDFDict(doc, false);
    pdfObj.put("S", new PDFName("C", doc, false));
    pdfObj.put("I", new PDFReal(1, doc, false));
    annot.getPDFDict().put(pdfKey, pdfObj);
    annot.setNormalAppearance(annot.generateAppearance());
// ... other code
}
```

The resulting file does contain the `BE` attributes, as shown in the following FDF code snippet:

```fdf
<</BE<</I 1.0/S/C>>/BS<</W 2.83465>>/C[1.0 0.0 0.0]/CA 0.5/NM(05d08e0e-47e4-4486-a8ef-f32e864087c5)/Page 0/Popup 3 0 R/Rect[342.789 380.458 448.747 439.893]/Subtype/Square/T(pengcheng)/Type/Annot>>
endobj
3 0 obj
```

Interestingly, the annotations generated this way have a solid border style, and the cloudy border effect does not seem to work. However, if I export the file containing the BE attributes using Adobe Acrobat Reader to an FDF file and then re-import this file back into Adobe Acrobat Reader, the border style appears as cloudy. Could you please explain why this happens?

In summary, my question is: Is it possible to achieve a cloudy border effect using the BE entry or any other method without manually drawing the cloudy border? Any sample code or guidance would be greatly appreciated.

Thank you for your assistance.

Best regards,
Pengcheng Lu

---

**Subject: Re: Customizing Free Text Annotation Appearance - Cloudy Border Implementation and BE Entry**

Dear Datalogic Team,

I hope this email finds you well.

I am writing to express my sincere gratitude for your prompt response and the provided code example for achieving a
cloudy border effect using a point-based approach. I have carefully reviewed the code and appreciate the effort put into
creating this solution.

While I understand the effectiveness of the provided code, I would like to explore the possibility of alternative
methods or workarounds that might offer a simpler or more direct approach to implementing the cloudy border effect.

Specifically, I am interested in investigating the feasibility of using a predefined cloudy border style or leveraging
other techniques to achieve the desired effect.

In addition, I have come across information in the PDF standard (ISO 32000-2:2020(E)) Section 12.5.4 regarding the use
of **BE** entries to set **S & I properties**. I have also experimented with this approach using the Datalogics.PDFL
library (version 18.35.0) and observed some interesting behavior.

The .FDF file code snippet from the generated PDF is as follows:

```fdf
<</BE<</I 1.0/S/C>>/BS<</W 2.83465>>/C[1.0 0.0 0.0]/CA 0.5/NM(05d08e0e-47e4-4486-a8ef-f32e864087c5)/Page 0/Popup 3 0 R/Rect[342.789 380.458 448.747 439.893]/Subtype/Square/T(pengcheng)/Type/Annot>>
endobj
3 0 obj
```

And the corresponding Java code:

```java
private static void translateRectangle(Document doc) {
// ... other code
    PDFName pdfKey = new PDFName("BE", doc, false);
    PDFDict pdfObj = new PDFDict(doc, false);
    pdfObj.put("S", new PDFName("C", doc, false));
    pdfObj.put("I", new PDFReal(1, doc, false));
    annot.getPDFDict().put(pdfKey, pdfObj);
    annot.setNormalAppearance(annot.generateAppearance());
// ... other code
}
```

Seem to produce annotations with a solid border style rather than the intended cloudy effect. However, when I export the
file containing these BE entries to FDF format using Adobe Acrobat Reader and then import the FDF file back into Adobe
Acrobat Reader, the border style for the annotations is correctly displayed as cloudy.

This observation raises a few questions:

1. Is there an issue with the direct application of **BE** entries to annotations using the Datalogics.PDFL library that
   prevents the cloudy border effect from being rendered correctly?

2. Could the cloudy border visualization be influenced by the PDF viewer or rendering environment?

3. Are there alternative approaches or configurations within the Datalogics.PDFL library to achieve the cloudy border
   effect without resorting to point-based drawing?

I would greatly appreciate your insights and guidance on these matters. If you have any additional code snippets,
documentation, or recommendations for achieving a more straightforward cloudy border implementation, I would be
immensely grateful.

Thank you again for your continued support and expertise in this endeavor. I look forward to your prompt response and
guidance.

Sincerely,
Pengcheng Lu
---
非常感谢您及时的回信，我已经收到了您提供的示例代码，并对其进行了仔细研究。我发现这个示例代码确实对我有所帮助，但是我仍然有一些问题想问一下。

根据您提供的代码，可以看出您是完全使用 point 的方式来绘制 cloudy border 的，需要根据点来构造半圆，我觉得这很复杂。我想问一下，是否有更简单的方法来实现
cloudy border 的效果？比如直接设置一个 cloudy 的 border style，或者使用其他的方法来实现？

而针对上面的问题，我在 PDF standard(ISO 32000-2:2020(E)) 12.5.4 章节中找到了一些信息，即使用 BE entry 去设置 S & I 属性。
其对应的 fdf 格式的代码类似：

```fdf
<</BE<</I 1.0/S/C>>/BS<</W 2.83465>>/C[1.0 0.0 0.0]/CA 0.5/NM(05d08e0e-47e4-4486-a8ef-f32e864087c5)/Page 0/Popup 3 0 R/Rect[342.789 380.458 448.747 439.893]/Subtype/Square/T(pengcheng)/Type/Annot>>
endobj
3 0 obj
```

同时，我使用了 Datalogics.PDFL library (version 18.35.0) 来添加外观流样式，代码类似：

```java
private static void translateRectangle(Document doc) {
// ... other code
    PDFName pdfKey = new PDFName("BE", doc, false);
    PDFDict pdfObj = new PDFDict(doc, false);
    pdfObj.put("S", new PDFName("C", doc, false));
    pdfObj.put("I", new PDFReal(1, doc, false));
    annot.getPDFDict().put(pdfKey, pdfObj);
    annot.setNormalAppearance(annot.generateAppearance());
// ... other code
}
```

PS: 有一个奇怪的现象，通过上述方式生成的文件的 annotation 的 border style 为 solid（cloudy 不起作用），但是通过 Adobe
Acrobat Reader 导出 fdf 格式的文件（包含了 BE 属性）之后，再把这个文件导入到 Adobe Acrobat Reader 中，border style 为
cloudy。请问这是什么原因导致的？

综上,我的问题是：能否通过使用 BE entry 或者其他的方法来实现 cloudy border 的效果而不是完全手绘 cloudy border的方式去实现？
如果有任何示例代码或者指导，我将不胜感激。

## 2024-06-13

顺便问一下，您之前有提到 if you require a 'Cloudy' border effect, the FreeTextAnnotation's NormalAppearance generation
function does not contain any cloudy-border generation code.
那么有一个业务需求，需要针对 FreeTextAnnotation 和 PolyLineAnnotation 设置一个 cloudy 的
border，我非常需要您的帮助，请告诉我如何实现这个效果。如果您有任何示例代码或者指导，我将不胜感激。

Dear Patrick,

I am writing to follow up on my previous inquiry regarding the implementation of a cloudy border effect for Annotation
in Datalogics.PDFL library (version 18.35.0).

Given the existence of a business requirement to achieve this visual style, I would greatly appreciate your guidance and
assistance in exploring potential methods or workarounds to achieve a cloudy border effect for both FreeTextAnnotation
SquareAnnotation and PolyLineAnnotation.

If you have any relevant code snippets, documentation, or alternative approaches to realizing this effect, I would be
incredibly grateful for your insights. Your expertise in this matter is highly valued.

Thank you again for your continued support and willingness to assist me in this endeavor. I look forward to your prompt
response and guidance.

Sincerely,
Pengcheng

## 2024-06-11

首先非常感谢您的回信，我已经收到了您提供的Java示例代码，并对其进行了仔细研究。我发现这个示例代码确实对我有所帮助，但是我仍然遇到了一些问题，希望您能帮助我解决。

- **单独设置border和填充的不透明度**
  ：我注意到示例代码中提供了设置border和填充的方法，他们总是一起生效，但是我想要单独设置border和填充的不透明度。请问是我遗漏了什么设置方法还是有其他的方法可以实现？

```java
public static Form generateAppearance(FreeTextAnnotation freeTextAnnot, Font dLFont) {
    // ...
    extgs.setOpacityForStroking(0.2);           // 设置不起作用
    extgs.setOpacityForOtherThanStroking(.5);  // 总是以这一行的设置为准
    // ...
}
```

---

Dear Patrick,

I hope this email finds you well.

I am writing to express my sincere gratitude for your prompt response and the provided Java code examples for
customizing free text annotations in Datalogics.PDFL. I have carefully reviewed the provided code and found it to be
very helpful in guiding my implementation.

However, I have encountered a specific challenge related to setting individual opacities for the border and filling of
free text annotations. While the provided code snippet demonstrates methods for setting the opacity of both border and
filling, it seems that these opacities are applied together rather than independently.

To illustrate, consider the following code excerpt:

```Java
public static Form generateAppearance(FreeTextAnnotation freeTextAnnot, Font dLFont) {
// ...
    extgs.setOpacityForStroking(0.2);           // Setting does not work
    extgs.setOpacityForOtherThanStroking(.5);   // The Settings on this line always prevail
// ...
}
```

In this instance, I would like to set the opacity of the border to 0.2 while maintaining the opacity of the filling at
0.5. However, the current implementation appears to apply the opacity value set in the second line (
extgs.setOpacityForOtherThanStroking(.5)) to both the filling and the border, overriding the intended border opacity of
0.2.

I would greatly appreciate your assistance in determining whether there is an existing method or alternative approach to
achieve individual opacity control for border and filling within the Datalogics.PDFL framework.

Thank you again for your continued support and expertise in this matter. I value your guidance and look forward to your
prompt response.

Sincerely,
Pengcheng

## 2024-06-11

Hello Pengcheng,

I find that I do have a Java sample which might help you get further. Please see the attached
FreeTextAnnotations_PCG3.Java, though it will take me a bit longer to address the specific points you raise, except for
#4.

For #4, as long as there is a Normal Appearance associated with an Annotation, that shall be used, but if the Annotation
is modified, then in principle, it is the modifiers responsibility to re-generate an updated appearance stream for that
annotation, and there are no published rules or guidelines for how to do so. So every PDF processor implements their
appearance generation according to their understanding of how to regenerate the appearance of that annotation, and there
is no way to ensure consistency.

And for your last query, an Annotation is always a PDFDictionary, a Normal Appearance stream is always a Form XObject (
see 8.10.1), and a Form XObject is always a PDFStream. The difference between a PDFStream and a PDFDictionary is that a
PDFStream is essentially a container for a data stream with an associated PDFDictionary.

Regards,
-Patrick

Patrick Gallot
Senior Solutions Architect | Datalogics, Inc.
pgallot@datalogics.com


---

## 2024-06-6

Dear Datalogic Team,

I hope this email finds you well.

I'm writing to follow up on my previous inquiry regarding the customization of free text annotation appearance. While I
appreciate your prompt response and the provided information, I'm still encountering some challenges in implementing the
desired border, padding, and color opacity settings.

To provide more context, I'm working within the Java programming environment and utilizing the datalogics.PDFL library (
version 18.35.0).

Specifically, I'm seeking guidance on the following aspects:

1. Setting Fill Color and Opacity: How can I set the fill color of the text annotation rectangle, including the ability
   to adjust its opacity level?

2. Defining Border Properties: I'd like to define the border's color, width, and style (e.g., cloudy, solid, dashed) for
   the text annotation rectangle.

3. Controlling Text Padding: How can I control the padding around the text within the rectangle?

4. Consistency Across PDF Viewers: I've noticed discrepancies in the visual appearance of the annotations between
   editing and non-editing states, particularly across different PDF viewers (such as Edge browser). Is there a way to
   ensure consistent visual rendering across various viewers?

Furthermore, I would like to clarify the distinction between `annot.getNormalAppearance().getStream().getDict().put()`
and `annot.getPDFDict().put()`. Understanding the appropriate usage of these methods is crucial for effective annotation
customization.

To facilitate understanding, providing code snippets or examples would be immensely helpful.

Thank you for your continued support and assistance in resolving these issues. I look forward to your insightful
guidance.