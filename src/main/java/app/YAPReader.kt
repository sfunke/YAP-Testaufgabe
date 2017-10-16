package app

/**
 * User: Steffen Funke <info@steffen-funke.de>
 * Date: 14.10.17
 * Time: 19:30
 */
class YAPReader(val transport: Transport) {

    fun read(dataPoint: DataPoint): String {
        val result = transport.read(dataPoint)
        return result.toString()
    }

    fun start() {
        DataPoint.values().forEach {
            try {
                transport.open()
                val result = read(it)
                println(result)
            } finally {
                transport.close()
            }
        }
    }
}