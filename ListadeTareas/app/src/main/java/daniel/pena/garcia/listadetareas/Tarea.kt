package daniel.pena.garcia.listadetareas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class Tarea (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var descripcion: String,
){
    constructor(descripcion: String) : this(0, descripcion)

}