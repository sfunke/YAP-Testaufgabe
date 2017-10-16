package app
import org.junit.Assert.assertEquals

/**
 * User: Steffen Funke <info@steffen-funke.de>
 * Date: 14.10.17
 * Time: 18:41
 */
class EndpointTests {
    val reader = YAPReader(app.DummyTransport())

    @org.junit.Test
    fun testString() {
        val result: String = reader.read(app.DataPoint.SERIAL_NUMBER)
        assertEquals("DataPoint: SERIAL_NUMBER => Result<String>: \"AFG4387X01\"", result)
    }

    @org.junit.Test
    fun testUInt() {
        val result: String = reader.read(app.DataPoint.WORK_INPUT_KILOWATTHOURS)
        assertEquals("DataPoint: WORK_INPUT_KILOWATTHOURS => Result<UInt32>: 23", result)
    }

    @org.junit.Test
    fun testFloat() {
        val result: String = reader.read(app.DataPoint.POWER_OUTPUT_WATT)
        assertEquals("DataPoint: POWER_OUTPUT_WATT => Result<Float>: 23456.543", result)
    }

}