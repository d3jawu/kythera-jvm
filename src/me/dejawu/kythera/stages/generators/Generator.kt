package me.dejawu.kythera.stages.generators

interface Generator {
    // kick off compilation process
    fun compile(): ByteArray
}