package daniel.pena.garcia.listadetareas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TareaDAO {
    @Query("SELECT * FROM tareas")
    fun getAll(): List<Tarea>;

    @Query("SELECT * FROM tareas WHERE descripcion = :descripcion")
    fun getTareaByDescripcion(descripcion: String): Tarea;

    @Query("UPDATE tareas SET descripcion = :descripcion WHERE id = :id")
    fun updateTarea(id: Int, descripcion: String);

    @Insert
    fun insertAll(tarea: Tarea);

    @Delete
    fun deleteTarea(tarea: Tarea);
}