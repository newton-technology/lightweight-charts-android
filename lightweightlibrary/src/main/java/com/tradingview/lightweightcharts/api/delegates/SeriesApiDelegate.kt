package com.tradingview.lightweightcharts.api.delegates

import com.tradingview.lightweightcharts.api.interfaces.SeriesApi
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Func.APPLY_OPTIONS
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Func.COORDINATE_TO_PRICE
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Func.CREATE_PRICE_LINE
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Func.PRICE_TO_COORDINATE
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Func.REMOVE_PRICE_LINE
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Func.SERIES_TYPE
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Func.SET_MARKERS
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Func.SET_SERIES
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Func.UPDATE
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Params.BAR
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Params.COORDINATE
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Params.DATA
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Params.LINE_ID
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Params.OPTIONS
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Params.PRICE
import com.tradingview.lightweightcharts.api.interfaces.SeriesApi.Params.SERIES_UUID
import com.tradingview.lightweightcharts.api.options.models.PriceLineOptions
import com.tradingview.lightweightcharts.api.options.models.SeriesOptionsCommon
import com.tradingview.lightweightcharts.api.serializer.PrimitiveSerializer
import com.tradingview.lightweightcharts.api.serializer.Deserializer
import com.tradingview.lightweightcharts.api.serializer.SeriesTypeDeserializer
import com.tradingview.lightweightcharts.api.series.common.*
import com.tradingview.lightweightcharts.api.series.enums.SeriesType
import com.tradingview.lightweightcharts.api.series.models.SeriesMarker
import com.tradingview.lightweightcharts.runtime.controller.WebMessageController
import com.tradingview.lightweightcharts.runtime.version.RuntimeObject

class SeriesApiDelegate<T: SeriesOptionsCommon>(
    override val uuid: String,
    private val controller: WebMessageController,
    private val optionsDeserializer: Deserializer<out T>
): SeriesApi, RuntimeObject {

    override fun getVersion(): Int {
        return controller.hashCode()
    }

    override fun setData(data: List<SeriesData>) {
        controller.callFunction(
            SET_SERIES,
            mapOf(
                SERIES_UUID to uuid,
                DATA to data
            )
        )
    }

    override fun priceToCoordinate(price: Float, onCoordinateReceived: (Float?) -> Unit) {
        controller.callFunction<Float?>(
            PRICE_TO_COORDINATE,
            mapOf(
                SERIES_UUID to uuid,
                PRICE to price
            ),
            callback = onCoordinateReceived,
            PrimitiveSerializer.FloatDeserializer
        )
    }

    override fun coordinateToPrice(coordinate: Float, onPriceReceived: (Float?) -> Unit) {
        controller.callFunction(
            COORDINATE_TO_PRICE,
            mapOf(
                SERIES_UUID to uuid,
                COORDINATE to coordinate
            ),
            callback = onPriceReceived,
            PrimitiveSerializer.FloatDeserializer
        )
    }

    override fun applyOptions(options: SeriesOptionsCommon) {
        controller.callFunction(
            APPLY_OPTIONS,
            mapOf(
                SERIES_UUID to uuid,
                OPTIONS to options
            )
        )
    }

    override fun options(onOptionsReceived: (SeriesOptionsCommon) -> Unit) {
        controller.callFunction(
            OPTIONS,
            mapOf(SERIES_UUID to uuid),
            callback = onOptionsReceived,
            deserializer = optionsDeserializer
        )
    }

    override fun setMarkers(data: List<SeriesMarker>) {
        controller.callFunction(
            SET_MARKERS,
            mapOf(
                SERIES_UUID to uuid,
                DATA to data
            )
        )
    }

    override fun createPriceLine(options: PriceLineOptions): PriceLine {
        val uuid = controller.callFunction(
            CREATE_PRICE_LINE,
            mapOf(
                SERIES_UUID to uuid,
                OPTIONS to options
            )
        )
        return PriceLineDelegate(
            uuid,
            controller
        )
    }

    override fun removePriceLine(line: PriceLine) {
        controller.callFunction(
            REMOVE_PRICE_LINE,
            mapOf(
                SERIES_UUID to uuid,
                LINE_ID to line.uuid
            )
        )
    }

    override fun update(bar: SeriesData) {
        controller.callFunction(
            UPDATE,
            mapOf(
                SERIES_UUID to uuid,
                BAR to bar
            )
        )
    }

    override fun seriesType(onSeriesTypeReceived: (SeriesType) -> Unit) {
        controller.callFunction(
            SERIES_TYPE,
            mapOf(SERIES_UUID to uuid),
            callback = onSeriesTypeReceived,
            deserializer = SeriesTypeDeserializer()
        )
    }
}
