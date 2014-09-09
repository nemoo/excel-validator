package models

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.File
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.DateUtil

case class Coordinate(row: Int, column: Int)
case class Error (description: String, location: Option[Coordinate], locationString: Option[String]) {
    override def toString =  (locationString match {
        case Some(location) => s"Cell $location: "
        case _ => ""
        }) + description
}

sealed trait Cardinality
case object Mandatory extends Cardinality
case object Optional extends Cardinality


trait Check {
  def check(sheet: Sheet): List[Option[Error]]
}



case class CheckAColumn(sheet: Sheet, name: String) extends Helper {

    // returns row index from column name
    def findRowIndex: Option[Int] = (0 to sheet.getRow(0).getLastCellNum - 1)
        .map{index => 
            val cell = sheet.getRow(0).getCell(index)
            (index, cell.getRichStringCellValue.getString)
        }.find(x => x._2 == name).map(x =>  Some(x._1) ).getOrElse(None)
    
    def checkColumnExists: Option[Error] = findRowIndex match {
      case Some(_) => None
      case _ => Some(Error(s"Could not find column $name.", 
                None,
                None))
    }
      
    def checkAll(f: Cell => Option[Error], mandatory: Cardinality): List[Option[Error]] = {
      checkColumnExists match {  
        case Some(error) => List(Some(error))
        case _ => 
          (1 to sheet.getPhysicalNumberOfRows -1 ).toList.map{ row => 
              mandatory match {
                  case Mandatory => sheet.getRow(row).getCell(findRowIndex.get, Row.CREATE_NULL_AS_BLANK) match {
                      case cell if isNotBlank(cell) => f(cell)
                      case cell => Some(Error(s"Cell must not be empty.", 
                                Some(Coordinate(cell.getRowIndex, cell.getColumnIndex)),
                                Some(renderCellCoordinates(cell))))
                  }
                  case Optional =>  val cell = sheet.getRow(row).getCell(findRowIndex.get)
                      cell.getCellType match {
                          case 3 => None   //blank cells are allowed here
                          case _ => f(cell)
                  }
              }
              
          }
      }    
    }   
}

trait CellCheck{
    def checkFunction: Cell => Option[Error]
}

trait Helper{
    def renderCellCoordinates(cell: Cell) = {
        import org.apache.poi.ss.util.CellReference
        val cellRef = new CellReference(cell)
        cellRef.formatAsString
    }

    def isNotBlank(cell: Cell) = cell.getCellType != Cell.CELL_TYPE_BLANK 

    def renderCell(cell: Cell): String = if (cell == null){
            "null"
        } else cell.getCellType match {
            case Cell.CELL_TYPE_STRING => cell.getRichStringCellValue().getString()
            case Cell.CELL_TYPE_NUMERIC => {
                if (DateUtil.isCellDateFormatted(cell)) {
                    cell.getDateCellValue.toString
                } else {
                    cell.getNumericCellValue.toString
                }
            }
            case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue.toString
            case Cell.CELL_TYPE_FORMULA => cell.getCellFormula.toString
        }
}





