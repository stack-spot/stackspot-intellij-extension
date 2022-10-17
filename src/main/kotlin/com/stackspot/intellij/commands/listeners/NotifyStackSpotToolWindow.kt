package com.stackspot.intellij.commands.listeners

import com.stackspot.intellij.commands.CommandRunner
import com.stackspot.intellij.ui.toolwindow.AbstractStackSpotTree

class NotifyStackSpotToolWindow(private val tree: AbstractStackSpotTree) : CommandRunner.CommandEndedListener {
    override fun notifyEnded() {
        tree.notifyChange()
    }
}