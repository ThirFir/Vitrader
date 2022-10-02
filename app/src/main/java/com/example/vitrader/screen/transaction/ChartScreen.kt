package com.example.vitrader.screen.transaction

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.vitrader.utils.model.Candle
import com.example.vitrader.utils.viewmodel.CoinViewModel
import com.example.vitrader.utils.viewmodel.UserAccountViewModel
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

fun getDateFormat(date: String, chartFormat: String): String {

    return when(chartFormat) {
        "minutes" -> {
            date.substring(5, 10) + " " + date.substring(11, 16)
        }
        "days", "weeks" -> {
            date.substring(5, 10)
        }
        "months" -> {
            date.substring(0, 7)
        }
        else -> ""
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ChartScreen(coinViewModel: CoinViewModel, userAccountViewModel: UserAccountViewModel) {
    val context = LocalContext.current

    val symbol = coinViewModel.coin?.info?.symbol!!
    val combinedChart by remember { mutableStateOf(CombinedChart(context)) }
    var currentCandles by remember { mutableStateOf(arrayOf<Candle>()) }
    var chartFormat by remember { mutableStateOf("minutes") }
    var time by remember { mutableStateOf(1) }
    var lastCandleTime by remember { mutableStateOf(coinViewModel.coin!!.ticker.trade_time) }

    if(currentCandles.isNotEmpty()) {
//        Log.d("lastCandleTime", coinViewModel.coin!!.ticker.trade_time)
//        Log.d("current last", currentCandles.last().candle_date_time_kst)
//        if (coinViewModel.coin!!.ticker.trade_time != currentCandles.last().candle_date_time_kst) {
//            Log.d("lastCandleTime", "different")
//            getCandles(symbol, 1, chartFormat, time) {
//
//                currentCandles += it
//
//                val priceEntries = arrayListOf<CandleEntry>()
//                val i = (combinedChart.xChartMax + 0.5f)
//
//                priceEntries.add(
//                    CandleEntry(
//                        i,
//                        it.first().high_price,
//                        it.first().low_price,
//                        it.first().opening_price,
//                        it.first().trade_price
//                    )
//                )
//                combinedChart.apply {
//                    this.data = getCombinedData(currentCandles)
//
//                    notifyDataSetChanged()
//                    invalidate()
//                }
//                //lastCandleTime = currentCandles.last().candle_date_time_kst
//            }
//        }
//        else {
//            getCandles(symbol, 1, chartFormat, time) {
//                val currentCandlesAsList = currentCandles.toMutableList()
//                currentCandlesAsList.removeLast()
//                currentCandlesAsList.add(it.first())
//
//                currentCandles = currentCandlesAsList.toTypedArray()
//
//                val priceEntries = arrayListOf<CandleEntry>()
//                val i = (combinedChart.xChartMax)
//
//                priceEntries.add(
//                    CandleEntry(
//                        i,
//                        it.first().high_price,
//                        it.first().low_price,
//                        it.first().opening_price,
//                        it.first().trade_price
//                    )
//                )
//                combinedChart.apply {
//                    this.data = getCombinedData(currentCandles)
//                    notifyDataSetChanged()
//                    invalidate()
//                }
//            }
//        }

    }


    combinedChart.apply {
        //setVisibleXRange(7f, 200f)
        isDragDecelerationEnabled = false
        isDoubleTapToZoomEnabled = false
        setDrawGridBackground(false)
        xAxis.setDrawGridLines(false)
        setPinchZoom(false)
        setVisibleXRangeMaximum(150f)
        setVisibleXRangeMinimum(12f)
        isScaleYEnabled = false


        isAutoScaleMinMaxEnabled = true
        axisRight.apply {
            setDrawGridLines(true)
            spaceTop = 0f
            spaceBottom = 40f

            if(userAccountViewModel.possessingCoins.contains(symbol))
            if (currentCandles.isNotEmpty()) {
                removeAllLimitLines()
                addLimitLine(LimitLine(userAccountViewModel.getAverage(symbol).toFloat()).apply {
                    lineWidth = 0.3f
                    lineColor = Color.rgb(0, 255, 150)

                })
            }

            labelCount = 5
            textColor = Color.rgb(255,255,255)
        }

        axisLeft.apply {
            setDrawGridLines(false)

            isEnabled = true


            isGranularityEnabled = false
            spaceBottom = 0f
            spaceTop = 400f

            axisMinimum = 100f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return ""
                }
            }
        }

        xAxis.apply {
            setDrawGridLines(true)
            this.position = XAxis.XAxisPosition.BOTTOM
            textColor = Color.rgb(255,255,255)

            //this.setCenterAxisLabels(true)
            this.setLabelCount(5, true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    if(value < currentCandles.size)
                        return getDateFormat(currentCandles[value.toInt()].candle_date_time_kst, chartFormat)
                    return ""
                }
            }
        }
    }


    combinedChart.onChartGestureListener = object : OnChartGestureListener{
        override fun onChartGestureStart(
            me: MotionEvent?,
            lastPerformedGesture: ChartTouchListener.ChartGesture?,
        ) {

        }
        override fun onChartGestureEnd(
            me: MotionEvent?,
            lastPerformedGesture: ChartTouchListener.ChartGesture?,
        ) {
            val prevVisible = combinedChart.highestVisibleX
            if(combinedChart.lowestVisibleX <= combinedChart.xChartMin + 1) {

                getCandles(symbol, 200, chartFormat, time, currentCandles[0].candle_date_time_kst) {

                    // currentCandles : 현재 보이는 캔들
                    // it : 새로 받아온 캔들
                    currentCandles = it + currentCandles

                    val priceEntries = arrayListOf<CandleEntry>()
                    var i = (combinedChart.xChartMin - 0.5f) - it.size

                    for (candle in it) {
                        // 캔들 차트 entry 생성
                        priceEntries.add(
                            CandleEntry(
                                i,
                                candle.high_price,
                                candle.low_price,
                                candle.opening_price,
                                candle.trade_price
                            )
                        )
                        i += 1f
                    }

                    combinedChart.apply {
                        this.data = getCombinedData(currentCandles)
                        moveViewToX(prevVisible)
                        notifyDataSetChanged()
                        invalidate()
                    }
                }

            }
        }
        override fun onChartLongPressed(me: MotionEvent?) {

        }
        override fun onChartDoubleTapped(me: MotionEvent?) {

        }
        override fun onChartSingleTapped(me: MotionEvent?) {
            Log.d("Gesture", "Single Tap")
            combinedChart.isDragEnabled = !combinedChart.isDragEnabled
        }
        override fun onChartFling(
            me1: MotionEvent?,
            me2: MotionEvent?,
            velocityX: Float,
            velocityY: Float,
        ) {

        }
        override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
            Log.d("Gesture", "Scale")

        }
        override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
            Log.d("Gesture", "Translate")
        }

    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column {
            Row(modifier = Modifier) {
                for (dateFormat in arrayOf(Pair("분", "minutes"),
                    Pair("일", "days"),
                    Pair("주", "weeks"),
                    Pair("월", "months"))) {
                    Column() {
                        var isExpanded by remember { mutableStateOf(false) }
                        Button(onClick = {
                            isExpanded = !isExpanded

                            if (dateFormat.second != "minutes") {
                                if(dateFormat.second == chartFormat)
                                    return@Button
                                getCandles(symbol, 200, dateFormat.second) {
                                    chartFormat = dateFormat.second
                                    currentCandles = it

                                    lastCandleTime = currentCandles.last().candle_date_time_kst

                                    combinedChart.apply {
                                        data = getCombinedData(it)
                                        notifyDataSetChanged()
                                        invalidate()
                                    }
                                }
                            }
                        }) {
                            Text(dateFormat.first)
                        }

                        if (dateFormat.second == "minutes") {
                            DropdownMenu(expanded = isExpanded,
                                onDismissRequest = { isExpanded = false },
                                modifier = Modifier.width(70.dp)) {


                                for (t in arrayOf(1, 3, 5, 15, 30, 60, 240)) {
                                    DropdownMenuItem(onClick = {
                                        isExpanded = false
                                        getCandles(symbol, 200, "minutes", t) {
                                            chartFormat = "minutes"
                                            currentCandles = it
                                            time = t

                                            lastCandleTime = currentCandles.last().candle_date_time_kst

                                            combinedChart.apply {
                                                data = getCombinedData(it)
                                                notifyDataSetChanged()
                                                invalidate()
                                            }

                                        }
                                    }) {
                                        Text(t.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Candle View
            AndroidView(modifier = Modifier
                .fillMaxSize(),
                factory = { context ->

                    getCandles(symbol, 200, "minutes") {
                        currentCandles = it + arrayOf(Candle(it[0].candle_date_time_kst,
                            coinViewModel.coin!!.ticker.opening_price.toFloat(),
                            coinViewModel.coin!!.ticker.high_price.toFloat(),
                            coinViewModel.coin!!.ticker.low_price.toFloat(),
                            coinViewModel.coin!!.ticker.trade_price.toFloat(),
                            coinViewModel.coin!!.ticker.acc_trade_price.toFloat(),
                            coinViewModel.coin!!.ticker.acc_trade_volume.toFloat()
                        ))

                        lastCandleTime = currentCandles.last().candle_date_time_kst

                        combinedChart.apply {
                            data = getCombinedData(it)
                            notifyDataSetChanged()
                            invalidate()
                        }
                        Log.d("qqqqqqq1", combinedChart.data.getDataSetByIndex(0).toString())
                        Log.d("qqqqqqq2", combinedChart.data.getDataSetByIndex(1).toString())
                    }

                    combinedChart
                })

        }
    }

}





fun getCombinedData(candles: Array<Candle>): CombinedData {
    val data = CombinedData()

    return data.apply {
        setData(getCandleData(candles))
        setData(getVolumeData(candles))
    }
}


fun getCandleData(candles: Array<Candle>): CandleData {

    val candleData = CandleData()

    var i = 0f
    val candleEntries = ArrayList<CandleEntry>()
    for (candle in candles) {
        // 캔들 차트 entry 생성
        candleEntries.add(
            CandleEntry(
                i,
                candle.high_price,
                candle.low_price,
                candle.opening_price,
                candle.trade_price
            )
        )
        i += 1f
    }

    candleData.addDataSet(CandleDataSet(candleEntries, "").apply {
        setDrawValues(false)
        valueTextSize = 0f

        axisDependency = YAxis.AxisDependency.RIGHT
        // 심지 부분 설정
        shadowColor = Color.LTGRAY
        shadowWidth = 0.7F
        // 음봉 설정
        decreasingColor = Color.RED
        decreasingPaintStyle = Paint.Style.FILL
        // 양봉 설정
        increasingColor = Color.GREEN
        increasingPaintStyle = Paint.Style.FILL

        neutralColor = Color.rgb(6, 18, 34)

        // 터치시 노란 선 제거

        highLightColor = Color.WHITE

    })

    return candleData
}

fun getVolumeData(candles: Array<Candle>): BarData {

    val barData = BarData()

    var i = 0f

    val upBarEntries = ArrayList<BarEntry>()
    val downBarEntries = ArrayList<BarEntry>()

    for (candle in candles) {
        if(candle.trade_price - candle.opening_price < 0)   // 시작가 > 종가 -> 매도세가 강함
            downBarEntries.add(BarEntry(
                i, candle.candle_acc_trade_price
            ))
        else
            upBarEntries.add(BarEntry(
                i, candle.candle_acc_trade_price
            ))
        i += 1f
    }


    barData.addDataSet(BarDataSet(upBarEntries, "").apply {
        axisDependency = YAxis.AxisDependency.LEFT
        setDrawValues(false)

        color = Color.rgb(0, 255, 0)

    })
    barData.addDataSet(BarDataSet(downBarEntries, "").apply {
        axisDependency = YAxis.AxisDependency.LEFT

        valueTextSize = 0f
        color = Color.rgb(255, 0, 0)
    })

    return barData
}




fun getCandles(symbol: String, count: Int = 20, dateFormat: String = "days", time: Int = 1, until: String = "", onCompletelyAdded: (Array<Candle>) -> Unit) {
    Log.d("getCandle until", until)
    if(dateFormat != "minutes" && dateFormat != "days" && dateFormat != "weeks" && dateFormat != "months")
        return

    val client = OkHttpClient()

    var url = "https://api.upbit.com/v1/candles/$dateFormat/"
    if(dateFormat == "minutes")
        url += time.toString()
    url += "?market="

    var _until = ""
    if(until != "")
        _until = "&to=$until%2B09:00"
    val requestUrl = "$url$symbol$_until&count=$count"
    Log.d("getCandle requestURL", requestUrl)

    val request = Request.Builder().url(requestUrl).build()

    val gson = Gson()


    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("getChart Failed", e.toString())
        }
        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body!!.string()
            Log.d("getChart response", responseString)
            val candles = gson.fromJson(responseString, Array<Candle>::class.java)

            if(candles.isNotEmpty())
                onCompletelyAdded(candles.reversedArray())
        }
    })

}