using System;
using System.Collections.Generic;
using Datalogics.PDFL;
using Microsoft.SqlServer.Server;

/*
 * 
 * This sample demonstrates rotating rectangular Square Annotations about a cemter, and applying Opacity to a Square Annotation whose Norma Appearance has already been generated.
 * 
  * Copyright (c) 2007-2023, Datalogics, Inc. All rights reserved.
 *
 */
namespace Annotations
{
    class Annotations
    {
        public struct ContentStackItem
        {
            public Content content;
            public int PrevQueueNum;
        }

        static void RotateNormalAppearance(Document doc, Page page, Annotation annot, Point center, double degree)
        {
            Matrix m = new Matrix()
            .Translate(center.H, center.V)
            .Rotate(degree)
            .Translate(-center.H, -center.V);
            // HELP: this transformation matrix does generate the desired shape of rotated rectangle
            // It turns the shape into a quadrilateral

            // reflect optional transformation matrix to appearance PDF Dictionary
            var matrix = new PDFArray(doc, false);
            matrix.Put(0, new PDFReal(m.A, doc, false));
            matrix.Put(1, new PDFReal(m.B, doc, false));
            matrix.Put(2, new PDFReal(m.C, doc, false));
            matrix.Put(3, new PDFReal(m.D, doc, false));
            matrix.Put(4, new PDFReal(m.H, doc, false));
            matrix.Put(5, new PDFReal(m.V, doc, false));
            annot.NormalAppearance.Stream.Dict.Put("Matrix", matrix);

            //Adjust the Annotation's Rectangle so that it fitst the rotated appearance without it scaling down to fit within the Rect.
            var annotRect = annot.Rect;
            var RectPoints = new Point[]{ new Point(annotRect.Left, annotRect.Bottom),
                                            new Point(annotRect.Right, annotRect.Bottom),
                                            new Point(annotRect.Right, annotRect.Top),
                                            new Point(annotRect.Left, annotRect.Top),
            };
            var TransPoints = new Point[4];
            for(int i = 0; i < RectPoints.Length;i++)
            {
                TransPoints[i] = RectPoints[i].Transform(m);
            }
            var minX= annotRect.Left;
            var minY= annotRect.Bottom;
            var maxX = annotRect.Right;
            var maxY= annotRect.Top;

            for (int i = 0; i < TransPoints.Length; i++)
            {
                minX = Math.Min(minX, TransPoints[i].H);
                maxX = Math.Max(maxX, TransPoints[i].H);
                minY = Math.Min(minY, TransPoints[i].V);
                maxX = Math.Max(maxX, TransPoints[i].V);
            }
            if (degree == 90.0 || degree == 270.0)
            {
                minX = 0.5 * (annotRect.Left + annotRect.Right - annotRect.Top + annotRect.Bottom);
                maxX = 0.5 * (annotRect.Left + annotRect.Right + annotRect.Top - annotRect.Bottom);
                minY = 0.5 * (annotRect.Bottom + annotRect.Top - annotRect.Right + annotRect.Left);
                maxY = 0.5 * (annotRect.Bottom + annotRect.Top + annotRect.Right - annotRect.Left);

            }
            annot.Rect = new Rect(minX, minY, maxX, maxY);
        }

        static void OpaqueNormalAppearance2(Annotation annot)
        {

            if (annot.Opacity != 0)
            {
                using var normApForm = annot.NormalAppearance;
                if (normApForm != null)
                {
                    using var apContent = normApForm.Content;
                    using var newContent = new Content();
                    bool bModified = false;

                    for (int i = 0; i < apContent.NumElements; i++)
                    {
                        using var elem = apContent.GetElement(i);
                        if (elem is Path)
                        {
                            var path = elem as Path;

                            if (path.GraphicState.ExtendedGraphicState != null)
                            {
                                path.GraphicState.ExtendedGraphicState.OpacityForStroking = annot.Opacity;
                                path.GraphicState.ExtendedGraphicState.OpacityForOtherThanStroking = annot.Opacity;
                                newContent.AddElement(path);
                                bModified = true;
                            }
                            else
                            {
                                using var gs = new GraphicState()
                                {
                                    StrokeColor = path.GraphicState.StrokeColor,
                                    FillColor = path.GraphicState.FillColor,
                                    ExtendedGraphicState = new ExtendedGraphicState() { OpacityForOtherThanStroking = annot.Opacity, OpacityForStroking = annot.Opacity },
                                    LineCap = path.GraphicState.LineCap,
                                    LineJoin = path.GraphicState.LineJoin,
                                    LineFlatness = path.GraphicState.LineFlatness,
                                    MiterLimit = path.GraphicState.MiterLimit,
                                };

                                path.GraphicState = gs;
                                newContent.AddElement(path);
                                bModified = true;
                            }
                        }
                        else
                        {
                            newContent.AddElement(elem);
                        }
                    }
                    if (bModified)
                    {
                        using (var newFrm = new Form(newContent))
                            annot.NormalAppearance = newFrm;
                    }
                }
            }
        }


        static void OpaqueNormalAppearance3(Annotation annot)
        {

            if (annot.Opacity != 0)
            {
                using var normApForm = annot.NormalAppearance;
                if (normApForm != null)
                {
                    var ContentStack = new Stack<ContentStackItem>();
                    int currentItem = 0;
                    var currentContent = normApForm.Content;
                    bool bModified = false;

                    do
                    {
                        if (currentItem == currentContent.NumElements && ContentStack.Count > 0)
                        {
                            ContentStackItem si = ContentStack.Pop();
                            var modContent = currentContent;
                            currentContent = si.content;
                            var olditem = currentContent.GetElement(si.PrevQueueNum);
                            if(olditem is Form)
                            {
                                using var moditem = olditem as Form;
                                moditem.Content = modContent;
                                currentContent.AddElement(moditem, si.PrevQueueNum);
                                currentContent.RemoveElement(si.PrevQueueNum);
                            }
                            else if(olditem is Container)
                            {
                                using var moditem = olditem as Container;
                                moditem.Content = modContent;
                                currentContent.AddElement(moditem, si.PrevQueueNum);
                                currentContent.RemoveElement(si.PrevQueueNum);
                            }

                            currentItem = si.PrevQueueNum + 1;
                        }

                        if (currentItem == currentContent.NumElements)
                            continue;

                        using (var elem = currentContent.GetElement(currentItem))
                        {
                            if (elem is Form)
                            {
                                var si = new ContentStackItem() { content = currentContent, PrevQueueNum = currentItem };
                                ContentStack.Push(si);
                                currentItem = 0;
                                currentContent = (elem as Form).Content;
                                continue;
                            }
                            else if (elem is Container)
                            {
                                var si = new ContentStackItem() { content = currentContent, PrevQueueNum = currentItem };
                                ContentStack.Push(si);
                                currentItem = 0;
                                currentContent = (elem as Container).Content;
                                continue;

                            }
                            else if (elem is Path)
                            {
                                var path = elem as Path;

                                if (path.GraphicState.ExtendedGraphicState != null)
                                {
                                    path.GraphicState.ExtendedGraphicState.OpacityForStroking = annot.Opacity;
                                    path.GraphicState.ExtendedGraphicState.OpacityForOtherThanStroking = annot.Opacity;

                                    bModified = true;
                                }
                                else
                                {
                                    using var gs = new GraphicState()
                                    {
                                        StrokeColor = path.GraphicState.StrokeColor,
                                        FillColor = path.GraphicState.FillColor,
                                        ExtendedGraphicState = new ExtendedGraphicState() { OpacityForOtherThanStroking = annot.Opacity, OpacityForStroking = annot.Opacity },
                                        LineCap = path.GraphicState.LineCap,
                                        LineJoin = path.GraphicState.LineJoin,
                                        LineFlatness = path.GraphicState.LineFlatness,
                                        MiterLimit = path.GraphicState.MiterLimit,
                                    };
                                    path.GraphicState = gs;
                                    bModified = true;
                                }
                                currentContent.AddElement(path, currentItem);
                                currentContent.RemoveElement(currentItem);
                            }
                            else if (elem is Text)
                            {
                                // TODO: more or less the same as:  elem as Path,
                                // probably do the same for Image elements as well.
                            }
                        }
                        ++currentItem;


                    } while (currentItem < currentContent.NumElements || ContentStack.Count > 0);
                    
                    using (var newFrm = new Form(currentContent))
                        annot.NormalAppearance = newFrm;

                    currentContent.Dispose();
                }
            }
        }

        static void Main(string[] args)
        {
            Console.WriteLine("Annotations Sample:");

            using (Library lib = new Library())
            {
                Console.WriteLine("Initialized the library.");
                var fileName = "../annotation-out3.pdf";
                using var doc = new Document();
                var pgRect = new Rect(0, 0, 612, 792);
                var center = new Point(306, 386);
                var rect = new Rect(center.H - 100, center.V - 50, center.H + 100, center.V + 50);

                var rect2 = new Rect(400, 600, 500, 650);

                Double[] domain = { 0.0, 1.0 };
                Double[] C0 = { 0.15 };
                Double[] C1 = { 0.85 };
                ExponentialFunction f = new ExponentialFunction(domain, 1, C0, C1, 1);

                Point[] coords = {new Point(rect2.Left-36, rect2.Bottom-36),
                                  new Point(rect2.Right+36,rect2.Top+36) };

                Function[] functionList = { f };
                using var asp = new AxialShadingPattern(ColorSpace.DeviceGray, coords, functionList);
                using var fc = new Color(asp);
                var gs = new GraphicState
                {
                    FillColor = fc,
                };

                int delta = 30;
                for (int degree = 0; Math.Abs(degree) < 360; degree += delta)
                {
                    using var page = doc.CreatePage(Document.LastPage, pgRect);
                    using (Path path = new Path(gs))
                    {
                        path.PaintOp = PathPaintOpFlags.Stroke | PathPaintOpFlags.Fill;
                        path.AddRect(coords[0], coords[1].H - coords[0].H, coords[1].V - coords[0].V);
                        page.Content.AddElement(path);
                    }
                    page.UpdateContent();

                    var opacity2 = Math.Abs((360.0 - degree) / 360.0); // this applies to both interior and border

                    var annot2 = new SquareAnnotation(page, rect2)
                    {
                        Width = 10,
                        Color = new Color(1, 1, 0),
                        InteriorColor = new Color(0, 1, 1),
                        Opacity = opacity2, // this applies to both interior and border
                    };
                    annot2.NormalAppearance = annot2.GenerateAppearance(); // Opacity setting not used when generating appearance (as of 18.0.4+P2c)

                    OpaqueNormalAppearance3(annot2);

                    // Attempt to set rotation, stroke and fill opacities of an annotation
                    // Did not achieve the desired result
                    var annot = new SquareAnnotation(page, rect)
                    {
                        Width = 10,
                        Color = new Color(1, 0, 0),
                        InteriorColor = new Color(0, 0, 1),
                    };
                    annot.NormalAppearance = annot.GenerateAppearance();

                    // custom functions to set rotation and opacities of an annotation
                    RotateNormalAppearance(doc, page, annot, center, degree);
                }
                doc.Save(SaveFlags.Full, fileName);
            }
        }
    }
}


        static void TranslateAnnotation(Annotation annotation, Document doc, double xMove = 0, double yMove = 0, double rotation = 0, double xScale = 1, double yScale = 1)
        {
            Matrix rotateMatrix = new Matrix().Rotate(rotation);

            Form form = annotation.GenerateAppearance();
            annotation.NormalAppearance = form;
            var oldRect = annotation.Rect;

            var transformMatrix = new Matrix()
                .Translate(xMove, yMove)
                .Translate((oldRect.Right + oldRect.Left) / 2, (oldRect.Top + oldRect.Bottom) / 2)
                .Rotate(rotation)
                .Scale(xScale, yScale)
                .Translate(-(oldRect.Right + oldRect.Left) / 2, -(oldRect.Top + oldRect.Bottom) / 2);

            var bl = (new Point(annotation.Rect.LLx, annotation.Rect.LLy)).Transform(transformMatrix);
            var tl = (new Point(annotation.Rect.LLx, annotation.Rect.URy)).Transform(transformMatrix);
            var br = (new Point(annotation.Rect.URx, annotation.Rect.LLy)).Transform(transformMatrix);
            var tr = (new Point(annotation.Rect.URx, annotation.Rect.URy)).Transform(transformMatrix);
            var minx = Math.Min(Math.Min(bl.H, br.H), Math.Min(tl.H, tr.H));
            var miny = Math.Min(Math.Min(bl.V, br.V), Math.Min(tl.V, tr.V));
            var maxx = Math.Max(Math.Max(bl.H, br.H), Math.Max(tl.H, tr.H));
            var maxy = Math.Max(Math.Max(bl.V, br.V), Math.Max(tl.V, tr.V));
            annotation.Rect = new Rect(minx, miny, maxx, maxy);

            PDFArray pdfArray = new PDFArray(doc, false);
            pdfArray.Put(0, new PDFReal(rotateMatrix.A, doc, false));
            pdfArray.Put(1, new PDFReal(rotateMatrix.B, doc, false));
            pdfArray.Put(2, new PDFReal(rotateMatrix.C, doc, false));
            pdfArray.Put(3, new PDFReal(rotateMatrix.D, doc, false));
            pdfArray.Put(4, new PDFReal(rotateMatrix.H, doc, false));
            pdfArray.Put(5, new PDFReal(rotateMatrix.V, doc, false));
            annotation.NormalAppearance.Stream.Dict.Put("Matrix", pdfArray);
        }