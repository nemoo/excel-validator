package models

case class PerformanceHochrechnung(fileName: String) extends FileType{
    val checks = List(
        TextSet(column = "Name",
            Mandatory,
            "John",
            "Max",
            "Frank")
    )    
}
