/**
 * You can edit, run, and share this code.
 * play.kotlinlang.org
 */


fun convertToAscii(uuid: String?, frienduuid: String?): String {


    var originalText = String()
    var friendText = String()
    val regEx = Regex("[a-zA-Z0-9_]")

    for (char in regEx.findAll(uuid!!.take(7))) {
        originalText += char.value[0].toInt()
    }

    for (char in regEx.findAll(frienduuid!!.take(7))) {
        friendText += char.value[0].toInt()
    }

    if (originalText.isEmpty() || friendText.isEmpty()) {
        return ""
    }
    val sumOfIds = originalText.toLong() + friendText.toLong()


    return sumOfIds.toString()
}