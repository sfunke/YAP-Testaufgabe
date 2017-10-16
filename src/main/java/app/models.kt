package app

import app.DataPointType.TYPE_FLOAT
import app.DataPointType.TYPE_STRING
import app.DataPointType.TYPE_UINT32
import java.lang.StringBuilder
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * User: Steffen Funke <info@steffen-funke.de>
 * Date: 14.10.17
 * Time: 21:11
 */
object DataPointType {
    const val TYPE_STRING = 0
    const val TYPE_UINT32 = 1
    const val TYPE_FLOAT = 2
}

enum class DataPoint(val idBytes: ByteArray, val type: Int, val endianness: ByteOrder = ByteOrder.LITTLE_ENDIAN, val length: Int = 0) {
    SERIAL_NUMBER(byteArrayOf(0x00, 0x00, 0x00, 0x01), TYPE_STRING, length = 10),
    PRODUCT_TYPE(byteArrayOf(0x00, 0x00, 0x00, 0x02), TYPE_STRING, length = 10),
    POWER_INPUT_WATT(byteArrayOf(0x00, 0x00, 0x01, 0x01), TYPE_FLOAT, ByteOrder.LITTLE_ENDIAN),
    POWER_OUTPUT_WATT(byteArrayOf(0x00, 0x00, 0x01, 0x02), TYPE_FLOAT, ByteOrder.LITTLE_ENDIAN),
    WORK_INPUT_KILOWATTHOURS(byteArrayOf(0x00, 0x00, 0x02, 0x01), TYPE_UINT32, ByteOrder.BIG_ENDIAN),
    WORK_OUTPUT_KILOWATTHOURS(byteArrayOf(0x00, 0x00, 0x02, 0x02), TYPE_UINT32, ByteOrder.BIG_ENDIAN),
    WORKING_HOURS(byteArrayOf(0x00, 0x00, 0x04, 0x01), TYPE_UINT32, ByteOrder.LITTLE_ENDIAN),
}

data class DataResult(val dataPoint:DataPoint, val bytes: ByteBuffer) {
    var intValue:Long? = -1
    var floatValue:Float? = -1f
    var stringValue:String? = null;

    override fun toString(): String {
        val stringBuilder = StringBuilder("DataPoint: ${dataPoint.name} => Result")
        when(dataPoint.type) {
            DataPointType.TYPE_STRING -> {
                stringBuilder.append("<String>: ")
                stringValue = String(bytes.array(), Charsets.UTF_8)
                stringBuilder.append("\"${stringValue!!}\"")
            }
            DataPointType.TYPE_UINT32 -> {
                stringBuilder.append("<UInt32>: ")
                intValue = bytesToUint(bytes.array(), dataPoint.endianness)
                stringBuilder.append(intValue!!)
            }
            DataPointType.TYPE_FLOAT -> {
                stringBuilder.append("<Float>: ")
                floatValue = bytesToFloat(bytes.array(), dataPoint.endianness)
                stringBuilder.append(floatValue!!)
            }
        }
        return stringBuilder.toString()
    }

}