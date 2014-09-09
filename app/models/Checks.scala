package models

import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.DateUtil

case class TextSet(column: String, mandatory: Cardinality, items: String*) extends Check {
  def check(sheet: Sheet): List[Option[Error]] = 
    CheckAColumn(sheet, column).checkAll( TextSetCellCheck(items.toSet).checkFunction, mandatory)
}

case class NumberRange(column: String, mandatory: Cardinality, von: Integer, bis: Integer) extends Check {
  def check(sheet: Sheet): List[Option[Error]] = 
    CheckAColumn(sheet, column).checkAll( NumberRangeCellCheck(von, bis).checkFunction, mandatory)
}

case class Number(column: String, mandatory: Cardinality ) extends Check {
  def check(sheet: Sheet): List[Option[Error]] = 
    CheckAColumn(sheet, column).checkAll( NumberCellCheck.checkFunction, mandatory)
}

case class TextSetCellCheck(items: Set[String]) extends CellCheck with Helper{
    def checkFunction: Cell => Option[Error] = { cell =>
        if (cell.getCellType == Cell.CELL_TYPE_STRING && items.exists(item => 
                item == cell.getRichStringCellValue.getString)) {    
            None
        } else
            Some(Error(s"${renderCell(cell)} is not one of " + items.mkString(", ") + ".", 
                  Some(Coordinate(cell.getRowIndex, cell.getColumnIndex)),
                  Some(renderCellCoordinates(cell))))
    }            
}

case class NumberRangeCellCheck(von: Integer, bis: Integer) extends CellCheck with Helper{
    def checkFunction: Cell => Option[Error] = { cell =>
        cell.getCellType match {
            case Cell.CELL_TYPE_NUMERIC => {
                    val value = cell.getNumericCellValue

                    ((value >= von) && (value <= bis)) match {
                        case true => None
                        case false => Some(Error(s"${renderCell(cell)} is not between $von and $bis.",
                                            Some(Coordinate(cell.getRowIndex, cell.getColumnIndex)),
                                            Some(renderCellCoordinates(cell))))
                    }
                }
            case _ => Some(Error(s"${renderCell(cell)} is not between $von and $bis.",
                            Some(Coordinate(cell.getRowIndex, cell.getColumnIndex)),
                            Some(renderCellCoordinates(cell))))
        }    
    }            
}

object NumberCellCheck extends CellCheck with Helper{
    def checkFunction: Cell => Option[Error] = { cell =>
        cell.getCellType match {
            case Cell.CELL_TYPE_NUMERIC => None
            case _ => Some(Error(s"${renderCell(cell)} must be a number.", 
                            Some(Coordinate(cell.getRowIndex, cell.getColumnIndex)),
                            Some(renderCellCoordinates(cell))))
        }    
    }            
}