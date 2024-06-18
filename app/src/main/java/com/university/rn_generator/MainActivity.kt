package com.university.rn_generator

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.university.rn_generator.databinding.ActivityMainBinding
import java.util.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    //Кнопка "Генерация"
    private lateinit var btnGen: Button
    //Переменные для диаграммы
    private lateinit var barChart: BarChart
    //Переменная для данных диаграммы
    private lateinit var barData: BarData
    //Переменная для набора данных диаграммы
    private lateinit var barDataSet: BarDataSet
    //Список массивов для данных диаграммы
    private lateinit var barEntriesList: ArrayList<BarEntry>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Портретный режим
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //Переменная для диаграммы
        barChart = findViewById(R.id.idBarChart)
        //Кнопка "Генерация"
        btnGen = findViewById(R.id.buttonGen)
        btnGen.setOnClickListener {
            showResults()
        }
    }

    private var N = ""
    private var A = ""
    private var P = ""
    private var T = ""
    private var K = ""

    //Отображение результатов генерирования и проверки последовательности случайных чисел
    private fun showResults() {
        N = binding.editTextN.text.toString().trim()
        A = binding.editTextA.text.toString().trim()
        P = binding.editTextP.text.toString().trim()
        T = binding.editTextT.text.toString().trim()
        K = binding.editTextK.text.toString().trim()
        //Проверка заполнения полей
        if (N.isBlank() || A.isBlank()|| P.isBlank() || T.isBlank()|| K.isBlank()) {
            Toast.makeText(this, (resources.getString(R.string.text_EmptyData)), Toast.LENGTH_SHORT).show()
            return
        }
        //Проверка корректности ввода параметра N
        if (N.toInt() <= 0) {
            Toast.makeText(this, (resources.getString(R.string.text_ErN)), Toast.LENGTH_SHORT).show()
            return
        }
        //Проверка корректности ввода параметра A
        if (A.toInt() <= 0) {
            Toast.makeText(this, (resources.getString(R.string.text_ErA)), Toast.LENGTH_SHORT).show()
            return
        }
        //Проверка корректности ввода параметра P
        if (P.toFloat() <= 0) {
            Toast.makeText(this, (resources.getString(R.string.text_ErP)), Toast.LENGTH_SHORT).show()
            return
        }
        //Проверка корректности ввода параметра T
        if (T.toFloat() != 1.65.toFloat()
            && T.toFloat() != 1.96.toFloat()
            && T.toFloat() != 2.06.toFloat()
            && T.toFloat() != 2.18.toFloat()
            && T.toFloat() != 2.33.toFloat()
            && T.toFloat() != 2.58.toFloat()
            && T.toFloat() != 3.30.toFloat()) {
            Toast.makeText(this, (resources.getString(R.string.text_ErT)), Toast.LENGTH_SHORT).show()
            return
        }
        //Проверка корректности ввода параметра K
        if (K.toInt() <= 0) {
            Toast.makeText(this, (resources.getString(R.string.text_ErK)), Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, (resources.getString(R.string.text_Generation)), Toast.LENGTH_SHORT).show()
        val genRN = generateRN()
        val moRN = moRN()
        val disRN = disRN()
        checkKolmogorov()
        checkZero()
        getBarChartData()
        //Инициализация набора данных диаграммы
        barDataSet = BarDataSet(barEntriesList, "Bar Chart Data")
        //Инициализация данных диаграммы
        barData = BarData(barDataSet)
        //Установка данных для гистограммы
        barChart.data = barData
        //Установка цвета текста для гистограммы
        barDataSet.valueTextColor = Color.BLACK
        //Установка цвета для набора данных гистограммы
        barDataSet.setColor(resources.getColor(R.color.grey))
        //Установка размера текста
        barDataSet.valueTextSize = 16f
        //Включение описания как ложное
        barChart.description.isEnabled = false
        //Вывод сгенерированной последовательности случайных чисел методом Макларена-Марсальи
        binding.editTextRN.setText((Arrays.toString(genRN)).toString())
        //Вывод математического ожидания сгенерированной последовательности случайных чисел
        binding.editTextMO.setText(moRN.toString())
        //Вывод дисперсии сгенерированной последовательности случайных чисел
        binding.editTextDis.setText(disRN.toString())
        Toast.makeText(this, (resources.getString(R.string.text_Complete)), Toast.LENGTH_SHORT).show()
    }

    //Генерирование последовательности случайных чисел методом Макларена-Марсальи
    private fun methodMM(): Double {
        //Сгенерированная 1-ая случайная последовательность
        val gen1 = DoubleArray(A.toInt())
        //Генерирование 1-ой случайной последовательности
        for (i in 0 until A.toInt()) {
            gen1[i] = Random.nextDouble()
        }
        //Случайное число 1-ой последовательности
        val rn1: Double = Random.nextDouble()
        //Случайное число 2-ой последовательности
        val rn2: Double = Random.nextDouble()
        val M: Int = (rn2 * A.toInt()).toInt()
        val res: Double = gen1[M]
        gen1[M] = rn1
        return res
    }

    //Сгенерированная последовательность случайных чисел методом Макларена-Марсальи
    private fun generateRN(): DoubleArray {
        val genRN = DoubleArray(N.toInt())
        for (i in 0 until N.toInt()) {
            genRN[i] = methodMM()
        }
        return genRN
    }

    //Расчет математического ожидания сгенерированной последовательности случайных чисел
    private fun moRN(): Double {
        val genRN = generateRN()
        val sumRN: Double = genRN.sum()
        val moRN: Double = sumRN / N.toInt()
        return moRN
    }

    //Расчет дисперсии сгенерированной последовательности случайных чисел
    private fun disRN(): Double {
        val genRN = generateRN()
        val moRN = moRN()
        val sumRN: Double = genRN.sum()
        val sumRN2: Double = sumRN.pow(2.0)
        val disRN: Double = sumRN2 / N.toInt() - moRN.pow(2.0)
        return disRN
    }

    //Проверка сгенерированной последовательности случайных чисел критерием Колмогорова
    private fun checkKolmogorov() {
        val genRN = generateRN()
        var dMax = 0.0
        val sortRN = genRN.sorted()
        for (i in 0 until N.toInt()) {
            val dp: Double = abs((i + 1).toDouble() / N.toInt() - sortRN[i])
            val dm: Double = abs(sortRN[i] - i.toDouble() / N.toInt())
            if (dp > dMax) dMax = dp
            if (dm > dMax) dMax = dm
        }
        val lambda = dMax * sqrt(N.toDouble())
        binding.editTextKolmogorov.setText("λ = $lambda")
    }

    //Проверка сгенерированной последовательности случайных чисел тестом длины серий нулей
    private fun checkZero() {
        val genRN = generateRN()
        val testRN = genRN
        for (i in 0 until N.toInt()) {
            if (genRN[i] < P.toFloat()) testRN[i] = 0.0
            else if (genRN[i] > P.toFloat()) testRN[i] = 1.0
        }
        var K0 = 0
        for (i in 1 until N.toInt())
            if (genRN[i - 1] == 0.0 && genRN[i] == 1.0)
                K0++
        if (genRN[N.toInt() - 1] == 0.0)
            K0++
        var N0 = 0
        for (i in 1 until N.toInt())
            if (testRN[i] == 0.0)
                N0++
        val mTestRN = (1 - P.toFloat() / P.toFloat()) + 1
        val dTestRN = (1 - P.toFloat()) / P.toFloat().pow(2)
        val mvTestRN = N0 / K0
        val RNi = mTestRN - T.toFloat() * sqrt(dTestRN / K0)
        val RNv = mTestRN + T.toFloat() * sqrt(dTestRN / K0)
        if (mvTestRN >= RNi && mvTestRN <= RNv) {
            binding.editTextZero.setText(resources.getString(R.string.editText_YesZero))
        }
        else binding.editTextZero.setText(resources.getString(R.string.editText_NoZero))
    }

    //Получение данных для диаграммы
    private fun MakeData(): DoubleArray {
        val genRN = generateRN()
        val delta: Double = (1.0 - 0.0) / K.toInt()
        val parDataPlot = DoubleArray(K.toInt())
        val parDataFunc = DoubleArray(K.toInt())
        for (i in 0 until N.toInt()) {
            var j: Double = (genRN[i] - 0) / delta
            if (j >= K.toInt()) j = (K.toInt() - 1).toDouble()
            else if (j < 0) j = 0.0
            parDataPlot[j.toInt()]++
        }
        for (i in 0 until K.toInt())
            parDataPlot[i] /= N.toDouble()
        parDataFunc[0] = parDataPlot[0]
        for (i in 1 until K.toInt())
            parDataFunc[i] = parDataFunc[i - 1] + parDataPlot[i]
        return parDataPlot
    }

    //Отображение диаграммы
    private fun getBarChartData() {
        val parDataPlot = MakeData()
        barEntriesList = ArrayList()
        for (i in 0 until K.toInt()) {
            barEntriesList.add(BarEntry(("" + i).toFloat(), (parDataPlot[i]).toFloat()))
        }
    }
}