package ruben.gutierrez.myfeelings

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ruben.gutierrez.utilities.CustomBarDrawable
import ruben.gutierrez.utilities.JSONFile
import ruben.gutierrez.utilities.customCircleDrawable
import ruben.gutierrez.utilities.emociones
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    // Variables de vistas
    private lateinit var icon: ImageView
    private lateinit var graph: View
    private lateinit var graphVeryHappy: View
    private lateinit var graphHappy: View
    private lateinit var graphNeutral: View
    private lateinit var graphSad: View
    private lateinit var graphVerySad: View
    private lateinit var guardarButton: Button
    private lateinit var veryHappyButton: ImageButton

    private lateinit var happyButton: ImageButton
    private lateinit var neutralButton: ImageButton
    private lateinit var sadButton: ImageButton
    private lateinit var verySadButton: ImageButton

    // Variables lógicas
    private var jsonFile: JSONFile? = null
    private var veryHappy = 0.0F
    private var happy = 0.0F
    private var neutral = 0.0F
    private var sad = 0.0F
    private var verysad = 0.0F

    private var data: Boolean = false
    private var lista = ArrayList<emociones>()

    // Validación anti-spam (evita doble click muy rápido)
    private var lastClickTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        icon = findViewById(R.id.icon)
        graph = findViewById(R.id.graph)
        graphVeryHappy = findViewById(R.id.graphVeryHappy)
        graphHappy = findViewById(R.id.graphHappy)
        graphNeutral = findViewById(R.id.graphNeutral)
        graphSad = findViewById(R.id.graphSad)
        graphVerySad = findViewById(R.id.graphVerySad)
        guardarButton = findViewById(R.id.guardarButton)
        veryHappyButton = findViewById(R.id.veryHappyButton)
        happyButton = findViewById(R.id.happyButton)
        neutralButton = findViewById(R.id.neutralButton)
        sadButton = findViewById(R.id.sadButton)
        verySadButton = findViewById(R.id.verySadButton)

        jsonFile = JSONFile()
        fetchingData()

        if (!data) {
            val emocionesVacias = ArrayList<emociones>()
            val fondo = customCircleDrawable(this, emocionesVacias)
            graph.background = fondo

            graphVeryHappy.background = CustomBarDrawable(this, emociones("Muy feliz", 0.0F, R.color.mustard, veryHappy))
            graphHappy.background = CustomBarDrawable(this, emociones("Feliz", 0.0F, R.color.orange, happy))
            graphNeutral.background = CustomBarDrawable(this, emociones("Neutral", 0.0F, R.color.greenie, neutral))
            graphSad.background = CustomBarDrawable(this, emociones("Triste", 0.0F, R.color.blue, sad))
            graphVerySad.background = CustomBarDrawable(this, emociones("Muy Triste", 0.0F, R.color.deepBlue, verysad))
        } else {
            actualizarGrafica()
            iconoMayoria()
        }

        guardarButton.setOnClickListener { guardar() }

        veryHappyButton.setOnClickListener { sumarEmocion(::sumarVeryHappy) }
        happyButton.setOnClickListener { sumarEmocion(::sumarHappy) }
        neutralButton.setOnClickListener { sumarEmocion(::sumarNeutral) }
        sadButton.setOnClickListener { sumarEmocion(::sumarSad) }
        verySadButton.setOnClickListener { sumarEmocion(::sumarVerySad) }
    }

    // -------- VALIDACIONES PARA SUMAS --------

    private fun sumarEmocion(accion: () -> Unit) {
        if (System.currentTimeMillis() - lastClickTime < 250) return
        lastClickTime = System.currentTimeMillis()

        accion()
        iconoMayoria()
        actualizarGrafica()
    }

    private fun sumarVeryHappy() {
        if (veryHappy >= 999) {
            Toast.makeText(this, "Límite alcanzado", Toast.LENGTH_SHORT).show()
            return
        }
        veryHappy++
    }

    private fun sumarHappy() {
        if (happy >= 999) {
            Toast.makeText(this, "Límite alcanzado", Toast.LENGTH_SHORT).show()
            return
        }
        happy++
    }

    private fun sumarNeutral() {
        if (neutral >= 999) {
            Toast.makeText(this, "Límite alcanzado", Toast.LENGTH_SHORT).show()
            return
        }
        neutral++
    }

    private fun sumarSad() {
        if (sad >= 999) {
            Toast.makeText(this, "Límite alcanzado", Toast.LENGTH_SHORT).show()
            return
        }
        sad++
    }

    private fun sumarVerySad() {
        if (verysad >= 999) {
            Toast.makeText(this, "Límite alcanzado", Toast.LENGTH_SHORT).show()
            return
        }
        verysad++
    }

    // ------------------------------------------

    private fun fetchingData() {
        try {
            val json: String = jsonFile?.getData(this) ?: ""
            if (json.isNotEmpty()) {
                data = true
                val jsonArray = JSONArray(json)
                lista = parseJson(jsonArray)

                for (i in lista) {
                    val valor = max(0f, i.total) // VALIDACIÓN: evita negativos

                    when (i.nombre) {
                        "Muy feliz" -> veryHappy = valor
                        "Feliz" -> happy = valor
                        "Neutral" -> neutral = valor
                        "Triste" -> sad = valor
                        "Muy triste" -> verysad = valor
                    }
                }
            } else {
                data = false
            }
        } catch (e: JSONException) {
            data = false
            Toast.makeText(this, "Error en archivo: se reiniciarán los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun iconoMayoria() {
        val drawable = when {
            happy > veryHappy && happy > neutral && happy > sad && happy > verysad ->
                ContextCompat.getDrawable(this, R.drawable.ic_happy)
            veryHappy > happy && veryHappy > neutral && veryHappy > sad && veryHappy > verysad ->
                ContextCompat.getDrawable(this, R.drawable.ic_veryhappy)
            neutral > happy && neutral > veryHappy && neutral > sad && neutral > verysad ->
                ContextCompat.getDrawable(this, R.drawable.ic_neutral)
            sad > happy && sad > veryHappy && sad > neutral && sad > verysad ->
                ContextCompat.getDrawable(this, R.drawable.ic_sad)
            verysad > happy && verysad > veryHappy && verysad > neutral && verysad > sad ->
                ContextCompat.getDrawable(this, R.drawable.ic_verysad)
            else -> null
        }

        drawable?.let { icon.setImageDrawable(it) }
    }

    private fun actualizarGrafica() {
        val total = veryHappy + happy + neutral + sad + verysad

        if (total == 0f) {
            Log.w("GRAPH", "No hay datos para graficar")
            return
        }

        val pVH = (veryHappy * 100 / total)
        val pH = (happy * 100 / total)
        val pN = (neutral * 100 / total)
        val pS = (sad * 100 / total)
        val pVS = (verysad * 100 / total)

        lista.clear()
        lista.add(emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        lista.add(emociones("Feliz", pH, R.color.orange, happy))
        lista.add(emociones("Neutral", pN, R.color.greenie, neutral))
        lista.add(emociones("Triste", pS, R.color.blue, sad))
        lista.add(emociones("Muy Triste", pVS, R.color.deepBlue, verysad))

        val fondo = customCircleDrawable(this, lista)
        graph.background = fondo

        graphVeryHappy.background = CustomBarDrawable(this, lista[0])
        graphHappy.background = CustomBarDrawable(this, lista[1])
        graphNeutral.background = CustomBarDrawable(this, lista[2])
        graphSad.background = CustomBarDrawable(this, lista[3])
        graphVerySad.background = CustomBarDrawable(this, lista[4])
    }

    private fun parseJson(jsonArray: JSONArray): ArrayList<emociones> {
        val lista = ArrayList<emociones>()
        for (i in 0 until jsonArray.length()) {
            try {
                val obj = jsonArray.getJSONObject(i)
                lista.add(
                    emociones(
                        obj.getString("nombre"),
                        obj.getDouble("porcentaje").toFloat(),
                        obj.getInt("color"),
                        obj.getDouble("total").toFloat()
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return lista
    }

    private fun guardar() {
        val total = veryHappy + happy + neutral + sad + verysad

        if (total == 0f) {
            Toast.makeText(this, "No has seleccionado ninguna emoción", Toast.LENGTH_SHORT).show()
            return
        }

        val jsonArray = JSONArray()
        for ((index, i) in lista.withIndex()) {
            val j = JSONObject()
            j.put("nombre", i.nombre)
            j.put("porcentaje", i.porcentaje)
            j.put("color", i.color)
            j.put("total", max(0f, i.total)) // Validación anti-negativos
            jsonArray.put(index, j)
        }

        jsonFile?.saveData(this, jsonArray.toString())
        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }
}
