package noh.jinil.anytime.log

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object LogFormat {
    private const val ANYTIME_TAG = "ANYTIME_LOG"
    private const val JSON_TAG = "ANYTIME_JSON"
    private const val HEAD_TAG = "ANYTIME_HEAD"

    var enable: Boolean = false

    fun w(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.w(ANYTIME_TAG, "[$tag] $log")
    }

    fun i(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.i(ANYTIME_TAG, "[$tag] $log")
    }

    fun d(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.d(ANYTIME_TAG, "[$tag] $log")
    }

    fun v(log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.v(ANYTIME_TAG, "  -> $log")
    }

    fun l(tag: String?, log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.v(ANYTIME_TAG, " => $log in [$tag]")
    }

    fun e(log: String?) {
        if (!enable || log == null) {
            return
        }
        Log.e(ANYTIME_TAG, log)
    }

    fun send(data: String) {
        json("Send", data)
    }

    fun receive(data: String) {
        json("Receive", data)
    }

    fun httpResponse(message: String) {
        try {
            message.toByteArray(Charsets.UTF_8)

            when {
                message.startsWith("-->") || message.startsWith("<--") -> {
                    Log.d(ANYTIME_TAG, message)
                }
                message.startsWith("{") || message.startsWith("[") -> {
                    json("  data  ", message)
                }
                else ->
                    Log.d(HEAD_TAG, message)
            }
        } catch (e: Exception) {
            Log.d(ANYTIME_TAG, "binary data!!")
        }
    }

    private fun json(title: String, data: String) {
        json(title, JsonParser.parseString(data))
    }

    private fun json(title: String, data: Any) {
        if (!enable) {
            return
        }

        fun handleObject(obj: JsonObject?, blank: String = "") {
            obj ?: return

            obj.keySet().forEach { key ->
                obj[key].let { value->
                    when (value) {
                        is JsonObject -> {
                            Log.v(JSON_TAG, "|$blank $key: {")
                            handleObject(value, "$blank  ")
                            Log.v(JSON_TAG, "|$blank }")
                        }
                        is JsonArray -> {
                            when {
                                value.size() == 0 -> {
                                    Log.v(JSON_TAG, "|$blank $key: []")
                                }
                                value[0] is JsonObject -> {
                                    Log.v(JSON_TAG, "|$blank $key: [")
                                    for (i in 0 until value.size()) {
                                        Log.v(JSON_TAG, "|$blank   {")
                                        handleObject(value[i] as JsonObject, "$blank    ")
                                        Log.v(JSON_TAG, "|$blank   }")
                                    }
                                    Log.v(JSON_TAG, "|$blank ]")
                                }
                                else -> {
                                    Log.v(JSON_TAG, "|$blank $key: $value")
                                }
                            }
                        }
                        else -> {
                            Log.v(JSON_TAG, "|$blank $key: $value")
                        }
                    }
                }
            }

        }

        fun handleArray(array: JsonArray, blank: String = "") {
            Log.v(JSON_TAG, "|$blank[")
            for (i in 0 until array.size()) {
                Log.v(JSON_TAG, "|  $blank{")
                handleObject(array[i] as? JsonObject, "$blank   ")
                Log.v(JSON_TAG, "|  $blank}")
            }
            Log.v(JSON_TAG, "|$blank]")
        }

        Log.v(JSON_TAG, " ------- $title -------------------------------------------------")
        when (data) {
            is JsonObject -> {
                handleObject(data)
            }

            is JsonArray ->
                handleArray(data)
        }
        Log.v(JSON_TAG, " ------------------------------------------------------------------")
    }
}