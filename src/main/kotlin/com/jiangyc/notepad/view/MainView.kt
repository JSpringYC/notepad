package com.jiangyc.notepad.view

import javafx.print.PrinterJob
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.FileChooser
import tornadofx.*
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*

class MainView : View() {

    override val root = BorderPane()

    private lateinit var editor: TextArea
    private lateinit var statusBar: FlowPane

    private var contentFile: File? = null
    private var contentText: String = ""

    init {
        title = messages["app.name"] + " - " + messages["untitled"]
        primaryStage.width = 600.0
        primaryStage.height = 400.0
        val image = Image(Thread.currentThread().contextClassLoader.getResourceAsStream("images/ico_notepad.png"))
        primaryStage.icons.add(image)

        with(root) {
            center {
                scrollpane {
                    isFitToHeight = true
                    isFitToWidth = true

                    textarea {
                        editor = this
                        isWrapText = true
                    }
                }
            }
            bottom {
                flowpane {
                    statusBar = this
                    isVisible = true
                }
            }
            top {
                menubar {
                    menu(messages["menu.file"]) {
                        isMnemonicParsing = true

                        item(messages["menu.file.new"], KeyCombination.keyCombination("Ctrl + N")) {
                            isMnemonicParsing = true
                            action {
                                if (isChanged()) {
                                    saveConfirm(yesAction = {
                                        if (contentFile != null) {
                                            contentFile?.writeText(editor.text)
                                            reset(null)
                                        } else {
                                            val fc = FileChooser()
                                            fc.initialFileName = getFileName()
                                            fc.showSaveDialog(primaryStage)?.let {
                                                it.writeText(editor.text)
                                                reset(null)
                                            }
                                        }
                                    }, noAction = {
                                        reset(null)
                                    })
                                } else {
                                    reset(null)
                                }
                            }
                        }
                        item(messages["menu.file.newWindow"], KeyCombination.keyCombination("Ctrl + Shift + N")) {
                            isMnemonicParsing = true
                            isDisable = true
                        }
                        item(messages["menu.file.open"], KeyCombination.keyCombination("Ctrl + O")) {
                            isMnemonicParsing = true
                            action {
                                if (isChanged()) {
                                    saveConfirm(yesAction = {
                                        if (contentFile != null) {
                                            contentFile?.writeText(editor.text)
                                            reset(contentFile)

                                            FileChooser().showOpenDialog(primaryStage)?.let {
                                                reset(it)
                                            }
                                        } else {
                                            val fc = FileChooser()
                                            fc.initialFileName = getFileName()

                                            fc.showSaveDialog(primaryStage)?.let {
                                                it.writeText(editor.text)
                                                reset(it)

                                                FileChooser().showOpenDialog(primaryStage)?.let { it1 ->
                                                    reset(it1)
                                                }
                                            }
                                        }
                                    }, noAction = {
                                        FileChooser().showOpenDialog(primaryStage)?.let {
                                            reset(it)
                                        }
                                    })
                                } else {
                                    FileChooser().showOpenDialog(primaryStage)?.let {
                                        reset(it)
                                    }
                                }
                            }
                        }
                        item(messages["menu.file.save"], KeyCombination.keyCombination("Ctrl + S")) {
                            isMnemonicParsing = true
                            action {
                                if (contentFile == null) {
                                    saveConfirm(yesAction = {
                                        val fc = FileChooser()
                                        fc.initialFileName = getFileName()
                                        fc.showSaveDialog(primaryStage)?.let {
                                            it.writeText(editor.text)
                                            reset(it)
                                        }
                                    })
                                } else {
                                    contentFile?.writeText(editor.text)
                                    reset(contentFile)
                                }
                            }
                        }
                        item(messages["menu.file.saveAs"], KeyCombination.keyCombination("Ctrl + Shift + S")) {
                            isMnemonicParsing = true
                            action {
                                val fc = FileChooser()
                                fc.initialFileName = getFileName()
                                fc.showSaveDialog(primaryStage)?.let {
                                    it.writeText(editor.text)
                                    reset(it)
                                }
                            }
                        }
                        separator()
                        item(messages["menu.file.pageSettings"]) {
                            isMnemonicParsing = true

                            action {
                                val printerJob = PrinterJob.createPrinterJob()
                                val success = printerJob?.showPageSetupDialog(primaryStage)?:false
                                if (success) {
                                    printerJob.endJob()
                                }
                            }
                        }
                        item(messages["menu.file.print"]) {
                            isMnemonicParsing = true

                            action {
                                val printerJob = PrinterJob.createPrinterJob()
                                val success = printerJob?.showPrintDialog(primaryStage)?:false
                                if (success) {
                                    printerJob.endJob()
                                }
                            }
                        }
                        separator()
                        item(messages["menu.file.exit"]) {
                            isMnemonicParsing = true

                            action {
                                saveConfirm(yesAction =  {
                                    primaryStage.close()
                                }, noAction = {
                                    primaryStage.close()
                                })
                            }
                        }
                    }
                    menu(messages["menu.edit"]) {
                        isMnemonicParsing = true

                        item(messages["menu.edit.undo"], KeyCombination.keyCombination("Ctrl + Z")) {
                            action {
                                editor.undo()
                            }
                        }
                        separator()
                        item(messages["menu.edit.cut"], KeyCombination.keyCombination("Ctrl + X")) {
                            action {
                                editor.cut()
                            }
                        }
                        item(messages["menu.edit.copy"], KeyCombination.keyCombination("Ctrl + C")) {
                            action {
                                editor.copy()
                            }
                        }
                        item(messages["menu.edit.paste"], KeyCombination.keyCombination("Ctrl + V")) {
                            action {
                                editor.paste()
                            }
                        }
                        item(messages["menu.edit.delete"], KeyCombination.keyCombination("DELETE")) {
                            action {
                                if (editor.selection.length > 0) {
                                    editor.deleteText(editor.selection)
                                }
                            }
                        }
                        separator()
                        item(messages["menu.edit.bingSearch"], KeyCombination.keyCombination("Ctrl + E")) {
                            action {
                                if (editor.selectedText.isNotEmpty()) {
                                    Desktop.getDesktop().browse(URI("https://bing.com/search?q=" + editor.selectedText))
                                }
                            }
                        }
                        item(messages["menu.edit.find"], KeyCombination.keyCombination("Ctrl + F")) {
                            isMnemonicParsing = true
                            isDisable = true
                        }
                        item(messages["menu.edit.findNext"], KeyCombination.keyCombination("F3")) {
                            isMnemonicParsing = true
                            isDisable = true
                        }
                        item(messages["menu.edit.findPrevious"], KeyCombination.keyCombination("Shift + F3")) {
                            isMnemonicParsing = true
                            isDisable = true
                        }
                        item(messages["menu.edit.replace"], KeyCombination.keyCombination("Ctrl + H")) {
                            isMnemonicParsing = true
                            isDisable = true
                        }
                        item(messages["menu.edit.goto"], KeyCombination.keyCombination("Ctrl + G")) {
                            isMnemonicParsing = true
                            isDisable = true
                        }
                        separator()
                        item(messages["menu.edit.selectAll"], KeyCombination.keyCombination("Ctrl + A")) {
                            isMnemonicParsing = true
                            action {
                                editor.selectAll()
                            }
                        }
                        item(messages["menu.edit.dateTime"], KeyCombination.keyCombination("F5")) {
                            isMnemonicParsing = true
                            action {
                                val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                                if (editor.selection.length == 0) {
                                    editor.insertText(editor.selection.start, dateTime)
                                } else {
                                    editor.replaceText(editor.selection, dateTime)
                                }
                            }
                        }
                    }
                    menu(messages["menu.format"]) {
                        isMnemonicParsing = true

                        checkmenuitem(messages["menu.format.wrapText"]) {
                            isSelected = true

                            action {
                                editor.isWrapText = isSelected
                            }
                        }
                        item(messages["menu.format.font"]) {
                            isMnemonicParsing = true
                            isDisable = true
                        }
                    }
                    menu(messages["menu.view"]) {
                        isMnemonicParsing = true

                        menu(messages["menu.view.zoom"]) {
                            isMnemonicParsing = true

                            item(messages["menu.view.zoomIn"], KeyCombination.keyCombination("Ctrl + ADD")) {
                                isMnemonicParsing = true
                                isDisable = true
                            }
                            item(messages["menu.view.zoomOut"], KeyCombination.keyCombination("Ctrl + SUBTRACT")) {
                                isMnemonicParsing = true
                                isDisable = true
                            }
                            item(messages["menu.view.zoomDefault"], KeyCombination.keyCombination("Ctrl + 0")) {
                                isDisable = true
                            }
                        }
                        checkmenuitem(messages["menu.view.statusBar"]) {
                            isSelected = true

                            action {
                                statusBar.isVisible = isSelected
                            }
                        }
                    }
                    menu(messages["menu.help"]) {
                        isMnemonicParsing = true

                        item(messages["menu.help.help"]) {
                            isDisable = true
                        }
                        item(messages["menu.help.feedback"]) {
                            isDisable = true
                        }
                        item(messages["menu.help.about"]) {
                            isDisable = true
                        }
                    }
                }
            }
        }

        primaryStage.show()
    }

    private fun isChanged(): Boolean {
        return contentText != editor.text
    }

    private fun getFileName() = contentFile?.name?:(messages["untitled"] + ".txt")

    private fun saveConfirm(yesAction: () -> Unit = {}, noAction: () -> Unit = {}) {
        val yesBtn = ButtonType(messages["save"], ButtonBar.ButtonData.YES)
        val noBtn = ButtonType(messages["notSave"], ButtonBar.ButtonData.NO)
        val content = MessageFormat.format(messages["save.confirmed"], getFileName())

        alert(Alert.AlertType.CONFIRMATION, "", content,
            yesBtn, noBtn, ButtonType.CANCEL, owner = primaryStage, title = messages["app.name"]
        ) {
            buttonType ->
            when(buttonType) {
                yesBtn -> yesAction.invoke()
                noBtn -> noAction.invoke()
            }
        }
    }

    private fun reset(file: File? = null) {
        contentFile = file
        contentText = contentFile?.readText()?:""
        editor.text = contentText

        val fileName = contentFile?.name?:messages["untitled"]
        title = messages["app.name"] + " - " + fileName
    }
}