package daniel.pena.garcia.listadetareas

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.room.Room

class MainActivity : AppCompatActivity() {

    lateinit var et_tarea: EditText;
    lateinit var btn_agregar: Button;
    lateinit var btn_editar: Button;
    lateinit var listview_tarea: ListView;
    lateinit var lista_tareas: ArrayList<String>;
    lateinit var adaptor: ArrayAdapter<String>;
    lateinit var db: AppDatabase;
    var isEditarTareaEnable: Boolean = false;
    var lastPositionTareaSelected: Int = -1;
    lateinit var lastTareaSelected: Tarea;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        et_tarea = findViewById(R.id.et_tarea);
        btn_agregar = findViewById(R.id.btn_agregar);
        btn_editar= findViewById(R.id.btn_editar);
        listview_tarea = findViewById(R.id.listview_tarea);
        lista_tareas = ArrayList();

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase:: class.java, name = "tareas-db"
        ).allowMainThreadQueries().build();

        cargarTareas();
        this.autoToggleEditButtonColor();

        adaptor = ArrayAdapter(this, android.R.layout.simple_list_item_1, lista_tareas);
        listview_tarea.adapter = adaptor;
        btn_agregar.setOnClickListener{
            //var tarea = et_tarea.getText(); También funciona así
            var descripcion = et_tarea.text.toString();

            if(this.isEditarTareaEnable){
                this.db.tareaDAO().updateTarea(this.lastTareaSelected.id, descripcion);
                this.lista_tareas[this.lastPositionTareaSelected] = descripcion;
                this.adaptor.notifyDataSetChanged();
                this.autoToggleEditButtonState();
                this.et_tarea.setText("");
            } else {

                if (descripcion.isNullOrEmpty()) {
                    Toast.makeText(this, "No se puede agregar una tarea vacía", Toast.LENGTH_SHORT)
                        .show();
                }else {
                    lista_tareas.add(descripcion);
                    val tarea: Tarea = Tarea(descripcion);
                    db.tareaDAO().insertAll(tarea);
                    adaptor.notifyDataSetChanged();
                    et_tarea.setText("");
                }
            }
        }

        btn_editar.setOnClickListener {
            this.autoToggleEditButtonState();
        }

        listview_tarea.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
            this.lastPositionTareaSelected = position;
            var tarea_desc = lista_tareas[this.lastPositionTareaSelected];
            var tareaEncontrada: Tarea = db.tareaDAO().getTareaByDescripcion(tarea_desc);
            if(!isEditarTareaEnable){
                db.tareaDAO().deleteTarea(tareaEncontrada);
                lista_tareas.removeAt(position);
                adaptor.notifyDataSetChanged();
            }else{
                this.lastTareaSelected = tareaEncontrada;
                this.et_tarea.setText(tareaEncontrada.descripcion);
            }
        }


    }

    private fun autoToggleEditButtonState(){
        this.isEditarTareaEnable = !this.isEditarTareaEnable;
        this.autoToggleEditButtonColor();
    }

    private fun autoToggleEditButtonColor(){
        var enableEditColor: Int = Color.rgb(97, 211, 94);
        var disableEditColor: Int = Color.rgb(103, 80, 163);

        if(isEditarTareaEnable){
           this.btn_editar.setBackgroundColor(enableEditColor);
            btn_agregar.setText("Guardar");
        }else{
            this.btn_editar.setBackgroundColor(disableEditColor);
            this.btn_agregar.setText("Agregar");
        }
    }

    private fun cargarTareas(){
        var lista_db = db.tareaDAO().getAll();
        for(tarea in lista_db){
            lista_tareas.add(tarea.descripcion);
        }
    }
}