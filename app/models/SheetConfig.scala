package models

import java.io.File
import java.io.FileInputStream
import java.io.PushbackInputStream
import java.io.FileOutputStream
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.CellStyle
import play.api.Play.current
import org.apache.commons.io.IOUtils

trait SheetConfig {

    val fileName: String
    val file = new File(fileName);
    val fis = new FileInputStream(file);
    val wb = WorkbookFactory.create(fis) 
    fis.close   
    val sheet = wb.getSheetAt(0)


    val checks: List[Check]
    def evaluate: List[Option[Error]] = checks.flatMap(check => check.check(sheet))
    def evaluate2Xls() = {
        val locations = evaluate.flatten.map(error => error.location).flatten

        locations.foreach{loc =>
            val row = sheet.getRow(loc.row)
            var cell = row.getCell(loc.column)
            if (cell == null)
                cell = row.createCell(3)

            val style = wb.createCellStyle
            style.setFillBackgroundColor(IndexedColors.RED.getIndex)
            style.setFillPattern(CellStyle.SPARSE_DOTS)
            cell.setCellStyle(style)          
        }
        
        // Write the output to a file
        val fileOut = new FileOutputStream("results_" + (System.currentTimeMillis / 1000) + "_" + fileName )
        wb.write(fileOut)
    }
}

