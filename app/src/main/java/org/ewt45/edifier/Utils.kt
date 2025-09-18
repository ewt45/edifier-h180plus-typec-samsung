package org.ewt45.edifier



class Utils {

}

fun ByteArray.toReadableString(): String = this.joinToString(separator = " ") { String.format("%02x", it) }

