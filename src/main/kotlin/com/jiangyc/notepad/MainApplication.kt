package com.jiangyc.notepad

import com.jiangyc.notepad.view.MainView
import tornadofx.App
import tornadofx.launch

class MainApplication : App(MainView::class)

fun main(args: Array<String>) {
    launch<MainApplication>(args)
}