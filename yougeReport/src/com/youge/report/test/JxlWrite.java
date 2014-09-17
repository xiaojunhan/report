package com.youge.report.test;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 写入excel
 * @author Michael sun
 */
public class JxlWrite {

    /**
     * 写入 excel 文件
     * @param filePath
     * @throws Exception
     */
    private void writeExcel(String filePath) throws Exception {
        OutputStream os = null;
        try {

            // 构建Workbook对象
            os = new FileOutputStream(filePath);
            WritableWorkbook wwb = Workbook.createWorkbook(os);

            // 构建Excel sheet
            WritableSheet sheet = wwb.createSheet("test write sheet", 0);

            // 设置标题格式
            WritableFont wfTitle = new jxl.write.WritableFont(
                    WritableFont.ARIAL, 18, WritableFont.BOLD, true);
            WritableCellFormat wcfTitle = new WritableCellFormat(wfTitle);
            // 设置水平对齐方式
            wcfTitle.setAlignment(Alignment.CENTRE);
            // 设置垂直对齐方式
            wcfTitle.setVerticalAlignment(VerticalAlignment.CENTRE);
            // 设置是否自动换行
            wcfTitle.setWrap(true);

            // 合并A1->C2
            sheet.mergeCells(0, 0, 2, 1);
            Label titleCell = new Label(0, 0, "Title Cell ", wcfTitle);
            sheet.addCell(titleCell);

            WritableFont wf = new WritableFont(WritableFont.ARIAL, 10,
                    WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
                    Colour.BLUE);
            WritableCellFormat wcf = new WritableCellFormat(wf);

            // A3
            Label labelCell = new Label(0, 2, "Label Cell ");
            sheet.addCell(labelCell);
            // B3
            Label labelCellFmt = new Label(1, 2,
                    "Label Cell with WritableCellFormat ", wcf);
            sheet.addCell(labelCellFmt);

            // A4 添加jxl.write.Number对象
            jxl.write.Number labelN = new jxl.write.Number(0, 3, 3.1415926);
            sheet.addCell(labelN);
            // B4 添加Number对象 jxl.write.NumberFormat
            NumberFormat nf = new NumberFormat("#.##");
            WritableCellFormat wcfN = new WritableCellFormat(nf);
            jxl.write.Number labelNF = new jxl.write.Number(1, 3, 3.1415926,
                    wcfN);
            sheet.addCell(labelNF);

            // A5 添加jxl.write.Boolean对象
            jxl.write.Boolean labelB = new jxl.write.Boolean(0, 4, true);
            sheet.addCell(labelB);

            // A6 添加 jxl.write.DateTime对象
            jxl.write.DateTime labelDT = new jxl.write.DateTime(0, 5,
                    new Date());
            sheet.addCell(labelDT);
            // B6 添加DateTime对象 jxl.write.DateFormat
            jxl.write.DateFormat df = new jxl.write.DateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            WritableCellFormat wcfDF = new WritableCellFormat(df);
            jxl.write.DateTime labelDTF = new jxl.write.DateTime(1, 5,
                    new Date(), wcfDF);
            sheet.addCell(labelDTF);
            //先调用write();再调用close();
            wwb.write();
            wwb.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != os) {
                os.close();
            }
        }

    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String filePath = "/tmp/testjxlwrite.xls";
        JxlWrite jxlwrite = new JxlWrite();
        jxlwrite.writeExcel(filePath);
    }

}