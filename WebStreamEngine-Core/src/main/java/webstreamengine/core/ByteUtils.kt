package webstreamengine.core

import java.nio.ByteBuffer


class ByteUtils {
    companion object {
        fun convertShortToBytes(value: Short): ByteArray {
            return byteArrayOf(
                (value.toInt() shr 0).toByte(),
                (value.toInt() shr 8).toByte(),
            )
        }

        fun convertIntToBytes(value: Int): ByteArray {
            return byteArrayOf(
                (value shr 0).toByte(),
                (value shr 8).toByte(),
                (value shr 16).toByte(),
                (value shr 24).toByte()
            )
        }

        fun convertIntToBytes(value: UInt): ByteArray {
            return byteArrayOf(
                (value shr 0).toByte(),
                (value shr 8).toByte(),
                (value shr 16).toByte(),
                (value shr 24).toByte()
            )
        }

        fun convertFloatToByteArray(value: Float): ByteArray {
            val intBits = java.lang.Float.floatToIntBits(value)
            return byteArrayOf(
                (intBits shr 0).toByte(),
                (intBits shr 8).toByte(),
                (intBits shr 16).toByte(),
                (intBits shr 24).toByte()
            )
        }

        fun convertBytesToInt(buffer: ByteArray, startByte: Int): Int {
            return (buffer[startByte + 3].toInt() shl 24) or
                    (buffer[startByte + 2].toInt() and 0xff shl 16) or
                    (buffer[startByte + 1].toInt() and 0xff shl 8) or
                    (buffer[startByte + 0].toInt() and 0xff)
        }

        fun convertBytesToShort(buffer: ByteArray, startByte: Int): Short {
            return ((buffer[startByte + 1].toInt() and 0xff shl 8) or
                    (buffer[startByte + 0].toInt() and 0xff)).toShort()
        }

        fun convertBytesToFloat(buffer: ByteArray, startByte: Int): Float {
            return ByteBuffer.wrap(buffer.sliceArray(IntRange(startByte, startByte + 3))).getFloat(0)
        }

        fun applyIntToByteArray(value: Int, array: ByteArray, startIndex: Int) {
            val newBytes = convertIntToBytes(value)
            array[startIndex + 0] = newBytes[0]
            array[startIndex + 1] = newBytes[1]
            array[startIndex + 2] = newBytes[2]
            array[startIndex + 3] = newBytes[3]
        }

        fun applyIntToByteArray(value: UInt, array: ByteArray, startIndex: Int) {
            val newBytes = convertIntToBytes(value)
            array[startIndex + 0] = newBytes[0]
            array[startIndex + 1] = newBytes[1]
            array[startIndex + 2] = newBytes[2]
            array[startIndex + 3] = newBytes[3]
        }

        fun applyShortToByteArray(value: Short, array: ByteArray, startIndex: Int) {
            val newBytes = convertShortToBytes(value)
            array[startIndex + 0] = newBytes[0]
            array[startIndex + 1] = newBytes[1]
        }

        fun applyFloatToByteArray(value: Float, array: ByteArray, startIndex: Int) {
            val newBytes = ByteBuffer.allocate(4).putFloat(value).array()
            array[startIndex + 0] = newBytes[3]
            array[startIndex + 1] = newBytes[2]
            array[startIndex + 2] = newBytes[1]
            array[startIndex + 3] = newBytes[0]
        }

        fun getBit(value: Int, position: Int): Int {
            return (value shr position) and 1;
        }

        fun flattenListOfByteArrays(list: List<ByteArray>): ByteArray {
            val output = ByteArray(list.sumOf { it.size } + 4)
            ByteUtils.applyIntToByteArray(list.size, output, 0)
            var counter = 4
            var i = 0
            while (i < list.size) {
                val arr = list[i]
                arr.copyInto(output, counter)
                counter += arr.size
                i++
            }
            return output
        }
    }
}
class ByteReader(val byteArray: ByteArray, var counter: Int = 0) {
    fun nextByte(): Byte {
        counter += 1
        return byteArray[counter - 1]
    }

    fun nextInt(): Int {
        counter += 4
        return ByteUtils.convertBytesToInt(byteArray, counter - 4)
    }

    fun nextFloat(): Float {
        counter += 4
        return ByteUtils.convertBytesToFloat(byteArray, counter - 4)
    }
}