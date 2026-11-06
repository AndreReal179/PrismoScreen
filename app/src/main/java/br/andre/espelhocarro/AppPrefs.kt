package br.andre.espelhocarro

import android.content.Context

object AppPrefs {
    private const val FILE = "espelho"
    private const val KEY = "apps"

    fun selected(ctx: Context): List<String> =
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY, "")
            .orEmpty()
            .split("|")
            .filter { it.isNotBlank() }

    fun save(ctx: Context, packages: List<String>) {
        ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, packages.joinToString("|"))
            .apply()
    }
}
