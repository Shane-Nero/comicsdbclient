package cz.kutner.comicsdb.model


data class Classified(val nick: String, val time: String, val category: String, val text: String):Item {
    var iconUrl: String? = null
}


