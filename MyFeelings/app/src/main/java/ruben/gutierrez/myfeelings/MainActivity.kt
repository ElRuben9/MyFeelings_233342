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

        veryHappyButton.setOnClickListener {
            veryHappy++
            iconoMayoria()
            actualizarGrafica()
        }

        happyButton.setOnClickListener {
            happy++
            iconoMayoria()
            actualizarGrafica()
        }
        neutralButton.setOnClickListener {
            neutral++
            iconoMayoria()
            actualizarGrafica()
        }
        sadButton.setOnClickListener {
            sad++
            iconoMayoria()
            actualizarGrafica()
        }
        verySadButton.setOnClickListener {
            verysad++
            iconoMayoria()
            actualizarGrafica()
        }

    }

    private fun fetchingData() {
        try {
            val json: String = jsonFile?.getData(this) ?: ""
            if (json.isNotEmpty()) {
                data = true
                val jsonArray = JSONArray(json)
                lista = parseJson(jsonArray)

                for (i in lista) {
                    when (i.nombre) {
                        "Muy feliz" -> veryHappy = i.total
                        "Feliz" -> happy = i.total
                        "Neutral" -> neutral = i.total
                        "Triste" -> sad = i.total
                        "Muy triste" -> verysad = i.total
                    }
                }
            } else {
                data = false
            }
        } catch (e: JSONException) {
            e.printStackTrace()
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
        val total = veryHappy + happy + neutral + verysad + sad
        if (total == 0f) return

        val pVH = (veryHappy * 100 / total)
        val pH = (happy * 100 / total)
        val pN = (neutral * 100 / total)
        val pS = (sad * 100 / total)
        val pVS = (verysad * 100 / total)

Log.d("porcentajes","very happy"+pVH)
        Log.d("porcentajes","happy"+pH)
        Log.d("porcentajes","neutral"+pN)
        Log.d("porcentajes","sad"+pS)
        Log.d("porcentajes","very sad"+pVS)


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
        val jsonArray = JSONArray()
        for ((index, i) in lista.withIndex()) {
            val j = JSONObject()
            j.put("nombre", i.nombre)
            j.put("porcentaje", i.porcentaje)
            j.put("color", i.color)
            j.put("total", i.total)
            jsonArray.put(index, j)
        }
        jsonFile?.saveData(this, jsonArray.toString())
        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }
}
