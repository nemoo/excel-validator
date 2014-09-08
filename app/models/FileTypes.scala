package models

case class Example1(fileName: String) extends FileType{
    val checks = List(
        TextSet(column = "Name",
            Mandatory,
            "John",
            "Max",
            "Frank")
    )    
}
