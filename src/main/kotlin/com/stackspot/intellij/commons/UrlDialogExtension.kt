package com.stackspot.intellij.commons

object InputDialog {
    const val REPOSITORY_URL = "Insert a stack repository URL"
}

object ErrorDialog {
    const val URL_IS_NOT_VALID_MESSAGE = "Requested URL is not valid"
    const val URL_IS_NOT_VALID_TITLE = "Invalid URL"
}

fun String?.isUrlValid(): Boolean {
    val regex = """((git|ssh|https)|(git@[\w\.]+))(:(//)?)([\w\.@\:/\-~]+)(/)?""".toRegex()
    return (this?.isNotEmpty() == true) && regex.matches(this)
}
