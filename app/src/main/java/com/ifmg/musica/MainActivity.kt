package com.ifmg.musica

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var iTunesAdapter: iTunesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        recyclerView = findViewById(R.id.recyclerView)

        iTunesAdapter = iTunesAdapter(emptyList())
        recyclerView.adapter = iTunesAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchButton.setOnClickListener {
            val artistName = searchEditText.text.toString()
            if (artistName.isNotEmpty()) {
                searchiTunes(artistName)
            }
        }
    }

    private fun searchiTunes(artistName: String) {
        val url =
            "https://itunes.apple.com/search?term=${artistName.replace(" ", "+")}&entity=musicVideo"

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    withContext(Dispatchers.Main) {
                        handleiTunesResponse(response.toString())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleiTunesResponse(response: String) {
        val iTunesList = parseiTunesResponse(response)
        iTunesAdapter = iTunesAdapter(iTunesList)
        recyclerView.adapter = iTunesAdapter
    }

    private fun parseiTunesResponse(response: String): List<iTunesItem> {
        val iTunesList = mutableListOf<iTunesItem>()

        try {
            val jsonObject = JSONObject(response)
            val resultsArray = jsonObject.getJSONArray("results")

            for (i in 0 until resultsArray.length()) {
                val itemObject = resultsArray.getJSONObject(i)
                val name = itemObject.getString("trackName")
                val kind = itemObject.getString("kind")
                val artist = itemObject.getString("artistName")

                // Verifica se a chave "collectionName" está presente antes de tentar acessar o valor
                val collectionName: String? = if (itemObject.has("collectionName")) {
                    itemObject.getString("collectionName")
                } else {
                    // Trate o caso em que a chave não está presente, talvez atribuindo um valor padrão ou lidando de outra forma.
                    Log.w("iTunes", "Chave 'collectionName' não encontrada para o item $i")
                    null
                }

                val previewUrl = itemObject.getString("previewUrl")

                val iTunesItem = iTunesItem(name, kind, artist, collectionName, previewUrl)
                iTunesList.add(iTunesItem)
            }
        } catch (e: JSONException) {
            // Trate a exceção conforme necessário
            e.printStackTrace()
        }

        return iTunesList
    }
}



