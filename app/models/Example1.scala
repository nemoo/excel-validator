package models

case class Example1(fileName: String) extends SheetConfig{
    val checks = List(
        TextSet(column = "Name",
            Mandatory,
            "John",
            "Max",
            "Frank")
    )    
}
