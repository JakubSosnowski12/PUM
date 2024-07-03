package com.example.hangman_game

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.hangman_game.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var licznikBledow = 0
    private var flagaKoniecGry = true
    private lateinit var slowo: String
    private lateinit var celSlowo: String
    private lateinit var indeksy: MutableList<Int>
    private var losowyNumer = 0
    private val listaSlow = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        wczytajSlowaZPliku()
        rozpocznijGre()
        for (litera in 'a'..'z') {
            val przyciskId = resources.getIdentifier(litera.toString(), "id", packageName)
            val przycisk = findViewById<View>(przyciskId)
            przycisk.setOnClickListener {
                indeksy = znajdzIndeksy(binding, slowo, litera)
                celSlowo = wyswietlLitery(indeksy, celSlowo, litera)
                przycisk.visibility = View.GONE
            }
        }
    }

    private fun wczytajSlowaZPliku() {
        val inputStream = assets.open("words.txt")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var linia: String?
        while (bufferedReader.readLine().also { linia = it } != null) {
            listaSlow.add(linia!!)
        }
        bufferedReader.close()
    }

    private fun rozpocznijGre() {
        przywrocPrzyciski()
        licznikBledow = 0
        binding.hangman.setImageResource(0)
        losowyNumer = Random.nextInt(0, listaSlow.size)
        slowo = listaSlow[losowyNumer]
        stworzPusteMiejsca(slowo.length, binding)
        celSlowo = binding.word.text.toString()
    }

    private fun przywrocPrzyciski() {
        for (litera in 'a'..'z') {
            val przyciskId = resources.getIdentifier(litera.toString(), "id", packageName)
            val przycisk = findViewById<View>(przyciskId)
            przycisk.visibility = View.VISIBLE
        }
    }

    private fun stworzPusteMiejsca(rozmiar: Int, binding: ActivityMainBinding) {
        binding.word.text = "_ ".repeat(rozmiar)
    }

    private fun znajdzIndeksy(binding: ActivityMainBinding, slowo: String, litera: Char): MutableList<Int> {
        val indeksy = mutableListOf<Int>()

        slowo.mapIndexed { index, char ->
            if (char == litera) {
                indeksy.add(index)
            }
        }
        if (indeksy.isEmpty()) {
            licznikBledow++
            zaktualizujObraz(binding, licznikBledow)
            if (licznikBledow == 10) {
                flagaKoniecGry = false
                pokazDialogKoniecGry(flagaKoniecGry)
            }
        }
        return indeksy
    }

    private fun zaktualizujObraz(binding: ActivityMainBinding, licznikBledow: Int) {
        val nazwaObrazu = "hangman_$licznikBledow"
        val idZasobuObrazu = resources.getIdentifier(nazwaObrazu, "drawable", packageName)
        binding.hangman.setImageResource(idZasobuObrazu)
    }

    private fun wyswietlLitery(indeksy: MutableList<Int>, celSlowo: String, litera: Char): String {
        val stringBuilder = StringBuilder(celSlowo)
        if (indeksy.isNotEmpty()) {
            indeksy.forEach { index ->
                stringBuilder.setCharAt(index * 2, litera.uppercaseChar())
            }
            binding.word.text = stringBuilder.toString()
        }

        if (!stringBuilder.contains("_")) {
            flagaKoniecGry = true
            pokazDialogKoniecGry(flagaKoniecGry)
        }

        return stringBuilder.toString()
    }

    private fun pokazDialogKoniecGry(flagaKoniecGry: Boolean) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)

        if (flagaKoniecGry) {
            builder.setTitle("WYGRAŁEŚ")
            builder.setMessage("Gratulacje! Wygrałeś grę.")

            builder.setPositiveButton("Zagraj ponownie") { _, _ ->
                rozpocznijGre()
            }
            builder.setNegativeButton("Wyjdź") { _, _ ->
                System.exit(0)
            }

        } else {
            builder.setTitle("KONIEC GRY")
            builder.setMessage("Przegrałeś. Słowo to było ${slowo.uppercase()}")

            builder.setPositiveButton("Zagraj ponownie") { _, _ ->
                rozpocznijGre()
            }
            builder.setNegativeButton("Wyjdź") { _, _ ->
                System.exit(0)
            }
        }

        builder.show()
    }
}
