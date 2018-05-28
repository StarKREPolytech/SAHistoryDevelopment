package com.activities.currentHistoryActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.example.starkre.sleepAlertHistory.R
import com.activities.historyListActivity.HistoryListActivity
import com.annotations.FuckingStaticSingleton
import com.annotations.XMLProvided
import com.historyManagement.history.historyData.DataAnalyser
import com.historyManagement.provider.HistoryManagerProvider
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.*
import java.util.logging.Logger

/**
 * @author Игорь Гулькин на 23.05.2018.
 *
 * Класс CurrentHistoryActivity описывает окно конкретной истории.
 */

@FuckingStaticSingleton
class CurrentHistoryActivity : AppCompatActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmField
        var THIS: CurrentHistoryActivity? = null
        @JvmStatic
        val log: Logger = Logger.getLogger(CurrentHistoryActivity::class.java.name)
    }

    /**
     * 1.) inScopeHistory история, на которую был переход;
     * 2.) pieTirednessChar - круговая диаграмма уровня усталости;
     * 3) headlineTextView - заголовок истории на окне;
     * 4.) passedDistanceTextView - информация о пройденном пути;
     * 5.) warningNumberTextView - информация о предупреждениях;
     */

    private var inScopeHistory = HistoryManagerProvider.THIS?.inScopeHistory

    private var pieTirednessChart: PieChart? = null

    private var headlineTextView: TextView? = null

    private var passedDistanceTextView: TextView? = null

    private var warningNumberTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THIS = this
        super.onCreate(savedInstanceState)
        //Инициализация ресурсов:
        this.setContentView(R.layout.current_history_activity)
        this.headlineTextView = this.findViewById(R.id.history_activity_headline_text_view)
        this.passedDistanceTextView = this.findViewById(R.id.history_distance_text_view)
        this.warningNumberTextView = this.findViewById(R.id.history_warnings_text_view)
        //Создание круговой диаграммы:
        this.initPieTirednessChart()
    }

    private fun initPieTirednessChart() {
        this.pieTirednessChart = this.findViewById(R.id.history_tiredness_pie_chart)
        this.pieTirednessChart?.setUsePercentValues(true)
        this.pieTirednessChart?.description?.isEnabled = false
        this.pieTirednessChart?.setExtraOffsets(5f, 10f, 5f, 5f)
        this.pieTirednessChart?.dragDecelerationFrictionCoef = 0.95f
        this.pieTirednessChart?.isDrawHoleEnabled = true
        this.configurePieTirednessEntryLabel()
        this.configurePieTirednessChartLegend()
    }

    /**
     * onStart() инициализирует activity при переходе.
     */

    @SuppressLint("SetTextI18n")
    override fun onStart() {
        super.onStart()
        val dataAnalyser = this.inScopeHistory?.dataAnalyser
        this.headlineTextView?.text = this.inScopeHistory?.headline
        this.pieTirednessChart?.data = this.getPieData(dataAnalyser!!)
        this.passedDistanceTextView?.text = dataAnalyser.distanceInterval
        //Здесь нужно сделать каст на String, иначе метод .setText будет искать String по ID:
        this.warningNumberTextView?.text = dataAnalyser.getWarningNumber().toString()
    }

    /**
     * getPieData(dataAnalyser: DataAnalyser) создает данные для диаграммы.
     *
     * @param dataAnalyser - анализатор уровней усталости.
     */

    private fun getPieData(dataAnalyser: DataAnalyser): PieData {
        val low = (dataAnalyser.low * 100).toInt()
        var medium = (dataAnalyser.medium * 100).toInt()
        val high = (dataAnalyser.high * 100).toInt()
        val critical = (dataAnalyser.critical * 100).toInt()
        val equality = 100 - (low + medium + high + critical)
        if (equality != 0) {
            medium += equality
        }
        val correctedMedium = medium
        log.info("IN_SCOPE_HISTORY: " + this.inScopeHistory)
        log.info("LOW: $low")
        val pieEntryList = object : ArrayList<PieEntry>() {
            init {
                this.add(PieEntry(low.toFloat(), "низкий"))
                this.add(PieEntry(correctedMedium.toFloat(), "средний"))
                this.add(PieEntry(high.toFloat(), "высокий"))
                this.add(PieEntry(critical.toFloat(), "критический"))
            }
        }
        val pieDataSet = PieDataSet(pieEntryList, "Уровень усталости")
        pieDataSet.sliceSpace = 3f
        pieDataSet.selectionShift = 10f
        pieDataSet.setColors(extractColor(R.color.history_green)
                , extractColor(R.color.history_yellow)
                , extractColor(R.color.history_red)
                , extractColor(R.color.history_dark_red))
        pieDataSet.formSize = 17f
        return object : PieData(pieDataSet) {
            init {
                this.setValueTextColor(Color.WHITE)
                this.setValueTextSize(14f)
            }
        }
    }

    /**
     * extractColor(id: Int) извлекает цвет из ресурсов.
     *
     * @param id - @color из директории res.
     */

    private fun extractColor(id: Int): Int {
        return ContextCompat.getColor(this.applicationContext, id)
    }

    /**
     * configurePieTirednessEntryLabel() настраивает логотипы на диаграмме.
     */

    private fun configurePieTirednessEntryLabel() {
        //Без логотипов:
        this.pieTirednessChart?.setDrawEntryLabels(false)
    }

    /**
     * configurePieTirednessChartLegend() настраивает легенду диаграммы.
     */

    private fun configurePieTirednessChartLegend() {
        val legend = this.pieTirednessChart?.legend
        legend?.textSize = 14f
        legend?.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        legend?.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend?.orientation = Legend.LegendOrientation.VERTICAL
        legend?.setDrawInside(false)
        legend?.textColor = R.color.blue
        legend?.form = Legend.LegendForm.SQUARE
    }

    /**
     * backToHistoryList(unused: View) возращается на окно списка историй.
     *
     * @param unused не используется.
     */

    @XMLProvided(layout = "activity_history.xml")
    fun backToHistoryList(unused: View) {
        this.startActivity(Intent(this, HistoryListActivity::class.java))
    }
}